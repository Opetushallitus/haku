<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
<div id="orgsearch" class="">
    <a href="#" class="expander">
        <span class="labelpos">
            <span class="label"><fmt:message key="virkailija.org.organisaatiohaku"/></span>
        </span>
    </a>

    <div class="expanded-content" style="">

        <form id="orgsearchform" class="orgsearchform" style="">
            <fieldset>

                <div class="field-search-containerbox">
                    <input type="text" value="${it.searchString}" name="searchString" class="text search"
                           placeholder="<fmt:message key="virkailija.org.hakuehto"/>"/>
                </div>

                <div class="field-select-containerbox">
                    <select name="organizationType">
                        <option value="" disabled selected><fmt:message
                                key="virkailija.org.valitse.organisaatiotyyppi"/></option>
                        <c:forEach var="option" items="${it.organizationTypes}">
                            <option value="${option.value}"><haku:i18nText value="${option.i18nText}"/></option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field-select-containerbox">
                    <select name="learningInstitutionType">
                        <option value="" disabled selected><fmt:message
                                key="virkailija.org.valitse.oppilaitostyyppi"/></option>
                        <c:forEach var="option" items="${it.learningInstitutionTypes}">
                            <option value="${option.value}"><haku:i18nText value="${option.i18nText}"/></option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field-container-checkbox">
                    <input type="checkbox" name="includePassive"
                           id="osc1" ${it.includePassive ? 'checked="checked"' : ''}
                           value="${not it.includePassive}"/>
                    <label for="osc1"><fmt:message key="virkailija.org.nayta.lakkautetut"/></label>
                </div>

                <div class="field-container-checkbox">
                    <input type="checkbox" name="includePlanned"
                           id="osc2" ${it.includePlanned ? 'checked="checked"' : ''}
                           value="${not it.includePlanned}"/>
                    <label for="osc2"><fmt:message key="virkailija.org.nayta.suunnitellut"/></label>
                </div>

                <div class="buttons">
                    <button id="reset-organizations" class="button small" type="reset"><fmt:message
                            key="virkailija.org.tyhjenna"/></button>
                    <button id="search-organizations" class="button primary small"><fmt:message
                            key="virkailija.org.hae"/></button>
                </div>

            </fieldset>
        </form>

        <div class="orgsearchlist" id="orgsearchlist">
        </div>
    </div>
</div>

