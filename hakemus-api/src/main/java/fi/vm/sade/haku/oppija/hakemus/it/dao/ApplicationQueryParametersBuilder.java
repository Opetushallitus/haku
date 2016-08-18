package fi.vm.sade.haku.oppija.hakemus.it.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.*;

public class ApplicationQueryParametersBuilder {
    private String searchTerms;
    private List<String> state;
    private String paymentState;
    private List<String> asIds;
    private List<String> oids;
    private Boolean preferenceChecked;
    private String aoId;
    private String lopOid;
    private List<String> aoOids;
    private List<String> personOids;
    private Boolean discretionaryOnly;
    private Boolean primaryPreferenceOnly;
    private String sendingSchool;
    private String sendingClass;
    private Date updatedAfter;
    private int start = 0;
    private int rows = 0;
    private String orderBy;
    private int orderDir= 1;
    private String groupOid;
    private Set<String> baseEducation = new HashSet<>();
    private String organizationFilter;

    public ApplicationQueryParametersBuilder setStates(List<String> state) {
        this.state = state;
        return this;
    }

    public ApplicationQueryParametersBuilder setPaymentState(String paymentState) {
        this.paymentState = paymentState;
        return this;
    }

    public ApplicationQueryParametersBuilder setState(String state) {
        this.state = ImmutableList.of(state);
        return this;
    }

    public ApplicationQueryParametersBuilder setAsIds(List<String> asIds) {
        this.asIds = asIds;
        return this;
    }

    public ApplicationQueryParametersBuilder setAsId(String asId) {
        this.asIds = ImmutableList.of(asId);
        return this;
    }

    public ApplicationQueryParametersBuilder setAoId(String aoId) {
        this.aoId = aoId;
        return this;
    }

    public ApplicationQueryParametersBuilder setLopOid(String lopOid) {
        this.lopOid = lopOid;
        return this;
    }

    public ApplicationQueryParametersBuilder setAoOids(List<String> aoOids) {
        this.aoOids = aoOids;
        return this;
    }
    public ApplicationQueryParametersBuilder setOids(List<String> oids) {
        this.oids = oids;
        return this;
    }
    public ApplicationQueryParametersBuilder addAoOid(String... aoOids) {
        if (this.aoOids == null)
            this.aoOids = new ArrayList<>(aoOids.length);

        for (String aoOid : aoOids) {
            this.aoOids.add(aoOid);
        }
        return this;
    }

    public ApplicationQueryParametersBuilder setPersonOids(List<String> personOids) {
        this.personOids = personOids;
        return this;
    }

    public ApplicationQueryParametersBuilder addPersonOid(String... personOids) {
        if (this.personOids == null)
            this.personOids = new ArrayList<>(personOids.length);

        for (String personOid : personOids) {
            this.personOids.add(personOid);
        }
        return this;
    }

    public ApplicationQueryParametersBuilder setDiscretionaryOnly(Boolean discretionaryOnly) {
        this.discretionaryOnly = discretionaryOnly;
        return this;
    }

    public ApplicationQueryParametersBuilder setSendingSchool(String sendingSchool) {
        this.sendingSchool = sendingSchool;
        return this;
    }

    public ApplicationQueryParametersBuilder setSendingClass(String sendingClass) {
        this.sendingClass = sendingClass;
        return this;
    }

    public ApplicationQueryParametersBuilder setUpdatedAfter(Date updatedAfter) {
        this.updatedAfter = updatedAfter;
        return this;
    }

    public ApplicationQueryParametersBuilder setStart(int start) {
        this.start = start;
        return this;
    }

    public ApplicationQueryParametersBuilder setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public ApplicationQueryParametersBuilder setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public ApplicationQueryParametersBuilder setOrderDir(int orderDir) {
        this.orderDir = orderDir;
        return this;
    }

    public ApplicationQueryParametersBuilder setGroupOid(String groupOid) {
        this.groupOid = groupOid;
        return this;
    }

    public ApplicationQueryParametersBuilder setOrganizationFilter(String organizationFilter) {
        this.organizationFilter = organizationFilter;
        return this;
    }


    public ApplicationQueryParametersBuilder setBaseEducation(String baseEducation) {
        this.baseEducation.add(baseEducation);
        return this;
    }

    public ApplicationQueryParametersBuilder setBaseEducation(Set<String> baseEducation) {
        if (baseEducation == null) {
            baseEducation = Sets.newHashSet();
        }
        this.baseEducation = baseEducation;
        return this;
    }

    public ApplicationQueryParameters build() {
        return new ApplicationQueryParameters(searchTerms, state, paymentState, preferenceChecked, asIds, aoId, lopOid, aoOids, oids,
                personOids, groupOid, baseEducation, discretionaryOnly, primaryPreferenceOnly, sendingSchool,
                sendingClass, updatedAfter, start, rows, orderBy, orderDir, organizationFilter);
    }

    public ApplicationQueryParametersBuilder setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
        return this;
    }

    public ApplicationQueryParametersBuilder setPrimaryPreferenceOnly(Boolean primaryPreferenceOnly) {
        this.primaryPreferenceOnly = primaryPreferenceOnly;
        return this;
    }

    public ApplicationQueryParametersBuilder setPreferenceChecked(Boolean preferenceChecked) {
        this.preferenceChecked = preferenceChecked;
        return this;
    }
}
