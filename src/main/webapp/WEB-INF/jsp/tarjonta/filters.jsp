<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="hakutulokset" action="/haku/fi/tarjontatiedot" method="GET">
    <input type="hidden" name="text" value="<c:out value='${parameters.text}'/>">
    <div class="grid16-6">
        <ul class="minimal">
            <li class="field-container-checbox">
                <input type="checkbox" name="tutkintoon_johtava" value="true"/>
                <span class="label">Näytä vain tutkintoon johtava koulutus</span>
            </li>
            <li class="field-container-checbox">
                <input type="checkbox" name="haku_nyt"/>
                <span class="label">Haku meneillään juuri nyt</span>
            </li>
        </ul>


        <c:forEach var="filter" items="${filters}">
            <legend class="h3"><c:out value="${filter.name}"/></legend>
            <ul class="minimal">
                <c:forEach var="filterValue" items="${filter.filterValues}" varStatus="index">
                    <li class="field-container-checbox">
                        <input class="haku_suodatin" type="checkbox" name='<c:out value="${filter.name}"/>' value='<c:out value="${filterValue.name}"/>' ${ (parameters.filters[filter.name][filterValue.name] eq filterValue.name) ? 'checked=checked' : ''} ></input>

                        <span class="label"><c:out value="${filterValue.label}" /></span>
                    </li>
                </c:forEach>
            </ul>
        </c:forEach>

        <div class="module">
            <legend class="h3">OPINTOJEN ALKAMISAJANKOHTA</legend>
            <div class="field-container-select">
                <select name="Alkamisajankohta" placeholder="Valitse alkamisajankohta" id="Alkamisajankohta"
                        required="required">
                    <option name="alkamisajankohta" value="Kevät 2013" selected="selected">Kevät 2013</option>
                    <option name="alkamisajankohta" value="Kevät 2013">Syksy 2014</option>
                </select>
            </div>
        </div>

        <div class="module">
            <legend class="h3">SIJAINTI</legend>
            <input type="text" value="" id="Sijainti"/>
            <button>
                <span><span>plusbutton</span></span>
            </button>

            <ul class="minimal">
                <li class="field-container-checbox">
                    <input type="checkbox" name="näytä_päättyneet"/>
                    <span class="label">Näytä myös ne koulutukset, joiden hakuaika on päättynyt.</span>
                </li>
            </ul>
        </div>
    </div>
    <input type="submit" value="päivitä" id="update">
</form>
