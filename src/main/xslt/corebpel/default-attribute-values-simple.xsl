<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make the simple default attribute values explicit.
     Attributes: createInstance, ignoreMissingFromData, initiate, isolated, succesfulBranchesOnly, validate -->

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
  
  <xsl:template match="bpel:pick[not(@createInstance)] | bpel:receive[not(@createInstance)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="createInstance">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:copy[not(@ignoreMissingFromData)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="ignoreMissingFromData">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:correlation[not(@initiate)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="initiate">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:scope[not(@isolated)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="isolated">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:branches[not(@successfulBranchesOnly)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="successfulBranchesOnly">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:assign[not(@validate)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="validate">no</xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>