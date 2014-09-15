package fi.vm.sade.haku.oppija.hakemus.service;


import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;

public class ApplicationModelUtil {

    private static final String FIELD_LOP_PARENTS = "preference%d-Opetuspiste-id-parents";

    private static final String MAP_AUTHORIZATION_META = "authorizationMeta";
    private static final String MAP_AO_ORGANIZATIONS = "aoOrganizations";
    private static final String MAP_ANSWERS = "answers";
    private static final String MAP_HAKUTOIVEET = "hakutoiveet";


    public static void restoreV0ModelLOPParentsToApplicationMap(Map<String,Object> application){
        Map<String, Object> authorizationMeta = (Map<String, Object>) application.get(MAP_AUTHORIZATION_META);
        if (null == authorizationMeta)
            return;
        Map <String, Set<String>> aoOrganizations = (Map<String, Set<String>>) authorizationMeta.get(MAP_AO_ORGANIZATIONS);
        if (null == aoOrganizations || 0 == aoOrganizations.size())
            return;
        Map<String, Map<String, String>> answers = (Map<String, Map<String, String>>) application.get(MAP_ANSWERS);
        if (null == answers)
            return;
        Map<String, String> hakutoiveet = answers.get(MAP_HAKUTOIVEET);
        for (String preferenceid : aoOrganizations.keySet()) {
            hakutoiveet.put(
              String.format(FIELD_LOP_PARENTS, Integer.valueOf(preferenceid)),
              StringUtils.join(aoOrganizations.get(preferenceid), ","));
        }
    }

    public static void removeAuthorizationMeta(Map<String,Object> application){
        application.remove(MAP_AUTHORIZATION_META);
    }
}
