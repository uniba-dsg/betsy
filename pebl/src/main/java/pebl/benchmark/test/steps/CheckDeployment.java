package pebl.benchmark.test.steps;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import pebl.benchmark.test.TestStep;

@XmlRootElement
@XmlAccessorOrder(value = XmlAccessOrder.ALPHABETICAL)
public class CheckDeployment extends TestStep {
    @Override
    public String toString() {
        return "DeployableCheckTestStep{" + getDescription() + "}";
    }

}
