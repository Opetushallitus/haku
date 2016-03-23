package fi.vm.sade.haku.virkailija.valinta;

public class ValintaServiceCallFailedException extends Exception {
    public ValintaServiceCallFailedException(Exception exception)  {
        super(exception);
    }
}
