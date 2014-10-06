package betsy.common.analytics.html

import betsy.common.analytics.CsvReportLoader
import betsy.common.analytics.model.CsvReport
import betsy.common.tasks.FileTasks
import groovy.text.SimpleTemplateEngine

import java.nio.file.Path
import java.nio.file.Paths

class HtmlAnalytics {

    CsvReport report

    public static void main(String[] args) {
        Path input = Paths.get(args[0]);
        Path output = input.parent.resolve("myreport.html").toAbsolutePath()

        new HtmlAnalytics(report: new CsvReportLoader(input).load()).toHtmlReport(output)
    }

    void toHtmlReport(Path filename) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Path templatePath = Paths.get(HtmlAnalytics.class.getResource("HtmlAnalytics.template").toURI())
        def template = engine.createTemplate(templatePath.toFile().text).make([
                "report": report
        ])

        FileTasks.createFile(filename, template.toString());
    }

}
