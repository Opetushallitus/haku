package fi.vm.sade.oppija.haku;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;


/**
 * @author jukka
 * @version 9/17/122:20 PM}
 * @since 1.1
 */
public class FormModelHelper {
    private final FormModel formModel;

    public FormModelHelper(FormModel formModel) {
        this.formModel = formModel;

    }

    public String getFormUrl(Vaihe vaihe) {
        return "lomake/" + getFirstApplicationPerioid().getId() + "/" + getFirstForm().getId() + "/" + vaihe.getId();
    }

    public String getFormId(Vaihe vaihe) {
        return "form-" + vaihe.getId();
    }

    public String getFirstCategoryFormId() {
        return "form-" + getFirstCategory().getId();
    }

    public Form getFirstForm() {
        return getFirstApplicationPerioid().getForms().values().iterator().next();
    }

    public ApplicationPeriod getFirstApplicationPerioid() {
        return formModel.getApplicationPerioidMap().entrySet().iterator().next().getValue();
    }

    public Vaihe getFirstCategory() {
        return getFirstForm().getFirstCategory();
    }

    public Element getFirstCategoryChild() {
        return getFirstCategory().getChildren().get(0);
    }

    public String getStartUrl() {
        return getFormUrl(getFirstCategory());
    }
}
