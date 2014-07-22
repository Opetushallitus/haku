package fi.vm.sade.haku.virkailija.lomakkeenhallinta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableSet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Document
@XmlRootElement
public class ApplicationSystemConfiguration {


    public static final Set<String> configurations = ImmutableSet.of(
            "YHTEISHAKU_KEVAT", "YHTEISHAKU_SYKSY", "LISAHAKU_SYKSY", "PERVAKO");


    @Id
    private final String asid;
    private final String configuration;

    @JsonCreator
    public ApplicationSystemConfiguration(final String asid, final String configuration) {
        checkNotNull(asid, "Application system id cannot be null");
        checkNotNull(configuration, "Configuration cannot be null");
        checkArgument(configurations.contains(configuration), "Illegal configuration +" + configuration + "'");
        this.asid = asid;
        this.configuration = configuration;
    }

    public String getAsid() {
        return asid;
    }

    public String getConfiguration() {
        return configuration;
    }
}
