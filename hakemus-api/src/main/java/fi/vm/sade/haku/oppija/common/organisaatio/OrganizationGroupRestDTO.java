package fi.vm.sade.haku.oppija.common.organisaatio;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.List;

public class OrganizationGroupRestDTO {

//    {
//        "oid": "1.2.246.562.28.30956399732",
//        "nimi": {
//            "fi": "Multitest"
//        },
//        "kuvaus2": { },
//        "yhteystietoArvos": [ ],
//        "yhteystiedot": [ ],
//        "parentOidPath": "|1.2.246.562.10.00000000001|",
//        "vuosiluokat": [ ],
//        "tyypit": [
//            "Ryhma"
//        ],
//        "parentOid": "1.2.246.562.10.00000000001",
//        "kieletUris": [ ],
//        "ryhmatyypit": [
//            "hakukohde"
//        ],
//        "kayttoryhmat": [
//            "hakukohde_liiteosoite"
//        ],
//        "kayntiosoite": { },
//        "postiosoite": { },
//        "toimipistekoodi": "",
//        "version": 1
//    }

    private String oid;
    private I18nText nimi;
    private List<String> tyypit;
    private List<String> kayttoRyhmat;

    public OrganizationGroupRestDTO(String oid, I18nText nimi, List<String> tyypit, List<String> kayttoRyhmat) {
        this.oid = oid;
        this.nimi = nimi;
        this.tyypit = tyypit;
        this.kayttoRyhmat = kayttoRyhmat;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public I18nText getNimi() {
        return nimi;
    }

    public void setNimi(I18nText nimi) {
        this.nimi = nimi;
    }

    public List<String> getTyypit() {
        return tyypit;
    }

    public void setTyypit(List<String> tyypit) {
        this.tyypit = tyypit;
    }

    public List<String> getKayttoRyhmat() {
        return kayttoRyhmat;
    }

    public void setKayttoRyhmat(List<String> kayttoRyhmat) {
        this.kayttoRyhmat = kayttoRyhmat;
    }
}
