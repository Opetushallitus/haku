package fi.vm.sade.oppija.haku.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: ville
 * Date: 05/09/12
 */
public class FormModel {

    final private String currentCategoryId;
    final private List<Map<String, Object>> categories;
    private int currentCategoryIndex;

    private List<Link> categoryLinks = new ArrayList<Link>();

    public FormModel(final List<Map<String, Object>> categories, final String currentCategoryId) {
        this.categories = categories;
        this.currentCategoryId = currentCategoryId;

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).get("id").equals(currentCategoryId)) {
                currentCategoryIndex = i;
            }
            categoryLinks.add(new Link(categories.get(i)));
        }
    }

    public String getPrev() {
        String id = null;
        if (currentCategoryIndex > 0) {
            id = categories.get(currentCategoryIndex - 1).get("id").toString();
        }
        return id;
    }

    public String getNext() {
        String id = null;
        if (currentCategoryIndex < categories.size() - 1) {
            id = categories.get(currentCategoryIndex + 1).get("id").toString();
        }
        return id;
    }

    public String getCurrentCategoryId() {
        return currentCategoryId;
    }

    public Map<String, Object> getCurrentCategory() {
        return categories.get(currentCategoryIndex);
    }

    public List<Link> getCategoryLinks() {
        return categoryLinks;
    }
}
