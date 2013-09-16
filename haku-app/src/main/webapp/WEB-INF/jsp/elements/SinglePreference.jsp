<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="form_messages" scope="session"/>
<haku:errorMessage id="${element.id}" additionalClass="margin-top-1"/>
<c:set value="${element.id}-Opetuspiste" var="opetuspiste" scope="page"/>
<c:set value="${element.id}-Opetuspiste-id" var="opetuspisteId" scope="page"/>
<c:set value="${element.id}-Koulutus" var="koulutus" scope="page"/>
<c:set value="${element.id}-Koulutus-id" var="koulutusId" scope="page"/>
<c:set value="${element.id}-Koulutus-educationDegree" var="educationDegree" scope="page"/>
<c:set value="${element.id}-Koulutus-id-lang" var="educationLang" scope="page"/>
<c:set value="${element.id}-Koulutus-id-sora" var="educationSora" scope="page"/>
<c:set value="${element.id}-Koulutus-id-aoIdentifier" var="aoIdentifier" scope="page"/>
<c:set value="${element.id}-Koulutus-id-athlete" var="educationAthlete" scope="page"/>


<table>
    <tbody>
        <tr>
            <td class="sublabel padding-top-3"><haku:i18nText value="${element.learningInstitutionLabel}"/></td>
            <td class="bold padding-top-3 padding-left-3">
                <c:out value="${categoryData[opetuspiste]}"/>
                <haku:errorMessage id="${opetuspiste}"/>
            </td>
        </tr>
        <tr>
            <td class="sublabel"><haku:i18nText value="${element.educationLabel}"/></td>
            <td class="bold padding-left-3">
                <c:out value="${categoryData[koulutus]}"/>
                <haku:errorMessage id="${koulutus}"/>
            </td>
        </tr>
    </tbody>
</table>
<haku:input-hidden id="${opetuspiste}" data="${categoryData}"/>
<haku:input-hidden id="${opetuspisteId}" data="${categoryData}"/>
<haku:input-hidden id="${koulutus}" data="${categoryData}"/>
<haku:input-hidden id="${koulutusId}" data="${categoryData}"/>
<haku:input-hidden id="${educationDegree}" data="${categoryData}"/>
<haku:input-hidden id="${educationLang}" data="${categoryData}"/>
<haku:input-hidden id="${educationSora}" data="${categoryData}"/>
<haku:input-hidden id="${aoIdentifier}" data="${categoryData}"/>
<haku:input-hidden id="${educationAthlete}" data="${categoryData}"/>

<div id="container-childLONames" class="notification block light-grey margin-2" style="display: none">
    <span><haku:i18nText value="${element.childLONameListLabel}"/>:&nbsp;</span>
    <span id="childLONames"></span>
</div>

<script type="text/javascript">

    var singlePreference_settings = {
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
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/javascript/singlepreference.js"></script>

<haku:viewChilds element="${element}"/>

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