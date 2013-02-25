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
        <input type="hidden" id="lopOid" name="lopoid">
        <div class="grid16-16">
            <table class="form-layout-table width-100">
                <tr>
                    <td>
						<label for="application-state">Hae hakemuksia:</label>
						<div class="field-search-containerbox">
							<input type="text" id="entry" class="search width-60"
								   placeholder=""/>
							
						</div>
						<small>Hae hakijan nimellä, henkilötunnuksella, oppijanumerolla tai hakemusnumerolla.</small>
                    </td>
                    <td>
                        <label for="application-state">Hakemuksen tila:</label>
						<div class="field-select-containerbox">
                        <select class="width-50" id="application-state" ng-model="applicationState">
                            <option value="">Kaikki</option>
                            <option value="ACTIVE">Voimassa</option>
                            <option value="PASSIVE">Peruttu</option>
                            <option value="INCOMPLETE">Puutteellinen</option>
                        </select>
						</div>
						<div class="field-container-checkbox">
							<input type="checkbox" value="fetch-passive" id="fetch-passive"/>
                            <label for="fetch-passive">Hae myös passiiviset</label>
						</div>
                    </td>
                </tr>

                <tr>
                    <td>
                        <label class="block" for="application-preference">Hakukohde:</label>
						<div class="field-text-containerbox">
                        <input class="width-60" type="text" id="application-preference"
                               placeholder="hakukohteen nimi tai koodi"/>
						</div>
                    </td>

                    <td style="vertical-align:bottom;">
                        <input id="reset-search" class="button secondary small" type="button" value="Tyhjennä" style="margin-bottom:1px;" />
                        <input id="search-applications" class="button primary small" type="submit" value="Hae" style="margin-bottom:1px;" />
                    </td>
                </tr>
            </table>
        </div>
    </form>
</section>
<section class="grid16-16 margin-top-2">
    <div class="tabs">
        <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakemukset" class="tab current"><span
                id="application-tab-label">Hakemukset 0</span></a>
    </div>
    <div class="tabsheets">
        <section id="hakemukset" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakemukset"
                 style="display: block">
            <a href="#" class="button small">Avaa hakemus</a>

            <div class="clear"></div>

            <span>Hakutulos: <span id="resultcount">0</span> osumaa</span>

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


