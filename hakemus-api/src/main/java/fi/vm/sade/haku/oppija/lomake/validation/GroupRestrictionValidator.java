package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

abstract public class GroupRestrictionValidator {
    protected final String groupId;
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
     * @param aoGroups aoId of input elem and groups it does have
     */
    public abstract Map<String, I18nText> validate(Map<String, List<String>> aoGroups);
}
