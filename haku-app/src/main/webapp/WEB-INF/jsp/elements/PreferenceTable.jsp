<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<c:set var="useDropdown" value="${element.getUseDropdownForLearningInstitution()}" scope="request"/>
<haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
<table id="${element.id}" class="preference-sort">
    <tbody>
    <c:set var="maxPreferences" value="${fn:length(element.children)}" />
    <c:choose>
        <c:when test="${empty answers['preferencesVisible']}">
            <c:set var="preferencesVisible" value="${element.preferencesInitiallyVisible}" />
        </c:when>
        <c:otherwise>
            <c:set var="preferencesVisible" value="${answers['preferencesVisible']}" />
        </c:otherwise>
    </c:choose>
    <input type="hidden" name="preferencesVisible" id="preferencesVisible" value="${preferencesVisible}" />
    <c:forEach var="child" items="${element.children}" varStatus="status">
        <c:set value="${child.id}-Koulutus-id" var="selectHiddenInputId" scope="page"/>
        <c:choose>
            <c:when test="${status.index lt preferencesVisible}">
                <tr>
                <c:set var="lastVisible" value="${status.index}" />
            </c:when>
            <c:otherwise>
                <tr style="display: none;">
            </c:otherwise>
        </c:choose>
            <c:if test="${element.usePriorities}">
                <td class="index">
                        <span>${status.index + 1}.</span>
                        <div class="sort-arrows">
                            <c:if test="${not status.first}">
                                <button aria-label="<fmt:message key="hakutoiveet.sort.up.button.label"/>" class="up sort" data-id="${child.id}" data-target="${element.children[status.index - 1].id}"
                                        type="button">
                                    <span>
                                        <span>^</span>
                                    </span>
                                </button>
                                <br/>
                            </c:if>
                            <c:if test="${status.index lt preferencesVisible - 1}">
                                <button aria-label="<fmt:message key="hakutoiveet.sort.down.button.label"/>" class="down sort" data-id="${child.id}"
                                        data-target="${element.children[status.index + 1].id}" type="button">
                                    <span>
                                        <span>v</span>
                                    </span>
                                </button>
                            </c:if>
                        </div>
                </td>
            </c:if>
            <td>
                <c:set var="index" value="${status.count}" scope="request"/>
                <c:set var="sortableItem" value="${child}" scope="request"/>
                <c:set var="preferenceTable" value="${element}" scope="request"/>
                <jsp:include page="${sortableItem.type}.jsp"/>
                <c:set var="element" value="${preferenceTable}" scope="request"/>
            </td>
        </tr>

    </c:forEach>
        <tr>

            <c:choose>
                <c:when test="${element.usePriorities}">
                    <td colspan="2">
                </c:when>
                <c:otherwise>
                    <td>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${maxPreferences gt preferencesVisible}">
                        <button id="add-preference" class="primary" type="button">
                            <span>
                                <span><fmt:message key="hakutoiveet.lisaakohde"/></span>
                            </span>
                        </button>
                    </td>
                </c:when>
                <c:otherwise>
                        <div class="notification info">
                            <fmt:message key="hakutoiveet.hakukohteidenmaksimimaara"/>&nbsp;${maxPreferences}
                        </div>
                </c:otherwise>
            </c:choose>
            </td>
        </tr>
    </tbody>
</table>

<!-- terveydentilavaatimukset -->
<div class="popup-dialog-wrapper" id="sora-popup">
    <span class="popup-dialog-close"></span>

    <div class="popup-dialog">
        <span class="popup-dialog-close">
            <fmt:message key="popup.sulje"/>
        </span>

        <div id="sora-popup_header" class="popup-dialog-header">
            <h3>
                <fmt:message key="hakutoiveet.terveydentilavaatimukset.otsikko"/>
            </h3>
        </div>
        <div class="popup-dialog-content">
            <fmt:message key="hakutoiveet.terveydentilavaatimukset.sisalto"/>
            <button aria-labelledby="sora-popup_header sora-popup_sulje" type="button" class="primary popup-dialog-close">
                <span>
                    <span id="sora-popup_sulje">
                        <fmt:message key="popup.sulje"/>
                    </span>
                </span>
            </button>
        </div>
    </div>
</div>

<script type="text/javascript">
    var prerequisiteStr = "${answers.pohjakoulutusvaatimus}";
    var prerequisiteArray = prerequisiteStr.split(',');

    var sortabletable_settings = {
        elementId: '<c:out value="${element.id}"/>',
        applicationSystemId: '<c:out value="${it.applicationSystemId}"/>',
        ongoing: '<c:out value="${it.ongoing}"/>',
        vaiheId: '<c:out value="${vaihe.id}"/>',
        teemaId: '<c:out value="${parent.id}"/>',
        <c:choose>
            <c:when test="${virkailijaEdit}">
        uiLang: '<c:out value="${answers._meta_filingLanguage}"/>',
            </c:when>
            <c:otherwise>
        uiLang: '<c:out value="${requestScope['fi_vm_sade_oppija_language']}"/>',
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${it.baseEducationDoesNotRestrictApplicationOptions}">
            </c:when>
            <c:otherwise>
                baseEducation: '<c:out value="${answers.POHJAKOULUTUS}"/>',
                <c:choose>
                    <c:when test="${answers.ammatillinenTutkintoSuoritettu}">
                        vocational : false,
                    </c:when>
                    <c:otherwise>
                        vocational : true,
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
        preferenceAndBaseEducationConflictMessage: '<fmt:message key="hakutoiveet.pohjakoulutusristiriita"/>'
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/preferencerow.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/javascript/preferencetable.js"></script>

