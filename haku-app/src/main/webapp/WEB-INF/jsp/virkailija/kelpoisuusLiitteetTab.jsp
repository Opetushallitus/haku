<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>



<h3> Kelpoisuus ja liitteet</h3>
<script type="text/javascript">
    console.log('hakutoiveet');
    var hakutoiveet = [];

</script>
<hr>
<c:forEach var="ao" items="${it.hakukohteet}">
    <div id="form-kelpoisuus-liitteet-${ao.index}" method="post" novalidate="novalidate" class="block" >

        <div class="grid16-3 inline-block">
            <b>${ao.index}.hakutoive</b>
            <br>
            <br>
            <b id="kesken-${ao.index}"  style="background-color: #000000; color: #ffffff; border-radius: 5px; padding: 5px; display: none;" >Kesken</b>
            <b id="hylatty-${ao.index}" style="background-color: #333333; color: #ffffff; border-radius: 5px; padding: 5px; display: none;" >Hylätty</b>
            <b id="valmis-${ao.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none;" >Valmis</b>
            <br>
            <br>
            <b id="kaikkiliitteet-${ao.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none">Kaikki liitteet saapuneet</b>
            <br>
            <br>
            <b id="muuttunut-${ao.index}" style="background-color: #d84a2a; color: #000000; border-radius: 5px; padding: 5px; " >Muuttettu</b>
            <b id="tallennettu-${ao.index}" style="background-color: #156c18; color: #000000; border-radius: 5px; padding: 5px; " >Tallennettu</b>
        </div>
        <div class="grid16-12 inline-block">
            <div class="grid16-16 inline-block">
                <b>${ao.name}</b> <br>
                    ${ao.opetuspiste}
            </div>
            <div class="grid16-16 inline-block">
                <br>
                <button class="button small primary" >Tallenna</button>
                <button class="button small" id="btn-kaikki-liitteet-saapuneet-${ao.index}" onclick="kjal.kaikkiLiitteetSaapuneet('${ao.index}')" >Kaikki liitteet saapuneet</button>
                <button class="button small disabled" id="btn-kaikki-liitteet-tarkastettu-${ao.index}" onclick="kjal.asetaKaikkiLiitteetTarkastetuksi('${ao.index}')" >Kaikki liitteet tarkastettu</button>
            </div>

            <div class="grid16-16 inline-block">
                <br>
                <table class="virkailija-table-2" id="liitteet-table-${ao.index}">
                </table>
            </div>
        </div>
    </div>
    <div class="clear">
        <br>
    </div>
    <hr>
    <script type="text/javascript">
        var kelpoisuus_liitteet = {
            indx: "<c:out value="${ao.index}"/>",
            hakukelpoisuus: "02",
            tietolahde: "03",
            hylkaamisperuste: "",
            liitteet: [
                {
                   id: 1,
                   nimi: "Tässä liite 1",
                   tila: "01",
                   liitteentila: "02"
                },
                {
                    id: 2,
                    nimi: "Tässä liite 2",
                    tila: "02",
                    liitteentila: "03"
                },
                {
                    id: 3,
                    nimi: "Tässä liite 3",
                    tila: "03",
                    liitteentila: "01"
                }
            ]
        };
        hakutoiveet.push(kelpoisuus_liitteet);
    </script>
</c:forEach>
<script src="${contextPath}/resources/javascript/virkailija/kelpoisuusLiitteet.js" type="text/javascript"></script>


