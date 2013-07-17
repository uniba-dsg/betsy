<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Remove all optional extensions and their declarations. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
  
  <xsl:include href="constants.xsl"/>

  <xsl:output indent="yes" method="xml" />
  
  <!-- Initiate the removal of optional extensions by collecting the set of
       namespace URIs for mandatory extensions. -->
  <xsl:template match="bpel:process">
    <xsl:call-template name="clean-bpel-node">
      <xsl:with-param name="mandatory-URIs" select="bpel:extensions/bpel:extension[@mustUnderstand='yes']/@namespace" /> 
    </xsl:call-template>
  </xsl:template>

  <!-- Remove the <extensions> element if there are no mandatory extensions. -->
  <xsl:template match="bpel:extensions[not(bpel:extension[@mustUnderstand='yes'])]" />

  <!-- Remove declarations of optional extensions. -->
  <xsl:template match="bpel:extension[@mustUnderstand='no']" />
  
  <!-- Replace optional <extensionActivity>s with <empty>. -->
  <xsl:template match="bpel:extensionActivity">
    <xsl:param name="mandatory-URIs" />
    
    <xsl:choose>
	    <xsl:when test="namespace-uri(*[1]) = $mandatory-URIs">
	      <xsl:copy>
	        <xsl:apply-templates select="@*">
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
	        <xsl:apply-templates>
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
	      </xsl:copy>
	    </xsl:when>
      <xsl:otherwise>
		    <bpel:empty>
	        <xsl:apply-templates select="*[1]/@*">
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
	        <xsl:apply-templates select="*[1]/child::node()">
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
		    </bpel:empty>
		  </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Replace <assign>s that will be empty after removing optional
       <extensionAssignOperation>s with <empty>. -->
  <xsl:template match="bpel:assign">
    <xsl:param name="mandatory-URIs" />

    <xsl:choose>
      <xsl:when test="bpel:copy or bpel:extensionAssignOperation/*[1][namespace-uri() = $mandatory-URIs]">
		    <xsl:call-template name="clean-bpel-node">
		      <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" /> 
		    </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
		    <bpel:empty>
	        <xsl:apply-templates select="@*[not(namespace-uri() = '' and local-name() = 'validate')]">
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
	        <xsl:apply-templates>
	          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
	        </xsl:apply-templates>
		    </bpel:empty>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Remove optional <extensionAssignOperation>s. -->
  <xsl:template match="bpel:extensionAssignOperation">
    <xsl:param name="mandatory-URIs" />
    
    <xsl:if test="namespace-uri(*[1]) = $mandatory-URIs">
      <xsl:copy>
        <xsl:apply-templates select="@*">
          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
        </xsl:apply-templates>
        <xsl:apply-templates>
          <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
        </xsl:apply-templates>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  
  <!-- Protect <literal>s. -->
  <xsl:template match="bpel:literal">
    <xsl:param name="mandatory-URIs" />

    <xsl:copy-of select="." />
  </xsl:template>
  
  <!-- BPEL constructs recursively. -->
  <xsl:template match="bpel:*" name="clean-bpel-node">
    <xsl:param name="mandatory-URIs" />

    <xsl:copy>
      <xsl:apply-templates select="@*">
        <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
      </xsl:apply-templates>
      <xsl:apply-templates>
        <xsl:with-param name="mandatory-URIs" select="$mandatory-URIs" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <!-- Remove extensions that are in an optional namespace, and leave the rest untouched. -->
  <xsl:template match="*">
    <xsl:param name="mandatory-URIs" />
    
    <xsl:if test="namespace-uri() = $mandatory-URIs">
      <xsl:copy-of select="." />
    </xsl:if>
  </xsl:template>

  <!-- Remove extension attributes that are in an optional namespace. -->
  <xsl:template match="@*">
    <xsl:param name="mandatory-URIs" />
    
    <xsl:if test="namespace-uri() = '' or namespace-uri() = $mandatory-URIs">
      <xsl:copy />
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>