<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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

<div style="clear:both;">

    <div id="orgsearch" class="expand">
        <a href="#" class="expander">
        <span class="labelpos">
            <span class="label">
                Organisaatiohaku
            </span>
        </span>
        </a>

        <div class="expanded-content" style="">

            <form class="orgsearchform" style="">
                <fieldset>
                    <div class="field-search-containerbox">
                        <input type="text" name="" class="text search"/>
                    </div>

                    <div class="field-select-containerbox">
                        <select>
                            <option>Valitse organisaatiotyyppi</option>
                        </select>
                    </div>

                    <div class="field-select-containerbox">
                        <select>
                            <option>Valitse oppilaitostyyppi</option>
                        </select>
                    </div>

                    <div class="field-container-checkbox">
                        <input type="checkbox" name="" id="osc1" value=""/>
                        <label for="osc1">Näytä myös lakkautetut</label>
                    </div>
                    <div class="field-container-checkbox">
                        <input type="checkbox" name="" id="osc2" value=""/>
                        <label for="osc2">Näytä myös suunnitellut</label>
                    </div>


                    <button class="button small">Hae</button>
                    <button class="button small float-right">Tyhjennä</button>
                    <div class="clear"></div>
                </fieldset>
            </form>

            <div class="orgsearchlist">
                <ul class="treelist collapsible">
                    <li>
                        <span class="icon folder collapse open">&#8203;</span>
                        <a href="#" class="label">Rantalohjan lukio</a>
                        <ul class="branch">
                            <li>
                                <span class="icon file">&#8203;</span>
                                <a href="#" class="label">Rantalohjan lukio</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <span class="icon folder collapse open">&#8203;</span>
                        <a href="#" class="label">Rantalohjan ammattiopisto</a>
                        <ul class="branch">
                            <li>
                                <span class="icon file">&#8203;</span>
                                <a href="#" class="label">Terveyden ja hyvinvoinnin toimipiste</a>
                            </li>
                            <li>
                                <span class="icon file">&#8203;</span>
                                <a href="#" class="label">Autoalan toimipiste</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
