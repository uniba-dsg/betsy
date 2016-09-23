package pebl.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class ResultFormatMetric {

    public static final ResultFormatMetric BOOLEAN = new ResultFormatMetric("BOOLEAN");
    public static final ResultFormatMetric INTEGER = new ResultFormatMetric("INTEGER");
    public static final ResultFormatMetric TRIVALENT = new ResultFormatMetric("TRIVALENT");
    public static final ResultFormatMetric AGGREGATED = new ResultFormatMetric("AGGREGATED");

    @XmlElement(required = true)
    private final String type;

    ResultFormatMetric() {
        this("");
    }

    public ResultFormatMetric(String type) {
        this.type = Objects.requireNonNull(type);
    }

    public String getType() {
        return type;
    }


}
