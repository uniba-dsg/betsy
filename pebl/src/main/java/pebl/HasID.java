package pebl;

import javax.xml.bind.annotation.XmlID;

public interface HasID {

    public static final String SEPARATOR = "__";

    @XmlID
    String getID();

}
