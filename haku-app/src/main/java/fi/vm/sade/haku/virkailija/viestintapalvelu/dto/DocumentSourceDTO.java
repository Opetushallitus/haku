package fi.vm.sade.haku.virkailija.viestintapalvelu.dto;

import java.util.List;

/**
 * Contains list of xhtml file contents, which will result in pdf:s
 * 
 */
public class DocumentSourceDTO {

    private List<String> sources;
    private String documentName;
    
    public List<String> getSources() {
        return sources;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
    
}
