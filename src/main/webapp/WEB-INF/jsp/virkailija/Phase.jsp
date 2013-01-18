<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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

<!DOCTYPE html>
<c:set var="vaihe" value="${it.element}" scope="request"/>
<c:set var="form" value="${it.form}" scope="request"/>
<c:set var="oid" value="${it.oid}" scope="request"/>
<c:set var="applicationPhaseId" value="${it.applicationPhaseId}" scope="request"/>
<c:set var="applicationProcessState" value="${it.applicationProcessState}" scope="request"/>
<c:set var="element" value="${it.element}" scope="request"/>
<c:set var="hakemusId" value="${it.hakemusId}" scope="request"/>
<c:set var="categoryData" value="${it.categoryData}" scope="request"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta charset="utf-8"/>
    <!--
        <link rel="stylesheet" href="${contextPath}/resources/css/screen.css" type="text/css">
        -->
    <link rel="stylesheet"
          href="${contextPath}/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css"
          type="text/css">
    <link href="${contextPath}/resources/css/virkailija.css" type="text/css" rel="stylesheet"/>
    <title>Opetushallitus</title>
    <script src="${contextPath}/resources/jquery/jquery.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui-1.8.23.custom.min.js"></script>
    <script src="${contextPath}/resources/javascript/rules.js"></script>
    <script src="${contextPath}/resources/javascript/master.js"></script>

    <!--[if gte IE 9]>
    <style type="text/css">
        .tabs .tab span {
            filter: none;
        }
    </style>
    <![endif]-->
