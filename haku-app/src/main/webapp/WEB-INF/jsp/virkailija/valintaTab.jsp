<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<c:forEach var="ao" items="${it.hakukohteet}">

<div>
    <div class="grid-16">
        <h2><c:out value="${ao.index}" /> &nbsp; <c:out value="${ao.opetuspiste}"/></h2>
        <h3><c:out value="${ao.name}"/></h3>
    </div>
    <div class="clear"></div>
    <div class="grid16-7">
        <table>
            <tr>
                <td><fmt:message key="virkailija.hakemus.valintatiedot.kokonaispisteet"/></td>
                <td><fmt:formatNumber value="${ao.yhteispisteet}" maxFractionDigits="2"/></td>
            </tr>
            <tr>
                <td><fmt:message key="virkailija.hakemus.valintatiedot.sijoittelunTulos"/></td>
                <td><haku:i18nText value="${ao.sijoittelunTulosText}" /></td>
            </tr>
            <tr>
                <td><fmt:message key="virkailija.hakemus.valintatiedot.vastaanottoTieto"/></td>
                <td><haku:i18nText value="${ao.vastaanottoTietoText}" /></td>
            </tr>
        </table>
    </div>
    <div class="grid16-7 .offset-left-16-7">
        <table class="striped">
            <colgroup>
                <col></col>
                <col style="width: 15%;"></col>
                <col style="width: 15%;"></col>
            </colgroup>
            <thead>
                <tr>
                    <th>&nbsp;</th>
                    <th>Kutsuttu</th>
                    <th>Pisteet</th>
                </tr>
            <thead>
            <tbody>
            <c:forEach var="test" items="${ao.pistetiedot}">
                <tr>
                    <td><haku:i18nText value="${test.nimi}"/></td>
                    <td><haku:i18nText value="${test.osallistuminenText}" /></td>
                    <td><c:out value="${test.pisteetToDisplay}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="clear"></div>
</div>


</c:forEach>
