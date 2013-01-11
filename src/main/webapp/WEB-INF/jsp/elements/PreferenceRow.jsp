<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<button class="reset" data-id="${sortableItem.id}" type="button">
    <span>
        <span><c:out value="${sortableItem.resetLabel}"/></span>
    </span>
</button>
<div class="preference-fields">
    <c:set value="${sortableItem.learningInstitutionInputId}" var="textInputId" scope="page"/>
    <c:set value="${sortableItem.learningInstitutionInputId}-id" var="hiddenInputId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}" var="selectInputId" scope="page"/>
    <c:set value="${sortableItem.id}-Lisakysymykset" var="additionalQuestionsId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}-id" var="hiddenKoulutusId" scope="page"/>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required'].value}" for="${textInputId}"><c:out value="${sortableItem.learningInstitutionLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-text">
                <input id="${textInputId}"
                       name="${textInputId}"
                       value="${categoryData[textInputId]}"
                       type="text"
                       data-selectinputid="${selectInputId}"
                       size="40"
                       ${sortableItem.attributes['required'].value}/>
                <span class="required-field"><c:out value="${errorMessages[textInputId]}"/></span>
                <input id="${hiddenInputId}" name="${hiddenInputId}" value="${categoryData[hiddenInputId]}" type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required'].value}" for="${selectInputId}"><c:out value="${sortableItem.educationLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-select">
                <select id="${selectInputId}" name="${selectInputId}" placeholder="${sortableItem.selectEducationPlaceholder}"
                        data-additionalquestions="${additionalQuestionsId}" ${sortableItem.attributes['required'].value}>
                </select>
                <input id="${hiddenKoulutusId}" name="${hiddenKoulutusId}" value="${categoryData[hiddenKoulutusId]}" type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div id="${additionalQuestionsId}" class="form-row">
    </div>
</div>
