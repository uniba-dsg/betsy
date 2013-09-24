<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Transform <repeatUntil> into <while>. -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema">

    <xsl:output indent="yes" method="xml"/>

    <xsl:include href="constants.xsl"/>
    <xsl:include href="fresh-names.xsl"/>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="bpel:repeatUntil">
        <xsl:variable name="conditionVariable">
            <xsl:call-template name="unique-element-name">
                <xsl:with-param name="postfix" select="$tmp-condition-variable-postfix"/>
            </xsl:call-template>
        </xsl:variable>

        <bpel:scope>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="bpel:targets | bpel:sources"/>

            <bpel:variables>
                <bpel:variable type="xsd:boolean">
                    <xsl:attribute name="name">
                        <xsl:value-of select="$conditionVariable"/>
                    </xsl:attribute>
                    <bpel:from expressionLanguage="{$xpathURN}">true()</bpel:from>
                </bpel:variable>
            </bpel:variables>

            <bpel:sequence>
                <bpel:assign>
                    <bpel:copy>
                        <bpel:from expressionLanguage="{$xpathURN}">true()</bpel:from>
                        <bpel:to variable="{$conditionVariable}"/>
                    </bpel:copy>
                </bpel:assign>

                <bpel:while>
                    <bpel:condition expressionLanguage="{$xpathURN}">$<xsl:value-of select="$conditionVariable"/>
                    </bpel:condition>
                    <bpel:sequence>
                        <xsl:apply-templates
                                select="*[not(self::bpel:targets or self::bpel:sources or self::bpel:condition)]"/>
                        <bpel:if>
                            <xsl:apply-templates select="bpel:condition"/>
                            <bpel:assign>
                                <bpel:copy>
                                    <bpel:from expressionLanguage="{$xpathURN}">false()</bpel:from>
                                    <bpel:to variable="{$conditionVariable}"/>
                                </bpel:copy>
                            </bpel:assign>
                        </bpel:if>
                    </bpel:sequence>
                </bpel:while>

            </bpel:sequence>
        </bpel:scope>
    </xsl:template>

</xsl:stylesheet>