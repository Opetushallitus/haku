package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author jukka
 * @version 9/12/1210:19 AM}
 * @since 1.1
 */
public class Attachment extends Titled {

    public Attachment(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
        addAttribute("name", id);

    }

    CommonsMultipartFile fileData;

    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

}
