package fi.vm.sade.haku.healthcheck;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Profile(value = {"it", "dev"})
public class StatusRepositoryMockImpl implements StatusRepository {

    @Override
    public List<Map<String, String>> read() {
        return new ArrayList<Map<String, String>>();
    }

    @Override
    public void write(final String operation) {
        // NOP
    }

    @Override
    public void write(final String operation, final Map<String, String> statusData) {
        // NOP
    }

}
