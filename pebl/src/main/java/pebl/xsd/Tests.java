package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.test.Test;
import pebl.tool.Tool;

@XmlRootElement
public class Tests {

    @XmlElement(name = "test")
    public List<Test> tests = new LinkedList<>();

    public Tests() {

    }

    public Tests(List<Test> tests) {
        this.tests.addAll(tests);
    }
}
