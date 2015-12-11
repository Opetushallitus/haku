package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.impl.SystemSession;
import fi.vm.sade.haku.oppija.postprocess.upgrade.*;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.haku.oppija.postprocess.UpgradeWorker;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

@Service
public class UpgradeWorkerImpl implements UpgradeWorker{

    public static final Logger LOGGER = LoggerFactory.getLogger(UpgradeWorkerImpl.class);

    private static final SystemSession systemSession = new SystemSession();
    private final ApplicationDAO applicationDAO;
    private final ApplicationService applicationService;
    private final LoggerAspect loggerAspect;
    private final StatusRepository statusRepository;
    private final KoodistoService koodistoService;

    private final List<ModelUpgrade<Application>> applicationUpgrades = new ArrayList<>();

    final String SYSTEM_USER = "järjestelmä";

    @Value("${scheduler.maxBatchSize:10}")
    private int maxBatchSize;
    @Value("${scheduler.modelUpgrade.enableV2:false}")
    private boolean enableUpgradeV2;
    @Value("${scheduler.modelUpgrade.enableV3:true}")
    private boolean enableUpgradeV3;
    @Value("${scheduler.modelUpgrade.enableV4:true}")
    private boolean enableUpgradeV4;
    @Value("${scheduler.modelUpgrade.enableV5:true}")
    private boolean enableUpgradeV5;
    @Value("${scheduler.modelUpgrade.enableV6:true}")
    private boolean enableUpgradeV6;
    @Value("${scheduler.modelUpgrade.enableV7:true}")
    private boolean enableUpgradeV7;

    @Autowired
    public UpgradeWorkerImpl(ApplicationService applicationService,
                             ApplicationDAO applicationDAO,
                             StatusRepository statusRepository,
                             fi.vm.sade.log.client.Logger logger,
                             AuditLogRepository auditLogRepository,
                             KoodistoService koodistoService,
                             @Value("${server.name}") final String serverName) {
        this.loggerAspect = new LoggerAspect(logger, systemSession, auditLogRepository, serverName);
        this.applicationService = applicationService;
        this.applicationDAO = applicationDAO;
        this.statusRepository = statusRepository;
        this.koodistoService = koodistoService;
    }

    @PostConstruct
    public void configure(){
        if (enableUpgradeV5)
            this.applicationUpgrades.add(new ApplicationModelV5Upgrade(koodistoService, loggerAspect));
        if (enableUpgradeV6)
            this.applicationUpgrades.add(new ApplicationModelV6Upgrade(loggerAspect));
        if (enableUpgradeV7)
            this.applicationUpgrades.add(new ApplicationModelV7Upgrade(applicationService));
    }


    @Override
    public void processModelUpdate() {
        LOGGER.info("Start upgrading application model");
        oldProcess();
        upgradeModelVersion2to3();
        upgradeModelVersion3to4();
        modelUpgrade();
        LOGGER.info("Done upgrading application model");
    }

