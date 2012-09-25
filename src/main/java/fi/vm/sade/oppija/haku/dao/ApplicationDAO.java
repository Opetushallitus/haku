package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.service.Application;

import java.util.List;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO {

    /**
     * Retrieves all applications by a specific user.
     *
     * @param userId user id
     * @return  list of applications
     */
    List<Application> findAllByUserId(String userId);

    /**
     * Find Application by userId and applicationId.
     *
     *
     * @param userId user identifier
     * @param applicationId application id
     * @return application
     */
    Application find(String userId, String applicationId);

    /**
     * Insert a new application.
     *
     * @param application application
     */
    void insert(Application application);

    /**
     * Update single application.
     *
     * @param application application to be updated
     */
    void update(Application application);

}
