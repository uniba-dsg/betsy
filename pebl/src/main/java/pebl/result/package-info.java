@XmlJavaTypeAdapters(
        @XmlJavaTypeAdapter(value = PathAdapter.class, type = Path.class)
)
package pebl.result;

import java.nio.file.Path;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import pebl.xsd.PathAdapter;
