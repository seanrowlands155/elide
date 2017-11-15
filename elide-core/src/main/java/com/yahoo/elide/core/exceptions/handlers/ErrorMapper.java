package com.yahoo.elide.core.exceptions.handlers;

import com.yahoo.elide.core.exceptions.HttpStatusException;

import java.util.List;

public interface BaseExceptionMapper {

    List<? extends HttpStatusException> exceptionList = null;

    String handleException(final HttpStatusException ex);

    BaseExceptionMapper(){};

}
