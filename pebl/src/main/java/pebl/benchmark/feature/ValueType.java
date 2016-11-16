package pebl.benchmark.feature;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Idea: only primitive types, aggregated as a special type (could be removed as well)
 */
@XmlEnum
public enum ValueType {
    BOOLEAN, LONG, DOUBLE, STRING;
}
