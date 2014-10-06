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
            String parentPath = getFile().toFile().getParentFile().getAbsolutePath();
            String relativePath = path.substring(parentPath.length() + 1);

            return relativePath;
        } catch (Exception e) {
            return "#";
        }
    }

}
