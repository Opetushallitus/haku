<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="haku" tagdir="/WEB-INF/tags" %>
<p><a href="<haku:i18nText value="${element.url}"/>" id="${element.id}" ${element.attributeString}><haku:i18nText value="${element.i18nText}"/></a></p>
<haku:viewChilds element="${element}"/>
