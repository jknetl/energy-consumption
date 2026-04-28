package com.github.jknetl.ec.data.model;

public interface TenantScopedEntity {

    Long getId();

    Tenant getTenant();
}
