package fi.vm.sade.haku.oppija.common.organisaatio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OrganizationRestDTO {

    private String oid;
    private String parentOid;
    private String alkuPvm;
    private String loppuPvm;
    private Map<String, String> nimi;
    private List<String> tyypit;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }

    public String getParentOid() {
        return parentOid;
    }

    public void setParentOid(String parentOid) {
        this.parentOid = parentOid;
    }

    public List<String> getTyypit() {
        return tyypit;
    }

    public void setTyypit(List<String> tyypit) {
        this.tyypit = tyypit;
    }

    public String getAlkuPvm() {
        return alkuPvm;
    }

    public void setAlkuPvm(String alkuPvm) {
        this.alkuPvm = alkuPvm;
    }

    public String getLoppuPvm() {
        return loppuPvm;
    }

    public void setLoppuPvm(String loppuPvm) {
        this.loppuPvm = loppuPvm;
    }

    public Date getLoppuPvmAsDate() {
        return pvmAsDate(loppuPvm);
    }

    public Date getAlkuPvmAsDate() {
        return pvmAsDate(alkuPvm);
    }

    private Date pvmAsDate(String pvm) {
        Date date = null;
        if (isNotBlank(pvm)) {
            try {
                date = date().parse(pvm);
            } catch (ParseException e) {
                // FIXME this is probably not a good idea
                e.printStackTrace();
            }
        }
        return date;
    }

    private DateFormat date() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
