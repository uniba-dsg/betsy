package betsy.bpmn.model;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class BPMNNamespaceContext implements NamespaceContext {

    public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @Override
    public String getNamespaceURI(String prefix) {
        if ("bpmn2".equals(prefix)) {
            return BPMN_NAMESPACE;
        }

        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
