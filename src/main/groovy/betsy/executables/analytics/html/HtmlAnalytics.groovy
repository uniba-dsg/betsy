package betsy.executables.analytics.html

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import groovy.text.SimpleTemplateEngine

class HtmlAnalytics {

    CsvReport report

    public static void main(String[] args) {
        String input = args[0];
        String output = new File(new File(input).parentFile, "myreport.html").getAbsolutePath()

        new HtmlAnalytics(report: new CsvReportLoader(csvFile: input).load()).toHtmlReport(output)
    }

    void toHtmlReport(String filename) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        File file = new File("src" + File.separator + "main" + File.separator + "groovy" + File.separator + "betsy" + File.separator + "executables" + File.separator + "analytics" + File.separator + "html" + File.separator + "HtmlAnalytics.template")
        def template = engine.createTemplate(file.text).make([
                "report": report
        ])

        new File(filename).withWriter("UTF-8") { it.write(template.toString()) }
    }

}
