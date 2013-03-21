package betsy.executables.analytics

import betsy.executables.analytics.model.CsvReport
import groovy.text.SimpleTemplateEngine

class LatexAnalytics {

    CsvReport report

    public static void main(String[] args) {
        new LatexAnalytics(report: new CsvReportLoader(csvFile: "test/reports/results.csv").load()).toLatexReport("test/reports/mytable.tex")
    }

    void toLatexReport(String filename) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(new File("src\\main\\groovy\\betsy\\executables\\analytics\\LatexAnalytics.template").text).make([
                "report": report
        ])

        new File(filename).write(template.toString())


    }

}
