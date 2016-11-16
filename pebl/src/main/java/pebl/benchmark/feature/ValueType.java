package pebl.benchmark.feature;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Idea: only primitive types, aggregated as a special type (could be removed as well)
 */
@XmlEnum
public enum ValueType {
    @XmlEnumValue("boolean")
    BOOLEAN,
    @XmlEnumValue("long")
    LONG,
    @XmlEnumValue("double")
    DOUBLE,
    @XmlEnumValue("string")
    STRING;
}
