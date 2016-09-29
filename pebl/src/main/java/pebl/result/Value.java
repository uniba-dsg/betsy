package pebl.result;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.result.metrics.AggregatedValue;
import pebl.result.metrics.BooleanValue;
import pebl.result.metrics.LongValue;
import pebl.result.metrics.StringValue;

@XmlSeeAlso({BooleanValue.class, LongValue.class, StringValue.class, AggregatedValue.class})
public class Value {

}
