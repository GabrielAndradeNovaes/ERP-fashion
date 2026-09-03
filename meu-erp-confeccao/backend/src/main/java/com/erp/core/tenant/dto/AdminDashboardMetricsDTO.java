package com.erp.core.tenant.dto;

public class AdminDashboardMetricsDTO {
    private long totalTenants;
    private long activeTenants;
    private long inactiveTenants;
    private long pendingTenants;
    private double estimatedMRR;

    public AdminDashboardMetricsDTO() {}

    public AdminDashboardMetricsDTO(long totalTenants, long activeTenants, long inactiveTenants, long pendingTenants, double estimatedMRR) {
        this.totalTenants = totalTenants;
        this.activeTenants = activeTenants;
        this.inactiveTenants = inactiveTenants;
        this.pendingTenants = pendingTenants;
        this.estimatedMRR = estimatedMRR;
    }

    public long getTotalTenants() { return totalTenants; }
    public void setTotalTenants(long totalTenants) { this.totalTenants = totalTenants; }

    public long getActiveTenants() { return activeTenants; }
    public void setActiveTenants(long activeTenants) { this.activeTenants = activeTenants; }

    public long getInactiveTenants() { return inactiveTenants; }
    public void setInactiveTenants(long inactiveTenants) { this.inactiveTenants = inactiveTenants; }

    public long getPendingTenants() { return pendingTenants; }
    public void setPendingTenants(long pendingTenants) { this.pendingTenants = pendingTenants; }

    public double getEstimatedMRR() { return estimatedMRR; }
    public void setEstimatedMRR(double estimatedMRR) { this.estimatedMRR = estimatedMRR; }
}
