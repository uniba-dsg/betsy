package betsy.common.analytics.html;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import betsy.common.analytics.CsvReportLoader;
import betsy.common.analytics.model.CsvReport;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class HtmlAnalytics {
    private final CsvReport report;

    public HtmlAnalytics(CsvReport report) {
        this.report = report;
    }

    public static void main(String... args) {
        Path input = Paths.get(args[0]);
        Path output = input.getParent().resolve("myreport.html").toAbsolutePath();

        new HtmlAnalytics(new CsvReportLoader(input, new CsvReport()).load()).toHtmlReport(output);
    }

    public void toHtmlReport(Path filename) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine();

        URL templatePath = ClasspathHelper.getURLFromClasspathPath("/betsy/common/analytics/html/HtmlAnalytics.template");
        URL cssPath = ClasspathHelper.getURLFromClasspathPath("/betsy/common/analytics/html/bootstrap.min.css");

        try {
            Writable template = engine.createTemplate(templatePath).make(getTemplateBinding());
            FileTasks.createFile(filename, template.toString());
            FileTasks.copyFileIntoFolderAndOverwrite(cssPath, "bootstrap.min.css", filename.getParent());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("could not load template", e);
        }

    }

    private Map<String, Object> getTemplateBinding() {
        Map<String, Object> binding = new HashMap<>();
        binding.put("report", report);
        return binding;
    }

}
