/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto;

import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.domain.Code;

import java.util.List;

public interface KoodistoService {
    List<SubjectRow> getSubjects();

    List<Option> getPostOffices();

    List<Option> getGradeRanges();

    List<Option> getSubjectLanguages();

    List<Option> getLearningInstitutionTypes();

    List<Option> getCountries();

    List<Option> getNationalities();

    List<Option> getLanguages();

    List<Option> getMunicipalities();

    List<Option> getLanguageAndLiterature();

    List<Option> getGenders();

    List<Option> getHakukausi();

    List<Option> getTeachingLanguages();

    List<Option> getOpintoalat();

    List<Option> getOpintoalat(String koulutusala);

    List<Option> getKoulutusalat();

    List<Code> getCodes(String koodistoUrl, int version);

    List<Code> getYliopistokoulutukset();

    List<Option> getLukioKoulukoodit();

    List<Option> getAmmattioppilaitosKoulukoodit();

    List<Option> getKorkeakouluKoulukoodit();

    List<Option> getAmmattitutkinnot();

    List<Option> getHakukohdekoodit();

    List<Option> getLaajuusYksikot();

    List<Option> getKorkeakouluTutkintotasot();

    List<Code> getAMKkoulutukset();

    List<Code> getYlemmatAMKkoulutukset();

    List<Option> getAmmatillisenTutkinnonArvosteluasteikko();

    List<Option> getYoArvosanaasteikko();

}
