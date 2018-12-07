package net.chinaedu.aedu.tools.compiler.processors;

import net.chinaedu.aedu.tools.annotations.BindView;
import net.chinaedu.aedu.tools.annotations.Consts;
import net.chinaedu.aedu.tools.annotations.OnClick;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * @author MartinKent
 * @time 2018/1/23
 */
class ViewBinderProcessor extends BaseProcessor {
    private Map<String, AnnotatedClass> mAnnotatedClassMap = new TreeMap<>();
    private static Class<?>[] mSupportedAnnotationTypes = {
            BindView.class,
            OnClick.class
    };

    @Override
    Class<?>[] getSupportedAnnotationClasses() {
        return mSupportedAnnotationTypes;
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (0 == roundEnv.getElementsAnnotatedWith(BindView.class).size() && 0 == roundEnv.getElementsAnnotatedWith(OnClick.class).size()) {
            return;
        }
        mAnnotatedClassMap.clear();
        try {
            processBindView(roundEnv);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            error(e.getMessage());
        }

        for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
            witeJavaFile(annotatedClass.generateFile());
        }
    }

    private void processBindView(RoundEnvironment roundEnv) throws IllegalArgumentException {
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(element);
            annotatedClass.addMethod(annotatedMethod);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            AnnotatedField bindViewField = new AnnotatedField(element);
            annotatedClass.addField(bindViewField);
        }
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullName);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(typeElement, getElementUtils());
            mAnnotatedClassMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }

    /**
     * @author MartinKent
     * @time 2018/1/5
     */
    @SuppressWarnings("UseSparseArrays")
    private static class AnnotatedClass {

        private static class TypeUtil {
            static final ClassName BINDER = ClassName.bestGuess("net.chinaedu.aedu.tools.viewbinder.ViewBinder");
            static final ClassName FINDER = ClassName.bestGuess("net.chinaedu.aedu.tools.viewbinder.ViewFinder");
        }

        private TypeElement mTypeElement;
        private ArrayList<AnnotatedField> mFields;
        private Map<Long, AnnotatedMethod> mMethods;
        private Elements mElements;

        AnnotatedClass(TypeElement typeElement, Elements elements) {
            mTypeElement = typeElement;
            mElements = elements;
            mFields = new ArrayList<>();
            mMethods = new HashMap<>();
        }

        void addField(AnnotatedField field) {
            mFields.add(field);
        }

        public void addMethod(AnnotatedMethod method) {
            for (long id : method.getResIds()) {
                mMethods.put(id, method);
            }
        }

        public JavaFile generateFile() {
            //generateMethod
            MethodSpec.Builder bindViewMethod = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                    .addParameter(TypeName.OBJECT, "source", Modifier.FINAL)
                    .addParameter(TypeUtil.FINDER, "finder", Modifier.FINAL);

            ClassName viewClass = ClassName.bestGuess("android.view.View");
            for (AnnotatedField field : mFields) {
                // find views
                bindViewMethod.addStatement("host.$N = ($T)(finder.findView(source, $L))", field.getFieldName(), ClassName.get(field.getFieldType()), field.getResId());
                AnnotatedMethod method = mMethods.get(field.getResId());
                if (null != method) {
                    bindViewMethod.addCode("\r\nhost.$N.setOnClickListener(new $T.OnClickListener() {\r\n" +
                            "            @Override\r\n" +
                            "            public void onClick(View v) {\r\n" +
                            "               " + (0 == method.getParamSize() ? "host.$N();\r\n" : "host.$N(v);\r\n") +
                            "            }\r\n" +
                            "        });", field.getFieldName(), viewClass, method.getName());
                    mMethods.remove(field.getResId());
                }
            }
            Iterator<Map.Entry<Long, AnnotatedMethod>> iterator = mMethods.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, AnnotatedMethod> entry = iterator.next();
                bindViewMethod.addCode("\n\nfinder.findView(source, $L).setOnClickListener(new $T.OnClickListener() {\r\n" +
                        "            @Override\r\n" +
                        "            public void onClick(View v) {\r\n" +
                        "               " + (0 == entry.getValue().getParamSize() ? "host.$N();\r\n" : "host.$N(v);\r\n") +
                        "            }\r\n" +
                        "        });", entry.getKey(), viewClass, entry.getValue().getName());
            }

            MethodSpec.Builder unBindViewMethod = MethodSpec.methodBuilder("unBindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(mTypeElement.asType()), "host")
                    .addAnnotation(Override.class);
            for (AnnotatedField field : mFields) {
                unBindViewMethod.addStatement("host.$N = null", field.getFieldName());
            }

            //generaClass
            TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + Consts.VIEW_BINDER_SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(TypeUtil.BINDER, TypeName.get(mTypeElement.asType())))
                    .addMethod(bindViewMethod.build())
                    .addMethod(unBindViewMethod.build())
                    .build();

            String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();

            return JavaFile.builder(packageName, injectClass).build();
        }
    }

    /**
     * @author MartinKent
     * @time 2018/1/5
     */
    private static class AnnotatedField {
        private VariableElement mVariableElement;
        private long mResId;

        AnnotatedField(Element element) throws IllegalArgumentException {
            if (element.getKind() != ElementKind.FIELD) {
                throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                        BindView.class.getSimpleName()));
            }
            mVariableElement = (VariableElement) element;

            BindView bindView = mVariableElement.getAnnotation(BindView.class);
            mResId = bindView.value();
            if (mResId < 0) {
                throw new IllegalArgumentException(
                        String.format("value() in %s for field %s is not valid !", BindView.class.getSimpleName(),
                                mVariableElement.getSimpleName()));
            }
        }

        /**
         * 获取变量名称
         *
         * @return
         */
        Name getFieldName() {
            return mVariableElement.getSimpleName();
        }

        /**
         * 获取变量id
         *
         * @return
         */
        long getResId() {
            return mResId;
        }

        /**
         * 获取变量类型
         *
         * @return
         */
        TypeMirror getFieldType() {
            return mVariableElement.asType();
        }
    }

    /**
     * @author MartinKent
     * @time 2018/1/5
     */
    private static class AnnotatedMethod {
        private final ExecutableElement targetElement;
        private long[] ids;

        AnnotatedMethod(Element element) {
            this.targetElement = (ExecutableElement) element;
            OnClick bindView = targetElement.getAnnotation(OnClick.class);
            this.ids = bindView.value();
        }

        @Override
        public boolean equals(Object o) {
            return null != o && o instanceof AnnotatedMethod && Objects.equals(((AnnotatedMethod) o).targetElement, this.targetElement);
        }

        long[] getResIds() {
            return ids;
        }

        Name getName() {

            return targetElement.getSimpleName();
        }

        int getParamSize() {
            return null == targetElement.getParameters() ? 0 : targetElement.getParameters().size();
        }
    }
}
