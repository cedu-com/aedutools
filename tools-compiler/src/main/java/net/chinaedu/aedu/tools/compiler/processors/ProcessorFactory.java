package net.chinaedu.aedu.tools.compiler.processors;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * @author MartinKent
 * @time 2018/1/23
 */
public class ProcessorFactory {
    private static List<BaseProcessor> processors = Arrays.asList(
            new ViewBinderProcessor(),
            new StateSaveProcessor(),
            new RouteAndIntentDataProcessor(),
            new JsonParserProcessor()
    );

    public static void init(ProcessingEnvironment processingEnv) {
        for (BaseProcessor processor : processors) {
            processor.init(processingEnv);
        }
    }

    public static boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (BaseProcessor processor : processors) {
            processor.process(annotations, roundEnv);
        }
        return true;
    }

    public static SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public static Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (BaseProcessor processor : processors) {
            types.addAll(processor.getSupportedAnnotationTypes());
        }
        return types;
    }
}
