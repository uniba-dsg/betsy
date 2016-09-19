package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import pebl.test.Test;
import pebl.tool.Tool;

@XmlRootElement
public class Tests {

    public List<Test> tests = new LinkedList<>();

    public Tests() {

    }

    public Tests(List<Test> tests) {
        this.tests.addAll(tests);
    }
}
