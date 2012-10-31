package fi.vm.sade.oppija.tarjonta.client;


import javax.xml.transform.Source;

/**
 * @author hannu
 */
public interface TarjontaClient {

    public String retrieveTarjontaAsString();

    public Source retrieveTarjontaAsSource();

}
