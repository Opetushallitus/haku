<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<c:forEach var="ao" items="${it.hakukohteet}">

<div>
    <div class="grid-16">
        <h2><c:out value="${ao.opetuspiste}"/></h2>
        <h3><c:out value="${ao.name}"/></h3>
    </div>
    <div class="clear"></div>
    <div class="grid16-7">
        <table>
            <tr>
                <td>Valinnan kokonaispisteet</td>
                <td><fmt:formatNumber value="${ao.totalScore}" maxFractionDigits="2"/></td>
            </tr>
            <tr>
                <td>Sijoittelun tulos</td>
                <td>${ao.sijoittelunTulos}</td>
            </tr>
            <tr>
                <td>Hylkäyksen syy</td>
                <td>${ao.hylkayksenSyy}</td>
            </tr>
            <tr>
                <td>Vastaanottotieto</td>
                <td>${ao.vastaanottoTieto}</td>
            </tr>
            <tr>
                <td>Ilmoittautumistieto</td>
                <td>${ao.ilmoittautuminen}</td>
            </tr>
        </table>
    </div>
    <div class="grid16-7 .offset-left-16-7">
        <table>
            <tr>
                <th>&nbsp;</th>
                <th>Kutsuttu</th>
                <th>Pisteet</th>
            </tr>
        <c:forEach var="test" items="${ao.tests}">
            <tr>
                <td><c:out value="${test.nimi}"/></td>
                <td><c:out value="${test.osallistuminen}"/></td>
                <td><c:out value="${test.score}"/></td>
            </tr>
        </c:forEach>
        </table>
    </div>
    <div class="clear"></div>
</div>


</c:forEach>