package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:25 AM}
 * @since 1.1
 */
public class FormModel {

    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private org.bson.types.ObjectId _id;

    final Map<String, ApplicationPeriod> applicationPerioidMap;

    public FormModel() {
        this.applicationPerioidMap = new HashMap<String, ApplicationPeriod>();
    }

    public FormModel(Map<String, ApplicationPeriod> applicationPeriods) {
        this.applicationPerioidMap = applicationPeriods;
    }

    public ApplicationPeriod getActivePeriodById(String id) {
        final ApplicationPeriod applicationPeriod = applicationPerioidMap.get(id);
        if (applicationPeriod == null) throw new ResourceNotFoundException("not found");
        if (!applicationPeriod.isActive()) throw new ResourceNotFoundException("Not active");
        return applicationPeriod;
    }

    public void addApplicationPeriod(ApplicationPeriod applicationPeriod) {
        applicationPerioidMap.put(applicationPeriod.getId(), applicationPeriod);
    }

    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        return applicationPerioidMap;
    }

    public org.bson.types.ObjectId get_id() {
        return _id;
    }

    public void set_id(org.bson.types.ObjectId _id) {
        this._id = _id;
    }
}
