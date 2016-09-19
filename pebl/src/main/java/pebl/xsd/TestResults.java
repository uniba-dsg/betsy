package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import pebl.result.TestResult;
import pebl.test.Test;

@XmlRootElement
public class TestResults {

    public List<TestResult> testResults = new LinkedList<>();

}
