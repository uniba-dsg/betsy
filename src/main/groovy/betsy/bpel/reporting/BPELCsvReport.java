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
            String path = new FileNameFinder().getFileNames("test/reports/html/soapui/${engine.name}/${group.name}", "*_${test.fullName}.html").get(0);
            String parentPath = getFile().getParent().toAbsolutePath().toString();

            return path.substring(parentPath.length() + 1);
        } catch (Exception e){
            return "#";
        }
    }
}
