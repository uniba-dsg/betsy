package pebl.xsd;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.test.Test;
import pebl.result.engine.Engine;
import pebl.result.feature.FeatureResult;
import pebl.result.test.TestResult;
import pebl.result.tool.Tool;

@XmlRootElement
public class PEBL {

    public void toJson(Path path) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        Marshaller m4json = jc.createMarshaller();
        m4json.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m4json.setProperty("eclipselink.media-type", "application/json");
        m4json.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        m4json.marshal(this, path.toFile());
    }

    public void toXml(Path path) throws IOException {
        JAXB.marshal(this, path.toFile());
    }

    public void writeTo(Path workingDirectory) {
        try {
            toJson(workingDirectory.resolve("pebl.json"));
            toXml(workingDirectory.resolve("pebl.xml"));
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    public static class Benchmark {

        @XmlElement(name = "capability")
        @XmlElementWrapper(name = "capabilities")
        public List<Capability> capabilities = new LinkedList<>();

        @XmlElement(name = "test")
        @XmlElementWrapper(name = "tests")
        public List<Test> tests = new LinkedList<>();

        @XmlElement(name = "metricType")
        @XmlElementWrapper(name = "metricTypes")
        public List<MetricType> metricTypes = new LinkedList<>();

    }

    public static class Result {

        @XmlElement(name = "engine")
        @XmlElementWrapper(name = "engines")
        public List<Engine> engines = new LinkedList<>();

        @XmlElement(name = "tool")
        @XmlElementWrapper(name = "tools")
        public List<Tool> tools = new LinkedList<>();

        @XmlElement(name = "testResult")
        @XmlElementWrapper(name = "testResults")
        public List<TestResult> testResults = new LinkedList<>();

        @XmlElement(name = "featureResult")
        @XmlElementWrapper(name = "featureResults")
        public List<FeatureResult> featureResults = new LinkedList<>();

    }

    @XmlElement(required = true)
    public Benchmark benchmark = new Benchmark();

    @XmlElement(required = true)
    public Result result = new Result();

}
