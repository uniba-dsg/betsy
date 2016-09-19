package pebl.test.steps.vars;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestStep;

public class ProcessStartWithVariablesTestStep extends TestStep {

    private List<Variable> variables = new LinkedList<>();
    private String process;

    public ProcessStartWithVariablesTestStep addVariable(Variable variable) {
        this.variables.add(variable);

        return this;
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    @XmlElement(required = true)
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
