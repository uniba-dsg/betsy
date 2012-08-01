package betsy.executables.reporting.csv

class CsvReportToDatawarehouse {

    CsvReport report
    int counter = 0

    void toHtmlReport(String filename) {

        new File(filename).withPrintWriter { writer ->
            writeHeader(writer)
            writeGroupTables(writer)
            writeFooter(writer)
        }


    }

    private void writeGroupTables(writer) {
        report.nameToGroup.each { groupName, group ->
            writeGroupTable(group, groupName, writer)
        }
    }

    private void writeGroupTable(Group group, groupName, PrintWriter writer) {
        writer.println("<table>")
        writer.println("<tr>")
        writer.println("<td></td><td></td><td></td><td class='result'>" + report.nameToEngine.keySet().join("</td><td class='result'>") + "</td>")
        writer.println("</tr>")

        boolean first = true
        group.tests.each { test ->
            writer.println("<tr>")
            if (first) {
                writer.println("<td class='group'>${group.name}</td>")
                first = false
            } else {
                writer.println("<td></td>")
            }
            writer.println("<td></td>")

            writer.println("<td class='${test.support}'>${test.fullName}</td>")
            writeResults(test, groupName, writer)
            counter++

            writer.println("</tr>")
        }
        writer.println("</table>")
    }

    private void writeResults(Test test, groupName, PrintWriter writer) {
        test.engineToResult.each {engine, result ->
            writer.println("<td class='${result.partial} result'>")

            String path = new FileNameFinder().getFileNames("test/reports/html/soapui/${engine.name}/${groupName}", "*_${test.fullName}.html").first()
            String parentPath = new File(report.file).parentFile.absolutePath
            String relativePath = path.substring(parentPath.length() + 1)
            writer.println("<a href='${relativePath}'>")
            writer.println("${result.partial.toNormalizedSymbol()}")
            writer.println("</a>")
            writer.println("</td>")
        }
    }

    private void writeFooter(PrintWriter writer) {
        writer.println("</body>")
        writer.println("</html>")
    }

    private void writeHeader(PrintWriter writer) {
        writer.println("<html>")
        writer.println("<head></head>")
        writer.println("<title>Results</title>")
        writer.println("""<style type='text/css'>
.NONE {
   background-color: red;
}
.PARTIAL {
   background-color: yellow;
}
.TOTAL {
   background-color: green;
}
.result {
    text-align: center;
    width: 60px;
}
body {
    font-size: large;
}
table {
    width: 900px;
}
.group {
    width: 150px;
    font-weight: bold
}
</style>""")
        writer.println("<body>")
    }
}
