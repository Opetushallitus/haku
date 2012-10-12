<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <jsp:include page="head.jsp"/><body>
    <body>
        <div id="viewport">
            <div id="overlay"></div>
            <div id="site">

                <jsp:include page="siteheader.jsp"/><body>

				<section id="page">

					<section id="pageheader" class="grid16-16">

                        <jsp:include page="navigation.jsp"/><body>
                        <jsp:include page="breadcrumb.jsp"/><body>

                        <div id="searchfield-wrapper">
                            <div id="searchfield">
                                <form action="tarjontatiedot">
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