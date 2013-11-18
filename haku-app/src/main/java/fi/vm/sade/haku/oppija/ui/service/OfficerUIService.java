package fi.vm.sade.haku.oppija.ui.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Map;

public interface OfficerUIService {
    UIServiceResponse getApplicationElement(final String oid,
                                            final String phaseId,
                                            final String elementId,
                                            final boolean validate)
            throws ResourceNotFoundException;

    UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws ResourceNotFoundException;

    UIServiceResponse getAdditionalInfo(final String oid) throws ResourceNotFoundException, IOException;

    UIServiceResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user)
            throws ResourceNotFoundException;

    Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException;

    UIServiceResponse getOrganizationAndLearningInstitutions();

    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo)
            throws ResourceNotFoundException;

    void addPersonAndAuthenticate(final String oid) throws ResourceNotFoundException;

    Application passivateApplication(String oid, String reason) throws ResourceNotFoundException;

    void addNote(String applicationOid, String note) throws ResourceNotFoundException;

    Application createApplication(final String asId);

    void addStudentOid(final String oid) throws ResourceNotFoundException;

    void postProcess(final String oid) throws ResourceNotFoundException;

    Application activateApplication(String oid, String reason) throws ResourceNotFoundException;
}
