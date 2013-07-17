<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Tranform <elseif>s into <else><if>s and add empty branches to the <if>s
     that lack them. -->

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

  <xsl:template match="bpel:elseif">
    <xsl:param name="if" />
    <xsl:param name="count" />
    <xsl:param name="index" />

    <xsl:choose>
	    <xsl:when test="$index &lt; $count">    
	      <bpel:else>
	        <bpel:if>
	          <xsl:copy-of select="*" />
	          <xsl:apply-templates select="$if/bpel:elseif[$index + 1]">
	            <xsl:with-param name="if" select="$if"/>
	            <xsl:with-param name="count" select="$count"/>
	            <xsl:with-param name="index" select="$index + 1"/>
	         </xsl:apply-templates>
	        </bpel:if>
	      </bpel:else>
	    </xsl:when>
	    
	    <xsl:otherwise>
	      <bpel:else>
	        <bpel:if>
	          <xsl:copy-of select="*" />
	          
	          <xsl:choose>
	            <xsl:when test="$if/bpel:else">
	              <xsl:copy-of select="$if/bpel:else" />
	            </xsl:when>
	            <xsl:otherwise>
	              <bpel:else>
	                <bpel:empty/>
	              </bpel:else>
	            </xsl:otherwise>
	          </xsl:choose>
	  
	        </bpel:if>
	      </bpel:else>
	    </xsl:otherwise>    
    </xsl:choose>

  </xsl:template>

  
  <xsl:template match="bpel:if[bpel:elseif]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:copy-of select="bpel:condition" />
      <xsl:apply-templates select="*[not(self::bpel:condition | self::bpel:else | self::bpel:elseif)]" />
      <xsl:apply-templates select="bpel:elseif[1]">
        <xsl:with-param name="if" select="self::node()"/>
        <xsl:with-param name="count" select="count(bpel:elseif)"/>
        <xsl:with-param name="index" select="1"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  
  <xsl:template match="bpel:if[not(bpel:else | bpel:elseif)]">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
      <bpel:else>
        <bpel:empty />
      </bpel:else>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>