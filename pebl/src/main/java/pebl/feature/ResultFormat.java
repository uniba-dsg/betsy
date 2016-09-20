package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ResultFormat {

    @XmlElement(required = true)
    private final List<ResultFormatElement> elements;

    public ResultFormat() {
        this(Collections.emptyList());
    }

    public ResultFormat(List<ResultFormatElement> elements) {
        this.elements = new LinkedList<>(Objects.requireNonNull(elements));
    }

    public List<ResultFormatElement> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
