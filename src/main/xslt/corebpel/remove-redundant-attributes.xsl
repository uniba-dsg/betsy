<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Remove the redundant attributes. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="*" mode="extensionActivity">
      <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'suppressJoinFailure')]" />
      <xsl:copy-of select="*" />
  </xsl:template>

  <xsl:template match="bpel:extensionActivity">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates mode="extensionActivity" />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:assign | bpel:compensate | bpel:compensateScope | bpel:empty | bpel:exit | bpel:forEach | bpel:if | bpel:invoke | bpel:pick | bpel:receive | bpel:repeatUntil | bpel:reply | bpel:rethrow | bpel:scope | bpel:sequence | bpel:throw | bpel:validate | bpel:wait | bpel:while">
    <xsl:copy>
      <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                  local-name() = 'suppressJoinFailure')]" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:process">
    <bpel:process>
      <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                  (local-name() = 'queryLanguage' or
                                   local-name() = 'expressionLanguage' or
                                   local-name() = 'suppressJoinFailure' or
                                   local-name() = 'exitOnStandardFault'))]" />
      <xsl:apply-templates />
    </bpel:process>
  </xsl:template>
  
</xsl:stylesheet>