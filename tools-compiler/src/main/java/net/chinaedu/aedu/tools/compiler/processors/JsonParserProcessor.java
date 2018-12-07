package net.chinaedu.aedu.tools.compiler.processors;

import net.chinaedu.aedu.tools.annotations.SaveState;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * @author MartinKent
 * @time 2018/4/2
 */
public class JsonParserProcessor extends BaseProcessor {
    private static Class<?>[] mSupportedAnnotationTypes = {
            SaveState.class,
    };

    @Override
    Class<?>[] getSupportedAnnotationClasses() {
        return mSupportedAnnotationTypes;
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    }
}