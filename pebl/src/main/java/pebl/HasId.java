package pebl;

import javax.xml.bind.annotation.XmlID;

public interface HasId {

    public static final String SEPARATOR = "__";

    String getId();

}
