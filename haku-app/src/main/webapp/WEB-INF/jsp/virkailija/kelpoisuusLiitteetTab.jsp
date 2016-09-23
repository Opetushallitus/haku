<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="virkailija" tagdir="/WEB-INF/tags/virkailija" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<fmt:setBundle basename="messages" scope="application"/>
<c:set var="preview" value="${it.preview}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="application" value="${it.application}" scope="request"/>
<c:set var="applicationSystem" value="${it.applicationSystem}" scope="request"/>
<c:set var="answers" value="${it.application.vastauksetMerged}" scope="request"/>
<c:set var="overridden" value="${it.application.overriddenAnswers}" scope="request" />
<c:set var="applicationMeta" value="${it.application.meta}" scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="errorMessages" value="${it.errorMessages}" scope="request"/>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<% pageContext.setAttribute("newLineEscaped", "\\n"); %>
<% pageContext.setAttribute("illegalUnicode1", "\u2028"); %>
<% pageContext.setAttribute("illegalUnicode2", "\u2029"); %>

<script src="${contextPath}/resources/javascript/virkailija/kelpoisuusLiitteet.js" type="text/javascript"></script>
<script src="${contextPath}/resources/javascript/underscore.string.min.js" type="text/javascript"></script>

<jsp:include page="../error/conflict.jsp"/>

<div class="grid16-16 inline-block hidden" id="error-kelpoisuus-liitteet">
    <h3 style="color: red">Tallennus ei onnistunut</h3>
</div>

<input type="button" class="button small primary disabled" id="btn-tallenna-kelpoisuus-liitteet"
       onclick="kjal.tallennaKelpoisuusJaLiitteet('${application.oid}', '${application.eligibilitiesAndAttachmentsUpdated}')" value="Tallenna" disabled />

