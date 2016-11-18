package pebl.benchmark.test.steps.vars;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import pebl.benchmark.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class StartProcess extends TestStep {

    @XmlElement(name="variable")
    @XmlElementWrapper(name="variables")
    private List<Variable> variables = new LinkedList<>();

    @XmlElement(required = true)
    private String processName;

    public StartProcess addVariable(Variable variable) {
        this.variables.add(variable);

        return this;
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
