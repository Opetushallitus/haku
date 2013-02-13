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
<section id="searchSection">
    <form id="searchform">
        <div class="grid16-16">
            <table class="form-layout-table width-100">
                <tr>
                    <td>
                        <input type="text" id="entry" class="search width-60"
                               placeholder="nimi, henkilötunnus, oppijanumero, hakemusnumero"/>
                    </td>
                    <td>
                        <label for="application-state">Hakemuksen tila:</label>
                        <select class="width-50" id="application-state">
                            <option value="">Kaikki</option>
                            <option value="voimassa">Voimassa</option>
                            <option value="peruttu">Peruttu</option>
                            <option value="myöhästynyt">Myöhästynyt</option>
                            <option value="eihakemusta">Ei hakemusta (perustiedot siirretty)</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                    <td>
                        <div>
                            <input type="checkbox" value="fetch-passive" id="fetch-passive"/>
                            <label for="fetch-passive">Hae myös passiiviset</label>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label class="block" for="application-preference">Hakukohde:</label>
                        <input class="width-60" type="text" id="application-preference" placeholder="hae nimellä tai koodilla"/>
                    </td>

                    <td>
                        <input class="button secondary small" type="button" value="Tyhjennä">
                        <input id="search-applications" class="button primary small" type="submit" value="Hae">
                    </td>
                </tr>
            </table>
        </div>
    </form>
</section>
<section class="grid16-16 margin-top-2">
    <div class="tabs">
        <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemukset" class="tab current"><span>Hakemukset</span></a>
    </div>
    <div class="tabsheets">
        <section id="hakemukset" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemukset" style="display: block">
                <a href="#" class="button small">Avaa hakemus</a>
                <div class="clear"></div>

                <span>Hakutulos: 0 osumaa</span>

                <div class="margin-top-2 margin-bottom-1">
                    <div class="field-container-checkbox inline-block">
                        <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId"/>
                        <label for="KenttaId">valitse kaikki</label>
                    </div>
                </div>

                <table id="application-table" class="virkailija-table-1">
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
                    </tbody>
                </table>
                <div class="clear"></div>
        </section>
    </div>
</section>


