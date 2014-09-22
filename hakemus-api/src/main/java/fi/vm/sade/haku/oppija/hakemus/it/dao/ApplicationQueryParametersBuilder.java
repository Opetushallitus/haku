package fi.vm.sade.haku.oppija.hakemus.it.dao;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

public class ApplicationQueryParametersBuilder {
    private String searchTerms;
    private List<String> state;
    private List<String> asIds;
    private String aoId;
    private String lopOid;
    private String aoOid;
    private Boolean discretionaryOnly;
    private String sendingSchool;
    private String sendingClass;
    private Date updatedAfter;
    private int start = 0;
    private int rows = 0;
    private String orderBy;
    private int orderDir= 1;
    private String groupOid;
    private String baseEducation;

    public ApplicationQueryParametersBuilder setStates(List<String> state) {
        this.state = state;
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

    public ApplicationQueryParametersBuilder setAoOid(String aoOid) {
        this.aoOid = aoOid;
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

    public ApplicationQueryParametersBuilder setBaseEducation(String baseEducation) {
        this.baseEducation = baseEducation;
        return this;
    }

    public ApplicationQueryParameters build() {
        return new ApplicationQueryParameters(searchTerms, state, asIds, aoId, lopOid, aoOid, groupOid, baseEducation,
                discretionaryOnly, sendingSchool, sendingClass, updatedAfter, start, rows, orderBy, orderDir);
    }

    public ApplicationQueryParametersBuilder setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
        return this;
    }
}
