package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.tool.Tool;

@XmlRootElement
public class Tools {

    @XmlElement(name = "tool")
    public List<Tool> tools = new LinkedList<>();

    public Tools() {
    }

    public Tools(List<Tool> tools) {
        this.tools.addAll(tools);
    }
}
