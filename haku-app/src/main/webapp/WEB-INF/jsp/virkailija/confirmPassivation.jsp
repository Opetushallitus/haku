<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="popup-dialog-wrapper" id="confirmPassivation" style="z-index:1000;display:none;">
    <span class="popup-dialog-close">&#8203;</span>

    <div class="popup-dialog">
        <span class="popup-dialog-close">&#8203;</span>

        <div class="popup-dialog-header">
            <h3><fmt:message key="virkailija.hakemus.passivoi.hakemus.varmistus"/></h3>
        </div>
        <div class="popup-dialog-content">
            <form method="post" action="${contextPath}/virkailija/hakemus/${application.oid}/passivate">
                <div class="margin-bottom-2">
                    <p>
                        <fmt:message key="virkailija.hakemus.passivoi.hakemus.viesti"/>
                    </p>
                    <textarea name="passivation-reason" id="passivation-reason" ></textarea>
                </div>
                <div>
                    <button class="small" name="nav-send" value="true" data-po-clear-on-hide="passivation-reason" data-po-hide="confirmPassivation">
                        <fmt:message key="lomake.send.confirm.no"/>
                    </button>
                    <button id="submit_confirm" class="primary set-right small" name="nav-send" type="submit" value="true">
                        <fmt:message key="lomake.send.confirm.yes"/>
                    </button>
                </div>
                <div class="clear"></div>
            </form>
        </div>
    </div>
</div>