package com.erp.core.tenant;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static final String MASTER_TENANT = "master";

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get() != null ? CURRENT_TENANT.get() : MASTER_TENANT;
    }

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
