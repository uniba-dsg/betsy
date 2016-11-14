package pebl.benchmark.test;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.MapAdapter;
import pebl.benchmark.test.assertions.AssertDeployed;
import pebl.benchmark.test.assertions.AssertExit;
import pebl.benchmark.test.assertions.AssertNotDeployed;
import pebl.benchmark.test.assertions.AssertScript;
import pebl.benchmark.test.assertions.AssertSoapFault;
import pebl.benchmark.test.assertions.AssertTrace;
import pebl.benchmark.test.assertions.AssertXpath;

@XmlSeeAlso({AssertExit.class, AssertSoapFault.class, AssertTrace.class, AssertXpath.class, AssertNotDeployed.class, AssertDeployed.class, AssertScript.class})
public class TestAssertion implements HasExtensions {

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public TestAssertion addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }
}
