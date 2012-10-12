<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<button class="reset" data-id="${sortableItem.id}" type="button">
    <span>
        <span><c:out value="${sortableItem.resetLabel}"/></span>
    </span>
</button>
<div class="preference-fields">
    <c:set value="${sortableItem.id}-Opetuspiste" var="textInputId" scope="page"/>
    <c:set value="${sortableItem.id}-Opetuspiste-id" var="hiddenInputId" scope="page"/>
    <c:set value="${sortableItem.id}-Koulutus" var="selectInputId" scope="page"/>
    <c:set value="${sortableItem.id}-Lisakysymykset" var="additionalQuestionsId" scope="page"/>
    <c:set value="${sortableItem.id}-Koulutus-id" var="hiddenKoulutusId" scope="page"/>
    <div class="form-row">
        <label class="form-row-label" for="${textInputId}"><c:out value="${sortableItem.learningInstitutionLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-text">
                <input id="${textInputId}"
                       name="${textInputId}"
                       value="${categoryData[textInputId]}"
                       type="text"
                       data-selectinputid="${selectInputId}"/>
                <input id="${hiddenInputId}" name="${hiddenInputId}" value="${categoryData[hiddenInputId]}" type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="form-row">
        <label class="form-row-label" for="${selectInputId}"><c:out value="${sortableItem.educationLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-select">
                <select id="${selectInputId}" name="${selectInputId}" placeholder="${sortableItem.selectEducationPlaceholder}" data-additionalquestions="${additionalQuestionsId}">
                </select>
                <input id="${hiddenKoulutusId}" name="${hiddenKoulutusId}" value="${categoryData[hiddenKoulutusId]}" type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div id="${additionalQuestionsId}" class="form-row">
    </div>
</div>
