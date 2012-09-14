<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE hml>
<html>
	<meta>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/styles.css" />" />
	</meta>
	<body>
		<div id="wrapper">
			<nav id="main-navigation">
				<h3>Oppijan verkkopalvelun päänavigaatio</h3>
			</nav>
			<div id="content-area">
				<div class="page-column" hidden="true">
					<div class="column-content">
						<aside id="page-navigation">
							<div class="page-navigation-header">
								<h3>Koulutuskori</h3>
							</div>
							<div class="page-navigation-content">
								<h3>Kiinnostavat koulutukset</h3>
								<ul>
									<li>
										<a href="#">Korkeakoulu, tutkinto, paikkakunta, koulutus</a>
									</li>
									<li>
										<a href="#">Korkeakoulu, tutkinto, paikkakunta, koulutus</a>
									</li>
									<li>
										<a href="#">Korkeakoulu, tutkinto, paikkakunta, koulutus</a>
									</li>
									<li>
										<a href="#">Korkeakoulu, tutkinto, paikkakunta, koulutus</a>
									</li>
									<li>
										<a href="#">Korkeakoulu, tutkinto, paikkakunta, koulutus</a>
									</li>
									<div class="clear"></div>
								</ul>
								<div class="tools">
									<form id="page-navigation-tools">
										<input type="text" class="search" />
										<input type="submit" value="Täytä lomake" />
									</form>
								</div>
							</div>
						</aside>
					</div>
				</div>
				<div class="page-column">
					<div class="column-content">
						<div id="hakuprosessi">
							<hgroup id="page-topic">
								<h1>Hakulomake</h1>
								<h3>Korkeakoulujen yhteishaku, syksy 2012</h3>
							</hgroup>

							<div class="application-navigation">
								<ul>
								    <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                                     <li class="item">
                                        <a ${link.attributeString}>${link.value}</a>&nbsp;
                                     </li>
                                     <c:if test="${not status.last}">
                                        <li class="spacer">></li>
                                     </c:if>
                                    </c:forEach>
									<div class="clear"></div>
								</ul>


							</div> <!-- application-navigation -->

							<div class="clear"></div>
							<div class="application-content">
								<div class="form-wrapper grid16-12">
									<form id="haku-henkilotiedot" method="post">
                                        <c:forEach var="child" items="${category.children}">
                                            <c:set var="element" value="${child}" scope="request"/>
                                            <jsp:include page="elements/${child.type}.jsp"/>
                                        </c:forEach>

                                        <c:choose>
                                            <c:when test="${category.hasPrev}">
                                                <input type="submit" value="Edellinen" />
                                            </c:when>
                                        </c:choose>
                                        <c:choose>
                                            <c:when test="${category.hasNext}">
                                                <input type="submit" value="Seuraava" />
                                            </c:when>
                                            <c:when test="${!category.hasNext}">
                                                <input type="submit" value="Tallenna" />
                                            </c:when>
                                        </c:choose>
  
									</form>
								</div>
								<div class="application-help grid16-4">
									<h4>Lorem</h4>
									<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
					tempor incididunt ut labore et dolore magna aliqua.</p>
									<p>Duis aute irure dolor in reprehenderit in voluptate velit esse
					cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
					proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
									<h4>Ipsum</h4>
									<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
					tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
					quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
					consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
					cillum dolore eu fugiat nulla pariatur.</p>
									<p>Excepteur sint occaecat cupidatat non
					proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
					</p>
								</div>
							</div> <!-- application-content -->
						</div>
					</div>
				</div> 
			</div> <!-- content-area -->
			<div class="clear"></div>
			<footer>
				<div class="footer-content">
					<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
					tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
					quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
					consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
					cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
					proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
					</p>
				</div>
			</footer>
		</div>
	</body>
</html>

