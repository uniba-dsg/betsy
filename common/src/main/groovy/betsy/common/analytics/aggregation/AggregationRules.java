package betsy.common.analytics.aggregation;

import static java.util.Objects.requireNonNull;

public class AggregationRules {

    public static AggregationRule<Boolean, TrivalentResult> GO_BIG_OR_GO_HOME = values -> {
        if(requireNonNull(values).stream().allMatch(i -> i)) {
            return TrivalentResult.PLUS;
        } else if(requireNonNull(values).stream().allMatch(i -> !i)) {
            return TrivalentResult.MINUS;
        } else {
            return TrivalentResult.PLUS_MINUS;
        }
    };

    public static AggregationRule<TrivalentResult, TrivalentResult> EXTREMA = values -> {
        if(requireNonNull(values).stream().allMatch(i -> i == TrivalentResult.PLUS)) {
            return TrivalentResult.PLUS;
        } else if (requireNonNull(values).stream().allMatch(i -> i == TrivalentResult.MINUS)) {
            return TrivalentResult.MINUS;
        } else {
            return TrivalentResult.PLUS_MINUS;
        }
    };


    /**
     * By sorting the first one is always the best one.
     *
     * UNUSED
     */
    public static AggregationRule<TrivalentResult, TrivalentResult> BEST = values -> requireNonNull(values).stream().sorted().findFirst().orElse(TrivalentResult.MINUS);


}
