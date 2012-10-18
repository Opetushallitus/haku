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

    <jsp:include page="../top/top.jsp"/>


                    <section id="vapaasanahaku" class="content-container">

                        

                        <div class="grid16-12">

                            <div class="tabs">
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="haut" class="tab current"><span>Koulutuksia (<c:out value="${searchResult.size}"/> kpl)</span></a>
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="koulutukset" class="tab"><span>Koulutustietoa (12 kpl)</span></a>
                                <a href="#" data-tabs-group="applicationtabs" data-tabs-id="hakukohteet" class="tab"><span>Tarinoita (3 kpl)</span></a>
                            </div>

                            <div class="tabsheets">
                                <section id="koulutuksia" style="display: block" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="haut">
                                    

                                            <jsp:include page="filters.jsp"/>
                                            
                                            
                                    <div class="grid16-10">
                                        <form id="hakutulokset" action="" method="post">

                                            <div class="toprow">
                                                <h1 class="set-left">Hakusana: <c:out value="${parameters.text}"/></h1>

                                                <div class="field-container-select set-right">
                                                    <select name="Aakkosjarjestys" placeholder="Järjestä aakkosittain" id="Aakkosjarjestys">
                                                        <option name="Aakkosjarjestys.Aakkosjarjestys.laskeva" value="Kevät 2013" selected="selected" id="Aakkosjarjestys-laskeva">laskeva</option>
                                                        <option name="Aakkosjarjestys.Aakkosjarjestys.nouseva" value="Syksy 2014" id="Aakkosjarjestys-nouseva">nouseva</option>
                                                    </select>
                                                </div>  

                                                <button class="set-right">
                                                    <span><span>aakkosjärjestys</span></span>
                                                </button>
                                                
                                            </div>
                                            
                                            <div class="clear"></div>

                                            <div class="form-item search-result">
                                                <ul class="result set-left" style="display: inline-block; margin-right: 20px">
                                                    <li><a href="tarjontatiedot/1" class="form-row-link bold">Liikunnanohjauksen perustutkinto PK; Liikunnanohjaaja; Kevät 2013; Kisakallion urheilupuisto; Lohja</a></li>
                                                    <li><a href="tarjontatiedot/1" class="form-row-link left-intend-2">- Liikunnanohjauksen koulutusohjelma</a></li>
                                                    
                                                </ul>
                                                <div class="result-options">
                                                    <div class="field-container-checkbox set-left left-intend-2" style="display: inline-block">
                                                        <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId" />
                                                        <label for="KenttaId">Lisää muistilistaan</label>
                                                    </div>

                                                    <div class="field-container-checkbox set-left left-intend-2">
                                                        <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId" />
                                                        <label for="KenttaId">Lisää vertailulistaan</label>
                                                    </div>
                                                </div>
                                            </div>  

                                            <div class="clear"></div>


                                            <div class="form-item search-result">
                                                <ul class="result set-left" style="display: inline-block; margin-right: 20px">
                                                    <li><a href="tarjontatiedot/1" class="form-row-link bold">Liikunnanohjauksen perustutkinto PK; Liikuntaneuvoja; Syksy 2013; Suomen Urheilupuisto; Vierumäki</a></li>
                                                    <li><a href="tarjontatiedot/1" class="form-row-link left-intend-2">- Liikunnanohjauksen koulutusohjelma</a></li>
                                                    
                                                </ul>
                                                <div class="result-options">
                                                    <div class="field-container-checkbox set-left left-intend-2" style="display: inline-block">
                                                        <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId" />
                                                        <label for="KenttaId">Lisää muistilistaan</label>
                                                    </div>

                                                    <div class="field-container-checkbox set-left left-intend-2">
                                                        <input type="checkbox" name="Kentta" value="Arvo" id="KenttaId" />
                                                        <label for="KenttaId">Lisää vertailulistaan</label>
                                                    </div>
                                                </div>
                                            </div>  

                                            <div class="clear"></div>
                                        </form>
                                    </div>
                                    <div class="clear"></div>
                                    
                                </section>
                                <section id="koulutustietoa" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="koulutukset">
                                    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                                    tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
                                    quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
                                    consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
                                    cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
                                    proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                                </section>
                                <section id="tarinoita" class="tabsheet" data-tabs-group="applicationtabs" data-tabs-id="hakukohteet">
                                    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                                    tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
                                    quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
                                    consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
                                    cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
                                    proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                                </section>
                            </div>
                        </div>
                        <div class="grid16-4">
                            <aside id="sidemenu">
                                <div id="notelist">
                                    <div class="heading">
                                        <h2>Muistilista</h2>
                                    </div>
                                    <div class="sidemenu-content">
                                        <ul>
                                            <li>Lorem ipsum dolor sit amet</li>
                                            <li>Lorem ipsum dolor sit amet</li>
                                        </ul>
                                        <form action="koulutuskuvaus.jsp" method="post">
                                            <button type="submit">
                                                <span><span>siirry muistilistaan</span></span>
                                            </button>
                                        </form>
                                        <p><i>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                                            tempor incididunt ut labore et dolore magna aliqua.</i></p>
                                    </div>
                                </div>
                                <div id="compare">
                                    <div class="heading">
                                        <h2>Vertailu</h2>
                                    </div>
                                    <div class="sidemenu-content">
                                        <ul class="removable-items">
                                            <li><a href="#" class="item">Lorem ipsum dolor sit amet</a><a class="btn-remove" href="#"></a></li>
                                            <li><a href="#" class="item">Lorem ipsum dolor sit amet</a><a class="btn-remove" href="#"></a></li>
                                            <li><a href="#" class="item">Lorem ipsum dolor sit amet</a><a class="btn-remove" href="#"></a></li>
                                            <li><a href="#" class="item">Lorem ipsum dolor sit amet</a><a class="btn-remove" href="#"></a></li>
                                        </ul>
                                        <button>
                                            <span><span>Vertaile</span></span>
                                        </button>
                                        <p><i>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                                        tempor incididunt ut labore et dolore magna aliqua.</i></p>
                                        <p><i>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
                                        tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
                                        quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
                                        consequat.</i></p>
                                    </div>
                                </div>
                                <div id="authentication">
                                    <div class="heading">
                                        <h2>Kirjautuminen</h2>
                                    </div>
                                    <div class="sidemenu-content">
                                
                                        <form id="kirjautuminen" action="/haku/j_spring_security_check" method="post">
                                            <legend class="h3">KÄYTTÄJÄTUNNUS</legend>
                                            <div class="form-item-content">
                                                <input type="text" name="j_username" id="kayttajatunnusId" size="30" />
                                            </div>
                                            <legend class="h3">SALASANA</legend>
                                            <div class="form-item-content">
                                                <input type="text" name="j_password" id="salasanaId" size="30" />
                                            </div>
                                            
                                            <input type="submit" value="Kirjaudu"/>
                                        </form> 
                                        <div class="clear"></div>
                                       

                                        <a href="#" class="helplink">?</a>
                                        <a href="#">Unohtuiko salasana?</a>
                                        <a href="#">Rekisteröidy palveluun</a>

                                    </div>
                                </div>
                            </aside>
                        </div>
                        <div class="clear"></div>
                    </section>
                </section>
                <footer id="sitefooter"></footer>
            </div>
            
        </div>
    </body>
</html>
