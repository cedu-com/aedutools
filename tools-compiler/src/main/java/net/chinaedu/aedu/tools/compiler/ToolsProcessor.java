package net.chinaedu.aedu.tools.compiler;

import com.google.auto.service.AutoService;

import net.chinaedu.aedu.tools.compiler.processors.ProcessorFactory;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class ToolsProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        for (Map.Entry<String, String> entry : processingEnv.getOptions().entrySet()) {
            System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
        }
        ProcessorFactory.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return ProcessorFactory.process(annotations, roundEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return ProcessorFactory.getSupportedSourceVersion();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ProcessorFactory.getSupportedAnnotationTypes();
    }
}
