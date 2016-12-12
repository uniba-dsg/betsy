package pebl.result.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.result.Measurement;
import pebl.result.engine.Engine;

public class AggregatedResult {

    @XmlElement(required = true, name="measurement")
    @XmlElementWrapper(name="measurements")
    private final List<Measurement> measurements;

    @XmlAttribute(required = true)
    @XmlIDREF
    private final Engine engine;

    public AggregatedResult() {
        this(Collections.emptyList(), new Engine());
    }

    public AggregatedResult(List<Measurement> measurements, Engine engine) {
        this.measurements = new LinkedList<>();
        this.measurements.addAll(measurements);
        this.engine = engine;
    }

    public List<Measurement> getMeasurement() {
        return measurements;
    }

    public Engine getEngine() {
        return engine;
    }

}

