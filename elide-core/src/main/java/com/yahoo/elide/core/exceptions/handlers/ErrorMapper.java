package com.yahoo.elide.core.exceptions.handlers;

import com.yahoo.elide.core.exceptions.HttpStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ErrorMapper {

    public Map<Class<? extends HttpStatusException>, Function<? super HttpStatusException, String>> errorMap = new HashMap<>();


    public ErrorMapper addErrorMapperEntry(Class<? extends HttpStatusException> classToMap, Function<? super HttpStatusException, String> handleError) {
        if (!errorMap.containsKey(classToMap)) {
            errorMap.put(classToMap, handleError);
        }
        else
        {
            throw new IllegalArgumentException("Mapping for exception already exists");
        }
        return this;
    }

    public Function<? super HttpStatusException, String> getHandleErrorFunctionForError(Class exception) {
        return errorMap.get(exception);
    }
}
