package fi.vm.sade.oppija.haku.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:27 AM}
 * @since 1.1
 */
public class Form extends Titled {

    private Navigation navigation = new Navigation("top");
    private String firstCategoryId;
    final transient Map<String, Category> categories = new HashMap<String, Category>();

    public Form(final String id, final String title) {
        super(id, title);
    }

    public Category getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public Category getFirstCategory() {
        return categories.get(firstCategoryId);
    }

    private void addCategory(Category category, Category prev) {
        this.categories.put(category.getId(), category);
        category.initChain(prev);
        navigation.addChild(category.asLink());
    }

    public void produceCategoryMap() {
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

    public Navigation getNavigation() {
        return navigation;
    }
}
