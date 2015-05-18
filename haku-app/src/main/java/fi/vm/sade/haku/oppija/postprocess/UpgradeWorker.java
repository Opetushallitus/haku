package fi.vm.sade.haku.oppija.postprocess;

public interface UpgradeWorker {

    String SCHEDULER_MODEL_UPGRADE = "MODEL UPGRADE";

    void processModelUpdate();
}
