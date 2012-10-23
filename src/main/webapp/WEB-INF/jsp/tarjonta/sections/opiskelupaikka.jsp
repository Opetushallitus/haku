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
<section id="opiskelupaikka" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="opiskelupaikka">

    <div class="clear"></div>
    <img src="/haku/content/bulevardi31.png"/>
    <legend class="h3"><c:out value="${searchResult['LOPInstitutionInfoName']}" /></legend>
    <a href="http://www.metropolia.fi">http://www.metropolia.fi</a>

    <p><c:out value="${searchResult['LOPInstitutionInfoGeneralDescription']}" /></p>

</section>
