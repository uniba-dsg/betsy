package pebl;

import java.util.Map;

public interface HasExtension {

    Map<String, String> getExtension();

    <T extends HasExtension> T addExtension(String key, String value);

}
