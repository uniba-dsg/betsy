<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:activiti="http://activiti.org/bpmn">
    <xsl:output omit-xml-declaration="yes" indent="yes" method="xml" />
    <xsl:strip-space elements="*" />

    <xsl:include href="../camunda/camunda.xsl" />

    <xsl:template match="bpmn2:dataObject/xsi:string">
        <bpmn2:extensionElements>
            <activiti:value><xsl:copy-of select="text()"/></activiti:value>
        </bpmn2:extensionElements>
    </xsl:template>

    <xsl:template match="bpmn2:dataObject/xsi:long">
        <bpmn2:extensionElements>
            <activiti:value><xsl:copy-of select="text()"/></activiti:value>
        </bpmn2:extensionElements>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>