<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>



<h3>Kk-haut: Kelpoisuus ja liitteet</h3>
<script type="text/javascript">
    console.log('hakutoiveet');
    var hakutoiveet = [],
        hakutoiveetCache = [];

</script>
<hr>
<c:forEach var="hakukohde" items="${it.hakukohteet}">
    <form id="form-kelpoisuus-liitteet-${hakukohde.index}" method="post" action="saveKelpoisuusLiitteet/${hakukohde.oid}" novalidate="novalidate" class="block" >

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
            <b id="muuttunut-${hakukohde.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none;" >Muuttettu</b>
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
                <input type="button" class="button small primary disabled" id="btn-tallenna-kelpoisuus-liitteet-${hakukohde.index}" onclick="kjal.tallennaKelpoisuusJaLiitteet('${hakukohde.index}')" value="Tallenna" />
                <input type="button" class="button small" id="btn-kaikki-liitteet-saapuneet-${hakukohde.index}" onclick="kjal.kaikkiLiitteetSaapuneet('${hakukohde.index}')"  value="Kaikki liitteet saapuneet" />
                <input type="button" class="button small disabled" id="btn-kaikki-liitteet-tarkastettu-${hakukohde.index}" onclick="kjal.asetaKaikkiLiitteetTarkastetuksi('${hakukohde.index}')" value="Kaikki liitteet tarkastettu" />
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
    <hr>

    <script type="text/javascript">
        var kelpoisuus_liitteet = {};
        <c:forEach var="kelpoisuus" items="${application.preferenceEligabilities}" />
            if ("<c:out value="${hakukohde.oid}"/>" === "<c:out value="${kelpoisuus.aoId}"/>") {
                kelpoisuus_liitteet.indx = "<c:out value="${hakukohde.index}"/>";
                kelpoisuus_liitteet.aoId = "<c:out value="${kelpoisuus.aoId}"/>";
                kelpoisuus_liitteet.aoId = "<c:out value="${kelpoisuus.status}"/>";
                kelpoisuus_liitteet.source = "<c:out value="${kelpoisuus.source}"/>";
                kelpoisuus_liitteet.rejectionBasis = "<c:out value="${kelpoisuus.rejectionBasis}"/>";
                kelpoisuus_liitteet.preferencesChecked = false
                kelpoisuus_liitteet.attachments = [];
                <c:forEach var="liite" items="${application.attachmentRequests}" varStatus="liiteCount" >
                    if("<c:out value="${hakukohde.oid}"/>" === "<c:out value="${liite.aoId}"/>") {
                        var attachment = {};
                        attachment.id = "<c:out value="${liiteCount.count}"/>"; //TODO: vaihda tämä <--
                        attachment.aoGroupId = "<c:out value="${liite.aoGroupId}"/>"
                        attachment.receptionStatus = "<c:out value="${liite.receptionStatus}"/>";
                        attachment.name = "<haku:i18nText value="${liite.applicationAttachment.name}"/>";
                        attachment.header = "<haku:i18nText value="${liite.applicationAttachment.header}"/>";
                        attachment.processingStatus = "<c:out value="${liite.processingStatus}"/>";
                        kelpoisuus_liitteet.attachments.push(attachment);
                    }
                </c:forEach>
            }
        </c:forEach>
        console.log('--->....---: ',kelpoisuus_liitteet);
        hakutoiveet.push(kelpoisuus_liitteet);
        hakutoiveetCache.push(JSON.parse(JSON.stringify(kelpoisuus_liitteet)));

    </script>
</c:forEach>
<script src="${contextPath}/resources/javascript/virkailija/kelpoisuusLiitteet.js" type="text/javascript"></script>


