package com.erp.core.tenant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmpresaContext {

    private static final ThreadLocal<List<UUID>> CONTEXT = new ThreadLocal<>();

    public static void setEmpresas(List<UUID> empresas) {
        CONTEXT.set(empresas);
    }

    public static List<UUID> getEmpresas() {
        return CONTEXT.get() != null ? CONTEXT.get() : new ArrayList<>();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
