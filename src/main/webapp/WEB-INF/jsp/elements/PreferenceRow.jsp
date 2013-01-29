<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>

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

<button class="reset" data-id="${sortableItem.id}" type="button">
    <span>
        <span><haku:i18nText value="${sortableItem.resetLabel}"/></span>
    </span>
</button>
<div class="preference-fields">
    <c:set value="${sortableItem.learningInstitutionInputId}" var="textInputId" scope="page"/>
    <c:set value="${sortableItem.learningInstitutionInputId}-id" var="hiddenInputId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}" var="selectInputId" scope="page"/>
    <c:set value="${sortableItem.id}-Lisakysymykset" var="additionalQuestionsId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}-id" var="hiddenKoulutusId" scope="page"/>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required'].value}" for="${textInputId}"><haku:i18nText
                value="${sortableItem.learningInstitutionLabel}"/></label>

        <div class="form-row-content">
            <div class="field-container-text">
                <input id="${textInputId}"
                       name="${textInputId}"
                       value="${categoryData[textInputId]}"
                       type="text"
                       data-selectinputid="${selectInputId}"
                       size="60"
                ${sortableItem.attributes['required'].value}/>
                <haku:errorMessage id="${textInputId}" additionalClass="margin-top-1"/>
                <input id="${hiddenInputId}" name="${hiddenInputId}" value="${categoryData[hiddenInputId]}"
                       type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required'].value}" for="${selectInputId}"><haku:i18nText
                value="${sortableItem.educationLabel}"/></label>

        <div class="form-row-content">
            <div class="field-container-select">
                <select id="${selectInputId}" name="${selectInputId}"
                        placeholder="${sortableItem.selectEducationPlaceholder}"
                        data-additionalquestions="${additionalQuestionsId}" ${sortableItem.attributes['required'].value}>
                </select>
                <haku:errorMessage id="${selectInputId}" additionalClass="margin-top-1"/>
                <input id="${hiddenKoulutusId}" name="${hiddenKoulutusId}" value="${categoryData[hiddenKoulutusId]}"
                       type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div id="${additionalQuestionsId}" class="form-row">
    </div>
</div>
