package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Opetuspiste;
import fi.vm.sade.oppija.haku.service.EducationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of education service.
 *
 * @author Mikko Majapuro
 */
@Service("educationService")
public class EducationServiceImpl implements EducationService {

    private List<Opetuspiste> institutes = new ArrayList<Opetuspiste>();

    public EducationServiceImpl() {
        // populate test data
        for (int i = 0; i < 100; ++i) {
            Opetuspiste op = new Opetuspiste(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }
    }

    @Override
    public List<Opetuspiste> searchEducationInstitutes(String term) {
        List<Opetuspiste> result = new ArrayList<Opetuspiste>();
        if (term != null && !term.trim().isEmpty()) {
            term = term.trim().toLowerCase(Locale.getDefault());
            for (Opetuspiste o : institutes) {
                if (o.getKey().startsWith(term)) {
                    result.add(o);
                    int MAX_RESULTS = 10;
                    if (result.size() >= MAX_RESULTS) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
