<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Transform <receive> into <pick>. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <!-- Copy all elements and attributes -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- Unfold receive -->
  <xsl:template match="bpel:receive">
    <bpel:pick>
      <xsl:copy-of select="@createInstance" />
      <xsl:copy-of select="@name" />
      <xsl:copy-of select="@suppressJoinFailure" />      
      <xsl:apply-templates select="bpel:targets" />
      <xsl:apply-templates select="bpel:sources" />
      <bpel:onMessage>
        <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                    (local-name() = 'createInstance' or
                                     local-name() = 'name' or
                                     local-name() = 'suppressJoinFailure'))]" />
        <xsl:apply-templates select="*[not(self::bpel:targets or self::bpel:sources)]" />
        <bpel:empty/>      
      </bpel:onMessage>
    </bpel:pick>
  </xsl:template>
  
</xsl:stylesheet>