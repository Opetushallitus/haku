package fi.vm.sade.haku.oppija.ui.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.ui.controller.dto.EligibilitiesDTO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface OfficerUIService {
    ModelResponse getApplicationElement(final String oid,
                                        final String phaseId,
                                        final String elementId,
                                        final boolean validate);

    ModelResponse getApplicationMultiElement(final String oid,
                                        final String phaseId,
                                        final List<String> elementIds,
                                        final boolean validate,
                                        final Map<String, String> currentAnswers);

    ModelResponse getValintaTab(final String oid) throws IOException;

    ModelResponse getValidatedApplication(final String oid, final String phaseId, final boolean withValintatiedot) throws IOException;

    ModelResponse getAdditionalInfo(final String oid);

    ModelResponse updateApplication(final String oid, final ApplicationPhase applicationPhase, User user) throws IOException;

    Application getApplicationWithLastPhase(final String oid);

    ModelResponse getOrganizationAndLearningInstitutions();

    List<ApplicationSystem> getApplicationSystems();

    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo);

    void addNote(String applicationOid, String note);

    Application createApplication(final String asId);

    void postProcess(final String oid, final boolean email);

    void changeState(String oid, Application.State state, String reason);

    ModelResponse getMultipleApplicationResponse(String applicationList, String selectedApplication) throws IOException;

    List<Map<String, Object>> getSchools(String term) throws UnsupportedEncodingException;

    List<Map<String, Object>> getPreferences(String term);

    List<Map<String, Object>> getGroups(String term) throws IOException;

    ModelResponse getApplicationPrint(final String oid);

    List<Map<String,String>> getHigherEdBaseEdOptions();

    void processAttachmentsAndEligibilities(String oid, EligibilitiesDTO attachmentsAndEligibilities);

    Map<String, String> getNamesForNoteUsers(List<String> personOids);
}
