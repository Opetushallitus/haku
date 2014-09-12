<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<h3> Kelpoisuus ja liitteet</h3>
<hr>
<c:forEach var="ao" items="${it.hakukohteet}">
    <div id="form-${oid}-${ao.index}" method="post" novalidate="novalidate" class="block" >

    <div class="grid16-3 inline-block">
        <b>${ao.index}.hakutoive</b>
    </div>
    <div class="grid16-12 inline-block">
        <b>${ao.name}</b> <br>
            ${ao.opetuspiste}
    </div>
        <div class="grid16-3 inline-block">
            <br>
        </div>
        <div class="grid16-12 inline-block">
            <br>
            <button class="btn" id="tt" >Tiedot tarkistettu</button>
            <button class="btn" id="ls" >Liitteet saapuneet</button>
        </div>

    <div class="grid16-3 inline-block">
        <br>
        <br>
        <b id="kesken"  style="background-color: #000000; color: #ffffff; border-radius: 5px; padding: 5px; display: none">Kesken</b>
        <b id="hylatty" style="background-color: #a9a9a9; color: #ffffff; border-radius: 5px; padding: 5px; display: none">Hylätty</b>
        <b id="valmis" style="background-color: #dfd52e; color: #000000; border-radius: 5px; padding: 5px; display: none">Valmis</b>
        <br>
        <br>
        <b id="kaikkiliitteet" style="background-color: greenyellow; color: #000000; border-radius: 5px; padding: 5px; display: none">Kaikki liitteet saapuneet</b>
        <br>
        <br>
        <b id="tiedotarkistettu" style="background-color: firebrick; color: #000000; border-radius: 5px; padding: 5px; display: none">Tiedot tarkistettu</b>

            <%--<button style="vertical-align: bottom;">Tallenna</button>--%>
    </div>

    <div class="grid16-9 inline-block">
        <table class="virkailija-table-2">
            <tr>
                <th></th>
                <th></th>
                <th></th>
            </tr>
            <tr>
                <td>
                    <%--<input type="checkbox" disabled id="hakukelpoisuus-checkbox-${oid}-${ao.index}" > Hakukelpoisuus--%>
                    Hakukelpoisuus
                    <%--<input type="checkbox" id="hakukelpoisuus-checkbox" > Hakukelpoisuus--%>
                </td>
                <td>
                    <%--<select class="width-12-11" id="hakukelpoisuus-select" >--%>
                    <select class="width-12-11" id="hakukelpoisuus-select" onchange="hakuKelpoisuus()">
                        <option value="" default selected disabled> valitse tila </option>
                        <option value="1">Kelpoisuus tarkistamatta</option>
                        <option value="2">Hakukelpoinen</option>
                        <option value="3">Ei hakukelpoinen</option>
                        <option value="4">Puuttelinen</option>
                    </select>

                </td>
                <td>
                    <select class="width-12-11" id="hakukelpoisuus-tietolahde" disabled>
                        <option value="" default selected disabled>valitse tarkistettu tietolähde</option>
                        <option value="">Oppilaitoksen toimittava tieto</option>
                        <option value="">Alkuperäinen todistus</option>
                        <option value="">Virallinen oikeaksi todistettu kopio</option>
                        <option value="">Oikeaksi todistettu kopio</option>
                        <option value="">Kopio</option>
                        <option value="">Rekisteri</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Hylkäämisen peruste</td>
                <td colspan="2">
                    <textarea id="hylkaamisenperuste" rows="4" class="width-12-11" disabled></textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <input type="checkbox" id="liitesaapunutCB1"> Liite saapunut liitexyz
                </td>
                <td>
                    <select class="width-12-11" id="liiteselect1" disabled>
                        <option value="" default selected disabled>valitse</option>
                        <option value="1">hyväksytty (rekisteri) </option>
                        <option value="2">hyväksytty </option>
                        <option value="3">hylätty</option>
                        <option value="4">Pyydetty täydennystä</option>
                    </select>
                </td>
                <td>
                    <select class="width-12-11" id="liiteselectsyy1" disabled>
                        <option value="" default selected>hylkäämisen syy</option>
                        <option value="">Puuttuva pakollinen suoritus tai arvosana</option>
                        <option value="">Riittämätön työkokemus</option>
                        <option value="">Riittämätön kieli taito</option>
                        <option value="">Muita ?</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <input type="checkbox" id="liitesaapunutCB2"> Liite saapunut liitexyz
                </td>
                <td>
                    <select class="width-12-11" id="liiteselect2" disabled>
                        <option value="" default selected>valitse</option>
                        <option value="">hyväksytty (rekisteri) </option>
                        <option value="">hyväksytty </option>
                        <option value="">hylätty</option>
                        <option value="">Pyydetty täydennystä</option>
                    </select>
                </td>
                <td>
                    <select class="width-12-11" id="liiteselectsyy2" disabled>
                        <option value="" default selected>hylkäämisen syy</option>
                        <option value="">Puuttuva pakollinen suoritus tai arvosana</option>
                        <option value="">Riittämätön työkokemus</option>
                        <option value="">Riittämätön kieli taito</option>
                        <option value="">Muita ?</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>
                    <input type="checkbox"> Kaikki liitteet saapuneet
                </td>
                <td>
                    <select class="width-12-11">
                        <option value="" default selected>valiste</option>
                        <option value="">Kyllä</option>
                        <option value="">Ei</option>
                    </select>
                </td>
                <td>
                </td>
            </tr>
            <tr>
                <td>
                    <input type="checkbox"> Tiedot tarkistettu
                </td>
                <td>
                    <select class="width-12-11">
                        <option value="" default selected>valitse</option>
                        <option value="">Kyllä</option>
                        <option value="">Ei</option>
                    </select>
                </td>
                <td>
                </td>
            </tr>
        </table>
    </div>

    </div>
    <div class="clear">
        <br>
    </div>
    <hr>
</c:forEach>


