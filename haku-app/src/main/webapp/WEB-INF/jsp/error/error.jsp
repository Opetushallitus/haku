<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isErrorPage="true" %>
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
  <fmt:setBundle basename="messages" scope="application"/>
  <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
  <!DOCTYPE HTML>
  <html>
  <head>
    <meta charset="utf-8"/>
    <title>Opintopolku.fi</title>
    <link rel="stylesheet" href="${contextPath}/resources/css/oppija.css" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=PT+Sans+Narrow:700|PT+Serif:400italic" rel="stylesheet"
    type="text/css">
    <haku:icons/>
</head>
<body class="front-page" style="margin:10px 50px;">
    <header>
        <div class="logo-bg">
            <div class="container">
                <a id="home-link" href="/wp/fi/"><img src="${contextPath}/resources/img/Opintopolku_FI_logo.png"
                    alt="Opintopolku.fi"/></a>
            </div>
        </div>
    </header>
    
    <!-- suomeksi -->
    <p>Tapahtui odottamaton virhe. Pahoittelemme tapahtunutta.</p>
    <p>Palaa takaisin <a href="/wp/fi/">opintopolku.fi-palveluun</a> ja hae koulutukseen uudelleen.</p>
    <p>Mikäli ehdit lähettää hakulomakkeen, mutta et saanut kuittausta antamaasi sähköpostiin etkä ehtinyt tulostaa hakulomakettasi, ota yhteyttä</p>
    <ul>
        <li>haettuasi korkeakouluun</li>
        <ul>
            <li>ammattikorkeakoulujen hakijapalvelut:
                <a href="/wp/fi/ammattikorkeakoulu/ammattikorkeakoulujen-hakutoimistot-ja-hakijapalvelut">https://opintopolku.fi/wp/fi/ammattikorkeakoulu/ammattikorkeakoulujen-hakutoimistot-ja-hakijapalvelut</a>
            </li>
            <li>yliopistojen hakijapalvelut:
                <a href="/wp/fi/yliopisto/yliopistojen-hakijapalvelut">https://opintopolku.fi/wp/fi/yliopisto/yliopistojen-hakijapalvelut</a>
          </li>
        </ul>
        <li>haettuasi ammatillisiin koulutuksiin</li>
        <ul>
            <li>Opetushallitukseen: neuvonta@opintopolku.fi</li>
        </ul>
    </ul>

    <p>Ohjaus- ja neuvontapalvelut<br/>
        Opetushallitus<br/>
        PL 380<br/>
        00531 Helsinki<br/>
    </p>

    <!-- på svenska -->
    <hr role="presentation" class="margin-top-2"/>
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/wp/sv/"><img src="${contextPath}/resources/img/Opintopolku_SV_logo.png" alt="Studieinfo.fi"/></a>
        </div>
    </div>

    <p>Ett oförutsett fel inträffade. Vi beklagar.</p>
    <p>Gå tillbaka till <a href="/wp/sv/">studienfo.fi-tjänsten</a> och sök till utbildningen på nytt.</p>
    <p>Om du redan skickade ansökningsblanketten, men inte fick en kvittering i den e-post du angett och inte hann printa ut ansökningsblanketten, kan du kontakta</p>
    <ul>
        <li>om du sökt till högskola</li>
        <ul>
            <li>ansökningsservicen vid yrkeshögskolorna:
                <a href="/wp/sv/yrkeshogskola/yrkeshogskolornas-ansokningsbyraer-och-ansokningsservice">https://opintopolku.fi/wp/sv/yrkeshogskola/yrkeshogskolornas-ansokningsbyraer-och-ansokningsservice</a>
            </li>
            <li>ansökningsservicen vid universiteten:
                <a href="/wp/sv/universitet/universitetens-ansokningsservice">https://opintopolku.fi/wp/sv/universitet/universitetens-ansokningsservice</a>
          </li>
        </ul>
        <li>om du sökt till yrkesutbildning</li>
        <ul>
            <li>Utbildningsstyrelsen: info@studieinfo.fi</li>
        </ul>
    </ul>

    <p>
        Studieinfos ansökningstjänst<br />
        Utbildningsstyrelsen<br />
        PB 380<br />
        00531 Helsingfors<br />
    </p>

    <!-- in english -->
    <hr role="presentation" class="margin-top-2"/>
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/wp2/en/"><img src="${contextPath}/resources/img/Opintopolku_EN_logo.png" alt="Studyinfo.fi"/></a>
        </div>
    </div>

    <p>An unexpected error has occurred. We apologize for the inconvenience.</p>
    <p>Return to the <a href="/wp2/en/">studyinfo.fi –service</a> and apply for the study programme again.</p>
    <p>If you have already submitted your application form, but have not received a confirmation e-mail to the given e-mail address and you do not have a printed copy of your application form, please contact</p>
    <ul>
        <li>if you have applied to a higher education institution</li>
        <ul>
            <li>the admissions services at polytechnics/universities of applied sciences (UAS):
                <a href="/wp2/en/higher-education/polytechnics-universities-of-applied-sciences">https://opintopolku.fi/wp2/en/higher-education/polytechnics-universities-of-applied-sciences</a>
            </li>
            <li>the admissions services at universities:
                <a href="/wp2/en/higher-education/universities">https://opintopolku.fi/wp2/en/higher-education/universities</a>
            </li>
        </ul>
        
        
            
        
    

    <footer style="width: 100%;bottom: 0; position: fixed; float: right">${it.timestamp} &nbsp;${it.error_id}</footer>
    <!-- Piwik -->
    <script src="${contextPath}/resources/piwik/piwik.js" type="text/javascript"></script>
    <!-- End Piwik Code -->
</body>
</html>
