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
<div class="form-item search-result">
	<ul>
    <c:forEach var="item" items="${it.searchResult.items}">
		<li>
			<div >
		        <%--
		        ${item['html_searchResult_fi']}
		         --%>
		         <div style="float: left; width: 70%; margin: 0; padding:0">
		         <a href="${pageContext.request.contextPath}/tarjontatiedot/${item['AOId']}">${item['AOTitle']}</a> <br />
		         ${item['ASName']} <br />
		         ${item['LOPInstitutionInfoName']} <br />
		         </div>
		        <jsp:include page="muistiJaVertailuValitsimet.jsp">
		            <jsp:param name="item" value="${item}"/>
		        </jsp:include>
			</div>
			<div class="clear"></div>
        </li>
    </c:forEach>
	</ul>
</div>

<div class="clear"></div>


