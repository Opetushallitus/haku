<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                    <section id="pageheader">
                        <jsp:include page="navigation.jsp"/>
                    </section>

                    <section id="koulutuskuvaus">
                        <div class="grid16-4">
                            <jsp:include page="sections/infobox.jsp"/>
                            <jsp:include page="sections/notelist-simple.jsp"/>
                        </div>

                        <div class="grid16-8">


                            <jsp:include page="definitions.jsp"/>

                            <div><a href="vapaasanahaku.html">Takaisin hakutuloksiin</a></div>
                            <div class="clear"></div>

                            <div class="pagetitle">
                                <h1>Liiketalouden koulutusohjelma 210op</h1>
                                <a href="#">Liikunnanohjauksen koulutusohjelma</a>
                            </div>

                            <div class="tabs">
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="kuvaus"
                                   class="tab current"><span>Koulutuksen kuvaus</span></a>
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakeutuminen" class="tab"><span>Koulutukseen hakeutuminen</span></a>
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="opiskelupaikka" class="tab"><span>Kisakallion urheilupuisto</span></a>
                            </div>

                            <jsp:include page="sections/kuvaus.jsp"/>

                        </div>
                        <div class="grid16-4">
                            <c:set var="infobox" value="${test}" scope="request"/>
                            <jsp:include page="sections/infobox.jsp">
                                <jsp:param name="infobox" value="${test}"/>
                            </jsp:include>
                            <aside id="sidemenu">
                                <jsp:include page="sections/notelist.jsp"/>
                                <jsp:include page="compare.jsp"/>
                                <jsp:include page="authentication.jsp"/>
                            </aside>
                        </div>
                        <div class="clear"></div>
                    </section>
                </section>
                <footer></footer>
            </div>
        </div>
    </body>
</html>
