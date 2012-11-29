package fi.vm.sade.oppija.haku;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Phase;


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

    public String getFormUrl(Phase phase) {
        return "lomake/" + getFirstApplicationPerioid().getId() + "/" + getFirstForm().getId() + "/" + phase.getId();
    }

    public String getFormId(Phase phase) {
        return "form-" + phase.getId();
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

    public Phase getFirstCategory() {
        return getFirstForm().getFirstCategory();
    }

    public Element getFirstCategoryFirstTeemaChild() {
        return getFirstCategory().getChildren().get(0).getChildren().get(0);
    }

    public String getStartUrl() {
        return getFormUrl(getFirstCategory());
    }
}
