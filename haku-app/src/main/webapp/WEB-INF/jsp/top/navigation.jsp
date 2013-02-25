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

<nav class="main-navigation">
    <ul class="navigation">
        <li class="home"><a href="${pageContext.request.contextPath}">Etusivu</a></li>
        <li><a href="osio/osio_etusivu">Lukio</a></li>
        <li><a href="#">Ammatillinen koulutus</a></li>
        <li><a href="#">Ammattikorkeakoulu</a></li>
        <li>
            <a href="#">Yliopisto</a>
            <jsp:include page="dropdown.jsp"/>
        </li>
        <li><a href="#">Täydennyskoulutus</a></li>
        <li><a href="#">Opintojen valinta</a></li>
    </ul>
</nav>
