package betsy.common.analytics.model;

import java.util.LinkedList;
import java.util.List;

import betsy.common.aggregation.AggregationRules;
import betsy.common.aggregation.TrivalentResult;

public class Result {

    private Integer failed;
    private Integer total;
    private Boolean deployable;

    public TrivalentResult getSupport() {
        return AggregationRules.GO_BIG_OR_GO_HOME.aggregate(getTestCaseResults());
    }

    private List<Boolean> getTestCaseResults() {
        List<Boolean> resultsPerTestCase = new LinkedList<>();
        for(int i = 0; i < total; i++) {
            if(i < failed) {
                resultsPerTestCase.add(false);
            } else {
                resultsPerTestCase.add(true);
            }
        }
        return resultsPerTestCase;
    }

    public boolean isSuccessful() {
        return getSupport().isTotal();
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

}
