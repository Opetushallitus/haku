package fi.vm.sade.haku.oppija.postprocess.upgrade;

public class UpgradeResult<T> {

    private final boolean modified;
    private final T upgradedDocument;

    public UpgradeResult(T upgradedDocument,boolean modified) {
        this.modified = modified;
        this.upgradedDocument = upgradedDocument;
    }

    public boolean isModified() {
        return modified;
    }

    public T getUpgradedDocument() {
        return upgradedDocument;
    }
}
