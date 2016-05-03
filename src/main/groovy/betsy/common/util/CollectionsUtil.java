package betsy.common.util;

import java.util.LinkedList;
import java.util.List;

public final class CollectionsUtil {

    private CollectionsUtil() {}

    public static <T> List<T> union(List<List<T>> collections) {
        if (collections.size() <= 0) {
            return new LinkedList<>();
        } else if (collections.size() == 1) {
            return collections.get(0);
        } else {
            List<T> result = new LinkedList<>();
            for (List<T> list : collections) {
                result.addAll(list);
            }
            return result;
        }
    }
}
