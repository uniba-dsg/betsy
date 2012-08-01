package betsy.executables.reporting

class XmlToCsv {

    File xml

    public void toCsv(File csv) {

        csv.withPrintWriter { w ->
            def testsuites = new XmlSlurper(false,false).parse(xml)
            testsuites.testsuite.each { testSuite ->
                String name = testSuite.@name.text()
                String pkg = testSuite.@package.text()
                String engine = pkg.replaceAll("soapui.","").tokenize(".").first()
                String group =  pkg.tokenize(".").last()
                String tests = testSuite.@tests.text()
                String totalFailures = testSuite.@failures.text()
                String failures = totalFailures == "0" ? "1" : "0"

                w.println([name,engine,group,failures,totalFailures,tests].join(";"))
            }
        }

    }

}