    private void oldProcess() {
        final Integer baseVersion =1;
        final Integer targetVersion =2;
        if ((!enableUpgradeV2) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion))){
            return;
        }

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while (applications.size() > 0) {
            for (Application application : applications) {
                statusRepository.startOperation(SCHEDULER_MODEL_UPGRADE + " old process", application.getOid());
                Application queryApplication = new Application(application.getOid(), application.getVersion());

                try {
                    LOGGER.info("Start upgrading model version for application: " + application.getOid());
                    if (null == application.getAuthorizationMeta()) {
                        application = applicationService.updateAuthorizationMeta(application);
                    }
                    if (null == application.getPreferenceEligibilities() || 0 == application.getPreferenceEligibilities().size() ||
                      null == application.getPreferencesChecked() || 0 == application.getPreferencesChecked().size()){
                        application = applicationService.updatePreferenceBasedData(application).getApplication();
                    }
                    application.setModelVersion(targetVersion);
                    LOGGER.info("Done upgrading model version for application: " + application.getOid());
                } catch (IOException | RuntimeException e) {
                    application.setModelVersion(-1 * targetVersion);
                    LOGGER.error("Upgrading model to "+ targetVersion+" failed for application: " + application.getOid() + " " + e.getMessage());
                } finally {
                    applicationDAO.update(queryApplication, application);
                }
                statusRepository.endOperation(SCHEDULER_MODEL_UPGRADE + " old process", application.getOid());
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void upgradeModelVersion2to3() {
        final Integer baseVersion = 2;
        final Integer targetVersion = 3;
        if ((!enableUpgradeV3) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion)))
            return;

        List<String> pk = new ArrayList<String>() {{
            add(OppijaConstants.PERUSKOULU);
            add(OppijaConstants.OSITTAIN_YKSILOLLISTETTY);
            add(OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY);
            add(OppijaConstants.YKSILOLLISTETTY);
        }};

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while (applications.size() > 0) {
            for (Application application : applications) {
                statusRepository.startOperation(SCHEDULER_MODEL_UPGRADE + " v3", application.getOid());
                Application updateQuery = new Application(application.getOid(), application.getVersion());
                Map<String, String> pohjakoulutus = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);

                Application original = null;
                if (pk.contains(pohjakoulutus.get(OppijaConstants.ELEMENT_ID_BASE_EDUCATION))) {
                    Map<String, String> osaaminen = application.getPhaseAnswers(OppijaConstants.PHASE_GRADES);
                    Map<String, String> toAdd = new HashMap<String, String>();
                    for (Map.Entry<String, String> entry : osaaminen.entrySet()) {
                        String key = entry.getKey();
                        String prefix = key.substring(0, 5);
                        String val3Key = prefix + "_VAL3";
                        if (!osaaminen.containsKey(val3Key)) {
                            toAdd.put(val3Key, "Ei arvosanaa");
                        }
                    }
                    if (toAdd.size() > 0) {
                        original = application.clone();
                        toAdd.putAll(osaaminen);
                        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_GRADES, toAdd);
                        application.setModelVersion(targetVersion);
                        addHistoryBasedOnChangedAnswers(application, original, SYSTEM_USER, "model upgrade 2-3");
                    }
                }
                if (null != original) {
                    applicationDAO.update(updateQuery, application);
                    loggerAspect.logUpdateApplication(original, new ApplicationPhase(original.getApplicationSystemId(),
                      OppijaConstants.PHASE_GRADES, application.getPhaseAnswers(OppijaConstants.PHASE_GRADES)));
                } else {
                    applicationDAO.updateModelVersion(updateQuery, targetVersion);
                }
                statusRepository.endOperation(SCHEDULER_MODEL_UPGRADE + " v3", application.getOid());
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void upgradeModelVersion3to4() {
        final Integer baseVersion =3;
        final Integer targetVersion =4;
        if ((!enableUpgradeV4) || (!applicationDAO.hasApplicationsWithModelVersion(baseVersion)))
            return;

        List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        while(applications.size() > 0) {
            for (Application application : applications) {
                statusRepository.startOperation(SCHEDULER_MODEL_UPGRADE + " v4", application.getOid());
                Application queryApplication = new Application(application.getOid(), application.getVersion());
                if (Level4.requiresPatch(application)) {
                    applicationDAO.update(queryApplication, Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect));
                } else {
                    applicationDAO.updateModelVersion(queryApplication, targetVersion);
                }
                statusRepository.endOperation(SCHEDULER_MODEL_UPGRADE + " v4", application.getOid());
            }
            applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
        }
    }

    private void modelUpgrade() {
        for (ModelUpgrade<Application> upgrade: applicationUpgrades) {
            int baseVersion = upgrade.getBaseVersion();
            int targetVersion = upgrade.getTargetVersion();
            if (!applicationDAO.hasApplicationsWithModelVersion(baseVersion))
                continue;
            String statusOperation = SCHEDULER_MODEL_UPGRADE + " " + upgrade.getClass().getSimpleName();
            List<Application> applications = applicationDAO.getNextUpgradable(baseVersion, maxBatchSize);
            while (applications.size() > 0) {
                for (Application application : applications) {
                    statusRepository.startOperation(statusOperation, application.getOid());
                    Application queryApplication = new Application(application.getOid(), application.getVersion());
                    try {
                        UpgradeResult<Application> upgradeResult = upgrade.processUpgrade(application);
                        if (upgradeResult.isModified()) {
                            applicationDAO.update(queryApplication, upgradeResult.getUpgradedDocument());
                        } else {
                            applicationDAO.updateModelVersion(queryApplication, targetVersion);
                        }
                    }  catch (Exception e) {
                        LOGGER.error("Upgrading model to "+ upgrade.getTargetVersion()+" failed for application: " + application.getOid() + " " + e.getMessage());
                        applicationDAO.updateModelVersion(queryApplication, -1 * targetVersion);
                    }
                    statusRepository.endOperation(statusOperation, application.getOid());
                }
                applications = applicationDAO.getNextUpgradable(upgrade.getBaseVersion(), maxBatchSize);
            }
        }
    }

}
