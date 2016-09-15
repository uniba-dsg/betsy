package pebl.featuretree;

import java.util.List;
import java.util.Objects;

public class ResultFormat {

    private final List<ResultFormatElement> elements;

    public ResultFormat(List<ResultFormatElement> elements) {
        this.elements = Objects.requireNonNull(elements);
    }

}
