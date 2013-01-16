package fi.vm.sade.oppija.application.process.dao.impl;

import fi.vm.sade.oppija.application.process.dao.ApplicationProcessStateDAO;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationProcessStateDAOMongoImplTest extends AbstractDAOTest {

    private static final String oid = "1.2.3.4.5.0";
    @Autowired
    @Qualifier("applicationProcessStateDAOMongoImpl")
    private ApplicationProcessStateDAO applicationProcessStateDAO;

    @Before
    public void setUp() {
        ApplicationProcessState state = new ApplicationProcessState(oid, ApplicationProcessStateStatus.CANCELLED.toString());
        applicationProcessStateDAO.create(state);
    }

    @Test
    public void testCreate() {
        String newOid = "1.2.3.4.5.1";
        ApplicationProcessState state = new ApplicationProcessState(newOid, ApplicationProcessStateStatus.ACTIVE.toString());
        applicationProcessStateDAO.create(state);
        ApplicationProcessState query = new ApplicationProcessState(newOid, null);
        state = applicationProcessStateDAO.findOne(query);
        Assert.assertNotNull(state);
        Assert.assertEquals(newOid, state.getOid());
        Assert.assertEquals(ApplicationProcessStateStatus.ACTIVE.toString(), state.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAlreadyExists() {
        ApplicationProcessState state = new ApplicationProcessState(oid, ApplicationProcessStateStatus.ACTIVE.toString());
        applicationProcessStateDAO.create(state);
    }

    @Test
    public void testFindOne() {
        ApplicationProcessState query = new ApplicationProcessState(oid, null);
        ApplicationProcessState state = applicationProcessStateDAO.findOne(query);
        Assert.assertNotNull(state);
        Assert.assertEquals(oid, state.getOid());
        Assert.assertEquals(ApplicationProcessStateStatus.CANCELLED.toString(), state.getStatus());
    }

    @Test
    public void testFindOneNotExists() {
        ApplicationProcessState query = new ApplicationProcessState("1.2.3.4.5.9", null);
        ApplicationProcessState state = applicationProcessStateDAO.findOne(query);
        Assert.assertNull(state);
    }

    @Test
    public void testUpdate() {
        ApplicationProcessState state = new ApplicationProcessState(oid, ApplicationProcessStateStatus.ACTIVE.toString());
        ApplicationProcessState query = new ApplicationProcessState(oid, null);
        applicationProcessStateDAO.update(query, state);
        state = applicationProcessStateDAO.findOne(query);
        Assert.assertNotNull(state);
        Assert.assertEquals(oid, state.getOid());
        Assert.assertEquals(ApplicationProcessStateStatus.ACTIVE.toString(), state.getStatus());
    }

    @Test
    public void testDelete() {
        ApplicationProcessState query = new ApplicationProcessState(oid, null);
        applicationProcessStateDAO.delete(query);
        ApplicationProcessState state = applicationProcessStateDAO.findOne(query);
        Assert.assertNull(state);
    }

    @Override
    protected String getCollectionName() {
        return "applicationProcessState";
    }
}
