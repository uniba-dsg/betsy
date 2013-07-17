<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make variable initialization explicit in all scopes (including <process>). -->
<!-- Make implicit variables and assignments in <onEvent>s explicit. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <xsl:include href="onEvent.xsl" />

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:process[bpel:variables/bpel:variable/bpel:from]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="bpel:extensions | bpel:import" />
      <xsl:call-template name="scope" />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:scope[bpel:variables/bpel:variable/bpel:from]"
                name="scope">

    <bpel:scope>
      <bpel:variables>
        <xsl:for-each select="bpel:variables/bpel:variable">
          <xsl:copy>
            <xsl:copy-of select="@*"/>
          </xsl:copy>
        </xsl:for-each>
      </bpel:variables>
      
      <bpel:faultHandlers>
        <bpel:catchAll>
          <bpel:rethrow/>
        </bpel:catchAll>    
      </bpel:faultHandlers>
    
      <bpel:sequence>    
        <bpel:scope>
          <bpel:faultHandlers>
            <bpel:catchAll>
              <bpel:throw faultName='bpel:scopeInitializationFault'/>
            </bpel:catchAll>    
          </bpel:faultHandlers>
        
          <bpel:assign>
            <xsl:for-each select="bpel:variables/bpel:variable[bpel:from]">
              <bpel:copy>
                <xsl:apply-templates select="bpel:from"/>
                <bpel:to>
                  <xsl:attribute name="variable"><xsl:value-of select="@name"/></xsl:attribute>
                </bpel:to>
              </bpel:copy>
            </xsl:for-each>
          </bpel:assign> 
        </bpel:scope>
      
        <bpel:scope>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                      local-name() = 'targetNamespace')]"/>
          <xsl:apply-templates select="*[not(self::bpel:variables or self::bpel:extensions or self::bpel:import)]" />      
        </bpel:scope>
        
      </bpel:sequence>
    </bpel:scope>

  </xsl:template>
  
</xsl:stylesheet>