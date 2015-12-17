package fi.vm.sade.haku.oppija.common.oppijantunnistus;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;

public class OppijanTunnistusDTO {

    public enum LanguageCodeISO6391 {
        @Value fi,
        @Value sv,
        @Value en
    }

    public static class Metadata {
        @Key
        public String hakemusOid;
        @Key
        public String personOid;
    }

    @Key
    public String subject; // Email subject

    @Key
    public String template; // Email body template

    @Key
    public String url;

    @Key
    public long expires; // Url expiration time in Unix milliseconds

    @Key
    public String email;

    @Key
    public LanguageCodeISO6391 lang;

    @Key
    public Metadata metadata;

}