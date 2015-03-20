package fi.vm.sade.haku.oppija.postprocess.upgrade;

public interface ModelUpgrade<T> {

    boolean enabled();

    int getBaseVersion();

    int getTargetVersion();

    UpgradeResult<T> processUpgrade(T application);
}
