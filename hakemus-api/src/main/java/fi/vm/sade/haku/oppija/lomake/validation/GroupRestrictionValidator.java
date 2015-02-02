package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.SortedSet;

abstract public class GroupRestrictionValidator {
    public final String groupId;
    protected final I18nText errorMessage;

    protected GroupRestrictionValidator(I18nText errorMessage, final String groupId) {
        Validate.notNull(errorMessage, "ErrorMessage can't be null");
        this.errorMessage = errorMessage;
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("groupId be non empty");
        }
        this.groupId = groupId;
    }

    /**
     * @param inputAosInGroup input elems which are in this group
     */
    public abstract Map<String, I18nText> validate(SortedSet<String> inputAosInGroup);

}
