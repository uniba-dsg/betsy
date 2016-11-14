package pebl.result.feature;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.result.Measurement;
import pebl.result.engine.Engine;
import pebl.result.tool.Tool;

public class FeatureResult {

    @XmlElement(required = true)
    private final Measurement measurement;

    @XmlAttribute(required = true)
    @XmlIDREF
    private final Engine engine;

    @XmlAttribute(required = true)
    @XmlIDREF
    private final Tool tool;

    public FeatureResult() {
        this(new Measurement(), new Engine(), new Tool());
    }

    public FeatureResult(Measurement measurement, Engine engine, Tool tool) {
        this.measurement = measurement;
        this.engine = engine;
        this.tool = tool;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public Engine getEngine() {
        return engine;
    }

    public Tool getTool() {
        return tool;
    }
}

