<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="grid16-6">
    <ul class="minimal">
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Näytä vain tutkintoon johtava koulutus</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Haku meneillään juuri nyt</span>
        </li>
    </ul>

    <legend class="h3">KOULUTUSTYYPPI</legend>
    <ul class="minimal">
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Lukiokoulutus</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammatillinen koulutus</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammatti- tai erikoisammattitutkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammattikorkeakoulututkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ylempi ammattikorkeakoulututkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">kandidaatin tutkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Maisterin tutkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Lisä- ja täydennyskoulutus</span>
        </li>
    </ul>


    <legend class="h3">POHJAKOULUTUS</legend>
    <ul class="minimal">
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Perusopetus (peruskoulu)</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Lukio / Ylioppilas</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammatillinen perustutkinto (120 ov)</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammatti- tai erikoisammattitutkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">kouluasteen, oppiasteen tai ammatillisen korkea-asteen tutkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ammattikorkeakoulututkinto</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Kandidaatin tutkinto</span>
        </li>
    </ul>


    <legend class="h3">KOULUTUKSEN KIELI</legend>
    <ul class="minimal">
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Suomi</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Ruotsi</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Englanti</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Muut</span>
        </li>
    </ul>


    <legend class="h3">OPETUKSEN MUOTO</legend>
    <ul class="minimal">
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Lähiopetus</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Monimuoto-opetus</span>
        </li>
        <li class="field-container-checbox">
            <input type="checkbox"/>
            <span class="label">Etäopetus</span>
        </li>
    </ul>

    <div class="module">
        <legend class="h3">OPPILAITOSTYYPPI</legend>
        <div class="field-container-select">
            <select name="Oppilaitostyyppi" placeholder="Valitse kansalaisuus" id="Oppilaitostyyppi"
                    required="required">
                <option name="Oppilaitostyyppi.Oppilaitostyyppi.suomi" value="Kaikki" selected="selected"
                        id="Oppilaitostyyppi-suomi">Kaikki
                </option>
                <option name="Oppilaitostyyppi.Oppilaitostyyppi.ruotsi" value="Ammattikoulu"
                        id="Oppilaitostyyppi-ruotsi">Ammattikoulu
                </option>
            </select>
        </div>
    </div>

    <div class="module">
        <legend class="h3">OPINTOJEN ALKAMISAJANKOHTA</legend>
        <div class="field-container-select">
            <select name="Alkamisajankohta" placeholder="Valitse kansalaisuus" id="Alkamisajankohta"
                    required="required">
                <option name="Alkamisajankohta.Alkamisajankohta.suomi" value="Kevät 2013" selected="selected"
                        id="Alkamisajankohta-suomi">Kevät 2013
                </option>
                <option name="Alkamisajankohta.Alkamisajankohta.ruotsi" value="Syksy 2014" id="Alkamisajankohta-ruotsi">
                    Syksy 2014
                </option>
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
                <input type="checkbox"/>
                <span class="label">Näytä myös ne koulutukset, joiden hakuaika on päättynyt.</span>
            </li>
        </ul>
    </div>
</div>
