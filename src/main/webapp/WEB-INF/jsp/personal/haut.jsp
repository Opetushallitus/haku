<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="../top/top.jsp"/>
    <section class="content-container">
        <div class="grid16-4">
            <nav class="subnavigation">
                <span class="menu-level-2-heading">Henkilökohtainen palvelu</span>
                <ul class="menu-level-2">
                    <li><a href="#">OmaOpas</a></li>
                    <li><a href="#">Omat viestit</a></li>
                    <li><a href="#" class="current">Haut koulutuksiin</a></li>
                    <li><a href="#">Omat tiedot</a></li>
                    <li><a href="#">Omat suoritukset</a></li>
                    <li><a href="#">Omat muistiinpanot</a></li>
                </ul>
            </nav>
        </div>

        <div class="grid16-12">
            <h1>Ajankohtaiset hakemukset (${hakemusListSize} kpl)</h1>

            <c:forEach var="hakemusInfo" items="${hakemusList}">
            <c:if test="${hakemusInfo.applicationPeriod.active}">
            <div class="application-options-info">
                 <a href="/haku/lomake/${hakemusInfo.applicationPeriod.id}/${hakemusInfo.form.id}">${hakemusInfo.form.title}</a>
                <small>Haku päättyy ${hakemusInfo.applicationPeriod.daysUntilEnd} päivän kuluttua (<fmt:formatDate pattern="dd.MM.yyyy" value="${hakemusInfo.applicationPeriod.end}" />)</small>
                <form action="/haku/lomake/${hakemusInfo.applicationPeriod.id}/${hakemusInfo.form.id}/esikatselu">
                <button type="submit" class="edit set-right">
                    <span><span>Muokkaa</span></span>
                </button>

                </form>
            </div>
            <div class="clear"></div>
            <div class="application-options">
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <td>Hakutoive</td>
                                <td>Opetuspiste</td>
                                <td>Koulutus</td>
                                <td>Kieli</td>
                                <td>Alkaa</td>
                                <td>Valintakoe</td>
                                <td>Terveydentilavaatimukset</td>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="preference" items="${hakemusInfo.preferences}">
                            <tr>
                                <td>${preference.order}</td>
                                <td>${preference.opetusPiste}</td>
                                <td>${preference.koulutus}</td>
                                <td>Suomi</td>
                                <td>Syksy 2013</td>
                                <td>Ejjole</td>
                                <td>OK</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="5">
                                    <button><span><span>Siirrä valitut hakulomakkeelle</span></span></button>
                                    <button class="link">Etsi lisää koulutuksia</button><br/>
                                    <small>
                                        Huomio. Voit hakea kerralla vain 6 yliopistokoulutukseen.<br/>
                                        Huomio. Kahdessa koulutuksessa on valintakokeet samaan aikaan.
                                    </small>

                                </td>
                            <tr>
                        </tfoot>
                    </table>
                </div>
            </div>
            </c:if>
            </c:forEach>

        </div>
        <div class="clear"></div>
    </section>

</section>

<footer id="sitefooter">

    <div class="footer-container">

        <div class="grid16-4">
            <div class="grid-container">
                Leipäteksti #000 line-height: 1.8em font-size: 1.3em (body font-size: 62.5% = 10px) Lorem ipsum dolor
                sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna
                aliquam erat volutpat. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh
                euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
                consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam
                erat volutpat.
            </div>
        </div>

        <div class="grid16-12">
            <div class="grid16-4 ">
                <div class="grid-container">
                    <h3> 1st column - Lorem ipsum</h3>

                    <p> Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod
                        tincidunt ut laoreet dolore magna aliquam erat volutpat. </p>
                </div>
            </div>
            <div class="grid16-4">
                <div class="grid-container">
                    <h3>2nd column - Lorem ipsum </h3>

                    <p> Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod
                        tincidunt ut laoreet dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
                        consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna
                        aliquam erat volutpat. </p>
                </div>
            </div>
            <div class="grid16-4">
                <div class="grid-container">
                    <h3> 3st column - Lorem ipsum</h3>

                    <p> Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod
                        tincidunt ut laoreet dolore magna aliquam erat volutpat. </p>
                </div>
            </div>
            <div class="grid16-4">
                <div class="grid-container">
                    <h3>4nd column - Lorem ipsum </h3>

                    <p> Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod
                        tincidunt ut laoreet dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
                        consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna
                        aliquam erat volutpat. </p>
                </div>
            </div>
            <div class="grid16-8 footer-logo">
                <img src="content/logo-opetus-ja-kulttuuriministerio.png">
            </div>
            <div class="grid16-8 footer-logo">
                <img src="content/logo-oph_fin_vaaka.png">
            </div>
        </div>

        <div class="clear"></div>

    </div>
</footer>
</div>
</div>
</body>
</html>