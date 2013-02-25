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

<div id="authentication">
    <div class="heading">
        <h2>Kirjautuminen</h2>
    </div>
    <div class="sidemenu-content">

        <form id="kirjautuminen" action="login" method="post">
            <legend class="h3">KÄYTTÄJÄTUNNUS</legend>
            <div class="form-item-content">
                <input type="text" name="j_username" id="kayttajatunnusId" size="30"/>
            </div>
            <legend class="h3">SALASANA</legend>
            <div class="form-item-content">
                <input type="password" name="j_password" id="salasanaId" size="30"/>
            </div>

            <input type="submit"/>
        </form>
        <div class="clear"></div>


        <a href="#" class="helplink">?</a>
        <a href="#">Unohtuiko salasana?</a>
        <a href="#">Rekisteröidy palveluun</a>

    </div>
</div>
