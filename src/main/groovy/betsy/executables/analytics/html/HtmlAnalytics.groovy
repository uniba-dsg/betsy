package betsy.executables.analytics.html

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import betsy.tasks.FileTasks
import groovy.text.SimpleTemplateEngine

import java.nio.file.Path
import java.nio.file.Paths

class HtmlAnalytics {

    CsvReport report

    public static void main(String[] args) {
        Path input = Paths.get(args[0]);
        Path output = input.parent.resolve("myreport.html").toAbsolutePath()

        new HtmlAnalytics(report: new CsvReportLoader(csvFile: input).load()).toHtmlReport(output)
    }

    void toHtmlReport(Path filename) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Path templatePath = Paths.get(HtmlAnalytics.class.getResource("/betsy/executables/analytics/html/HtmlAnalytics.template").toURI())
        def template = engine.createTemplate(templatePath.toFile().text).make([
                "report": report
        ])

        FileTasks.createFile(filename, template.toString());
    }

}
