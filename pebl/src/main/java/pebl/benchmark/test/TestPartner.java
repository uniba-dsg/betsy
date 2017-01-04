package pebl.benchmark.test;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.MapAdapter;
import pebl.benchmark.test.partner.RuleBasedWSDLTestPartner;
import pebl.benchmark.test.partner.ScriptBasedWSDLTestPartner;

@XmlSeeAlso({RuleBasedWSDLTestPartner.class, ScriptBasedWSDLTestPartner.class})
public class TestPartner implements HasExtensions {

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public TestPartner addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }

}
