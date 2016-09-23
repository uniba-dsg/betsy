package betsy.common.engines;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.xml.sax.SAXException;

public final class Util {

    private Util() {}

    public static String canonicalizeXML(String text) {
        Init.init();
        try {
            Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
            return new String(canon.canonicalize(text.getBytes()));
        } catch (InvalidCanonicalizerException | CanonicalizationException | ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalStateException("could not create a canonical version of the xml", e);
        }
    }
}
