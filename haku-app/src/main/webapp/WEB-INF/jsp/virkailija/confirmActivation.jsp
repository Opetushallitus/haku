<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="popup-dialog-wrapper" id="confirmActivation" style="z-index:1000;display:none;">
    <span class="popup-dialog-close">&#8203;</span>

    <div class="popup-dialog">
        <span class="popup-dialog-close">&#8203;</span>

        <div class="popup-dialog-header">
            <h3><fmt:message key="virkailija.hakemus.aktivoi.hakemus.varmistus"/></h3>
        </div>
        <div class="popup-dialog-content">
            <form method="post" action="${contextPath}/virkailija/hakemus/${application.oid}/addPersonAndAuthenticate">
                <p><fmt:message key="virkailija.hakemus.aktivoi.hakemus.viesti"/></p>
                <textarea name="activation-reason" ></textarea>
                <button name="nav-send" value="true" data-po-hide="confirmActivation">
                    <span>
                        <span><fmt:message key="lomake.send.confirm.no"/></span>
                    </span>
                </button>
                <button id="submit_confirm" class="primary set-right" name="nav-send" type="submit"
                        value="true">
                    <span>
                        <span><fmt:message key="lomake.send.confirm.yes"/></span>
                    </span>
                </button>
                <div class="clear"></div>
            </form>
        </div>
    </div>
</div>