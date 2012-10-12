<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

<header id="siteheader">

    <div class="sitelogo">
        <a href="/haku">Sivuston logo</a>
    </div>

    <div class="actions">
        
        <c:choose>
            <c:when test="${user}">
                <ul>
                    <li><a href="/logout">Kirjaudu ulos</a></li>
                    <li><a href="/me"><c:out value="${user.firstname}"/> <c:out value="${user.lastname}"/></a></li>
                </ul>
            </c:when>
            <c:otherwise>
                <ul>
                    <li><a href="#" class="open-login-popup">Kirjaudu sisään</a></li>
                    <div id="login-popup" class="display-none">
                        <div id="authentication">
                            <div class="heading">
                                <h2>Kirjautuminen</h2>
                            </div>
                            <div class="login-content">
                                <form>
                                    <div>
                                        <legend class="h3">KÄYTTÄJÄTUNNUS</legend>
                                        <input type="text"/>
                                    </div>
                                    <div>
                                        <legend class="h3">SALASANA</legend>
                                        <input type="text"/>
                                    </div>
                                </form>
                                <a href="#" class="helplink">?</a>
                                <a href="#">Unohtuiko salasana?</a>

                                <div class="clear"></div>
                                <a href="#">Rekisteröidy palveluun</a>

                                <div class="clear"></div>
                                <a href="#" class="close-login-popup">Sulje</a>
                            </div>
                        </div>
                    </div>
                </ul>
            </c:otherwise>
        </c:choose>
        <ul>
            <li><a href="http://www.sanasto.fi/">Sanasto</a></li>
            <li><a href="http://google.fi">Kysy neuvoa</a></li>
            <li><a href="http://www.koulutusnetti.fi/?path=hakuajat">Hakuajat</a></li>
        </ul>

        <ul>
            <li><a href="#">På svenska</a></li>
            <li><a href="#">in English</a></li>
            <li><a href="#">Mobiili</a></li>
            <li><a href="#">Tekstiversio</a></li>
        </ul>

        <div class="clear"></div>
    </div>

    <div class="line clear"></div>
</header>



