package fi.vm.sade.haku.oppija.hakemus.it.dao;

import java.util.ArrayList;
import java.util.List;

public class ApplicationFilterParametersBuilder {

    private int maxApplicationOptions;
    private List<String> organizationsReadable;
    private List<String> organizationsOpo;
    private List<String> organizationsHetuttomienKasittely;
    private String kohdejoukko;
    private String hakutapa;

    public ApplicationFilterParametersBuilder() {
        this.organizationsReadable = new ArrayList<String>();
        this.organizationsOpo = new ArrayList<String>();
        this.organizationsHetuttomienKasittely = new ArrayList<String>();
    }

    public int getMaxApplicationOptions() {
        return maxApplicationOptions;
    }

    public ApplicationFilterParametersBuilder setMaxApplicationOptions(int maxApplicationOptions) {
        this.maxApplicationOptions = maxApplicationOptions;
        return this;
    }

    public List<String> getOrganizationsReadble() {
        return organizationsReadable;
    }

    public ApplicationFilterParametersBuilder addOrganizationsReadable(List<String> organizationsReadable) {
        this.organizationsReadable.addAll(organizationsReadable);
        return this;
    }

    public ApplicationFilterParametersBuilder addOrganizationsReadable(String organizationReadable) {
        this.organizationsReadable.add(organizationReadable);
        return this;
    }

    public List<String> getOrganizationsOpo() {
        return organizationsOpo;
    }

    public ApplicationFilterParametersBuilder addOrganizationsOpo(List<String> organizationsOpo) {
        this.organizationsOpo.addAll(organizationsOpo);
        return this;
    }

    public ApplicationFilterParametersBuilder addOrganizationsOpo(String organizationOpo) {
        this.organizationsOpo.add(organizationOpo);
        return this;
    }

    public ApplicationFilterParametersBuilder addOrganizationsHetuttomienKasittely(
            List<String> organizationsHetuttomienKasittely) {
        this.organizationsHetuttomienKasittely = organizationsHetuttomienKasittely;
        return this;
    }

    public ApplicationFilterParametersBuilder addOrganizationsHetuttomienKasittely(String organizationHetuttomienKasittely) {
        this.organizationsHetuttomienKasittely.add(organizationHetuttomienKasittely);
        return this;
    }

    public List<String> getOrganizationsHetuttomienKasittely() {
        return organizationsHetuttomienKasittely;
    }

    public ApplicationFilterParametersBuilder setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
        return this;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public ApplicationFilterParametersBuilder setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
        return this;
    }

    public String getHakutapa() {
        return hakutapa;
    }

    public ApplicationFilterParameters build() {
        int realMaxApplicationOptions = maxApplicationOptions == 0 ? 6 : maxApplicationOptions;
        return new ApplicationFilterParameters(realMaxApplicationOptions, organizationsReadable, organizationsOpo,
                organizationsHetuttomienKasittely, kohdejoukko, hakutapa);
    }

}