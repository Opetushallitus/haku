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
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<section id="searchSection">
    <form id="searchform">
        <input type="hidden" id="lopoid" name="lopoid">

        <h3 id="lop-title"></h3>

        <div class="grid16-16">
            <table class="form-layout-table width-100">
                <tr>
                    <td>
                        <label for="entry"><fmt:message key="virkailija.hakemus.hae.hakemuksia"/></label>

                        <div class="field-search-containerbox">
                            <input type="text" id="entry" name="entry" class="search width-60" placeholder=""/>
                        </div>
                        <small><fmt:message key="virkailija.hakemus.hae.otsikko"/></small>
                    </td>
                    <td>
                        <script type="text/javascript">
                            hakukausiDefaultYear = '<c:out value="${it.defaultYear}"/>';
                            hakukausiDefaultSemester = '<c:out value="${it.defaultSemester}"/>';
                        </script>
                        <label for="hakukausi"><fmt:message key="virkailija.hakemus.hakukausi"/></label>
                        <div class="field-search-containerbox">
                            <select id="hakukausi" name="hakukausi">
                                <option value="">&nbsp;</option>
                                <c:forEach var="option" items="${it.hakukausiOptions}">
                                    <option value="${option.value}" <c:if test="${option.value == it.defaultSemester}">selected="selected"</c:if> ><haku:i18nText value="${option.i18nText}"/>&nbsp;</option>
                                </c:forEach>
                            </select>
                            <input type="text" id="hakukausiVuosi" name="hakukausiVuosi" value="${it.defaultYear}"/>
                        </div>
                    </td>
                    <td>
                        <label for="sendingSchool"><fmt:message key="virkailija.hakemus.lahtokoulu"/></label>
                        <div class="field-search-containerbox">
                            <input id="sendingSchool" type="text" name="sendingSchool" />
                            <input id="sendingClass" type="text" name="sendingClass" />
                            <input id="sendingSchoolOid" type="hidden" />
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="application-system"><fmt:message key="virkailija.hakemus.haku"/></label>
                        <div class="field-search-containerbox">
                            <select id="application-system">
                            </select>
                        </div>
                    </td>
                    <td>
                        <label for="application-state"><fmt:message
                                key="virkailija.hakemus.hakemuksen.tila.otsikko"/></label>

                        <div class="field-select-containerbox">
                            <select class="width-50" id="application-state" name="application-state">
                                <option value=""><fmt:message key="virkailija.hakemus.tila.kaikki"/></option>
                                <option selected="selected" value="ACTIVE"><fmt:message
                                        key="virkailija.hakemus.tila.voimassa"/></option>
                                <option value="PASSIVE"><fmt:message key="virkailija.hakemus.tila.peruttu"/></option>
                                <option value="INCOMPLETE"><fmt:message
                                        key="virkailija.hakemus.tila.puutteellinen"/></option>
                                <option value="NOT_IDENTIFIED"><fmt:message
                                        key="virkailija.hakemus.tila.yksiloimatta"/></option>
                            </select>
                        </div>
                        <div class="field-search-containerbox">
                            <input type="checkbox" id="discretionary-only"/>
                            <label for="discretionary-only">Näytä vain harkinnanvaraisesti hakeneet</label>
                        </div>
                    </td>
                    <td>
                        <div id="search-spinner" style="display: none">
                            <p>Haetaan<br />hakemuksia...</p>
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

                    <td>

                    </td>

                    <td class="padding-top-4">
                        <input id="reset-search" class="button secondary small" type="button"
                               value="<fmt:message key="virkailija.hakemus.tyhjenna"/>"/>
                        <input id="search-applications" class="button primary small" type="submit"
                               value="<fmt:message key="virkailija.hakemus.hae"/>"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                </tr>
            </table>
        </div>
    </form>
    <form method="POST" id="open-applications"
            action="${pageContext.request.contextPath}/virkailija/hakemus/multiple">
        <input type="hidden" name="applicationList" id="applicationList" />
        <input type="hidden" name="selectedApplication" id="selectedApplication" />
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
            <div class="clear"></div>

            <a href="#" id="open-application"
               class="button secondary small"><fmt:message key="virkailija.hakemus.avaa"/></a>
            <a href="#" id="create-application" data-po-show="createApplication"
               class="button secondary small"><fmt:message key="virkailija.hakemus.syota"/></a>

            <table id="application-table" class="virkailija-table-1">
                <thead>
                <tr>
                    <td id="application-table-header-checkbox" class="sorted-not"><input type="checkbox" id="check-all-applications"/></td>
                    <td id="application-table-header-fullName" class="sorted-not"><fmt:message key="virkailija.hakemus.nimi"/></td>
                    <td id="application-table-header-ssn" class="sorted-not"><fmt:message key="virkailija.hakemus.henkilotunnus"/></td>
                    <td id="application-table-header-applicationOid" class="sorted-not"><fmt:message key="virkailija.hakemus.hakemusnro"/></td>
                    <td id="application-table-header-state" class="sorted-not"><fmt:message key="virkailija.hakemus.hakemuksen.tila"/></td>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <div class="clear"></div>
            <div id="pagination"/>
        </section>
    </div>
</section>


