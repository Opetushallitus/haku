<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

<!DOCTYPE html>
<html>
<jsp:include page="head.jsp"/>
<body>
<div id="viewport">
    <div id="overlay"></div>
    <div id="site">

        <jsp:include page="siteheader.jsp"/>

        <section id="page">

            <section id="pageheader" class="grid16-16">

                <jsp:include page="navigation.jsp"/>
                <jsp:include page="breadcrumb.jsp"/>

                <div id="searchfield-wrapper">
                    <div id="searchfield">
                        <form action="/haku/tarjontatiedot">
                            <div class="field-container-text">
                                <input type="text" name="text" required="required" value="${parameters.text}"
                                       class="haku"/>
                            </div>
                            <button class="btn-search" type="submit" id="btn-search" onclick="submit();">
                                <span><span></span></span>
                            </button>
                        </form>
                    </div>
                </div>

            </section>
            <div class="clear"></div>
