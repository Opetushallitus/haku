<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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

<jsp:include page="../top/top.jsp"/>


<section id="koulutuskuvaus">
    <div class="grid16-4">
        <jsp:include page="sections/koulutuksenPerustiedot.jsp"/>
        <jsp:include page="sections/notelist-simple.jsp"/>
    </div>

    <div class="grid16-8">
        <jsp:include page="sections/definitions.jsp"/>
        <div><a href="vapaasana.html">Takaisin hakutuloksiin</a></div>

        <div class="clear"></div>
        <div class="pagetitle">
            <h1>${it.searchResult['AOTitle']}</h1>
            <a href="#">${it.searchResult['LOSDegreeTitle']}</a>

            <div class="set-right">
                <jsp:include page="sections/muistiJaVertailuValitsimet.jsp"/>
            </div>

        </div>

        <jsp:include page="sections/koulutustiedonvalilehdet.jsp"/>

    </div>
    <div class="grid16-4">
        <jsp:include page="sections/hakufaktoja.jsp"/>
        <aside id="sidemenu">
            <jsp:include page="sections/infobox.jsp"/>
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
