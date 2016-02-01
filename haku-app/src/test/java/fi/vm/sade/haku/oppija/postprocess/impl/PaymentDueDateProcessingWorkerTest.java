package fi.vm.sade.haku.oppija.postprocess.impl;


import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PaymentDueDateProcessingWorkerTest {
    private ApplicationDAO applicationDAO;
    private HakumaksuService hakumaksuService;
    private PaymentDueDateProcessingWorker paymentDueDateProcessingWorker;

    private final ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
    private final String aoid1 = "1.2.246.562.11.1337133713371337.1";

    @Before
    public void setUp() {
        applicationDAO = mock(ApplicationDAO.class);
        hakumaksuService = mock(HakumaksuService.class);
        paymentDueDateProcessingWorker = new PaymentDueDateProcessingWorker(
                applicationDAO,
                hakumaksuService
        );
        when(applicationDAO.getNextForPaymentDueDateProcessing(PaymentDueDateProcessingWorker.BATCH_SIZE)).thenReturn(
                ImmutableList.<Application>of(
                        new Application() {{
                            setOid(aoid1);
                            setUpdated(new Date());
                            setState(State.ACTIVE);
                        }}
                )
        );

        when(hakumaksuService.allApplicationOptionsRequirePayment(Matchers.<Application>any())).thenReturn(true);
    }

    @Test
    public void applicationWithExpiredDueDateIsSetToPassive() {
        when(applicationDAO.update(Mockito.<Application>any(), Mockito.<Application>any())).thenReturn(1);
        paymentDueDateProcessingWorker.processPaymentDueDates();
        verify(applicationDAO).update(Mockito.<Application>any(), captor.capture());
        assertEquals(Application.State.PASSIVE, captor.getValue().getState());
    }

    @Test
    public void applicationWithoutExpiredDueDateIsNotSetToPassive() {
        when(hakumaksuService.allApplicationOptionsRequirePayment(Matchers.<Application>any())).thenReturn(false);
        when(applicationDAO.update(Mockito.<Application>any(), Mockito.<Application>any())).thenReturn(1);
        paymentDueDateProcessingWorker.processPaymentDueDates();
        verify(applicationDAO, times(0)).update(Mockito.<Application>any(), Mockito.<Application>any());
        verify(hakumaksuService, times(1)).allApplicationOptionsRequirePayment(Matchers.<Application>any());
    }

    @Test
    public void testProcessPaymentDueDates() throws Exception {
        when(applicationDAO.update(Mockito.<Application>any(), Mockito.<Application>any())).thenReturn(1);
        paymentDueDateProcessingWorker.processPaymentDueDates();
        verify(applicationDAO, Mockito.times(1)).update(Mockito.<Application>any(), Mockito.<Application>any());
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateMultipleApplicationsThrowsException() {
        when(applicationDAO.update(Mockito.<Application>any(), Mockito.<Application>any())).thenReturn(2);
        paymentDueDateProcessingWorker.processPaymentDueDates();
    }

    @Test
    public void testHasFiniteRetryCount() {
        when(applicationDAO.update(Mockito.<Application>any(), Mockito.<Application>any())).thenReturn(0);
        when(applicationDAO.getApplication(aoid1)).thenReturn(
                new Application() {{
                    setOid(aoid1);
                    setUpdated(new Date());
                }}
        );
        paymentDueDateProcessingWorker.processPaymentDueDates();

        verify(applicationDAO, times(PaymentDueDateProcessingWorker.MAX_RETRIES)).update(Mockito.<Application>any(), Mockito.<Application>any());

    }

    @Test
    public void testProcessPaymentDueDate() throws Exception {
        assertEquals(Application.State.PASSIVE, paymentDueDateProcessingWorker.processPaymentDueDate(new Application() {{
            setOid("1.2.246.562.11.1");
            setPaymentDueDate(new Date(0));
            setState(State.ACTIVE);
            setRequiredPaymentState(PaymentState.NOTIFIED);
        }}).getState());
    }

    @Test
    public void testProcessPaymentDueDateDoesNotMutate() throws Exception {
        assertEquals(Application.State.ACTIVE, paymentDueDateProcessingWorker.processPaymentDueDate(new Application() {{
            setOid("1.2.246.562.11.2");
            setPaymentDueDate(new Date(0));
            setState(State.ACTIVE);
            setRequiredPaymentState(PaymentState.OK);
        }}).getState());
    }

    @Test
    public void testProcessPaymentDueDateDoesNotMutateWhenNotAllApplicationOptionsRequirePayment() throws Exception {
        Application application = new Application() {{
            setOid("1.2.246.562.11.1111");
            setPaymentDueDate(new Date(0));
            setState(State.ACTIVE);
            setRequiredPaymentState(PaymentState.NOT_OK);
        }};
        when(hakumaksuService.allApplicationOptionsRequirePayment(application)).thenReturn(false);
        assertEquals(Application.State.ACTIVE, paymentDueDateProcessingWorker.processPaymentDueDate(application).getState());
    }

}
