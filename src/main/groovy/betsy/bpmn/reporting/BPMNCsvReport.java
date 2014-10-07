package betsy.bpmn.reporting;

import betsy.common.analytics.model.CsvReport;
import betsy.common.analytics.model.Engine;
import betsy.common.analytics.model.Group;
import betsy.common.analytics.model.Test;
import groovy.util.FileNameFinder;

public class BPMNCsvReport extends CsvReport {

    @Override
    public String getRelativePath(final Group group, final Engine engine, final Test test) {
        try {
            String path = new FileNameFinder().getFileNames("test/reports/html/" + engine.getName() + "/" + group.getName(), "*_" + test.getFullName() + ".html").get(0);
            String parentPath = getFile().getParent().toAbsolutePath().toString();

            return path.substring(parentPath.length() + 1);
        } catch (Exception e) {
            return "#";
        }
    }

}
