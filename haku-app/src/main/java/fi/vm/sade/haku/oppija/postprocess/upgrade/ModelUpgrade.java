package fi.vm.sade.haku.oppija.postprocess.upgrade;

public interface ModelUpgrade<T> {

    int getBaseVersion();

    int getTargetVersion();

    UpgradeResult<T> processUpgrade(T application);
}
