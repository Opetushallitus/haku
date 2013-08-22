<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

<div class="popup-dialog-wrapper" id="createApplication" style="z-index:1000;display:none;">
    <span class="popup-dialog-close">&#8203;</span>

    <div class="popup-dialog">
        <span class="popup-dialog-close">&#8203;</span>
        <div class="popup-dialog-header">
            <h3><fmt:message key="virkailija.hakemus.syota"/></h3>
        </div>
        <div class="popup-dialog-content">
            <form method="post" action="${contextPath}/virkailija/hakemus/new">
                <div class="margin-top-2">
                    <label for="asSelect"><fmt:message key="virkailija.hakemus.syota.valitsehaku"/></label>
                    <select id="asSelect" name="asId">
                        <c:forEach var="option" items="${applicationSystems}">
                            <option value="${option.key}"><haku:i18nText value="${option.value.name}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="clear margin-top-3">
                    <button name="nav-send" value="true" data-po-hide="createApplication">
                        <fmt:message key="virkailija.hakemus.syota.peru"/>
                    </button>

                    <button id="submit_confirm" class="primary set-right" name="nav-send" type="submit" value="true">
                        <fmt:message key="virkailija.hakemus.syota.laheta"/>
                    </button>
                </div>
                <div class="clear"></div>
            </form>
        </div>
    </div>
</div>