<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make temporary variables and assignments, due to the use of <toParts>,
     and/or references to an element variable, explicit. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <xsl:include href="to-from-parts-element-variables.xsl" />

  <!-- Copy all elements and attributes -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- Unfold reply -->
  <xsl:template match="bpel:reply">
    <xsl:variable name="inputVariable" select="@variable" />
    <xsl:variable name="inputElement" select="count(ancestor::*/bpel:variables/bpel:variable[@name=$inputVariable][1]/@element) = 1" />
    
    <xsl:choose>
      <xsl:when test="bpel:toParts or $inputElement">        
        <bpel:scope>
          <bpel:variables>
            <xsl:call-template name="message-activities-temp-variables">
              <xsl:with-param name="messageActivities" select="." />
            </xsl:call-template>
          </bpel:variables>
          
          <bpel:sequence>
            <!-- Transform toParts into an assignment, if present -->
            <xsl:if test="bpel:toParts">
              <xsl:call-template name="copy-to-parts-explicitly" />
            </xsl:if>

            <!-- Create assignment to copy element variable to single part -->
            <xsl:if test="$inputElement">
              <xsl:call-template name="copy-input-element-explicitly">
                <xsl:with-param name="inputVariable" select="$inputVariable" />
              </xsl:call-template>
            </xsl:if>
            
            <xsl:copy>
              <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                          (local-name() = 'variable' or
                                           local-name() = 'portType'))]" />
              <xsl:call-template name="attribute-with-unique-element-name">
                <xsl:with-param name="attributeName" select="'variable'" />
                <xsl:with-param name="element" select="." />
                <xsl:with-param name="postfix" select="$tmp-input-message-variable-postfix" />
              </xsl:call-template>
              <xsl:apply-templates select="*[not(self::bpel:toParts)]" />
            </xsl:copy>
          </bpel:sequence>
        </bpel:scope>
      </xsl:when>

      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'portType')]" />
          <xsl:apply-templates select="*" />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>
  
</xsl:stylesheet>