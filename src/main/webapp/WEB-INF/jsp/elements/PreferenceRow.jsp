<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<button class="reset" data-id="${sortableItem.id}" type="button">
    <span>
        <span><c:out value="${sortableItem.resetLabel}"/></span>
    </span>
</button>
<div class="preference-fields">
    <div class="form-row">
        <c:set value="${sortableItem.id}-Opetuspiste" var="textInputId" scope="page"/>
        <label class="form-row-label" for="${textInputId}"><c:out value="${sortableItem.learningInstitutionLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-text">
                <input id="${textInputId}" name="${textInputId}" value="${categoryData[textInputId]}" type="text" />
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="form-row">
        <c:set value="${sortableItem.id}-Koulutus" var="selectInputId" scope="page"/>
        <label class="form-row-label" for="${selectInputId}"><c:out value="${sortableItem.educationLabel}"/></label>
        <div class="form-row-content">
            <div class="field-container-select">
                <select id="${selectInputId}" name="${selectInputId}" placeholder="${sortableItem.selectEducationPlaceholder}">
                    <option></option>
                    <c:forEach var="option" items="${sortableItem.options}">
                        <c:set value="${sortableItem.id}.${option.id}" var="optionId" scope="page"/>
                        <option name="${optionId}"
                                value="${option.value}"
                                ${(categoryData[selectInputId] eq option.value) ? "selected=\"selected\" " : " "}
                                ${option.attributeString}>${option.title}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>
<script type="text/javascript" src="/haku/resources/javascript/preferencerow.js"></script>