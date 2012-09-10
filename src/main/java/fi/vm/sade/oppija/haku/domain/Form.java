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

    final transient Map<String, Category> categories = new HashMap<String, Category>();

    public Form(String id) {
        super(id);
    }

    public Category getCategory(String categoryId) {
        return categories.get(categoryId);
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
            }
        }
    }


}
