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

<fmt:setBundle basename="form_messages" scope="session"/>
<haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
<table id="${element.id}" class="preference-sort">
    <tbody>
    <c:forEach var="child" items="${element.children}" varStatus="status">
        <tr>
            <td class="index">
                <span><haku:i18nText value="${child.i18nText}"/></span>
                <div class="sort-arrows">
                    <c:if test="${not status.first}">
                        <button class="up sort" data-id="${child.id}" data-target="${element.children[status.index - 1].id}"
                                type="button">
                            <span>
                                <span>^</span>
                            </span>
                        </button>
                        <br/>
                    </c:if>
                    <c:if test="${not status.last}">
                        <button class="down sort" data-id="${child.id}"
                                data-target="${element.children[status.index + 1].id}" type="button">
                            <span>
                                <span>v</span>
                            </span>
                        </button>
                    </c:if>
                </div>
            </td>
            <td>
                <c:set var="index" value="${status.count}" scope="request"/>
                <c:set var="sortableItem" value="${child}" scope="request"/>
                <c:set var="preferenceTable" value="${element}" scope="request"/>
                <jsp:include page="${sortableItem.type}.jsp"/>
                <c:set var="element" value="${preferenceTable}" scope="request"/>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- terveydentilavaatimukset -->
<div class="popup-dialog-wrapper" id="sora-popup">
    <span class="popup-dialog-close"></span>
    <div class="popup-dialog">
        <span class="popup-dialog-close">
            <fmt:message key="form.popup.sulje" />
        </span>
        <div class="popup-dialog-header">
            <h3>
                <fmt:message key="form.hakutoiveet.terveydentilavaatimukset.otsikko"/>
            </h3>
        </div>
        <div class="popup-dialog-content">
            <fmt:message key="form.hakutoiveet.terveydentilavaatimukset.sisalto"/>
            <button type="button" class="primary popup-dialog-close">
                <span>
                    <span>
                        <fmt:message key="form.popup.sulje" />
                    </span>
                </span>
            </button>
        </div>
    </div>
</div>

<script type="text/javascript">
    var prerequisiteStr = "${categoryData.pohjakoulutusvaatimus}";
    var prerequisiteArray = prerequisiteStr.split(',');

    var sortabletable_settings = {
        elementId: '<c:out value="${element.id}"/>',
        contextPath: '<c:out value="${pageContext.request.contextPath}"/>',
        applicationSystemId: '<c:out value="${it.applicationSystemId}"/>',
        vaiheId: '<c:out value="${vaihe.id}"/>',
        teemaId: '<c:out value="${parent.id}"/>',
        baseEducation: '<c:out value="${categoryData.POHJAKOULUTUS}"/>',
        vocational: '<c:out value="${categoryData.ammatillinenTutkintoSuoritettu}"/>',
        preferenceAndBaseEducationConflictMessage: '<fmt:message key="hakutoiveet.pohjakoulutusristiriita"/>',
        <c:if test="${fn:containsIgnoreCase(it.koulutusinformaatioBaseUrl, 'http') or fn:startsWith(it.koulutusinformaatioBaseUrl, '/')}">
            koulutusinformaatioBaseUrl: '<c:out value="${it.koulutusinformaatioBaseUrl}"/>'
        </c:if>
        <c:if test="${not fn:containsIgnoreCase(it.koulutusinformaatioBaseUrl, 'http') and not fn:startsWith(it.koulutusinformaatioBaseUrl, '/')}">
            koulutusinformaatioBaseUrl: location.protocol + '//<c:out value="${it.koulutusinformaatioBaseUrl}"/>'
        </c:if>
    }
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/preferencerow.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/preferencetable.js"></script>