<h3 id="kun">Kk-haut: Kelpoisuus ja liitteet</h3>
<hr>
<c:forEach var="hakukohde" items="${it.hakukohteet}">
    <form id="form-kelpoisuus-liitteet-${hakukohde.index}" method="post" novalidate="novalidate" class="block" >
        <div class="grid16-3 inline-block">
            <b>${hakukohde.index}.hakutoive</b>
            <br>
            <br>
            <b id="hylatty-${hakukohde.index}" style="background-color: #333333; color: #ffffff; border-radius: 5px; padding: 5px; display: none;" >Hylätty</b>
            <b id="hakukelpoinen-${hakukohde.index}" style="background-color: #438c48; color: #ffffff; border-radius: 5px; padding: 5px; display: none;" >Hakukelpoinen</b>
            <b id="puutteellinen-${hakukohde.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none;" >Puutteellinen</b>
            <br>
            <br>
            <b id="kaikkiliitteet-${hakukohde.index}" style="background-color: #438c48; color: #ffffff; border-radius: 5px; padding: 5px; display: none">Kaikki liitteet saapuneet</b>
            <br>
            <br>
            <b id="muuttunut-${hakukohde.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none;" >Muutettu</b>
            <b id="tallennettu-${hakukohde.index}" style="background-color: #438c48; color: #ffffff; border-radius: 5px; padding: 5px; display: none;" >Tallennettu</b>
            <br>
            <br>
            <input id="kaikki-tiedot-tarkistettu-${hakukohde.index}" type="checkbox" onchange="kjal.kaikkiTiedotTarkistettuCheckBox('${hakukohde.index}')" > <span style="font-weight: bold">Kaikki tiedot tarkistettu</span>

        </div>
        <div class="grid16-12 inline-block">
            <div class="grid16-16 inline-block">
                <div class="grid16-12">
                    <b>${hakukohde.name}</b> <br>
                        ${hakukohde.opetuspiste}
                </div>
                <div class="grid16-3">

                </div>

            </div>
            <div class="grid16-16 inline-block">
                <br>
                <input type="button" class="button small" id="btn-kaikki-liitteet-saapuneet-${hakukohde.index}" onclick="kjal.asetaKaikkiLiitteetSaapuneet('${hakukohde.index}')"  value="Kaikki liitteet saapuneet" disabled />
                <input type="button" class="button small disabled" id="btn-kaikki-liitteet-tarkastettu-${hakukohde.index}" onclick="kjal.asetaKaikkiLiitteetTarkastetuksi('${hakukohde.index}')" value="Kaikki liitteet tarkastettu" disabled/>
            </div>
            <div class="grid16-16 inline-block">
                <br>
                <table class="virkailija-table-2" id="liitteet-table-${hakukohde.index}">
                </table>
            </div>
        </div>
    </form>
    <div class="clear">
        <br>
    </div>
    <hr id="form-kelpoisuus-liitteet-hr-${hakukohde.index}" >

    <script type="text/javascript">
        var kelpoisuus_liitteet = {},
            aoGroupIds = [];
        <c:set var="aoGroups" value="preference${hakukohde.index}-Koulutus-id-ao-groups"/>
        <c:set var="aoGrAr" value="${fn:split(answers[aoGroups], ',')}"/>;
        <c:forEach var="aoGrId" items="${aoGrAr}">
            <c:if test="${fn:length(aoGrId) >0}" >
                aoGroupIds.push("<c:out value="${aoGrId}" />");
            </c:if>
        </c:forEach>
        kjal.LOGS('hakujohteen ', "<c:out value="${hakukohde.oid}"/>",' ryhmä idt: ', aoGroupIds);
        <c:forEach var="kelpoisuus" items="${application.preferenceEligibilities}">
            if ("<c:out value="${hakukohde.oid}"/>" === "<c:out value="${kelpoisuus.aoId}"/>") {
                kelpoisuus_liitteet.indx = "<c:out value="${hakukohde.index}"/>";
                kelpoisuus_liitteet.aoId = "<c:out value="${kelpoisuus.aoId}"/>";
                kelpoisuus_liitteet.status = "<c:out value="${kelpoisuus.status}"/>";
                kelpoisuus_liitteet.source = "<c:out value="${kelpoisuus.source}"/>";
                kelpoisuus_liitteet.rejectionBasis = _.str.unescapeHTML("<c:out value="${fn:replace(kelpoisuus.rejectionBasis, newLineChar, newLineEscaped )}" />");
                <c:forEach var="tiedotTarkistettu" items="${application.preferencesChecked}">
                    if ("<c:out value="${hakukohde.oid}"/>" === "<c:out value="${tiedotTarkistettu.preferenceAoOid}"/>") {
                        kelpoisuus_liitteet.preferencesChecked = "<c:out value="${tiedotTarkistettu.checked}"/>";
                    }
                </c:forEach>
                kelpoisuus_liitteet.attachments = [];

                <c:forEach var="liite" items="${application.attachmentRequests}" varStatus="liiteCount" >
                    if("<c:out value="${hakukohde.oid}"/>" === "<c:out value="${liite.preferenceAoId}"/>"
                        || _.contains(aoGroupIds, "<c:out value="${liite.preferenceAoGroupId}"/>")) {
                        var attachment = {};
                        attachment.id = "<c:out value="${liite.id}"/>";
                        attachment.aoId = "<c:out value="${liite.preferenceAoId}"/>";
                        attachment.aoGroupId = "<c:out value="${liite.preferenceAoGroupId}"/>";
                        attachment.receptionStatus = "<c:out value="${liite.receptionStatus}"/>";
                        attachment.name = "<haku:i18nText value="${liite.applicationAttachment.name}" />";
                        attachment.header = "<haku:i18nText value="${liite.applicationAttachment.header}" />";
                        attachment.processingStatus = "<c:out value="${liite.processingStatus}"/>";
                        <c:set var="desc" value="${fn:replace(liite.applicationAttachment.description, newLineChar, newLineEscaped)}"/>;
                        <c:set var="desc" value="${fn:replace(desc, illegalUnicode1, '')}"/>;
                        <c:set var="desc" value="${fn:replace(desc, illegalUnicode2, '')}"/>;
                        var desc = "<c:out value="${desc}"/>",
                            lng = 'fi=';
                        if (desc !== undefined && desc.length > 0 && (desc.match(lng) !== null) ) {
                            attachment.description = _.str.unescapeHTML(desc.split(lng)[1].split(',')[0]).replace(/<[^>]*>/g, '');
                        } else {
                            attachment.description = "";
                        }
                        kelpoisuus_liitteet.attachments.push(attachment);
                    }
                </c:forEach>
            }
        </c:forEach>
        kjal.LOGS('kelpoisuus ja liitteet objeckti: ', kelpoisuus_liitteet);
        hakutoiveet.push(kelpoisuus_liitteet);
        hakutoiveetCache.push(JSON.parse(JSON.stringify(kelpoisuus_liitteet)));

    </script>
</c:forEach>



