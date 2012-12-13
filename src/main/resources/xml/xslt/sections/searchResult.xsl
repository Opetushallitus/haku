<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                exclude-result-prefixes="types xsi xs fn"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template name="searchResult">
        <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
        <ul class="result set-left" style="display: inline-block; margin-right: 20px">
            <li><a class="form-row-link bold">
                <xsl:attribute name="href">tarjontatiedot/<xsl:value-of select="types:Identifier"/></xsl:attribute>
                <xsl:value-of select="types:Title"/>
            </a></li>
            <xsl:for-each select="types:LearningOpportunities/types:InstanceRef">
                <xsl:variable name="loiRef" select="@ref"/>
                <xsl:for-each select="//types:LearningOpportunityInstance[@id=$loiRef]/types:SpecificationRef">
                    <xsl:variable name="losRef" select="@ref"/>
                    <xsl:variable name="los" select="//types:LearningOpportunitySpecification[@id=$losRef]"/>
                    <li><a href="#" class="form-row-link left-intend-2">
                        <xsl:value-of select="$los/types:Name"/>,&#160;<xsl:value-of select="$los/types:Qualification/types:Title"/>
                    </a></li>
                </xsl:for-each>
            </xsl:for-each>
        </ul>
        <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
    </xsl:template>

</xsl:stylesheet>