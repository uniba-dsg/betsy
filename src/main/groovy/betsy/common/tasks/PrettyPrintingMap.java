package betsy.common.tasks;

import java.util.Iterator;
import java.util.Map;

/*
 * from http://stackoverflow.com/questions/10120273/pretty-print-a-map-in-java
 */
public class PrettyPrintingMap<K, V> {
    private Map<K, V> map;

    public PrettyPrintingMap(Map<K, V> map) {
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }
}
