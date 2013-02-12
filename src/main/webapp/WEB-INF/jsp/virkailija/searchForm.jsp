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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<table class="structural-table" style="margin-left: 0.625%;width:99.375%;">
    <tbody>
    <tr>
        <td ng-include="'partials/organisaatiohaku.html'">

        </td>
        <td>

            <section id="searchSection">
                <form id="searchform" ng-submit="search()">
                    <div class="grid16-16">
                        <table class="form-layout-table width-100">
                            <tr>
                                <td>
                                    <input type="text" id="entry" class="search width-60"
                                           placeholder="nimi, henkilötunnus, oppijanumero, hakemusnumero" ng-model="q"/>
                                </td>

                                <td>
                                    <label for="application-state">Hakemuksen tila:</label>
                                    <select class="width-50" id="application-state" ng-model="applicationState">
                                        <option value="">Kaikki</option>
                                        <option value="voimassa">Voimassa</option>
                                        <option value="peruttu">Peruttu</option>
                                        <option value="myöhästynyt">Myöhästynyt</option>
                                        <option value="eihakemusta">Ei hakemusta (perustiedot siirretty)</option>
                                    </select>
                                </td>
                                <!--
                                <td>
                                    <div class="inline-block">
                                        <label class="block" for="vuosi">Lähtökoulu</label>
                                        <input type="text" name="vuosi" id="vuosi"/>
                                    </div>
                                    <div class="inline-block">
                                        <label class="block" for="paattoluokka">Päättöluokka</label>
                                        <select id="paattoluokka">
                                            <option value="all">kaikki</option>
                                        </select>
                                    </div>

                                </td>
                                -->
                            </tr>
                            <tr>
                                <td>
                                    <!--
                                    <label class="block" for="search-selection">Haku:</label>
                                    <select name="search-selection" id="search-selection">
                                        <option value="default">Oletuksena käynnissä oleva haku</option>
                                    </select>
                                    -->
                                </td>

                                <td>
                                    <div>
                                        <input type="checkbox" value="fetch-passive" id="fetch-passive"
                                               ng-model="fetchPassive"/>
                                        <label for="fetch-passive">Hae myös passiiviset</label>
                                    </div>
                                    <!--
                                    <div>
                                        <input type="checkbox" name="discretionary" value="discretionary" id="discretionary"/>
                                        <label for="discretionary">Hae vain harkinnanvaraisesti hakeneet</label>
                                    </div>
                                    -->
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label class="block" for="application-preference">Hakukohde:</label>
                                    <input class="width-60" type="text" id="application-preference"
                                           placeholder="hae nimellä tai koodilla" ng-model="applicationPreference"/>
                                </td>

                                <td>
                                    <input class="button secondary small" type="button" value="Tyhjennä"
                                           ng-click="reset()">
                                    <input class="button primary small" type="submit" value="Hae">
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
            </section>
            <!--
            <section class="grid16-16 margin-top-2">
                <a href="#" class="button small">Yksilöi henkilöitä (0 kpl)</a>
                <a href="#" class="button small">Päällekkäisiä hakemuksia (0 kpl)</a>
                <a href="#" class="button small">Tarkasta sähköisiä liitteitä (0 kpl)</a>
            </section>
            -->
            <section class="grid16-16 margin-top-2">

                <tabs>
                    <pane title="Hakemukset {{applications.length}}">
                        <!--
                        <section id="hakemukset" style="display: block" class="tabsheet" data-tabs-group="applicationtabs"
                                 data-tabs-id="hakemukset">
                    -->
                        <a href="#" class="button small">Avaa hakemus</a>
                        <!--
                        <a href="#" class="button small">Merkitse paperiliitteitä</a>
                        <a href="#" class="button small">Syötä paperihakemuksia</a>
                        <a href="#" class="button small">Vie lista exceliin</a>
                        <a href="#" class="button small">Tulosta lista</a>
                        -->
                        <div class="clear"></div>

                        <span>Hakutulos: {{applications.length}} osumaa</span>

                        <div class="margin-top-2 margin-bottom-1">
                            <div class="field-container-checkbox inline-block">
                                <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId"/>
                                <label for="KenttaId">valitse kaikki</label>
                            </div>

                            <!--
                            <div class="offset-left-16-12 float-right">
                                <div class="float-right">
                                    <div class="pagination inline-block margin-right-2">
                                        <a href="#">&laquo</a>
                                        <a href="#">1</a>
                                        <span>|</span>
                                        <a href="#">2</a>
                                        <span>|</span>
                                        <a href="#">3</a>
                                        <a href="#">&raquo</a>
                                    </div>

                                    <select name="showresults" id="showResultsTop">
                                        <option value="50">Näytä 50/sivu</option>
                                        <option value="100">Näytä 100/sivu</option>
                                    </select>
                                </div>
                            </div>
                            -->
                        </div>

                        <table class="virkailija-table-1">
                            <thead>
                            <tr>
                                <td></td>
                                <td>Sukunimi</td>
                                <td>Etunimi</td>
                                <td>Henkilötunnus</td>
                                <td>Hakemusnro</td>
                                <td>Hakemuksen tila</td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="application in applications">
                                <td><input type="checkbox"/></td>
                                <td>{{application.answers.henkilotiedot.Sukunimi}}</td>
                                <td>{{application.answers.henkilotiedot.Etunimet}}</td>
                                <td>{{application.answers.henkilotiedot.Henkilotunnus}}</td>
                                <td><a href="{{context}}/virkailija/hakemus/{{application.oid}}/" target="_blank">{{application.oid}}</a>
                                </td>
                                <td>{{application.state}}</td>
                            </tr>
                            </tbody>
                        </table>

                        <!--
                        <div class="offset-left-16-12 float-right margin-top-1">
                            <div class="pagination margin-top-1 inline-block margin-right-2">
                                <a href="#">&laquo</a>
                                <a href="#">1</a>
                                <span>|</span>
                                <a href="#">2</a>
                                <span>|</span>
                                <a href="#">3</a>
                                <a href="#">&raquo</a>
                            </div>

                            <select name="showresults" id="showResultsBottom">
                                <option value="50">Näytä 50/sivu</option>
                                <option value="100">Näytä 100/sivu</option>
                            </select>
                        </div>
                        -->

                        <div class="clear"></div>
                    </pane>
                    <!--
                    <pane title="Hakutoiveet">

                    </pane>
                    -->
                </tabs>

            </section>

        </td>
    </tr>
    </tbody>
</table>


