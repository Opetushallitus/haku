<%@ page session="false"%>
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

<button class="reset" data-id="${sortableItem.id}" id="${sortableItem.id}-reset" type="button">
    <span>
        <span><haku:i18nText value="${sortableItem.resetLabel}"/></span>
    </span>
</button>
<div id="${sortableItem.id}-row-content" class="preference-fields">
    <c:set value="${sortableItem.learningInstitutionInputId}" var="textInputId" scope="page"/>
    <c:set value="${sortableItem.learningInstitutionInputId}-id" var="hiddenInputId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}" var="selectInputId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}-id" var="hiddenKoulutusId" scope="page"/>
    <c:set value="${sortableItem.educationInputId}-educationDegree" var="hiddenEducationDegreeId" scope="page"/>
    <c:set value="${sortableItem.id}-childLONames" var="childLONamesId" scope="page"/>
    <c:set value="${sortableItem.id}-attachments" var="attachmentsId" scope="page"/>
    <c:set value="${hiddenKoulutusId}-lang" var="hiddenKoulutusIdLang" scope="page"/>
    <c:set value="${hiddenKoulutusId}-educationcode" var="hiddenEducationCode" scope="page"/>
    <c:set value="${hiddenKoulutusId}-sora" var="hiddenKoulutusIdSora" scope="page"/>
    <c:set value="${hiddenKoulutusId}-aoIdentifier" var="hiddenKoulutusIdAoIdentifier" scope="page"/>
    <c:set value="${hiddenKoulutusId}-ao-groups" var="hiddenKoulutusIdAoGroups" scope="page"/>
    <c:set value="${hiddenKoulutusId}-athlete" var="hiddenKoulutusIdAthlete" scope="page"/>
    <c:set value="${hiddenKoulutusId}-kaksoistutkinto" var="hiddenKoulutusIdKaksoistutkinto" scope="page"/>
    <c:set value="${hiddenKoulutusId}-vocational" var="hiddenKoulutusIdVocational" scope="page"/>
    <c:set value="${hiddenKoulutusId}-attachmentgroups" var="hiddenAttachmentgroups" scope="page"/>
    <c:set value="${hiddenKoulutusId}-attachments" var="hiddenAttachments" scope="page"/>

    <haku:errorMessage id="${sortableItem.id}" additionalClass="margin-top-1"/>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required']}" for="${textInputId}"><haku:i18nText
                value="${sortableItem.learningInstitutionLabel}"/></label>

        <div class="form-row-content">
            <div class="field-container-text">
                <input id="${textInputId}"
                       name="${textInputId}"
                       <haku:value value='${answers[textInputId]}'/>
                       type="text"
                       data-special-id="preferenceLopInput"
                       data-selectinputid="${selectInputId}"
                       size="60"
                ${sortableItem.attributes['required']}/>
                <haku:errorMessage id="${textInputId}" additionalClass="margin-top-1"/>
                <input id="${hiddenInputId}" name="${hiddenInputId}"
                       <haku:value value='${answers[hiddenInputId]}'/>
                       type="hidden"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="form-row">
        <label class="form-row-label ${sortableItem.attributes['required']}" for="${selectInputId}"><haku:i18nText
                value="${sortableItem.educationLabel}"/></label>

        <div class="form-row-content">
            <div class="field-container-select">
                <select id="${selectInputId}" name="${selectInputId}"
                        data-childlonames="${childLONamesId}"
                        data-attachments="${attachmentsId}"
                        data-selectedname="<c:out value='${answers[selectInputId]}'/>"
                ${sortableItem.attributes['required']}>
                </select>
                <haku:errorMessage id="${selectInputId}" additionalClass="margin-top-1"/>
                <haku:input-hidden id="${hiddenKoulutusId}" data="${answers}"/>
                <haku:input-hidden id="${hiddenEducationDegreeId}" data="${answers}"/>
                <haku:input-hidden id="${hiddenEducationCode}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdLang}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdSora}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdAoIdentifier}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdAoGroups}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdAthlete}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdKaksoistutkinto}" data="${answers}"/>
                <haku:input-hidden id="${hiddenKoulutusIdVocational}" data="${answers}"/>
                <haku:input-hidden id="${hiddenAttachmentgroups}" data="${answers}"/>
                <haku:input-hidden id="${hiddenAttachments}" data="${answers}"/>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div id="container-${childLONamesId}" class="notification block light-grey margin-2" style="display: none">
        <span><haku:i18nText value="${sortableItem.childLONameListLabel}"/>:&nbsp;</span>
        <span id="${childLONamesId}"></span>
    </div>

    <haku:viewChilds element="${sortableItem}"/>
</div>
