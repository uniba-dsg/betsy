package betsy.common.analytics.html;

import betsy.common.analytics.CsvReportLoader;
import betsy.common.analytics.model.CsvReport;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HtmlAnalytics {
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

        Path templatePath = ClasspathHelper.getFilesystemPathFromClasspathPath("/betsy/common/analytics/html/HtmlAnalytics.template");
        Path cssPath = ClasspathHelper.getFilesystemPathFromClasspathPath("/betsy/common/analytics/html/bootstrap.min.css");

        try {
            Writable template = engine.createTemplate(templatePath.toFile()).make(getTemplateBinding());
            FileTasks.createFile(filename, template.toString());
            FileTasks.copyFileIntoFolderAndOverwrite(cssPath, filename.getParent());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("could not load template", e);
        }

    }

    private Map<String, Object> getTemplateBinding() {
        Map<String, Object> binding = new HashMap<>();
        binding.put("report", report);
        return binding;
    }

    private final CsvReport report;
}
