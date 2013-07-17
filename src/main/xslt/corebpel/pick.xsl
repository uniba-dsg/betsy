<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make temporary variables and assignments, due to the use of
     <fromParts>, and/or references to element variables in <onMessage>s, explicit. -->

<xsl:stylesheet 
  version="1.0" 
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

  <xsl:template name="onMessage">
    <xsl:variable name="outputVariable" select="@variable" />
    <xsl:variable name="outputElement" select="count(ancestor::*/bpel:variables/bpel:variable[@name=$outputVariable][1]/@element) = 1" />

    <xsl:copy>
      <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'portType')]" />
      <xsl:copy-of select="bpel:correlations" />
      
      <xsl:choose>
        <xsl:when test="bpel:fromParts or $outputElement">
          <xsl:call-template name="attribute-with-unique-element-name">
            <xsl:with-param name="attributeName" select="'variable'" />
            <xsl:with-param name="element" select="." />
            <xsl:with-param name="postfix" select="$tmp-output-message-variable-postfix" />
          </xsl:call-template>
  
          <bpel:sequence>
            <!-- Transform fromParts into an assignment, if present -->
            <xsl:if test="bpel:fromParts">
              <xsl:call-template name="copy-from-parts-explicitly" />
            </xsl:if>
  
            <!-- Create assignment to copy single part to element variable -->
            <xsl:if test="$outputElement">
              <xsl:call-template name="copy-output-element-explicitly">
                <xsl:with-param name="outputVariable" select="$outputVariable" />
              </xsl:call-template>
            </xsl:if>
  
            <xsl:apply-templates select="*[not(self::bpel:fromParts or self::bpel:correlations)]"/>
  
          </bpel:sequence>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[not(self::bpel:correlations)]"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:pick">
    <xsl:variable name="needs-temp-variables">
      <xsl:call-template name="message-activities-need-temp-variables">
        <xsl:with-param name="messageActivities" select="bpel:onMessage" />
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="$needs-temp-variables = 'true'">
        <bpel:scope>
          <bpel:variables>
            <xsl:call-template name="message-activities-temp-variables">
              <xsl:with-param name="messageActivities" select="bpel:onMessage" />
            </xsl:call-template>
          </bpel:variables>
    
          <xsl:copy>
            <xsl:copy-of select="@*" />
    
            <xsl:for-each select="bpel:onMessage">
              <xsl:call-template name="onMessage" />
            </xsl:for-each>
            
            <xsl:apply-templates select="*[not(self::bpel:onMessage)]" />
          </xsl:copy>
        </bpel:scope>
       </xsl:when>
      
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*" />
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>
  
</xsl:stylesheet>