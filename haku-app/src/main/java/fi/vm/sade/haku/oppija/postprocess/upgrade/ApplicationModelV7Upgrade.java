package fi.vm.sade.haku.oppija.postprocess.upgrade;


import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;

public final class ApplicationModelV7Upgrade implements ModelUpgrade<Application> {

    private static final int baseVersion = 6;
    private static final int targetVersion = 7;

    private static final String SYSTEM_USER = "järjestelmä";

    private final ApplicationService applicationService;

    public ApplicationModelV7Upgrade(final ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public int getBaseVersion() {
        return baseVersion;
    }

    @Override
    public int getTargetVersion() {
        return targetVersion;
    }

    @Override
    public UpgradeResult<Application> processUpgrade(final Application application) throws Exception{
        application.setModelVersion(targetVersion);
        return new UpgradeResult<>(applicationService.updateAuthorizationMeta(application), true);
    }
}