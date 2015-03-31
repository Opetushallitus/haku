package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;

import java.util.List;
import java.util.Map;

public interface HakuPermissionService {

    String ROLE_OPO = "APP_HAKEMUS_OPO";
    String ROLE_RU = "APP_HAKEMUS_READ_UPDATE";
    String ROLE_CRUD = "APP_HAKEMUS_CRUD";
    String ROLE_LISATIETORU = "APP_HAKEMUS_LISATIETORU";
    String ROLE_LISATIETOCRUD = "APP_HAKEMUS_LISATIETOCRUD";
    String ROLE_HETUTTOMIENKASITTELY = "APP_HAKEMUS_HETUTTOMIENKASITTELY";

    /**
     * Return list of organizations such that current user has at least read privilege to applications
     * addressed to that organization. Check is done against every organization user is member of.
     *
     * @return list of organization oids
     */
    List<String> userCanReadApplications();

    /**
     * Return list of organizations such that current user has at least read privilege to applications
     * addressed to that organization. Check is done against the list of organizations given as parameter.
     *
     * @param organizations list of organizations oids to check against
     * @return list of organization oids
     */
    List<String> userCanReadApplications(List<String> organizations);

    /**
     * Return list of organizations such that current user has opo privilege to applications coming from students
     * of that organization. Check is done against every organization user is member of.
     *
     * @return list of organization oids
     */
    List<String> userHasOpoRole();

    List<String> userHasHetuttomienKasittelyRole();

    List<String> userHasHetuttomienKasittelyRole(List<String> organizations);

    /**
     * Return list of organizations such that current user has opo privilege to applications coming from students
     * of that organization. Check is done against the list of organizations given as parameter.
     *
     * @param organizations list of organizations oids to check against
     * @return list of organization oids
     */
    List<String> userHasOpoRole(List<String> organizations);

    boolean userCanReadApplication(Application application);

    Map<String, Boolean> userHasEditRoleToPhases(Application application, Form form);

    boolean userCanDeleteApplication(Application application);

    boolean userCanPostProcess(Application application);

    boolean userCanEnterApplication();

    List<String> userCanEnterApplications();

    boolean userCanSearchBySendingSchool();

    boolean userCanEditApplicationAdditionalData(Application application);
}
