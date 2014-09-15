<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<script src="${contextPath}/resources/javascript/virkailija/kelpoisuusLiitteet.js" type="text/javascript"></script>

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
            <b id="kesken-${ao.index}"  style="background-color: #000000; color: #ffffff; border-radius: 5px; padding: 5px; display: none">Kesken</b>
            <b id="hylatty-${ao.index}" style="background-color: #a9a9a9; color: #ffffff; border-radius: 5px; padding: 5px; display: none">Hylätty</b>
            <b id="valmis-${ao.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none">Valmis</b>
            <br>
            <br>
            <b id="kaikkiliitteet-${ao.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none">Kaikki liitteet saapuneet</b>
            <br>
            <br>
            <b id="tiedotarkistettu-${ao.index}" style="background-color: #feba00; color: #000000; border-radius: 5px; padding: 5px; display: none">Tiedot tarkistettu</b>
        </div>
        <div class="grid16-12 inline-block">
            <div class="grid16-16 inline-block">
                <b>${ao.name}</b> <br>
                    ${ao.opetuspiste}
            </div>
            <div class="grid16-16 inline-block">
                <br>
                <button class="button small primary" id="ls" >Tallenna</button>
                <%--<button class="button small" id="tt" onclick="ttt('${ao.index}')" >Tiedot tarkistettu</button>--%>
                <button class="button small" id="ls" onclick="kaikkiLiitteetSaapuneet('${ao.index}')" >Kaikki liitteet saapuneet</button>

            </div>

            <div class="grid16-16 inline-block">
                <br>
                <table class="virkailija-table-2" id="liitteet-table-${ao.index}">
                    <tr>
                        <td>
                            Hakukelpoisuus
                        </td>
                        <td>
                                <select class="width-12-11" id="hakukelpoisuus-select" onchange="hakuKelpoisuus(${ao.index})">
                                <option value="1">Kelpoisuus tarkistamatta</option>
                                <option value="2">Hakukelpoinen</option>
                                <option value="3">Ei hakukelpoinen</option>
                                <option value="4">Puuttelinen</option>
                            </select>

                        </td>
                        <td>
                            <select class="width-12-11" id="hakukelpoisuus-tietolahde" disabled onchange="tietoLahde(${ao.index})">
                                <option value="" default selected disabled>valitse tarkistettu tietolähde</option>
                                <option value="1">Oppilaitoksen toimittava tieto</option>
                                <option value="2">Alkuperäinen todistus</option>
                                <option value="3">Virallinen oikeaksi todistettu kopio</option>
                                <option value="4">Oikeaksi todistettu kopio</option>
                                <option value="5">Kopio</option>
                                <option value="6">Rekisteri</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>Hylkäämisen peruste</td>
                        <td colspan="2">
                            <textarea id="hylkaamisenperuste" rows="4" class="width-12-11" disabled onblur="hylkaamisenSyy(${ao.index})"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td><b>Liitteiden määrä 2 kpl</b></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>
                            <input type="checkbox" id="liitesaapunutCB1"> Liite saapunut liitexyz
                        </td>
                        <td>
                            <select class="width-12-11" id="liiteselect1" disabled>
                                <option value="" default selected disabled>valitse</option>
                                <option value="1">Saapunut</option>
                                <option value="2">Saapunut myöhässä</option>
                                <option value="3">Odottaa täydennystä</option>
                                <option value="4">Puutteellinen</option>
                                <option value="5">Ei tarkistettu</option>
                                <option value="6">Ei saapunut</option>
                                    <%--<option value="1">hyväksytty (rekisteri) </option>
                                    <option value="2">hyväksytty </option>
                                    <option value="3">hylätty</option>
                                    <option value="4">Pyydetty täydennystä</option>--%>
                            </select>
                        </td>
                        <td>
                                <%--<select class="width-12-11" id="liiteselectsyy1" disabled>
                                    <option value="" default selected>hylkäämisen syy</option>
                                    <option value="">Puuttuva pakollinen suoritus tai arvosana</option>
                                    <option value="">Riittämätön työkokemus</option>
                                    <option value="">Riittämätön kieli taito</option>
                                    <option value="">Muita ?</option>
                                </select>--%>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="checkbox" id="liitesaapunutCB2"> Liite saapunut liitexyz
                        </td>
                        <td>
                            <select class="width-12-11" id="liiteselect2" disabled>
                                <option value="1">Saapunut</option>
                                <option value="2">Saapunut myöhässä</option>
                                <option value="3">Odottaa täydennystä</option>
                                <option value="4">Puutteellinen</option>
                                <option value="5">Ei tarkistettu</option>
                                <option value="6">Ei saapunut</option>
                                    <%--<option value="" default selected>valitse</option>
                                    <option value="">hyväksytty (rekisteri) </option>
                                    <option value="">hyväksytty </option>
                                    <option value="">hylätty</option>
                                    <option value="">Pyydetty täydennystä</option>--%>
                            </select>
                        </td>
                        <td>
                                <%--<select class="width-12-11" id="liiteselectsyy2" disabled>
                                    <option value="" default selected>hylkäämisen syy</option>
                                    <option value="">Puuttuva pakollinen suoritus tai arvosana</option>
                                    <option value="">Riittämätön työkokemus</option>
                                    <option value="">Riittämätön kieli taito</option>
                                    <option value="">Muita ?</option>
                                </select>--%>
                        </td>
                    </tr>

                    <%--<tr>
                        <td>
                                &lt;%&ndash;<input type="checkbox">&ndash;%&gt; Kaikki liitteet saapuneet
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
                                &lt;%&ndash;<input type="checkbox"> &ndash;%&gt;Tiedot tarkistettu
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
                    </tr>--%>
                </table>
            </div>

            <div class="grid16-16 inline-block">
                <br>
                <button class="button small primary" id="ls" >Tallenna</button>
                <%--<button class="button small" id="tt" onclick="ttt('${ao.index}')" >Tiedot tarkistettu</button>--%>
                <button class="button small" id="ls" onclick="lss('${ao.index}')" >Kaikki liitteet saapuneet</button>

            </div>

        </div>
    </div>
    <div class="clear">
        <br>
    </div>
    <hr>
    <script type="text/javascript">
        var kelpoisuus_liitteet = {
            indx: '<c:out value="${ao.index}"/>',
            hakukelpoisuus: 1,
            tietolahde: '',
            hylkaamisperuste: ''/*,
            liiteet: [
                1: {
                   tila: 6
                },
                2: {
                    tila: 6
                }
            ]*/
        };
        hakutoiveet.push(kelpoisuus_liitteet);
    </script>
</c:forEach>


