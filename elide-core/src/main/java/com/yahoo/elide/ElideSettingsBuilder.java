/*
 * Copyright 2017, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide;

import com.yahoo.elide.audit.AuditLogger;
import com.yahoo.elide.audit.Slf4jLogger;
import com.yahoo.elide.core.DataStore;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.HttpStatus;
import com.yahoo.elide.core.RequestScope;
import com.yahoo.elide.core.exceptions.handlers.ErrorMapper;
import com.yahoo.elide.core.filter.dialect.DefaultFilterDialect;
import com.yahoo.elide.core.filter.dialect.JoinFilterDialect;
import com.yahoo.elide.core.filter.dialect.SubqueryFilterDialect;
import com.yahoo.elide.core.pagination.Pagination;
import com.yahoo.elide.jsonapi.JsonApiMapper;
import com.yahoo.elide.security.PermissionExecutor;
import com.yahoo.elide.security.executors.ActivePermissionExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Builder for ElideSettings.
 */
public class ElideSettingsBuilder {
    private final DataStore dataStore;
    private AuditLogger auditLogger;
    private JsonApiMapper jsonApiMapper;
    private EntityDictionary entityDictionary = new EntityDictionary(new HashMap<>());
    private Function<RequestScope, PermissionExecutor> permissionExecutorFunction = ActivePermissionExecutor::new;
    private List<JoinFilterDialect> joinFilterDialects;
    private List<SubqueryFilterDialect> subqueryFilterDialects;
    private int defaultMaxPageSize = Pagination.MAX_PAGE_LIMIT;
    private int defaultPageSize = Pagination.DEFAULT_PAGE_LIMIT;
    private boolean useFilterExpressions;
    private int updateStatusCode;
    private ErrorMapper errorMapper;

    /**
     * A new builder used to generate Elide instances. Instantiates an {@link EntityDictionary} without
     * providing a mapping of security checks and uses the provided {@link Slf4jLogger} for audit.
     *
     * @param dataStore the datastore used to communicate with the persistence layer
     */
    public ElideSettingsBuilder(DataStore dataStore) {
        this.dataStore = dataStore;
        this.auditLogger = new Slf4jLogger();
        this.jsonApiMapper = new JsonApiMapper(entityDictionary);
        this.joinFilterDialects = new ArrayList<>();
        this.subqueryFilterDialects = new ArrayList<>();
        updateStatusCode = HttpStatus.SC_NO_CONTENT;
    }

    public ElideSettings build() {
        if (joinFilterDialects.isEmpty()) {
            joinFilterDialects.add(new DefaultFilterDialect(entityDictionary));
        }

        if (subqueryFilterDialects.isEmpty()) {
            subqueryFilterDialects.add(new DefaultFilterDialect(entityDictionary));
        }

        return new ElideSettings(
                auditLogger,
                dataStore,
                entityDictionary,
                jsonApiMapper,
                permissionExecutorFunction,
                joinFilterDialects,
                subqueryFilterDialects,
                defaultMaxPageSize,
                defaultPageSize,
                useFilterExpressions,
                updateStatusCode,
                errorMapper);
    }

    public ElideSettingsBuilder withErrorMapper(ErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
        return this;
    }

    public ElideSettingsBuilder withAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
        return this;
    }

    public ElideSettingsBuilder withEntityDictionary(EntityDictionary entityDictionary) {
        this.entityDictionary = entityDictionary;
        return this;
    }

    public ElideSettingsBuilder withJsonApiMapper(JsonApiMapper jsonApiMapper) {
        this.jsonApiMapper = jsonApiMapper;
        return this;
    }

    public ElideSettingsBuilder withPermissionExecutor(
            Function<RequestScope, PermissionExecutor> permissionExecutorFunction) {
        this.permissionExecutorFunction = permissionExecutorFunction;
        return this;
    }

    public ElideSettingsBuilder withPermissionExecutor(Class<? extends PermissionExecutor> permissionExecutorClass) {
        permissionExecutorFunction = (requestScope) -> {
            try {
                try {
                    // Try to find a constructor with request scope
                    Constructor<? extends PermissionExecutor> ctor =
                            permissionExecutorClass.getDeclaredConstructor(RequestScope.class);
                    return ctor.newInstance(requestScope);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                        | InstantiationException e) {
                    // If that fails, try blank constructor
                    return permissionExecutorClass.newInstance();
                }
            } catch (IllegalAccessException | InstantiationException e) {
                // Everything failed. Throw hands up, not sure how to proceed.
                throw new RuntimeException(e);
            }
        };
        return this;
    }

    public ElideSettingsBuilder withJoinFilterDialect(JoinFilterDialect dialect) {
        joinFilterDialects.add(dialect);
        return this;
    }

    public ElideSettingsBuilder withSubqueryFilterDialect(SubqueryFilterDialect dialect) {
        subqueryFilterDialects.add(dialect);
        return this;
    }

    public ElideSettingsBuilder withDefaultMaxPageSize(int maxPageSize) {
        defaultMaxPageSize = maxPageSize;
        return this;
    }

    public ElideSettingsBuilder withDefaultPageSize(int pageSize) {
        defaultPageSize = pageSize;
        return this;
    }

    public ElideSettingsBuilder withUpdate200Status() {
        updateStatusCode = HttpStatus.SC_OK;
        return this;
    }

    public ElideSettingsBuilder withUpdate204Status() {
        updateStatusCode = HttpStatus.SC_NO_CONTENT;
        return this;
    }

    public ElideSettingsBuilder withUseFilterExpressions(boolean useFilterExpressions) {
        this.useFilterExpressions = useFilterExpressions;
        return this;
    }
}
