package com.victor.utilities.utils.gson;

import java.lang.annotation.Annotation;
import java.util.Collection;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;


public class IgnoreStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }


    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        Collection<Annotation> annotations = fieldAttributes.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Ignore.class) {
                return true;
            }
        }
        return false;
    }

}
