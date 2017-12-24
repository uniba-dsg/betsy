package pebl;

import java.util.Map;

public interface HasExtensions {

    Map<String, String> getExtensions();

    <T extends HasExtensions> T addExtension(String key, String value);

}
