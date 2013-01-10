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

<div class="popup" id="authentication">
    
      <h2>Kirjautuminen</h2>
  
      <form action="${pageContext.request.contextPath}/j_spring_security_check" method="POST">

          <legend class="h3">KÄYTTÄJÄTUNNUS</legend>
          <input name="j_username" type="text"/>

          <legend class="h3">SALASANA</legend>
          <input name="j_password" type="password"/>

          <div>
              <input name="login" value="Kirjaudu" type="submit"/>
          </div>
      </form>

      <a href="#">Unohtuiko salasana?</a>

      <div class="clear"></div>
      <a href="#">Rekisteröidy palveluun</a>

      <div class="clear"></div>
      <a href="#" data-popup-action="close">Sulje</a>
      <a href="#" class="helplink">?</a>
   
</div>
