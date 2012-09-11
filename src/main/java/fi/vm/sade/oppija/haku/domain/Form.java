package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:27 AM}
 * @since 1.1
 */
public class Form extends Titled {

    private transient Navigation navigation = new Navigation("top");

    private transient String firstCategoryId;

    final transient Map<String, Category> categories = new HashMap<String, Category>();

    public Form(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public Category getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    private void addCategory(Category category, Category prev) {
        this.categories.put(category.getId(), category);
        category.initChain(prev);
        navigation.addChild(category.asLink());
    }

    public void init() {
        Category prev = null;
        for (Element child : children) {
            if (child instanceof Category) {
                final Category child1 = (Category) child;
                addCategory(child1, prev);
                prev = child1;
                if (firstCategoryId == null) {
                    firstCategoryId = child.getId();
                }
            }
        }
    }

    @JsonIgnore
    public Navigation getNavigation() {
        return navigation;
    }

    @JsonIgnore
    public Category getFirstCategory() {
        return getCategory(firstCategoryId);
    }
}
