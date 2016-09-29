<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL">
    <xsl:param name="processName"/>
    <xsl:template match="bpmn2:script">

        <xsl:choose>

            <xsl:when test="text() = 'CREATE_LOG_FILE'">
                <xsl:text disable-output-escaping="yes">
                &lt;bpmn2:script&gt;&lt;![CDATA[
try {
    java.io.File f = new java.io.File("log-</xsl:text><xsl:value-of select="$processName" /><xsl:text disable-output-escaping="yes">-" + testCaseNumber + ".txt");
    f.createNewFile();
} catch (java.io.IOException ignore) {}
]]&gt;&lt;/bpmn2:script&gt;
                </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'CREATE_MARKER_FILE'">
                <bpmn2:script><![CDATA[
try {
    java.io.File f = new java.io.File("MARKER");
    f.createNewFile();
} catch (java.io.IOException ignore) {}
]]></bpmn2:script>
            </xsl:when>

            <xsl:when test="text() = 'CREATE_TIMESTAMP_LOG_1'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
int taskWaitingDuration = 10000; // milliseconds to wait between begin and end

// create log file for task 1
java.io.File file = new java.io.File("log" + testCaseNumber + "_parallelOne.txt");

java.io.BufferedWriter bw = null;
try {
    file.createNewFile();
    bw = new java.io.BufferedWriter(new java.io.FileWriter(file, true));
    // log start time
    long current = System.currentTimeMillis();
    bw.append(String.valueOf(current));
    long future = current + taskWaitingDuration;
    bw.newLine();
    try {
        while (System.currentTimeMillis() &lt; future) {
            Thread.sleep(1000);
        }
    } catch (InterruptedException ignored) {}
    // log end time
    bw.append(String.valueOf(System.currentTimeMillis()));
    bw.newLine();
} catch(java.io.IOException ignored) {
} finally {
    if (bw != null) {
        try {
            bw.close();
        } catch(java.io.IOException ignored) {}
    }
}
]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'CREATE_TIMESTAMP_LOG_2'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
int taskWaitingDuration = 10000; // milliseconds to wait between begin and end

// create log file for task 2
java.io.File file = new java.io.File("log" + testCaseNumber + "_parallelTwo.txt");

java.io.BufferedWriter bw = null;
try {
    file.createNewFile();
    bw = new java.io.BufferedWriter(new java.io.FileWriter(file, true));
    // log start time
    long current = System.currentTimeMillis();
    bw.append(String.valueOf(current));
    long future = current + taskWaitingDuration;
    bw.newLine();
    try {
        while (System.currentTimeMillis() &lt; future) {
            Thread.sleep(1000);
        }
    } catch (InterruptedException ignored) {}
    // log end time
    bw.append(String.valueOf(System.currentTimeMillis()));
    bw.newLine();
} catch(java.io.IOException ignored) {
} finally {
    if (bw != null) {
        try {
            bw.close();
        } catch(java.io.IOException ignored) {}
    }
}
]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'WAIT_TEN_SECONDS'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
int taskWaitingDuration = 10000; // milliseconds to wait between begin and end

long current = System.currentTimeMillis();
long future = current + taskWaitingDuration;

try {
    while (System.currentTimeMillis() &lt; future) {
        Thread.sleep(1000);
    }
} catch (InterruptedException ignored) {}
]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'SET_STRING_DATA' or text() = 'LOG_DATA'"> <!-- keep SET_DATA and LOG_DATA tokens -->
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'INCREMENT_INTEGER_VARIABLE_AND_LOG'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'THROW_ERROR'">
                <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;</xsl:text>
                <xsl:value-of select="text()"/>
                <xsl:text disable-output-escaping="yes">&lt;/bpmn2:script&gt;</xsl:text>
            </xsl:when>

           <xsl:otherwise>
        <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[
java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("log-</xsl:text><xsl:value-of select="$processName" /><xsl:text disable-output-escaping="yes">-" + testCaseNumber + ".txt", true));
try{
    bw.append("</xsl:text> <xsl:copy-of select="text()"/> <xsl:text disable-output-escaping="yes">");
    bw.newLine();
} catch (java.io.IOException ignored){
} finally {
    if (bw != null) {
        try {
            bw.close();
        } catch (java.io.IOException ignored){}
    }
}
]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:otherwise>

        </xsl:choose>

    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>