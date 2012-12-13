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
            <!--
            <xsl:variable name="loiRef" select="types:LearningOpportunities/types:InstanceRef/@loiRef"/>


            <xsl:template match="//types:LearningOpportunityInstance">
                <field name="LOIId">
                    <xsl:value-of select="@id"/>
                </field>

                <xsl:variable name="id" select="types:SpecificationRef/@ref"/>
                <xsl:apply-templates
                        select="/types:LearningOpportunityDownloadData/types:LearningOpportunitySpecification[@id=$id]"/>

                <xsl:for-each select="types:LanguagesOfInstruction/types:Language">
                    <field name="LOILanguagesOfInstruction">
                        <xsl:value-of select="@code"/>
                    </field>
                </xsl:for-each>
                <xsl:for-each select="types:FormsOfTeaching/types:FormOfTeaching">
                    <field name="LOIFormOfTeaching">
                        <xsl:value-of select="@type"/>
                    </field>
                </xsl:for-each>
                <field name="LOIPrerequisite">
                    <xsl:value-of select="types:Prerequisite/types:Code"/>
                </field>

                <xsl:apply-templates select="types:WebLinks/types:Link/types:Uri"/>


            </xsl:template>

-->



            <!--<c:forEach var="key" items="${item['LOIIndexes']}">
                <c:set var="loi" value="${item[key]}"/>

                <li><a href="tarjontatiedot/mihinkähäntämänpitäisiosoittaa"
                       class="form-row-link left-intend-2">- ${loi['LOSName']}, ${loi['LOSQualification']}</a>     </li>
            </c:forEach>
-->

        </ul>

        <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
    </xsl:template>

</xsl:stylesheet>