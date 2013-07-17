<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Utility templates for generating fresh names. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="freshPrefix" />
  
  <xsl:template name="unique-element-name">
    <xsl:param name="element" select="." />
    <xsl:param name="postfix" select="''" />
    <xsl:value-of select="$freshPrefix" />
    <xsl:value-of select="generate-id($element)" />
    <xsl:value-of select="$postfix" />
  </xsl:template>
  
  <xsl:template name="attribute-with-unique-element-name">
    <xsl:param name="attributeName" />
    <xsl:param name="element" select="." />
    <xsl:param name="postfix" select="''" />
    <xsl:attribute name="{$attributeName}">
      <xsl:call-template name="unique-element-name">
        <xsl:with-param name="element" select="$element" />
        <xsl:with-param name="postfix" select="$postfix" />
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>
  
</xsl:stylesheet>