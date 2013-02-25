<!--
  ~ Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
  ~
  ~ This program is free software:  Licensed under the EUPL, Version 1.1 or - as
  ~ soon as they will be approved by the European Commission - subsequent versions
  ~ of the EUPL (the "Licence");
  ~
  ~ You may not use this work except in compliance with the Licence.
  ~ You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ European Union Public Licence for more details.
  -->
<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                exclude-result-prefixes="types xsi xs fn f"
                xmlns:f="Functions"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:variable name="properties" select="unparsed-text('../../../messages_fi.properties')" as="xs:string"/>
    <xsl:template name="educationBasicData_fi">

        <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
        <div class="infobox">

            <h3>
                <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.otsikko')"/>
            </h3>

            <ul class="minimal">
                <xsl:apply-templates select="./types:Classification/types:EducationDomain"/>
                <xsl:apply-templates select="./types:Classification/types:StudyDomain"/>
                <xsl:apply-templates select="./types:Name"/>
                <xsl:apply-templates select="./types:DegreeTitle"/>
                <xsl:apply-templates select="./types:Qualification"/>

                <xsl:variable name="id" select="./@id"/>
                <xsl:apply-templates
                        select="/types:LearningOpportunityDownloadData/types:LearningOpportunityInstance/types:SpecificationRef[@ref=$id]"/>
                <xsl:apply-templates select="./types:Credits"/>


                <!--
         <li class="heading">
             <fmt:message key="tarjonta.koulutuksenperustiedot.koulutusohjelma"/>
         </li>
         <li class="emphasized">
             <c:out value="${searchResult['LOSName']}"/>
         </li>

         <li class="heading">
             <fmt:message key="tarjonta.koulutuksenperustiedot.tutkinto"/>
         </li>
         <li class="emphasized">
             <c:out value="${searchResult['LOSDegreeTitle']}"/>
         </li>

         <li class="heading">
             <fmt:message key="tarjonta.koulutuksenperustiedot.tutkintonimike"/>
         </li>
         <li class="emphasized">
             <c:out value="${searchResult['LOSQualification']}"/>
         </li>

         <li class="heading">
             <fmt:message key="tarjonta.koulutuksenperustiedot.opintojenlaajuus"/>
         </li>
         <li class="emphasized"><c:out value="${searchResult['LOSCredits']}"/>&nbsp;
             <spring:message
                     code="tarjonta.koulutuskuvaus.${searchResult['LOSCreditsUnit']}" text="?_?"/>
         </li>       -->
            </ul>
        </div>


        <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
    </xsl:template>
    <xsl:template match="//types:LearningOpportunitySpecification/types:Classification/types:EducationDomain">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.koulutusala')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="./types:Label[@lang=fi]"/>
        </li>
    </xsl:template>

    <xsl:template match="//types:LearningOpportunitySpecification/types:Classification/types:StudyDomain">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.opintoala')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="./types:Label[@lang=fi]"/>
        </li>
    </xsl:template>

    <xsl:template match="//types:LearningOpportunitySpecification/types:Name">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.koulutusohjelma')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select=".[@lang=fi]"/>
        </li>
    </xsl:template>
    <xsl:template match="//types:LearningOpportunitySpecification/types:DegreeTitle">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.tutkinto')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select=".[@lang=fi]"/>
        </li>
    </xsl:template>
    <xsl:template match="//types:LearningOpportunitySpecification/types:Qualification">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.tutkintonimike')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="./types:Code"/>
        </li>
    </xsl:template>
    <xsl:template match="//types:LearningOpportunitySpecification/types:Credits">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.opintojenlaajuus')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="./types:Value"/>
        </li>
    </xsl:template>

    <xsl:template
            match="/types:LearningOpportunityDownloadData/types:LearningOpportunityInstance/types:SpecificationRef">
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.opetuskieli')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="../types:LanguagesOfInstruction/types:Code"/>
        </li>
        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.opetusmuoto')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="../types:FormsOfTeaching/types:Code"/>
        </li>

        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.koulutuksenalkupaivamaara')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="../types:StartDate"/>
        </li>

        <li class="heading">
            <xsl:value-of select="f:getProperty('tarjonta.koulutuksenperustiedot.suunniteltukesto')"/>
        </li>
        <li class="emphasized">
            <xsl:value-of select="../types:Duration/types:Value"/>
        </li>
    </xsl:template>


</xsl:stylesheet>
