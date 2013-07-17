<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make the default join, transition, and completion conditions explicit. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />
  
  <xsl:include href="constants.xsl" />

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:source[not(bpel:transitionCondition)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <bpel:transitionCondition expressionLanguage="{$xpathURN}">true()</bpel:transitionCondition>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:targets[not(bpel:joinCondition)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <bpel:joinCondition expressionLanguage="{$xpathURN}">
        <xsl:for-each select="bpel:target">
          <xsl:value-of select="concat('$', @linkName)"/>
          <xsl:if test="following-sibling::bpel:target"> or </xsl:if>
        </xsl:for-each>        
      </bpel:joinCondition>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- Add empty completion condition where missing -->
  <xsl:template match="bpel:forEach[not(bpel:completionCondition)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
      <bpel:completionCondition />        
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>