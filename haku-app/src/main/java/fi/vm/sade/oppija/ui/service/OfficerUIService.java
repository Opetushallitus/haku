package fi.vm.sade.oppija.ui.service;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Map;

public interface OfficerUIService {
    UIServiceResponse getValidatedApplication(final String oid, final String phaseId) throws IOException, ResourceNotFoundException;

    UIServiceResponse getAdditionalInfo(final String oid) throws ResourceNotFoundException, IOException;

    UIServiceResponse updateApplication(final String oid, final ApplicationPhase applicationPhase) throws ResourceNotFoundException;

    Application getApplicationWithLastPhase(final String oid) throws ResourceNotFoundException;

    UIServiceResponse getOrganizationAndLearningInstitutions();

    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException;
}
