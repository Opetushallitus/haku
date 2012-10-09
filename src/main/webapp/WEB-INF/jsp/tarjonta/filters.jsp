<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

<div class="grid16-6">
    <form id="hakutulokset" action="/haku/fi/tarjontatiedot" method="GET">

        <fieldset class="form-item">
            <div class="form-item-content">

                <div class="field-container-checkbox">
                    <input type="checkbox" name="suodatin" value="dolor" id="Liikennevaline-dolor"/>
                    <label for="Liikennevaline-dolor">Näytä vain tutkintoon johtava koulutus</label>
                </div>

                <div class="field-container-checkbox">
                    <input type="checkbox" name="suodatin" value="sit" id="Liikennevaline-sit"/>
                    <label for="Liikennevaline-sit">Haku meneillään juuri nyt</label>
                </div>
            </div>
            <div class="clear"></div>
        </fieldset>

        <c:forEach var="filter" items="${filters}">
            <fieldset class="form-item">
                <legend class="h3 form-item-label"><c:out value="${filter.name}"/></legend>
                <div class="form-item-content">
                    <c:forEach var="filterValue" items="${filter.filterValues}" varStatus="index">
                        <div class="field-container-checkbox">
                            <input class="haku_suodatin" type="checkbox" name='<c:out value="${filter.name}"/>' id="id_${filter.name}"
                                                               value='<c:out value="${filterValue.name}"/>' ${ (parameters.filters[filter.name][filterValue.name] eq filterValue.name) ? 'checked=checked' : ''} ></input>
                            <label for="id_${filter.name}"><c:out value="${filterValue.label}"/></label>
                        </div>
                    </c:forEach>
                </div>
                <div class="clear"></div>
            </fieldset>
        </c:forEach>

        <div class="form-item">
            <legend class="h3">SIJAINTI</legend>
            <div class="form-item-content">
                <input type="text" name="Kentta" id="KenttaId" size="30"/>
                <button class="plus">
                    <span><span></span></span>
                </button>
            </div>
            <div class="clear"></div>
        </div>

        <button>
            <span><span>Lähetä</span></span>
        </button>
    </form>
</div>
