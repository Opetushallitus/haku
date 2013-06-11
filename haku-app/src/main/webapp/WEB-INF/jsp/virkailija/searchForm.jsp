<%--
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
  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<section id="searchSection">
    <form id="searchform">
        <input type="hidden" id="lopOid" name="lopoid">

        <div class="grid16-16">
            <table class="form-layout-table width-100">
                <tr>
                    <td>
                        <label for="application-state"><fmt:message key="virkailija.hakemus.hae.hakemuksia"/></label>

                        <div class="field-search-containerbox">
                            <input type="text" id="entry" class="search width-60" placeholder=""/>
                        </div>
                        <small><fmt:message key="virkailija.hakemus.hae.otsikko"/></small>
                    </td>
                    <td>
                        <label for="application-state"><fmt:message
                                key="virkailija.hakemus.hakemuksen.tila.otsikko"/></label>

                        <div class="field-select-containerbox">
                            <select class="width-50" id="application-state" ng-model="applicationState">
                                <option value=""><fmt:message key="virkailija.hakemus.tila.kaikki"/></option>
                                <option selected="selected" value="ACTIVE"><fmt:message key="virkailija.hakemus.tila.voimassa"/></option>
                                <option value="PASSIVE"><fmt:message key="virkailija.hakemus.tila.peruttu"/></option>
                                <option value="INCOMPLETE"><fmt:message
                                        key="virkailija.hakemus.tila.puutteellinen"/></option>
                            </select>
                        </div>
                    </td>
                </tr>

                <tr>
                    <td>
                        <label class="block" for="application-preference"><fmt:message
                                key="virkailija.hakemus.hakukohde.otsikko"/></label>

                        <div class="field-text-containerbox">
                            <input class="width-60" type="text" id="application-preference"
                                   placeholder="<fmt:message key="virkailija.hakemus.hakukohde.otsikko.kentta"/>"/>
                        </div>
                    </td>

                    <td style="vertical-align:bottom;">
                        <input id="reset-search" class="button secondary small" type="button"
                               value="<fmt:message key="virkailija.hakemus.tyhjenna"/>" style="margin-bottom:1px;"/>
                        <input id="search-applications" class="button primary small" type="submit"
                               value="<fmt:message key="virkailija.hakemus.hae"/>" style="margin-bottom:1px;"/>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</section>
<section class="grid16-16 margin-top-2">
    <div class="tabs">
        <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemukset" class="tab current"><span
                id="application-tab-label"><fmt:message key="virkailija.hakemus.hakutulos.hakemukset"/> 0</span></a>
    </div>
    <div class="tabsheets">
        <section id="hakemukset" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemukset"
                 style="display: block">

            <div class="clear"></div>

            <span><fmt:message key="virkailija.hakemus.hakutulos"/><span id="resultcount">0</span> <fmt:message
                    key="virkailija.hakemus.hakutulos.osumaa"/></span>

            <table id="application-table" class="virkailija-table-1">
                <thead>
                <tr>
                    <td></td>
                    <td><fmt:message key="virkailija.hakemus.sukunimi"/></td>
                    <td><fmt:message key="virkailija.hakemus.etunimi"/></td>
                    <td><fmt:message key="virkailija.hakemus.henkilotunnus"/></td>
                    <td><fmt:message key="virkailija.hakemus.hakemusnro"/></td>
                    <td><fmt:message key="virkailija.hakemus.hakemuksen.tila"/></td>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <div class="clear"></div>
        </section>
    </div>
</section>