</head>
<body>
<div id="viewport">
    <div id="overlay" style="display: none;">
    </div>
    <div id="wrapper">
        <header id="siteheader">

            <div class="primarylinks">
                <a href="#">Oppijan verkkopalvelu</a> &nbsp;
                <a href="#">Virkailijan työpöytä</a>
            </div>

            <div class="secondarylinks">
                <a href="#">Omat tiedot</a> &nbsp;
                <a href="#">Viestintä</a> &nbsp;
                <a href="#">Asiakaspalvelu</a> &nbsp;
                <a href="#">Tukipalvelut</a>
            </div>

        </header>

        <nav id="navigation" class="grid16-16">

            <ul class="level1">
                <li><a href="#" class=""><span>Organisaation tiedot</span></a></li>
                <li>
                    <a href="index.html" class="current"><span>Koulutustarjonta</span></a>
                    <ul class="level2">
                        <li><a href="#" class="">Koulutuksen tiedot</a></li>
                        <li><a href="#" class="">Koulutuksen toteutus ja hakukohde</a></li>
                        <li><a href="#" class="">Organisaation kuvailevat tiedot</a></li>
                        <li><a href="#" class="">Järjestämissopimukset</a></li>
                        <li><a href="#" class="">Järjestämisluvat</a></li>
                    </ul>
                </li>
                <li><a href="#" class=""><span>Valintaperusteet</span></a></li>
                <li><a href="#" class=""><span>Koulutussuunnittelu</span></a></li>
                <li><a href="#" class=""><span>Sisällönhallinta</span></a></li>
            </ul>

            <div class="clear"></div>
        </nav>

        <div id="breadcrumbs">
            <ul>
                <li><span><a href="#">Koulutustarjonta</a></span></li>
                <li><span><a href="#">Hakemuksen esikatselu</a></span></li>
            </ul>
        </div>

        <div class="grid16-16">
            <a href="#" class="button small back"></a>
            <a href="#" class="button small">Tee VRK haku</a>
            <a href="#" class="button small disabled">Tee TOR haku</a>
            <c:if test="${applicationProcessState.status ne 'Peruttu'}">
                <form class="inline-block" method="post"
                      action="${contextPath}/virkailija/hakemus/${oid}/applicationProcessState/CANCELLED/">
                    <button type="submit"><span><span>Passivoi hakemus</span></span></button>
                </form>
            </c:if>
        </div>

        <div class="grid16-16">
            <h3><c:out value="${categoryData['Etunimet']}"/>&nbsp;<c:out
                            value="${categoryData['Sukunimi']}"/></h3>
            <table class="width-50 margin-top-2">
                <tr>
                    <td><span class="bold">Hakemusnumero: </span><c:out value="${oid}"/></td>
                    <td><span class="bold">Hakemuksen tila: </span><c:out
                            value="${applicationProcessState.status}"/></td>
                    <td><span class="bold">Puhelin</span>050 35302195</td>
                </tr>
                <tr>
                    <td><span class="bold">Henkilötunnus: </span><c:out
                            value="${categoryData['Henkilotunnus']}"/></td>
                    <td><span class="bold">Oppijanumero: </span>xxxx</td>
                    <td><span class="bold">Sähköposti: </span>erkki.esimerkki@gmail.com</td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td><span class="bold">Äidinkieli: </span>suomi</td>
                </tr>

            </table>
        </div>

        <section class="grid16-16 margin-top-2">

            <div class="tabs">
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemus"
                   class="tab"><span>Hakemus</span></a>
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="suoritustiedot" class="tab"><span>Suoritustiedot</span></a>
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="lisatiedot" class="tab"><span>Kelpoisuus ja liitteet</span></a>
                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="arvosanat" class="tab current"><span>Arvosanat</span></a>
            </div>

            <div class="tabsheets">
                
                <section id="hakemus" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemus">

                    
                    <c:set var="preview" value="${vaihe.preview}" scope="request"/>
                    
                    <c:choose>
                        <c:when test="${preview}">
                        
                            <div class="form">
                                <c:forEach var="child" items="${vaihe.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}Preview.jsp"/>
                                </c:forEach>
                            </div>
                            
                        </c:when>
                        <c:otherwise>
                        
                            <form id="form-${vaihe.id}" class="form" method="post">
                                <c:forEach var="child" items="${vaihe.children}">
                                    <c:set var="element" value="${child}" scope="request"/>
                                    <c:set var="parentId" value="${form.id}.${vaihe.id}" scope="request"/>
                                    <jsp:include page="../elements/${child.type}.jsp"/>
                                </c:forEach>
                                <button class="save" name="vaiheId" type="submit"
                                        value="${applicationPhaseId}"><span><span><spring:message
                                        code="lomake.button.save"/></span></span></button>
                            </form>
                           
                        </c:otherwise>

                    </c:choose>
                    
                    <hr/>
                    
                    <c:if test="${(preview)}">
                        <div>
                            <a href="#" class="button small back"></a>
                            <a href="#" class="button small">Tee VRK haku</a>
                            <a href="#" class="button small disabled">Tee TOR haku</a>
                            <c:if test="${applicationProcessState.status ne 'Peruttu'}">
                                <form class="inline-block" method="post"
                                      action="${contextPath}/virkailija/hakemus/${oid}/applicationProcessState/CANCELLED/">
                                    <button type="submit"><span><span>Passivoi hakemus</span></span></button>
                                </form>
                            </c:if>
                        </div>

                    </c:if>
                   
                </section>
                
                
                <section id="suoritustiedot" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="suoritustiedot">
                    
                    <div class="linklist margin-bottom-4">
                        <a href="#">Perusaste</a>
                        <span>|</span>
                        <a href="#">Lukion oppimäärä & YO-todistus</a>
                        <span>|</span>
                        <a href="#">Ammatillinen koulutus</a>
                        <span>|</span>
                        <a href="#">Alemmat korkeakouluopinnot</a>
                        <span>|</span>
                        <a href="#">Ylemmät korkeakouluopinnot</a>
                        <span>|</span>
                        <a href="#">Jatkotutkinnot</a>
                    </div>

                    <div class="grid16-8">
                        <table class="form-layout-table">
                            <tr>
                                <td>
                                    <label for="pohjakoulutus">Pohjakoulutus</label>
                                </td>
                                <td>              
                                    <select id="pohjakoulutus" placeholder="pohjakoulutus" name="pohjakoulutus">
                                        <option value="perusopetus">Perusopetuksen oppimäärä</option>
                                        <option value="yksilollistetty">Perusopetuksen yksilöllistetty oppimäärä, opetus järjestetty toiminta-alueittain</option>
                                        <option value="paaosinyksilollistetty">Perusopetuksen pääosin tai kokonaan yksilöllistetty oppimäärä</option>
                                        <option value="keskeytynyt">Oppivelvollisuuden suorittaminen keskeytynyt</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label for="paatos">Päättö- tai erovuosi</label>
                                </td>
                                <td>
                                    <input type="text" id="paatos" name="paatos" size="20" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label for="opetuskieli">Opetuskieli</label>
                                </td>
                                <td>
                                    <select id="opetuskieli" placeholder="opetuskieli" name="opetuskieli">
                                        <option value="suomi">Suomi</option>
                                        <option value="ruotsi">Ruotsi</option>
                                    </select>    
                                </td>
                            </tr>      
                        </table>
                    </div>

                    <div class="grid16-8">
                        <table class="form-layout-table">
                            <tr>
                                <td>
                                    <label for="paatos">Päättö- tai erovuosi</label>
                                </td>
                                <td>
                                    <input type="text" id="lahtokoulu1" name="lahtokoulu1" size="2" />
                                    <input type="text" id="lahtokoulu2" name="lahtokoulu2" size="40" />
                                    <a href="#">Valitse</a>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label for="paattoluokka">Päättöluokka</label>
                                </td>
                                <td>
                                    <input type="text" id="paattoluokka" name="paattoluokka" size="40" />
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>
                                    <input type="checkbox" id="suoritettu" name="suoritettu" />
                                    <label class="form-content-text" for="suoritettu">Suoritettu ulkomailla</label>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="grid16-16 margin-top-2 margin-bottom-4">
                        <table class="form-layout-table">
                            <tr>
                                <td>Lisäpistekoulutus</td>
                                <td>
                                    <input type="checkbox" name="kymppiluokka" id="kymppiluokka" />
                                    <label for="kymppiluokka">Perusopetuksen lisäopetuksen oppimäärä (kymppiluokka)</label>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>
                                    <input type="checkbox" name="kuntouttavakoulutus" id="kuntouttavakoulutus" />
                                    <label for="kuntouttavakoulutus">Vammaisten valmentava ja kuntouttava opetus ja ohjaus</label>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>
                                    <input type="checkbox" name="maahanmuuttajienkoulutus" id="maahanmuuttajienkoulutus" />
                                    <label for="maahanmuuttajienkoulutus">Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus</label>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>
                                    <input type="checkbox" name="muukoulutus" id="muukoulutus" />
                                    <label for="muukoulutus">Muuna kuin ammatillisena peruskoulutuksena järjestettävä kotitalousopetus</label>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <hr />
                    <div class="grid16-16">
                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                        tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
                        quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
                        consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
                        cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
                        proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>

                        <h3>Laskennassa käytettävät arvosanat</h3>
                        <table class="width-50 margin-vertical-1">
                            <tr>
                                <td><label for="todistus">Todistus</label></td>
                                <td>
                                    <select name="todistus" id="todistus">
                                        <option value="valitse">Valitse todistus</option>
                                        <option value="paattotodistus">Päättötodistus</option>
                                        <option value="erotodistus">Erotodistus</option>
                                    </select>
                                </td>
                                <td>
                                    <a href="#">Muokkaa arvosanoja</a>
                                </td>
                            </tr>

                        </table>
                            
                            
                            
                         
                    </div>
                    <div class="grid16-8">
                        <table class="virkailija-table-1">
                            <thead>
                                <tr>
                                    <th>Oppiaine</th>
                                    <th>Lukioon haettaessa</th>
                                    <th>Haettaessa ammatilliseen opetukseen</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>Äidinkieli ja kirjallisuus</td>
                                    <td>8</td>
                                    <td>7</td>
                                </tr>
                                <tr>
                                    <td>A1-kieli</td>
                                    <td>8</td>
                                    <td>7</td>
                                </tr>
                                <tr>
                                    <td>B1-kieli</td>
                                    <td>6</td>
                                    <td>6</td>
                                </tr>
                                <tr>
                                    <td>Matematiikka</td>
                                    <td>9</td>
                                    <td>9</td>
                                </tr>
                                <tr>
                                    <td>Biologia</td>
                                    <td>8</td>
                                    <td>7</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="grid16-8">
                        <table class="virkailija-table-1">
                            <tbody>
                                <tr>
                                    <td class="bold">00,0</td>
                                    <td>Yleistäkoulumenestystä mittaava aritmeettinen keskiarvo (keskiarvo, jota käytetään perusopetuksen oppimäärällä ammatilliseen koulutukseen haettaessa)</p></td>
                                </tr>
                                <tr>
                                    <td class="bold">00,0</td>
                                    <td>Painotettavien arvosanojen keskiarvo (keskiarvo, jota käytetään perusopetuksen oppimäärällä ammatilliseen koulutukseen haettaessa</td>
                                </tr>
                                <tr>
                                    <td class="bold">00,0</td>
                                    <td>Lukuaineiden keskiarvo (keskiarvo, jota käytetään lukioon heattaessa sekä tasapistetilanteessa ammatilliseen koulutukseen haettaessa)</td>
                                </tr>
                                <tr>
                                    <td class="bold">00,0</td>
                                    <td>Lukiovalinnoissa tasapistetilanteessa käytettävä keskiarvo</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="clear"></div>
                </section>

                <section id="lisatiedot" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="lisatiedot">
                    <h1>Kelpoisuus ja liitteet</h1>
                </section>
                <section id="arvosanat" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="arvosanat" style="display: block">
                    <h3>Perusopetus</h3>
                    <table class="virkailija-table-1">
                        <thead>
                            <tr>
                                <th></th>
                                <th colspan="2" class="bold align-center">Hakemus 15.4.2012</th>
                                <th colspan="2" class="bold align-center">Rekisteri TOR</th>
                                <th colspan="2" class="bold align-center">Muokkaa tietoja</th>
                            </tr>
                            <tr>
                                <td class="bold align-center">Oppiaine</td>
                                <td class="bold align-center">Yhteinen oppiaine</td>
                                <td class="bold align-center">Valinnaisaine</td>
                                <td class="bold align-center">Yhteinen oppiaine</td>
                                <td class="bold align-center">Valinnaisaine</td>
                                <td class="bold align-center">Yhteinen oppiaine</td>
                                <td class="bold align-center">Valinnaisaine</td>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Äidinkieli ja kirjallisuus</td>
                                <td>8</td>
                                <td></td>
                                <td>8</td>
                                <td></td>
                                <td><input type="text" value="8" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>
                                    A1-Kieli
                                    <select name="a1kieli" id="a1kieli">
                                        <option value="ruotsi">Ruotsi</option>
                                        <option value="englanti">Englanti</option>
                                        <option value="saksa">Saksa</option>
                                    </select>
                                </td>
                                <td>8</td>
                                <td></td>
                                <td>8</td>
                                <td></td>
                                <td><input type="text" value="8" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>
                                    B1-Kieli
                                    <select name="b1kieli" id="b1kieli">
                                        <option value="ruotsi">Ruotsi</option>
                                        <option value="englanti">Englanti</option>
                                        <option value="saksa">Saksa</option>
                                    </select>
                                </td>
                                <td></td>
                                <td>7</td>
                                <td></td>
                                <td>8</td>
                                <td></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>
                                    B2-Kieli
                                    <select name="b2kieli" id="b2kieli">
                                        <option value="ruotsi">Ruotsi</option>
                                        <option value="englanti">Englanti</option>
                                        <option value="saksa">Saksa</option>
                                    </select>
                                </td>
                                <td>8</td>
                                <td></td>
                                <td>8</td>
                                <td></td>
                                <td><input type="text" value="8" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td><a href="#">Lisää kieli</a></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td>Matematiikka</td>
                                <td>8</td>
                                <td></td>
                                <td>8</td>
                                <td></td>
                                <td><input type="text" value="8" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>Biologia</td>
                                <td>7</td>
                                <td></td>
                                <td>7</td>
                                <td></td>
                                <td><input type="text" value="7" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>Maantieto</td>
                                <td>6</td>
                                <td></td>
                                <td>6</td>
                                <td></td>
                                <td><input type="text" value="6" /></td>
                                <td><input type="text" /></td>
                            </tr>
                            <tr>
                                <td>Käsityö</td>
                                <td>Ei arvosanaa(#)</td>
                                <td></td>
                                <td>Ei arvosanaa(#)</td>
                                <td></td>
                                <td><input type="text" value="Ei arvosanaa" /></td>
                                <td><input type="text" /></td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="grid16-4">
                        <h4>(#) Hakijan vahvistama tieto</h4>
                    </div>
                    <div class="grid16-4 offset-left-16-8">
                        <small class="float-right block">S = suoritettu</small>
                        <div class="clear"></div>
                        <small class="float-right block">- = ei arvosanaa</small>
                    </div>
                    <div class="clear"></div>
                </section>
                
            </div>
        </section>

        <footer id="footer" class="grid16-16">
            <div class="footer-container">
                <div class="grid16-8 footer-logo">
                    <img src="${contextPath}/resources/img/logo-opetus-ja-kulttuuriministerio.png">
                </div>
                <div class="grid16-8 footer-logo">
                    <img src="${contextPath}/resources/img/logo-oph.png">
                </div>
                <div class="clear"></div>
            </div>
        </footer>
        <div class="clear"></div>
        </div>
</div>
</body>
</html>
