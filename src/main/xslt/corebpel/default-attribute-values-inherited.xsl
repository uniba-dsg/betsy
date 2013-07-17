<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Make the inherited default attribute values explicit.
     Attributes: suppressJoinFailure, exitOnStandardFault -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
  
  <xsl:output indent="yes" method="xml" />
  
  <xsl:template match="bpel:process">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates>
        <xsl:with-param name="exitOnStandardFault">
          <xsl:choose>
            <xsl:when test="@exitOnStandardFault">
              <xsl:value-of select="@exitOnStandardFault" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'no'" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="suppressJoinFailure">
          <xsl:choose>
            <xsl:when test="@suppressJoinFailure">
              <xsl:value-of select="@suppressJoinFailure" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'no'" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:scope">
    <xsl:param name="exitOnStandardFault" />
    <xsl:param name="suppressJoinFailure" />
    
    <xsl:copy>
      <xsl:copy-of select="@*" />
      
      <xsl:if test="not(@exitOnStandardFault)">
        <xsl:attribute name="exitOnStandardFault">
          <xsl:value-of select="$exitOnStandardFault" />
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="not(@suppressJoinFailure)">
        <xsl:attribute name="suppressJoinFailure">
          <xsl:value-of select="$suppressJoinFailure" />
        </xsl:attribute>
      </xsl:if>
      
      <xsl:apply-templates>
        <xsl:with-param name="exitOnStandardFault">
          <xsl:choose>
            <xsl:when test="@exitOnStandardFault">
              <xsl:value-of select="@exitOnStandardFault" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$exitOnStandardFault" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="suppressJoinFailure">
	        <xsl:choose>
	          <xsl:when test="@suppressJoinFailure">
              <xsl:value-of select="@suppressJoinFailure" />
	          </xsl:when>
	          <xsl:otherwise>
              <xsl:value-of select="$suppressJoinFailure" />
	          </xsl:otherwise>
	        </xsl:choose>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="bpel:assign | bpel:compensate | bpel:compensateScope | bpel:empty | bpel:exit | bpel:extensionActivity/* | bpel:flow | bpel:forEach | bpel:if | bpel:invoke | bpel:pick | bpel:receive | bpel:repeatUntil | bpel:reply | bpel:rethrow | bpel:sequence | bpel:throw | bpel:validate | bpel:wait | bpel:while">
    <xsl:param name="exitOnStandardFault" />
    <xsl:param name="suppressJoinFailure" />

    <xsl:copy>
      <xsl:copy-of select="@*" />      
      <xsl:if test="not(@suppressJoinFailure)">
        <xsl:attribute name="suppressJoinFailure">
          <xsl:value-of select="$suppressJoinFailure" />
        </xsl:attribute>
      </xsl:if>

      <xsl:apply-templates>
        <xsl:with-param name="exitOnStandardFault" select="$exitOnStandardFault" />
        <xsl:with-param name="suppressJoinFailure">
          <xsl:choose>
            <xsl:when test="@suppressJoinFailure">
              <xsl:value-of select="@suppressJoinFailure" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$suppressJoinFailure" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="*">
    <xsl:param name="exitOnStandardFault" />
    <xsl:param name="suppressJoinFailure" />
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates>
        <xsl:with-param name="exitOnStandardFault" select="$exitOnStandardFault" />
        <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>