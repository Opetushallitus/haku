package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

public class ArvioDTO {

    private String arvosana;
    private String asteikko;
    // Käytössä yo-arvosanoilla
    private Integer pisteet;


//        "arvio": {
//            "arvosana": "9",
//            "asteikko": "4-10"
//        },

    public ArvioDTO(String arvosana, String asteikko, Integer pisteet) {
        this.arvosana = arvosana;
        this.asteikko = asteikko;
        this.pisteet = pisteet;
    }

    public ArvioDTO() {
        // NOP
    }

    public String getArvosana() {
        return arvosana;
    }

    public void setArvosana(String arvosana) {
        this.arvosana = arvosana;
    }

    public String getAsteikko() {
        return asteikko;
    }

    public void setAsteikko(String asteikko) {
        this.asteikko = asteikko;
    }

    public Integer getPisteet() {
        return pisteet;
    }

    public void setPisteet(Integer pisteet) {
        this.pisteet = pisteet;
    }

}
