<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make temporary variables and assignments, due to the use of
     <fromParts>, and/or references to element variables in <onEvent>s, explicit. -->
<!-- This stylesheet is included by scope.xsl and shouldn't be applied alone. -->

<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:include href="to-from-parts-element-variables.xsl" />

  <xsl:template match="bpel:onEvent[bpel:fromParts or @element]">
    <xsl:copy>
      <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                  (local-name() = 'portType' or
                                   local-name() = 'element'))]" />
      
      <xsl:attribute name="messageType">
        <xsl:call-template name="inbound-message-type" />
      </xsl:attribute>
      <xsl:call-template name="attribute-with-unique-element-name">
        <xsl:with-param name="attributeName" select="'variable'" />
        <xsl:with-param name="element" select="." />
        <xsl:with-param name="postfix" select="$tmp-output-message-variable-postfix" />
      </xsl:call-template>

      <xsl:apply-templates select="bpel:correlations" />
      
      <bpel:scope>
        <xsl:copy-of select="bpel:scope/@*" />
        
        <xsl:apply-templates select="bpel:scope/bpel:partnerLinks | bpel:scope/bpel:messageExchanges" />
        
        <bpel:variables>
          <xsl:for-each select="bpel:fromParts/bpel:fromPart">
            <bpel:variable name="{@toVariable}">
              <xsl:call-template name="inbound-message-part-typing">
                <xsl:with-param name="messageActivity" select="../.." />
                <xsl:with-param name="part" select="@part" />
              </xsl:call-template>
            </bpel:variable>
          </xsl:for-each>
          <xsl:if test="@element">
            <bpel:variable name="{@variable}" element="{@element}" />
          </xsl:if>
          <xsl:apply-templates select="bpel:scope/bpel:variables/*" />
        </bpel:variables>
        
        <xsl:apply-templates select="bpel:scope/bpel:correlationSets | bpel:scope/bpel:faultHandlers | bpel:scope/bpel:compensationHandler | bpel:scope/bpel:terminationHandler | bpel:scope/bpel:eventHandlers" />

        <bpel:sequence>
          <!-- Transform fromParts into an assignment, if present -->
          <xsl:if test="bpel:fromParts">
            <xsl:call-template name="copy-from-parts-explicitly" />
          </xsl:if>
    
          <!-- Create assignment to copy single part to element variable -->
          <xsl:if test="@element">
            <xsl:call-template name="copy-output-element-explicitly">
              <xsl:with-param name="outputVariable" select="@variable" />
            </xsl:call-template>
          </xsl:if>
    
          <xsl:apply-templates select="bpel:scope/*[not(self::bpel:partnerLinks or self::bpel:messageExchanges or self::bpel:correlationSets or self::bpel:faultHandlers or self::bpel:compensationHandler or self::bpel:terminationHandler or self::bpel:eventHandlers)]"/>
        </bpel:sequence>
      </bpel:scope>
      
      <xsl:apply-templates select="*[not(self::bpel:*)]" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>