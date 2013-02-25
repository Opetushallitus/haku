<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<legend class="h3">${group.name}</legend>
<ul class="minimal">
    <c:forEach var="item" items="${group.items}">
        <li class="field-container-checbox">
            <input type="checkbox" name="${item['name']}"/>
            <span class="label">${item['label']}"</span>
        </li>
    </c:forEach>
</ul>

