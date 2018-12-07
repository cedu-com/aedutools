package net.chinaedu.aedu.tools.compiler.processors;

import net.chinaedu.aedu.tools.annotations.Consts;
import net.chinaedu.aedu.tools.annotations.SaveState;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @author MartinKent
 * @time 2018/1/23
 */
class StateSaveProcessor extends BaseProcessor {
    private Map<String, AnnotatedClass> mAnnotatedClassMap = new TreeMap<>();
    private static Class<?>[] mSupportedAnnotationTypes = {
            SaveState.class,
    };

    @Override
    Class<?>[] getSupportedAnnotationClasses() {
        return mSupportedAnnotationTypes;
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (0 == roundEnv.getElementsAnnotatedWith(SaveState.class).size()) {
            return;
        }
        mAnnotatedClassMap.clear();
        try {
            processAnnotations(roundEnv);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            error(e.getMessage());
        }

        for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
            witeJavaFile(annotatedClass.generateFile());
        }
    }

    private void processAnnotations(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SaveState.class)) {
            if (!(element instanceof VariableElement)) {
                continue;
            }
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addField(new AnnotatedField(element));
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

    private static class AnnotatedClass {
        static final ClassName BUNDLE_CLASS = ClassName.bestGuess("android.os.Bundle");
        static final ClassName STATE_SAVER = ClassName.bestGuess("net.chinaedu.aedu.tools.statesaver.StateSaver");

        private TypeElement mTypeElement;
        private ArrayList<AnnotatedField> mFields;
        private Elements mElements;

        private static final Map<String, String[]> typeMap = new HashMap<>();

        static {
            typeMap.put("android.os.Bundle", new String[]{"getBundle", "putBundle"});
            typeMap.put("boolean", new String[]{"getBoolean", "putBoolean"});
            typeMap.put("boolean[]", new String[]{"getBooleanArray", "putBooleanArray"});
            typeMap.put("byte", new String[]{"getByte", "putByte"});
            typeMap.put("byte[]", new String[]{"getByteArray", "putByteArray"});
            typeMap.put("char", new String[]{"getChar", "putChar"});
            typeMap.put("char[]", new String[]{"getCharArray", "putCharArray"});
            typeMap.put("java.lang.CharSequence", new String[]{"getCharSequence", "putCharSequence"});
            typeMap.put("java.lang.CharSequence[]", new String[]{"getCharSequenceArray", "putCharSequenceArray"});
            typeMap.put("java.util.ArrayList<java.lang.CharSequence>", new String[]{"getCharSequenceArrayList", "putCharSequenceArrayList"});
            typeMap.put("double", new String[]{"getDouble", "putDouble"});
            typeMap.put("double[]", new String[]{"getDoubleArray", "putDoubleArray"});
            typeMap.put("float", new String[]{"getFloat", "putFloat"});
            typeMap.put("float[]", new String[]{"getFloatArray", "putFloatArray"});
            typeMap.put("int", new String[]{"getInt", "putInt"});
            typeMap.put("int[]", new String[]{"getIntArray", "putIntArray"});
            typeMap.put("java.util.ArrayList<java.lang.Integer>", new String[]{"getIntegerArrayList", "putIntegerArrayList"});
            typeMap.put("long", new String[]{"getLong", "putLong"});
            typeMap.put("long[]", new String[]{"getLongArray", "putLongArray"});
            typeMap.put("android.os.Parcelable", new String[]{"getParcelable", "putParcelable"});
            typeMap.put("android.os.Parcelable[]", new String[]{"getParcelableArray", "putParcelableArray"});
            typeMap.put("java.util.ArrayList<android.os.Parcelable>", new String[]{"getParcelableArrayList", "putParcelableArrayList"});
            typeMap.put("java.io.Serializable", new String[]{"getSerializable", "putSerializable"});
            typeMap.put("short", new String[]{"getShort", "putShort"});
            typeMap.put("short[]", new String[]{"getShortArray", "putShortArray"});
            typeMap.put("android.util.SparseArray<android.os.Parcelable>", new String[]{"getSparseParcelableArray", "putSparseParcelableArray"});
            typeMap.put("java.lang.String", new String[]{"getString", "putString"});
            typeMap.put("java.lang.String[]", new String[]{"getStringArray", "putStringArray"});
            typeMap.put("java.util.ArrayList<java.lang.String>", new String[]{"getStringArrayList", "putStringArrayList"});
        }

        AnnotatedClass(TypeElement typeElement, Elements elements) {
            mTypeElement = typeElement;
            mElements = elements;
            mFields = new ArrayList<>();
        }

        void addField(AnnotatedField annotatedField) {
            mFields.add(annotatedField);
        }

        JavaFile generateFile() {
            MethodSpec.Builder saveState = MethodSpec.methodBuilder("save")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(BUNDLE_CLASS)
                    .addParameter(TypeName.get(mTypeElement.asType()), "target", Modifier.FINAL)
                    .addParameter(BUNDLE_CLASS, "data", Modifier.FINAL);
            for (AnnotatedField field : mFields) {
                String fieldType = field.getTypeName().toString();
                SaveState ann = field.getAnnotation();
                String key = ann.value();
                if ("".equals(key)) {
                    key = field.getKey();
                }
                if (!typeMap.containsKey(fieldType)) {
                    throw new RuntimeException("Field of type[" + field.getTypeName().toString() + "] is not surpported. See android.os.Bundle");
                }
                saveState.addStatement("data.$L($S,target.$N)", typeMap.get(fieldType)[1], key, field.getFieldName());
            }
            saveState.addStatement("return data");

            MethodSpec.Builder restoreState = MethodSpec.methodBuilder("restore")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(mTypeElement.asType()), "target", Modifier.FINAL)
                    .addParameter(BUNDLE_CLASS, "data", Modifier.FINAL);
            for (StateSaveProcessor.AnnotatedField field : mFields) {
                String fieldType = field.getTypeName().toString();
                SaveState ann = field.getAnnotation();
                String key = ann.value();
                if ("".equals(key)) {
                    key = field.getKey();
                }
                if (!typeMap.containsKey(fieldType)) {
                    throw new RuntimeException("Field of type[" + field.getTypeName().toString() + "] is not surpported. See android.os.Bundle");
                }
                restoreState.addStatement("target.$N = data.$L($S)", field.getFieldName(), typeMap.get(fieldType)[0], key);
            }
            TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + Consts.STATE_SAVER_SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(STATE_SAVER, TypeName.get(mTypeElement.asType())))
                    .addMethod(saveState.build())
                    .addMethod(restoreState.build())
                    .build();
            String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();
            return JavaFile.builder(packageName, injectClass).build();
        }
    }

    private static class AnnotatedField {

        private final VariableElement mVariableElement;

        AnnotatedField(Element element) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                        SaveState.class.getSimpleName()));
            }
            this.mVariableElement = (VariableElement) element;
        }

        Name getFieldName() {
            return mVariableElement.getSimpleName();
        }

        TypeName getTypeName() {
            return TypeName.get(mVariableElement.asType());
        }

        String getKey() {
            return mVariableElement.getSimpleName().toString();
        }

        public SaveState getAnnotation() {
            return mVariableElement.getAnnotation(SaveState.class);
        }
    }
}
