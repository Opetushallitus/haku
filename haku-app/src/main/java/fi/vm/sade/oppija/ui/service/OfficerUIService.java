package fi.vm.sade.oppija.ui.service;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Map;

public interface OfficerUIService {
    UIServiceResponse getApplicationElement(final String oid,
                                            final String phaseId,
                                            final String elementId,
                                            final boolean validate)
            throws ResourceNotFoundException;

    UIServiceResponse getValidatedApplication(final String oid, final String phaseId)
            throws IOException, ResourceNotFoundException;

    UIServiceResponse getAdditionalInfo(final String oid) throws ResourceNotFoundException, IOException;

    UIServiceResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user)
            throws ResourceNotFoundException;

    Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException;

    UIServiceResponse getOrganizationAndLearningInstitutions();

    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo)
            throws ResourceNotFoundException;

    void addPersonAndAuthenticate(final String oid)
            throws ResourceNotFoundException;

    Application passivateApplication(String oid, String reason, User user) throws ResourceNotFoundException;

    void addNote(String applicationOid, String note, User user) throws ResourceNotFoundException;

    Application createApplication(final String asId);

    void addPersonOid(final String oid, final String personOid) throws ResourceNotFoundException;
}
