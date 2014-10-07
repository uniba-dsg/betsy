package betsy.bpel.reporting;

import betsy.common.analytics.model.CsvReport;
import betsy.common.analytics.model.Engine;
import betsy.common.analytics.model.Group;
import betsy.common.analytics.model.Test;
import groovy.util.FileNameFinder;

public class BPELCsvReport extends CsvReport {

    @Override
    public String getRelativePath(Group group, Engine engine, Test test) {
        try {
            String basedir = "test/reports/html/soapui/" + engine.getName() + "/" + group.getName();
            String pattern = "*_" + test.getFullName() + ".html";
            String path = new FileNameFinder().getFileNames(basedir, pattern).get(0);
            String parentPath = getFile().getParent().toAbsolutePath().toString();

            return path.substring(parentPath.length() + 1);
        } catch (Exception e){
            return "#";
        }
    }
}
