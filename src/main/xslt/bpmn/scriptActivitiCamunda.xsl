<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:template match="bpmn2:scriptTask">
        <bpmn2:scriptTask scriptFormat="groovy">
            <xsl:apply-templates select="@*|node()"/>
        </bpmn2:scriptTask>
    </xsl:template>

    <!-- for gateways in general -->
    <xsl:template match="bpmn2:conditionExpression">
        <bpmn2:conditionExpression>
            <xsl:apply-templates select="@*"/>
            ${<xsl:value-of select="." />}
        </bpmn2:conditionExpression>
    </xsl:template>

    <!-- for conditional event definitions -->
    <xsl:template match="bpmn2:script">
        <bpmn2:condition>
            <xsl:apply-templates select="@*"/>
            ${<xsl:value-of select="." />}
        </bpmn2:condition>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>