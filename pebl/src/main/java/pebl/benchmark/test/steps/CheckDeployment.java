package pebl.benchmark.test.steps;

import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestStep;

@XmlRootElement
public class CheckDeployment extends TestStep {
    @Override
    public String toString() {
        return "DeployableCheckTestStep{" + getDescription() + "}";
    }

}
