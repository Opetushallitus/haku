<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
<fmt:setBundle basename="messages"/>
<div class="grid16-6">
    <form id="hakusuodattimet" method="GET">
        <input type="hidden" name="text" value="${parameters.text}"/>
        <fieldset class="form-item">
            <div class="form-item-content">
                <div class="field-container-checkbox">
                    <input type="checkbox" name="tutkintoonjohtava" class="suodatin" value="DegreeProgramme"
                           id="tutkintoonjohtava" ${ (param['tutkintoonjohtava'] eq 'DegreeProgramme') ? "checked='checked'" : "" }/>
                    <label for="tutkintoonjohtava"><fmt:message
                            key="tarjonta.haku.näytävaintutkintoonjohtavakoulutus"/></label>
                </div>

                <div class="field-container-checkbox">
                    <input type="checkbox" class="suodatin" value="true" name="hakumeneillaan"
                           id="hakumeneillaan" ${ (param['hakumeneillaan'] eq 'true') ? "checked='checked'" : "" }/>
                    <label for="hakumeneillaan"><fmt:message key="tarjonta.haku.hakumeneilläänjuurinyt"/></label>
                </div>
            </div>
            <div class="clear"></div>
        </fieldset>
        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="koulutustyyppi"/>
        </jsp:include>

        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="pohjakoulutus"/>
        </jsp:include>

        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="koulutuksenkieli"/>
        </jsp:include>

        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="opetusmuoto"/>
        </jsp:include>

        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="oppilaitostyyppi"/>
        </jsp:include>

        <jsp:include page="sections/valintaruutuSuodatin.jsp">
            <jsp:param name="name" value="opintojenalkamisajankohta"/>
        </jsp:include>

        <jsp:include page="sections/valintalistaSuodatin.jsp">
            <jsp:param name="name" value="oppilaitostyyppi"/>
        </jsp:include>

        <jsp:include page="sections/valintalistaSuodatin.jsp">
            <jsp:param name="name" value="opintojenalkamisajankohta"/>
        </jsp:include>

        <div class="form-item">
            <legend class="h3"><fmt:message key="tarjonta.haku.sijainti"/></legend>
            <div class="form-item-content">
                <input type="text" name="location" size="30"/>
                <button class="plus">
                    <span><span></span></span>
                </button>
            </div>
            <div class="clear"></div>
        </div>

        <!--<button>
            <span><span>Lähetä</span></span>
        </button>-->
    </form>
</div>
