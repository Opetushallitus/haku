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
<div>
    <form id="loginForm" action="${pageContext.request.contextPath}/j_spring_security_check" method="post">
        <h1>Kirjautuminen</h1>
        <fieldset>
            <input name="j_username" type="text" placeholder="Username" autofocus required></br>
            <input name="j_password" type="password" placeholder="Password" required></br>
            <input name="login" id="login" value="Kirjaudu" type="submit"/>
        </fieldset>
    </form>
</div>
