package net.chinaedu.aedu.tools.compiler.processors;

import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author MartinKent
 * @time 2018/1/23
 */
abstract class BaseProcessor {
    private Filer mFiler; //文件相关的辅助类
    private Elements mElementUtils; //元素相关的辅助类
    private Messager mMessager; //日志相关的辅助类

    private Map<String, String> args;

    synchronized void init(ProcessingEnvironment processingEnv) {
        args = processingEnv.getOptions();
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    public Map<String, String> getArguments() {
        return args;
    }

    Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new TreeSet<>();
        for (Class<?> annClass : getSupportedAnnotationClasses()) {
            types.add(annClass.getCanonicalName());
        }
        return types;
    }

    void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    public Elements getElementUtils() {
        return mElementUtils;
    }

    void witeJavaFile(JavaFile file) {
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    abstract Class<?>[] getSupportedAnnotationClasses();

    public abstract void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
