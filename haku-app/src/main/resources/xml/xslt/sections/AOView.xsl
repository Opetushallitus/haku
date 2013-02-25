<xsl:stylesheet version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:types="http://publication.tarjonta.sade.vm.fi/types"
                xmlns:xsd="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="types xsi xs fn"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template name="AOView_fi">

        <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>

        <div class="pagetitle">
            <h1><xsl:value-of select="types:Title"/></h1>
            <!-- koulutusohjelma listing here -->
            <!--<a href="#">${searchResult['LOSDegreeTitle']}</a>-->

            <xsl:variable name="id" select="types:Identifier"/>
            <div class="set-right">
                <div class="result-options set-right">
                    <div class="field-container-checkbox left-intend-2" style="display: inline-block">
                        <input type="checkbox" name="muistilistaan" id="">
                            <xsl:attribute name="value"><xsl:value-of select="$id"/></xsl:attribute>
                            <xsl:attribute name="id">muistilista_<xsl:value-of select="$id"/></xsl:attribute>
                        </input>
                        <label>
                            <xsl:attribute name="for">muistilista_<xsl:value-of select="$id"/></xsl:attribute>
                            tarjonta.lisaamuistilistaan
                        </label>
                    </div>

                    <div class="field-container-checkbox left-intend-2">
                        <input type="checkbox" name="vertailulistaan">
                            <xsl:attribute name="value"><xsl:value-of select="$id"/></xsl:attribute>
                            <xsl:attribute name="id">vertailulista_<xsd:value-of select="$id"/></xsl:attribute>
                        </input>
                        <label>
                            <xsl:attribute name="for">vertailulista_<xsl:value-of select="$id"/></xsl:attribute>
                            tarjonta.lisaavertailulistaan
                        </label>
                    </div>
                </div>
            </div>

        </div>

        <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
    </xsl:template>

</xsl:stylesheet>