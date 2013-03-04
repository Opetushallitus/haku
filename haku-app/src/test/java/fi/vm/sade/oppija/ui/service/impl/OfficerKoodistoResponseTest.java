package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class OfficerKoodistoResponseTest {

    private OfficerKoodistoResponse officerKoodistoResponse;

    @Before
    public void setUp() throws Exception {
        officerKoodistoResponse = new OfficerKoodistoResponse();
    }

    @Test
    public void testSetOrganizationTypes() throws Exception {
        ArrayList<Option> listOfOptions = new ArrayList<Option>();
        officerKoodistoResponse.setOrganizationTypes(listOfOptions);
        assertEquals(listOfOptions, officerKoodistoResponse.getModel().get(OfficerKoodistoResponse.ORGANIZATION_TYPES));
    }

    @Test
    public void testSetLearninginstitutionTypes() throws Exception {
        ArrayList<Option> listOfOptions = new ArrayList<Option>();
        officerKoodistoResponse.setLearninginstitutionTypes(listOfOptions);
        assertEquals(listOfOptions, officerKoodistoResponse.getModel().get(OfficerKoodistoResponse.LEARNINGINSTITUTION_TYPES));
    }
}
