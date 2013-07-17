<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make the default fault, compensation, and termination handlers explicit. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:faultHandler[not(bpel:catchAll)]">
    <xsl:copy>
      <xsl:apply-templates select="bpel:catch" />
      <bpel:catchAll>
        <bpel:sequence>
          <bpel:compensate />
          <bpel:rethrow />
        </bpel:sequence>
      </bpel:catchAll>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:scope[not(bpel:faultHandlers) or not(bpel:compensationHandler) or not(bpel:terminationHandler)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="bpel:targets" />
      <xsl:apply-templates select="bpel:sources" />
      <xsl:apply-templates select="bpel:partnerLinks" />
      <xsl:apply-templates select="bpel:messageExchanges" />
      <xsl:apply-templates select="bpel:variables" />
      <xsl:apply-templates select="bpel:correlationSets" />
      
      <xsl:apply-templates select="bpel:faultHandlers" />
      <xsl:if test="not(bpel:faultHandlers)">
        <bpel:faultHandlers>
          <bpel:catchAll>
            <bpel:sequence>
              <bpel:compensate />
              <bpel:rethrow />
            </bpel:sequence>
          </bpel:catchAll>
        </bpel:faultHandlers>
      </xsl:if>
      
      <xsl:apply-templates select="bpel:compensationHandler" />
      <xsl:if test="not(bpel:compensationHandler)">
        <bpel:compensationHandler>
          <bpel:compensate />
        </bpel:compensationHandler>
      </xsl:if>
            
      <xsl:apply-templates select="bpel:terminationHandler" />
      <xsl:if test="not(bpel:terminationHandler)">
        <bpel:terminationHandler>
          <bpel:compensate />
        </bpel:terminationHandler>
      </xsl:if>
    
      <xsl:apply-templates select="bpel:eventHandlers" />
      
      <xsl:apply-templates select="*[not(
        self::bpel:targets or 
        self::bpel:sources or 
        self::bpel:partnerLinks or 
        self::bpel:messageExchanges or 
        self::bpel:variables or 
        self::bpel:candrelationSets or 
        self::bpel:faultHandlers or 
        self::bpel:compensationHandler or 
        self::bpel:terminationHandler or 
        self::bpel:eventHandlers
      )]" />
    </xsl:copy>   
  </xsl:template>

</xsl:stylesheet>