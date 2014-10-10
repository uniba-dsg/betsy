package betsy.common.analytics.model;

public class Result {
    public Support getPartial() {
        if (failed == 0) {
            return Support.TOTAL;
        } else if (failed < total) {
            return Support.PARTIAL;
        } else {
            return Support.NONE;
        }

    }

    public boolean isSuccessful() {
        return failed == 0;
    }

    @Override
    public String toString() {
        return "Result{" + "failed=" + failed + ", total=" + total + "}";
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getDeployable() {
        return deployable;
    }

    public void setDeployable(Boolean deployable) {
        this.deployable = deployable;
    }

    private Integer failed;
    private Integer total;
    private Boolean deployable;
}
