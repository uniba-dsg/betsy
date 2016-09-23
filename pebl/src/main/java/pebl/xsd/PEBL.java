package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.engine.Engine;
import pebl.feature.Capability;
import pebl.feature.Feature;
import pebl.feature.FeatureSet;
import pebl.feature.Group;
import pebl.feature.Language;
import pebl.result.TestResult;
import pebl.test.Test;
import pebl.tool.Tool;

@XmlRootElement
public class PEBL {

    @XmlElement(name="engine")
    public List<Engine> engines = new LinkedList<>();

    @XmlElement(name = "tool")
    public List<Tool> tools = new LinkedList<>();

    @XmlElement(name="capability")
    public List<Capability> capabilities = new LinkedList<>();

    @XmlElement(name = "test")
    public List<Test> tests = new LinkedList<>();

    @XmlElement(name = "testResult")
    public List<TestResult> testResults = new LinkedList<>();

}
