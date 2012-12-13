<?xml version="1.0" encoding="utf-8"?>
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
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ European Union Public Licence for more details.
  -->
<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                exclude-result-prefixes="types xsi xs fn"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xsi:schemaLocation="/../main/resources/wsdl/learningServiceCommon.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--<xsl:import href="src/main/resources/xml/xslt/sections/searchResult.xsl"/>-->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <add>
            <xsl:apply-templates select="/types:LearningOpportunityDownloadData/types:ApplicationOption"/>
        </add>
    </xsl:template>

    <xsl:template match="types:LearningOpportunityDownloadData/types:ApplicationOption">
        <doc>
            <field name="html_searchResult_fi">

                <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
                <ul class="result set-left" style="display: inline-block; margin-right: 20px">
                    <li><a class="form-row-link bold">
                        <xsl:attribute name="href">tarjontatiedot/<xsl:value-of select="types:Identifier"/></xsl:attribute>
                        <xsl:value-of select="types:Title"/>
                    </a></li>


                    <!--<c:forEach var="key" items="${item['LOIIndexes']}">
                        <c:set var="loi" value="${item[key]}"/>

                        <li><a href="tarjontatiedot/mihinkähäntämänpitäisiosoittaa"
                               class="form-row-link left-intend-2">- ${loi['LOSName']}, ${loi['LOSQualification']}</a>     </li>
                    </c:forEach>
        -->

                </ul>
                <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>

                <!--
                <xsl:call-template name="searchResult"/>
                -->
            </field>

            <field name="AOId">
                <xsl:variable name="identifier" select="types:Identifier"/>
                <xsl:value-of select="fn:tokenize($identifier, '/')[last()]"/>
            </field>
            <field name="AOTitle">
                <xsl:value-of select="types:Title"/>
            </field>
            <!-- DEPRECATED
            <field name="AODescription">
                <xsl:value-of select="types:Description"/>
            </field>
            -->
            <field name="AOSelectionCriterionDescription">
                <xsl:value-of select="types:SelectionCriterions/types:Description"/>
            </field>
            <field name="AOLastYearMaxScore">
                <xsl:value-of select="types:SelectionCriterions/types:LastYearMaxScore"/>
            </field>
            <field name="AOLastYearMinScore">
                <xsl:value-of select="types:SelectionCriterions/types:LastYearMinScore"/>
            </field>
            <field name="AOLastYearTotalApplicants">
                <xsl:value-of select="types:SelectionCriterions/types:LastYearTotalApplicants"/>
            </field>
            <field name="AOStartingQuota">
                <xsl:value-of select="types:SelectionCriterions/types:StartingQuota"/>
            </field>
            <field name="AOEligibilityRequirements">
                <xsl:value-of select="types:EligibilityRequirements/types:Description"/>
            </field>
            <xsl:apply-templates select="types:SelectionCriterions/types:Attachments/types:Attachment"/>
            <xsl:apply-templates select="types:SelectionCriterions/types:EntranceExaminations"/>
            <xsl:apply-templates select="types:SelectionCriterions/types:EntranceExaminations/types:Examination"/>
            <xsl:apply-templates
                    select="types:SelectionCriterions/types:EntranceExaminations/types:Examination/types:ExaminationEvent"/>
            <xsl:apply-templates select="types:LearningOpportunities"/>

            <xsl:variable name="ref" select="types:LearningOpportunities/types:InstanceRef/@ref"/>
            <xsl:apply-templates
                    select="/types:LearningOpportunityDownloadData/types:LearningOpportunityInstance[@id=$ref]"/>

            <field name="tmpLomakeId">yhteishaku</field>

            <xsl:variable name="asRef" select="types:ApplicationSystemRef/types:OidRef"/>
            <xsl:apply-templates select="/types:LearningOpportunityDownloadData/types:ApplicationSystem[@id=$asRef]"/>
        </doc>
    </xsl:template>

    <xsl:template match="types:SelectionCriterions/types:Attachments/types:Attachment">
        <field name="AOAttachmentDescription">
            <xsl:value-of select="./types:Description/types:Text"/>
        </field>
        <field name="AOAttachmentType">
            <xsl:value-of select="./types:Type/types:Value"/>
        </field>

        <field name="AOAttachmentReturnDueDate">
            <xsl:variable name="time" select="./types:Return/types:DueDate"/>
            <xsl:value-of select="fn:adjust-dateTime-to-timezone($time,xs:dayTimeDuration('PT0H'))"/>
        </field>
        <field name="AOAttachmentReturnToEmailAddress">
            <xsl:value-of select="./types:Return/types:To/types:EmailAddress"/>
        </field>
        <field name="AOAttachmentReturnToPostalAddress">
            <!-- TODO: Pilkotaanko kenttiin, vai voidaanko yhdistää osoiterivit -->
            <xsl:value-of select="./types:Return/types:To/types:PostalAddress"/>
        </field>
    </xsl:template>

    <xsl:template match="types:SelectionCriterions/types:EntranceExaminations">
        <field name="AOExaminationDescription">
            <xsl:value-of select="./types:Description"/>
        </field>

    </xsl:template>

    <xsl:template match="types:SelectionCriterions/types:EntranceExaminations/types:Examination">
        <field name="AOExaminationTitle">
            <xsl:value-of select="./types:ExaminationType"/>
        </field>
        <field name="AOExaminationDescription">
            <xsl:value-of select="./types:Description"/>
        </field>

    </xsl:template>

    <xsl:template match="types:SelectionCriterions/types:EntranceExaminations/types:Examination/types:ExaminationEvent">
        <field name="AOExaminationStart">
            <xsl:variable name="start" select="./types:Start"/>
            <xsl:value-of select="fn:adjust-dateTime-to-timezone($start, xs:dayTimeDuration('PT0H'))"/>
        </field>
        <field name="AOExaminationEnd">
            <xsl:variable name="end" select="./types:End"/>
            <xsl:value-of select="fn:adjust-dateTime-to-timezone($end, xs:dayTimeDuration('PT0H'))"/>
        </field>
        <xsl:apply-templates select="types:Locations/types:Location"/>
    </xsl:template>

    <xsl:template match="types:Locations/types:Location">
        <field name="AOExaminationLocation">
            <xsl:value-of select="."/>
        </field>
    </xsl:template>

    <!--  -->

    <!-- start of LOI -->

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
        <!-- TODO: replace code with proper value -->
        <field name="LOIPrerequisite">
            <xsl:value-of select="types:Prerequisite/types:Code"/>
        </field>

        <xsl:apply-templates select="types:WebLinks/types:Link/types:Uri"/>


    </xsl:template>

    <xsl:template match="//types:LearningOpportunityInstance/types:WebLinks/types:Link/types:Uri">
        <field name="LOIWebLinkUri">
            <xsl:value-of select="."/>
        </field>
        <field name="LOIWebLinkLabel">
            <xsl:if test="../types:Label">
                <xsl:value-of select="../types:Label"/>
            </xsl:if>
            <xsl:if test="not(../types:Label)">
                <xsl:value-of select="../types:Uri"/>
            </xsl:if>
        </field>
        <field name="LOIWebLinkType">
            <xsl:value-of select="../@type"/>
        </field>

    </xsl:template>
    <!-- end of LOI -->

    <!-- start of LOS -->

    <xsl:template match="/types:LearningOpportunityDownloadData/types:LearningOpportunitySpecification">

        <field name="LOSId">
            <xsl:value-of select="@id"/>
        </field>
        <field name="LOSName">
            <xsl:value-of select="./types:Name"/>
        </field>
        <field name="LOSDegreeTitle">
            <xsl:value-of select="./types:DegreeTitle"/>
        </field>
        <field name="LOSCredits">
            <xsl:value-of select="./types:Credits/types:Value"/>
        </field>
        <field name="LOSCreditsUnit">
            <xsl:value-of select="./types:Credits/@unit"/>
        </field>
        <field name="LOSDescriptionAccessToFurtherStudies">
            <xsl:value-of select="./types:Description/types:AccessToFurtherStudies"/>
        </field>
        <field name="LOSDescriptionStructureDiagram">
            <xsl:value-of select="./types:Description/types:StructureDiagram"/>
        </field>
        <!-- DEPRECATED
        <field name="LOSDescriptionEducationAndProfessionalGoals">
            <xsl:value-of select="./types:Description/types:EducationAndProfessionalGoals"/>
        </field>
        -->
        <field name="LOSQualification">
            <xsl:value-of select="./types:Qualification/types:Title"/>
        </field>

        <xsl:apply-templates select="//types:LearningOpportunitySpecification/types:Classification"/>

        <xsl:variable name="lop" select="types:OfferedBy/@ref"/>
        <xsl:apply-templates
                select="/types:LearningOpportunityDownloadData/types:LearningOpportunityProvider[@id=$lop]"/>

    </xsl:template>

    <xsl:template match="//types:LearningOpportunitySpecification/types:Classification">
        <!-- TODO: select the actual value instead of koodisto code once the value is available -->

        <field name="LOSEducationClassification">
            <xsl:value-of select="./types:EducationClassification/types:Code"/>
        </field>
        <field name="LOSEducationDomain">
            <xsl:value-of select="./types:EducationDomain/types:Code"/>
        </field>
        <field name="LOSEducationDegree">
            <xsl:value-of select="./types:EducationDegree/types:Code"/>
        </field>
        <field name="LOSStydyDomain">
            <xsl:value-of select="./types:StudyDomain/types:Code"/>
        </field>
    </xsl:template>

    <!-- end of LOS -->
    <!-- start of LOP -->

    <xsl:template match="/types:LearningOpportunityDownloadData/types:LearningOpportunityProvider">
        <field name="LOPId">
            <xsl:value-of select="@id"/>
        </field>
        <field name="LOPInstitutionInfoName">
            <xsl:value-of select="./types:InstitutionInfo/types:Name"/>
        </field>
        <field name="LOPInstitutionInfoGeneralDescription">
            <xsl:value-of select="./types:InstitutionInfo/types:GeneralDescription"/>
        </field>
    </xsl:template>
    <!-- end of LOP -->

    <!-- start AS -->
    <xsl:template match="/types:LearningOpportunityDownloadData/types:ApplicationSystem">
        <field name="ASId">
            <xsl:value-of select="@id"/>
        </field>
        <field name="ASName">
            <xsl:value-of select="types:Name"/>
        </field>
    </xsl:template>
    <!-- end of AS -->

</xsl:stylesheet>