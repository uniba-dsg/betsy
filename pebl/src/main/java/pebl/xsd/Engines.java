package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.result.engine.Engine;

@XmlRootElement
public class Engines {

    @XmlElement(name="engine")
    public List<Engine> engines = new LinkedList<>();

    public Engines() {

    }

    public Engines(List<Engine> engines) {
        this.engines.addAll(engines);
    }
}
