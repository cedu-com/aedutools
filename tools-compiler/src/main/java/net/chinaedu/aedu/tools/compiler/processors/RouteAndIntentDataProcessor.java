package net.chinaedu.aedu.tools.compiler.processors;

import net.chinaedu.aedu.tools.annotations.Consts;
import net.chinaedu.aedu.tools.annotations.IntentData;
import net.chinaedu.aedu.tools.annotations.Route;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @author MartinKent
 * @time 2018/1/24
 */
public class RouteAndIntentDataProcessor extends BaseProcessor {
    private Map<String, AnnotatedClass> classMap = new TreeMap<>();
    private static Class<?>[] mSupportedAnnotationTypes = {
            IntentData.class,
            Route.class
    };

    @Override
    Class<?>[] getSupportedAnnotationClasses() {
        return mSupportedAnnotationTypes;
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        classMap.clear();
        parse(annotations, roundEnv);
        generateCode();
    }

    private void generateCode() {
        TypeSpec.Builder routesClassBuilder = TypeSpec.classBuilder(ClassName.bestGuess(Consts.ROUTES_CLASS_NAME).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        TypeName routesMapTypeName = ParameterizedTypeName.get(ClassName.bestGuess("java.util.HashMap"), ClassName.bestGuess("java.lang.String"), ClassName.bestGuess("java.lang.String"));
        FieldSpec.Builder routesMapFieldBuilder = FieldSpec.builder(routesMapTypeName, "routesMap", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
        routesMapFieldBuilder.initializer("new $T()", routesMapTypeName);
        routesClassBuilder.addField(routesMapFieldBuilder.build());
        CodeBlock.Builder initMapBlockBuilder = CodeBlock.builder();
        for (Map.Entry<String, AnnotatedClass> entry : classMap.entrySet()) {
            witeJavaFile(entry.getValue().generateJavaFile());
            JavaFile helper = entry.getValue().generateJavaHelperFile();
            if (null != helper) {
                witeJavaFile(helper);
            }

            TypeElement element = entry.getValue().mTypeElement;
            Route route = element.getAnnotation(Route.class);
            String path;
            if (null == route || 0 == route.value().length) {
                path = TypeName.get(element.asType()).toString();
            } else {
                path = route.value()[0];
            }
            String fieldName = Utils.makeConstName(path);
            if (null == fieldName) {
                throw new RuntimeException("Routes field name make failed:" + path);
            }
            FieldSpec.Builder constBuilder = FieldSpec.builder(ClassName.get(String.class), fieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            constBuilder.initializer("$S", path);
            routesClassBuilder.addField(constBuilder.build());
            initMapBlockBuilder.addStatement("routesMap.put($L, $S)", fieldName, TypeName.get(element.asType()));
        }
        routesClassBuilder.addStaticBlock(initMapBlockBuilder.build());

        MethodSpec.Builder queryClass = MethodSpec.methodBuilder(Consts.ROUTES_CLASS_QUERY_ACTIVITY_CLASS)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess("java.lang.String"), "path", Modifier.FINAL)
                .returns(ClassName.bestGuess("java.lang.String"))
                .addStatement("return routesMap.get(path)");
        routesClassBuilder.addMethod(queryClass.build());

        JavaFile file = JavaFile.builder(ClassName.bestGuess(Consts.ROUTES_CLASS_NAME).packageName(), routesClassBuilder.build()).build();
        witeJavaFile(file);
    }

    private void parse(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Route.class)) {
            getAnnotatedClass((TypeElement) element);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(IntentData.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass((TypeElement) element.getEnclosingElement());
            annotatedClass.addField(new AnnotatedField((VariableElement) element));
        }
    }

    private AnnotatedClass getAnnotatedClass(TypeElement element) {
        String key = element.toString();
        AnnotatedClass annotatedClass = classMap.get(key);
        if (null == annotatedClass) {
            annotatedClass = new AnnotatedClass(element, getElementUtils());
            classMap.put(key, annotatedClass);
        }
        return annotatedClass;
    }

    private static class AnnotatedClass {
        private static final Map<String, String[]> intentGetMethodMap = new HashMap<>();

        static {
            intentGetMethodMap.put("byte[]", new String[]{"putExtra", "getByteArrayExtra", ""});
            intentGetMethodMap.put("char[]", new String[]{"putExtra", "getCharArrayExtra", ""});
            intentGetMethodMap.put("double[]", new String[]{"putExtra", "getDoubleArrayExtra", ""});
            intentGetMethodMap.put("float[]", new String[]{"putExtra", "getFloatArrayExtra", ""});
            intentGetMethodMap.put("int[]", new String[]{"putExtra", "getIntArrayExtra", ""});
            intentGetMethodMap.put("long[]", new String[]{"putExtra", "getLongArrayExtra", ""});
            intentGetMethodMap.put("android.os.Parcelable[]", new String[]{"putExtra", "getParcelableArrayExtra", ""});
            intentGetMethodMap.put("java.lang.CharSequence[]", new String[]{"putExtra", "getCharSequenceArrayExtra", ""});
            intentGetMethodMap.put("java.lang.String[]", new String[]{"putExtra", "getStringArrayExtra", ""});
            intentGetMethodMap.put("short[]", new String[]{"putExtra", "getShortArrayExtra", ""});
            intentGetMethodMap.put("boolean[]", new String[]{"putExtra", "getBooleanArrayExtra", ""});
            intentGetMethodMap.put("android.os.Parcelable", new String[]{"putExtra", "getParcelableExtra", ""});
            intentGetMethodMap.put("boolean", new String[]{"putExtra", "getBooleanExtra", "2"});
            intentGetMethodMap.put("byte", new String[]{"putExtra", "getByteExtra", "2"});
            intentGetMethodMap.put("char", new String[]{"putExtra", "getCharExtra", "2"});
            intentGetMethodMap.put("double", new String[]{"putExtra", "getDoubleExtra", "2"});
            intentGetMethodMap.put("float", new String[]{"putExtra", "getFloatExtra", "2"});
            intentGetMethodMap.put("int", new String[]{"putExtra", "getIntExtra", "2"});
            intentGetMethodMap.put("java.io.Serializable", new String[]{"putExtra", "getSerializableExtra", ""});
            intentGetMethodMap.put("java.lang.CharSequence", new String[]{"putExtra", "getCharSequenceExtra", ""});
            intentGetMethodMap.put("java.lang.String", new String[]{"putExtra", "getStringExtra", ""});
            intentGetMethodMap.put("long", new String[]{"putExtra", "getLongExtra", "2"});
            intentGetMethodMap.put("short", new String[]{"putExtra", "getShortExtra", "2"});
            intentGetMethodMap.put("android.os.Bundle", new String[]{"putExtra", "getBundleExtra", ""});
            intentGetMethodMap.put("java.util.ArrayList<java.lang.Integer>", new String[]{"putIntegerArrayListExtra", "getIntegerArrayListExtra", ""});
            intentGetMethodMap.put("java.util.ArrayList<? extends android.os.Parcelable>", new String[]{"putParcelableArrayListExtra", "getParcelableArrayListExtra", ""});
            intentGetMethodMap.put("java.util.ArrayList<java.lang.String>", new String[]{"putStringArrayListExtra", "getStringArrayListExtra", ""});
            intentGetMethodMap.put("java.util.ArrayList<java.lang.CharSequence>", new String[]{"putCharSequenceArrayListExtra", "getCharSequenceArrayListExtra", ""});
        }

        private final TypeElement mTypeElement;
        private final Elements mElements;
        private List<AnnotatedField> fields = new ArrayList<>();

        static final ClassName INTENT_BINDER_CLASS = ClassName.bestGuess(Consts.INTENT_BINDER_CLASS_NAME);
        static final ClassName ROUTER_CLASS = ClassName.bestGuess(Consts.ROUTER_CLASS_NAME);
        static final ClassName ROUTER_BUILDER_CLASS = ClassName.bestGuess(Consts.ROUTER_BUILDER_CLASS_NAME);

        public AnnotatedClass(TypeElement typeElement, Elements elements) {
            this.mTypeElement = typeElement;
            this.mElements = elements;
        }

        void addField(AnnotatedField field) {
            fields.add(field);
        }

        JavaFile generateJavaFile() {

            MethodSpec.Builder bindMethod = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess("android.content.Intent"), "intent", Modifier.FINAL)
                    .addParameter(TypeName.get(mTypeElement.asType()), "target", Modifier.FINAL);
            for (AnnotatedField field : fields) {
                IntentData data = field.getAnnotation();
                if (null == data) {
                    continue;
                }
                String key = "".equals(data.value()) ? field.getName().toString() : data.value();
                String[] pair = intentGetMethodMap.get(field.getTypeName().toString());
                if (null == pair) {
                    throw new RuntimeException("Type not supported[" + field.getTypeName().toString() + "]");
                }
                if ("".equals(pair[2])) {
                    bindMethod.addStatement("target.$N = intent.$L($S)", field.getName(), pair[1], key);
                } else {
                    bindMethod.addStatement("target.$N = intent.$L($S, target.$N)", field.getName(), pair[1], key, field.getName());
                }
            }

            TypeName typeName = ParameterizedTypeName.get(INTENT_BINDER_CLASS, TypeName.get(mTypeElement.asType()));
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(mTypeElement.getSimpleName().toString() + Consts.INTENT_BINDER_SUFFIX)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(typeName)
                    .addMethod(bindMethod.build());

            return JavaFile.builder(mTypeElement.getEnclosingElement().toString(), typeBuilder.build()).build();
        }

        public JavaFile generateJavaHelperFile() {
            String helperName = mTypeElement.getSimpleName().toString() + Consts.ROUTER_HELPER_SUFFIX;

            FieldSpec.Builder buiderField = FieldSpec.builder(ROUTER_BUILDER_CLASS, "mBuilder", Modifier.PRIVATE)
                    .initializer(CodeBlock.of("$T.builder()", ROUTER_CLASS));

            TypeSpec.Builder helperBuilderType = TypeSpec.classBuilder("Builder")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .addField(buiderField.build());

            MethodSpec.Builder helperBuilderConstructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE);
            helperBuilderType.addMethod(helperBuilderConstructor.build());

            MethodSpec.Builder helperBuilderMethod = MethodSpec.methodBuilder("builder")
                    .returns(ClassName.bestGuess(helperName + ".Builder"))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addStatement("return new $T()", ClassName.bestGuess(helperName + ".Builder"));

            for (AnnotatedField field : fields) {
                IntentData data = field.getAnnotation();
                if (null == data) {
                    continue;
                }
                String fieldName = field.getName().toString();
                fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                String key = "".equals(data.value()) ? field.getName().toString() : data.value();
                String[] pair = intentGetMethodMap.get(field.getTypeName().toString());
                if (null == pair) {
                    throw new RuntimeException("Type not supported[" + field.getTypeName().toString() + "]");
                }
                MethodSpec.Builder putMethod = MethodSpec.methodBuilder("with" + fieldName)
                        .addParameter(field.getTypeName(), field.getName().toString(), Modifier.FINAL)
                        .addStatement("mBuilder.$L($S,$L)", pair[0], key, field.getName().toString())
                        .returns(ClassName.bestGuess(helperName + ".Builder"))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this");
                helperBuilderType.addMethod(putMethod.build());
            }
            helperBuilderType.addMethod(MethodSpec.methodBuilder("addFlags").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess(helperName + ".Builder")).addParameter(TypeName.INT, "flags", Modifier.FINAL).addStatement("mBuilder.addFlags(flags)").addStatement("return this").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("setFlags").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess(helperName + ".Builder")).addParameter(TypeName.INT, "flags", Modifier.FINAL).addStatement("mBuilder.setFlags(flags)").addStatement("return this").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("setData").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess(helperName + ".Builder")).addParameter(ClassName.bestGuess("android.net.Uri"), "uri", Modifier.FINAL).addStatement("mBuilder.setData(uri)").addStatement("return this").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("setType").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess(helperName + ".Builder")).addParameter(ClassName.bestGuess("java.lang.String"), "type", Modifier.FINAL).addStatement("mBuilder.setType(type)").addStatement("return this").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("setDataAndType").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess(helperName + ".Builder")).addParameter(ClassName.bestGuess("android.net.Uri"), "uri", Modifier.FINAL).addParameter(ClassName.bestGuess("java.lang.String"), "type", Modifier.FINAL).addStatement("mBuilder.setDataAndType(uri, type)").addStatement("return this").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("getRouteBuilder").addModifiers(Modifier.PUBLIC).returns(ROUTER_BUILDER_CLASS).addStatement("return mBuilder").build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("getIntent").addModifiers(Modifier.PUBLIC).returns(ClassName.bestGuess("android.content.Intent")).addStatement("return mBuilder.getIntent()").build());
            Route route = mTypeElement.getAnnotation(Route.class);
            String path;
            if (null == route || 0 == route.value().length) {
                path = TypeName.get(mTypeElement.asType()).toString();
            } else {
                path = route.value()[0];
            }
            String constName = Utils.makeConstName(path);
            helperBuilderType.addMethod(MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC).addParameter(ClassName.bestGuess("android.content.Context"), "context").addStatement("mBuilder.start(context, $L.$L)", Consts.ROUTES_CLASS_SIMPLE_NAME, constName).build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC).addParameter(ClassName.bestGuess("android.app.Activity"), "context").addParameter(ClassName.INT, "requestCode").addStatement("mBuilder.start(context, $L.$L, requestCode)", Consts.ROUTES_CLASS_SIMPLE_NAME, constName).build());
            helperBuilderType.addMethod(MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC).addParameter(ClassName.bestGuess("android.app.Activity"), "context").addParameter(ClassName.INT, "requestCode").addParameter(ClassName.bestGuess("android.os.Bundle"), "options").addStatement("mBuilder.start(context, $L.$L, requestCode, options)", Consts.ROUTES_CLASS_SIMPLE_NAME, constName).addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("android.support.annotation.RequiresApi")).addMember("api", "$T.VERSION_CODES.JELLY_BEAN", ClassName.bestGuess("android.os.Build")).build()).build());
            TypeSpec.Builder helperType = TypeSpec.classBuilder(helperName)
                    .addModifiers(Modifier.PUBLIC)
                    .addType(helperBuilderType.build())
                    .addMethod(helperBuilderMethod.build());
            return JavaFile.builder(ROUTER_CLASS.packageName(), helperType.build()).build();
        }
    }

    private class AnnotatedField {

        private final VariableElement mVariableElement;

        AnnotatedField(VariableElement element) {
            this.mVariableElement = element;
        }

        TypeName getTypeName() {
            return TypeName.get(mVariableElement.asType());
        }

        Name getName() {
            return mVariableElement.getSimpleName();
        }

        public IntentData getAnnotation() {
            return mVariableElement.getAnnotation(IntentData.class);
        }

        @Override
        public String toString() {
            return mVariableElement.asType() + ":" + mVariableElement.toString();
        }
    }
}
