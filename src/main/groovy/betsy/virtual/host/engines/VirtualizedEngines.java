package betsy.virtual.host.engines;

import java.util.LinkedList;
import java.util.List;

public class VirtualizedEngines {

    /**
     * Returns a list of all available virtualized engines.
     *
     * @return a list of all available virtualized engines
     */
    public static List<VirtualizedEngine> availableEngines() {
        LinkedList<VirtualizedEngine> engines = new LinkedList<>();
        engines.add(new VirtualizedOdeEngine());
        engines.add(new VirtualizedBpelgEngine());
        engines.add(new VirtualizedOpenEsbEngine());
        engines.add(new VirtualizedPetalsEsbEngine());
        engines.add(new VirtualizedOrchestraEngine());
        engines.add(new VirtualizedActiveBpelEngine());

        return engines;
    }

}
