package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

public class ArvosanaDTO {

    private String id;
    private String aine;
    private String arvosana;
    private boolean valinnainen;
    private String lisatieto;

    public ArvosanaDTO(String id, String aine, String arvosana, Boolean valinnainen, String lisatieto) {
        this.id = id;
        this.aine = aine;
        this.arvosana = arvosana;
        this.valinnainen = valinnainen != null ? valinnainen.booleanValue() : false;
        this.lisatieto = lisatieto;
    }

    public ArvosanaDTO() {
        // NOP
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAine() {
        return aine;
    }

    public void setAine(String aine) {
        this.aine = aine;
    }

    public String getArvosana() {
        return arvosana;
    }

    public void setArvosana(String arvosana) {
        this.arvosana = arvosana;
    }

    public boolean isValinnainen() {
        return valinnainen;
    }

    public void setValinnainen(Boolean valinnainen) {
        if (valinnainen != null) {
            this.valinnainen = valinnainen.booleanValue();
        } else {
            this.valinnainen = false;
        }
    }

    public String getLisatieto() {
        return lisatieto;
    }

    public void setLisatieto(String lisatieto) {
        this.lisatieto = lisatieto;
    }
}
