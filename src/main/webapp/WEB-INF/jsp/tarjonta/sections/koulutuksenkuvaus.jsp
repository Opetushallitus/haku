<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<section id="kuvaus" style="display: block" class="tabsheet" data-tabs-group="applicationtabs"
         data-tabs-id="kuvaus">

    <div class="clear"></div>
    <legend class="h3"><spring:message code="tarjonta.koulutuksentavoite"/></legend>
    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
        tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
        quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
        consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
        cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
        proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
    <legend class="h3">Suuntautumisvaihtoehdon valinta</legend>
    <p>Opintojesi aikana erikoistut joko markkinointiin, laskentaan ja rahoitukseen tai työyhteisön
        kehittämiseen.</p>
    <ul>
        <li><a href="#">Markkinointi</a></li>
        <li><a href="#">Laskenta ja rahoitus</a></li>
        <li><a href="#">Työyhteisöjen kehittäminen</a></li>
    </ul>


    <legend class="h3"><spring:message code="tarjonta.jatkoopintomahdollisuudet"/></legend>
    <p><c:out value="${searchResult['LOSDescriptionAccessToFurtherStudies']}"/></p>

    <legend class="h3"><spring:message code="tarjonta.opintojenrakenne"/></legend>
    <div class="hierarchy-list">
        <ul class="lvl-1">
            <li class="closed" hierarchy-list-action='closed'>
                <span>Perusopinnot</span>
                <ul class="lvl-2">
                    <li>
                        <span>Lorem ipsum dolor sit amet</span>
                        <ul class="lvl-3">
                            <li><span>Lorem ipsum dolor sit amet</span></li>
                        </ul>
                    </li>
                    <li><span>Lorem ipsum dolor sit amet</span></li>
                    <li><span>Lorem ipsum dolor sit amet</span></li>
                </ul>
            </li>
            <li class="closed" hierarchy-list-action='closed'>
                <span>Muut valinnaiset opinnot</span>
                <ul class="lvl-2">
                    <li><span>Lorem ipsum dolor sit amet</span></li>
                </ul>
            </li>
            <li class="closed" hierarchy-list-action='closed'>
                <span>Opinnäytetyö</span>
                <ul class="lvl-2">
                    <li><span>Lorem ipsum dolor sit amet</span></li>
                </ul>
            </li>
            <li class="closed" hierarchy-list-action='closed'>
                <span>Työharjoittelu</span>
                <ul class="lvl-2">
                    <li><span>Lorem ipsum dolor sit amet</span></li>
                </ul>
            </li>

        </ul>
    </div>

</section>
