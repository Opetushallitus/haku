package fi.vm.sade.oppija.haku.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author jukka
 * @version 9/12/123:44 PM}
 * @since 1.1
 */
public interface AdminService {

    void replaceModel(MultipartFile file);

    void replaceModel(InputStream inputStream);
}
