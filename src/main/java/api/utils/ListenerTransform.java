package api.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ListenerTransform implements IAnnotationTransformer {
    public void transform(ITestAnnotation testAnnotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        Class retry = testAnnotation.getRetryAnalyzerClass();
        if(retry!= RetryListener.class){
            testAnnotation.setRetryAnalyzer(RetryListener.class);
        }
    }
}
