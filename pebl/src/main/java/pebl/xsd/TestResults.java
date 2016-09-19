package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.result.TestResult;

@XmlRootElement
public class TestResults {

    @XmlElement(name = "testResult")
    public List<TestResult> testResults = new LinkedList<>();

}
