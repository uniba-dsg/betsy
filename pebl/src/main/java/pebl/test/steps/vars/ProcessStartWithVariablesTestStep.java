package pebl.test.steps.vars;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
public class ProcessStartWithVariablesTestStep extends TestStep {

    @XmlElement
    private List<Variable> variables = new LinkedList<>();

    @XmlElement(required = true)
    private String process;

    public ProcessStartWithVariablesTestStep addVariable(Variable variable) {
        this.variables.add(variable);

        return this;
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
