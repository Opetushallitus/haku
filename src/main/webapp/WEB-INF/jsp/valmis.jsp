<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
        <meta charset="utf-8"/>
        <link rel="stylesheet" href="/haku/resources/css/screen.css" type="text/css">
        <link rel="stylesheet" href="/haku/resources/jquery-ui-theme/jquery-ui-1.8.23.custom.css" type="text/css">
        <title>${form.title}</title>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
        <script src="/haku/resources/javascript/rules.js"></script>
        <script src="/haku/resources/javascript/master.js"></script>
    </head>
    <body>
        <div id="viewport">
            <div id="overlay"></div>
            <div id="site">

                <header id="siteheader">

                </header>

				<section id="page">

					<section id="pageheader" class="grid16-16">

						<nav class="main-navigation">
				            <ul class="navigation">
				                <li class="home"><a href="index.html">Etusivu</a></li>
				                <li><a href="lukio.html">Lukio</a></li>
				                <li><a href="#">Ammatillinen koulutus</a></li>
				                <li><a href="#">Ammattikorkeakoulu</a></li>
				                <li><a href="#">Yliopisto</a></li>
				                <li><a href="#">Täydennyskoulutus</a></li>
				                <li><a href="#">Opintojen valinta</a></li>
				            </ul>
				        </nav>

					</section>
					<div class="clear"></div>

					<section class="content-container">


						<div class="grid16-16">

                            <h1>Hakulomake</h1>

                            <h2>Ammatillisen koulutuksen ja lukiokoulutuksen yhteishaku, syksy 2012</h2>
                            <ul class="form-steps">
                                <c:forEach var="link" items="${form.navigation.children}" varStatus="status">
                                    <li><span><span class="index">${status.count}</span>${link.value} &gt;</span></li>
                                </c:forEach>
                                <li><a class="current"><span class="index"><c:out value="${fn:length(form.navigation.children) + 1}"/></span>Valmis</a></li>
                            </ul>
                            <div class="clear"></div>
						</div>
						<div class="clear"></div>

						<div class="form" data-form-step-id="7">
							<img src="content/Valmis-Kuva1.jpg" title="" alt="" class="set-right" />

							<h3 class="h2">Kiitos, hakemuksesi on vastaanotettu</h3>

							<p class="application-number">
							Hakulomakenumerosi: <span class="number"><c:out value="${applicationNumber}"/></span>
							</p>

							<c:if test="${(not empty categoryData['Sähköposti'])}">
                                <p>
                                Sinulle on lähetetty vahvistus sähköpostiisi: <c:out value="${categoryData['Sähköposti']}"/>
                                </p>
							</c:if>

							<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
							</p>

							<button class="print"><span><span>Tulosta</span></span></button>
							<button class="pdf"><span><span>Tallenna PDF</span></span></button>


							<div class="clear"></div>
							<hr/>

							<img src="content/Valmis-Kuva2.jpg" title="" alt="" class="set-left" />

							<h3>Muutoksen tekeminen</h3>

							<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
							</p>
							<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
							</p>

							<div class="clear"></div>
							<hr/>

							<img src="content/Valmis-Kuva3.jpg" title="" alt="" class="set-right" />

							<h3>Palautekysely</h3>

							<p>
							Anna palautetta palvelun toiminnasta vastaamalla lyhyeen kyselyyn. Voit vastata kyselyyn 26.6.2012 asti.
							</p>

							<p>
							<a href="#">Siirry palautekyselyyn</a>
							</p>

							<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam nisi nisl, dignissim id molestie non, vehicula eu risus. Donec eu magna neque, eget sodales lacus. Vivamus eget enim justo, sed consectetur enim. Curabitur nisl erat, egestas ut facilisis vel, interdum ac risus.
							</p>
							<div class="clear"></div>
						<div>

					</section>
				</section>
			</div>
		</div>
	</body>
</html>