<%@ tag description="Officer's html meta data" body-content="empty" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="option" required="true" type="fi.vm.sade.haku.oppija.lomake.domain.elements.Element" %>
<%@ attribute name="selectedValue" required="true" type="java.lang.String" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<option value="${option.value}" ${selectedValue eq option.value ? "selected=\"selected\" " : " "} ><haku:i18nText
        value="${option.i18nText}"/>&nbsp;</option>
