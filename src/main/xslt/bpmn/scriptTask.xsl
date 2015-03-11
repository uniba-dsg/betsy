<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL">

    <xsl:template match="bpmn2:script">

        <xsl:choose>

        <xsl:when test="text() = 'CREATE_LOG_FILE'">
            <bpmn2:script><![CDATA[try{
java.io.File f = new java.io.File("log" + testCaseNumber + ".txt");
f.createNewFile();
}catch(java.io.IOException e){}]]></bpmn2:script>
        </xsl:when>

        <xsl:when test="text() = 'CREATE_TIMESTAMP_LOG_1'">
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[// create log file for task 1
				try {
					java.io.File f = new java.io.File("log" + testCaseNumber + "_1.txt");
					f.createNewFile();
				} catch(java.io.IOException e) {
				}
				java.io.BufferedWriter bw = null;
				try {
					bw = new java.io.BufferedWriter(
							new java.io.FileWriter("log" + testCaseNumber + "_1.txt", true)
					);
					// log start time
                    long current = System.currentTimeMillis();
					bw.append("" + current);
                    long future = current + 10000;
                    bw.newLine();
					try {
                        while (System.currentTimeMillis() &lt; future) {
                            Thread.sleep(1000);
                        }
					} catch (InterruptedException e) {
					}
					// log end time
					bw.append("" + future);
					bw.newLine();
				} catch(java.io.IOException e) {
				} finally {
					if (bw != null) {
						try {
							bw.close();
						} catch(java.io.IOException e) {
						}
					}
				}]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
        </xsl:when>

            <xsl:when test="text() = 'CREATE_TIMESTAMP_LOG_2'">
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[// create log file for task 2
				try {
					java.io.File f = new java.io.File("log" + testCaseNumber + "_2.txt");
					f.createNewFile();
				} catch(java.io.IOException e) {
				}
				java.io.BufferedWriter bw = null;
				try {
					bw = new java.io.BufferedWriter(
							new java.io.FileWriter("log" + testCaseNumber + "_2.txt", true)
					);
					// log start time
                    long current = System.currentTimeMillis();
					bw.append("" + current);
                    long future = current + 10000;
                    bw.newLine();
					try {
                        while (System.currentTimeMillis() &lt; future) {
                            Thread.sleep(1000);
                        }
					} catch (InterruptedException e) {
					}
					// log end time
					bw.append("" + future);
					bw.newLine();
				} catch(java.io.IOException e) {
				} finally {
					if (bw != null) {
						try {
							bw.close();
						} catch(java.io.IOException e) {
						}
					}
				}]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:when test="text() = 'EVALUATE_TIMESTAMP_LOGS'">
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[//read intervals
                java.io.File f1 = new java.io.File("log" + testCaseNumber + "_1" + ".txt");
                java.io.File f2 = new java.io.File("log" + testCaseNumber + "_2 " + ".txt");
                long start1 = 0, start2 = 0, end1 = 0, end2 = 0;

                try {
                   List&lt;String&gt; listOne = java.nio.file.Files.readAllLines(f1.toPath(), java.nio.charset.StandardCharsets.ISO_8859_1);
                   List&lt;String&gt; listTwo = java.nio.file.Files.readAllLines(f2.toPath(), java.nio.charset.StandardCharsets.ISO_8859_1);
                   start1 = Long.parseLong(listOne.get(0));
                   end1 = Long.parseLong(listOne.get(1));
                   start2 = Long.parseLong(listTwo.get(0));
                   end2 = Long.parseLong(listTwo.get(1));
                } catch(java.io.IOException e) {
                }

                // compare intervals
                boolean wasParallel = false;
                if (start1 &lt;= start2 &amp;&amp; start2 &lt; end1) {
                   wasParallel = true;
                } else if (start1 &lt;= start2 &amp;&amp; start1 &lt; end2) {
                   wasParallel = true;
                }

                // write result of comparison
                java.io.BufferedWriter bw = null;
                try {
                   bw = new java.io.BufferedWriter(new java.io.FileWriter("log" + testCaseNumber + ".txt", true));
                   if (wasParallel) {
                      bw.append("SCRIPT_task1");
                   } else {
                      bw.append("SCRIPT_task2");
                   }
                   bw.newLine();
                } catch(java.io.IOException e) {
                } finally {
                   if (bw != null) {
                      try {
                         bw.close();
                      } catch(java.io.IOException e) {
                      }
                   }
                }]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
            </xsl:when>

            <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&lt;bpmn2:script&gt;&lt;![CDATA[java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("log" + testCaseNumber + ".txt", true));
try{
bw.append("</xsl:text><xsl:copy-of select="text()"/> <xsl:text disable-output-escaping="yes">");
bw.newLine();
}catch(java.io.IOException e){}finally{if(bw != null){try{bw.close();}catch(java.io.IOException e){}}}]]&gt;&lt;/bpmn2:script&gt; </xsl:text>
        </xsl:otherwise>

        </xsl:choose>

    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>