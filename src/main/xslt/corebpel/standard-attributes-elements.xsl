<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Move <targets>, <sources>, and suppressJoinFailure from activities to a
     new wrapping <flow>, except for activities that have no <targets> or
     <sources>, where we push the value of that attribute to all the child
     activities. -->
<!-- Names are also moved from activities (except <scope>s) to a new wrapping
     <flow> and fresh names are added to unnamed <flow>s and <scope>s. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bpel="http://docs.oasis-open.org/wsbpel/2.0/process/executable">

  <xsl:output indent="yes" method="xml" />

  <xsl:include href="constants.xsl"/>
  <xsl:include href="fresh-names.xsl" />
    
  <xsl:template match="*">
    <xsl:param name="suppressJoinFailure" select="false()" />
    
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates>
        <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template name="name">
    <xsl:param name="name" />
    
    <xsl:choose>
      <xsl:when test="normalize-space(string($name)) = ''">
        <xsl:call-template name="attribute-with-unique-element-name">
          <xsl:with-param name="attributeName" select="'name'" />
          <xsl:with-param name="element" select="." />
          <xsl:with-param name="postfix" select="$fresh-activity-name-postfix" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$name" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="*" mode="extensionActivity">
      <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'suppressJoinFailure')]" />
      <xsl:copy-of select="*[not(self::bpel:targets or self::bpel:sources)]" />
  </xsl:template>

  <xsl:template match="bpel:extensionActivity[*[1]/bpel:targets or *[1]/bpel:sources or *[1]/@suppressJoinFailure]">
    <xsl:param name="suppressJoinFailure" select="false()" />
    
    <xsl:choose>
      <xsl:when test="not(*[1]/bpel:targets or *[1]/bpel:sources)">
        <xsl:copy>
          <xsl:copy-of select="*" />
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <bpel:flow>
          <xsl:copy-of select="*[1]/@suppressJoinFailure" />
          <xsl:if test="not(*[1]/@suppressJoinFailure) and $suppressJoinFailure">
            <xsl:copy-of select="$suppressJoinFailure" />
          </xsl:if>
          <xsl:copy-of select="*[1]/bpel:targets" />
          <xsl:copy-of select="*[1]/bpel:sources" />
          <xsl:copy>
            <xsl:apply-templates mode="extensionActivity" />
          </xsl:copy>
        </bpel:flow>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="bpel:flow">
    <xsl:param name="suppressJoinFailure" select="false()" />

    <xsl:copy>
      <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'name')]" />
      <xsl:if test="not(@suppressJoinFailure) and $suppressJoinFailure">
        <xsl:copy-of select="$suppressJoinFailure" />
      </xsl:if>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="bpel:scope">
    <xsl:param name="suppressJoinFailure" select="false()" />

    <xsl:choose>
      <xsl:when test="$suppressJoinFailure">
        <xsl:call-template name="scope-with-links">
          <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and local-name() = 'name')]" />
          <xsl:call-template name="name">
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="bpel:scope[bpel:targets or bpel:sources or @suppressJoinFailure]"
                name="scope-with-links">
    <xsl:param name="suppressJoinFailure" select="false()" />

    <xsl:choose>
      <xsl:when test="not(bpel:targets or bpel:sources)">
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and 
                                      (local-name() = 'suppressJoinFailure' or
                                       local-name() = 'name'))]" />
          <xsl:call-template name="name">
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
          
          <xsl:choose>
            <xsl:when test="@suppressJoinFailure">
              <xsl:apply-templates>
                <xsl:with-param name="suppressJoinFailure" select="@suppressJoinFailure" />
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates>
                <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
              </xsl:apply-templates>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <bpel:flow>
          <xsl:copy-of select="@suppressJoinFailure" />
          <xsl:if test="not(@suppressJoinFailure) and $suppressJoinFailure">
            <xsl:copy-of select="$suppressJoinFailure" />
          </xsl:if>
          <xsl:copy-of select="bpel:targets" />
          <xsl:copy-of select="bpel:sources" />
          <xsl:copy>
            <xsl:copy-of select="@*[not(namespace-uri() = '' and 
                                        (local-name() = 'suppressJoinFailure' or
                                         local-name() = 'name'))]" />
            <xsl:call-template name="name">
              <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[not(self::bpel:targets or self::bpel:sources)]" />
          </xsl:copy>
        </bpel:flow>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="bpel:assign
                     | bpel:compensate
                     | bpel:compensateScope
                     | bpel:empty
                     | bpel:exit
                     | bpel:forEach
                     | bpel:if
                     | bpel:invoke
                     | bpel:pick
                     | bpel:receive
                     | bpel:repeatUntil
                     | bpel:reply
                     | bpel:rethrow
                     | bpel:sequence
                     | bpel:throw
                     | bpel:validate
                     | bpel:wait
                     | bpel:while">
    <xsl:param name="suppressJoinFailure" select="false()" />

    <xsl:choose>
      <xsl:when test="$suppressJoinFailure">
        <xsl:call-template name="activity-with-links">
          <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and
                                      local-name() = 'name')]" />
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="bpel:assign[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:compensate[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:compensateScope[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:empty[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:exit[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:forEach[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:if[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:invoke[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:pick[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:receive[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:repeatUntil[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:reply[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:rethrow[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:sequence[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:throw[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:validate[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:wait[bpel:targets or bpel:sources or @suppressJoinFailure]
                     | bpel:while[bpel:targets or bpel:sources or @suppressJoinFailure]"
                name="activity-with-links">
    <xsl:param name="suppressJoinFailure" select="false()" />
    
    <xsl:choose>
      <xsl:when test="not(bpel:targets or bpel:sources)">
        <xsl:copy>
          <xsl:copy-of select="@*[not(namespace-uri() = '' and 
                                      (local-name() = 'name' or
                                       local-name() = 'suppressJoinFailure'))]" />
          
          <xsl:choose>
            <xsl:when test="@suppressJoinFailure">
              <xsl:apply-templates>
                <xsl:with-param name="suppressJoinFailure" select="@suppressJoinFailure" />
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates>
                <xsl:with-param name="suppressJoinFailure" select="$suppressJoinFailure" />
              </xsl:apply-templates>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
		    <bpel:flow>
		      <xsl:copy-of select="@suppressJoinFailure" />
		      <xsl:if test="not(@suppressJoinFailure) and $suppressJoinFailure">
		        <xsl:copy-of select="$suppressJoinFailure" />
		      </xsl:if>
		      <xsl:copy-of select="bpel:targets" />
		      <xsl:copy-of select="bpel:sources" />
		      <xsl:copy>
		        <xsl:copy-of select="@*[not(namespace-uri() = '' and
		                                    (local-name() = 'name' or
		                                     local-name() = 'suppressJoinFailure'))]" />
		        <xsl:apply-templates select="*[not(self::bpel:targets or self::bpel:sources)]" />
		      </xsl:copy>
		    </bpel:flow>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>