package fi.vm.sade.oppija.lomake.util;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DataRelatedQuestion;
import fi.vm.sade.oppija.lomake.exception.ElementNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public final class ElementTree {

    public static final Logger LOGGER = LoggerFactory.getLogger(ElementTree.class);
    private final Element root;

    public ElementTree(final Element root) {
        this.root = root;
    }

    public Serializable getRelatedData(final String elementId, final String key) {
        try {
            @SuppressWarnings("unchecked")
            DataRelatedQuestion element =
                    (DataRelatedQuestion<Serializable>) getChildById(elementId);
            return element.getData(key);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }

    public Element getChildById(final String id) {
        Element element = getChildById(root, id);
        if (element == null) {
            throw new ElementNotFound(id);
        }
        return element;
    }

    private Element getChildById(final Element element, final String id) {
        if (element.getId().equals(id)) {
            return element;
        }
        Element tmp = null;
        for (Element child : element.getChildren()) {
            tmp = getChildById(child, id);
            if (tmp != null) {
                return tmp;
            }
        }
        return tmp;
    }

    public boolean isValidationNeeded(final String currentId, final String nextId) {
        List<Element> children = root.getChildren();
        if (children.get(children.size() - 1).getId().equals(currentId)) {
            return true;
        }
        int nextIndex = children.indexOf(getChildById(nextId));
        int currentIndex = children.indexOf(getChildById(currentId));
        return nextIndex > currentIndex;
    }

    public static List<Element> getAllChildren(final Element element) {
        ArrayList<Element> allChildren = new ArrayList<Element>();
        for (Element child : element.getChildren()) {
            allChildren.add(child);
            getAllChildren(child);
        }
        return allChildren;
    }

    public boolean isStateValid(String currentId, String saveId) {
        List<Element> children = root.getChildren();
        int nextIndex = children.indexOf(getChildById(saveId));
        if (nextIndex < 0) {
            return false;
        } else if ((currentId == null && nextIndex == 0) || (currentId != null && currentId.equals(saveId))) {
            return true;
        }
        return false;
    }

    public void checkPhaseTransfer(String currentId, String nextId) {

        List<Element> children = root.getChildren();
        int nextIndex = children.indexOf(getChildById(nextId));
        if (currentId == null && nextIndex == 0) {
            return;
        } else if (currentId != null) {
            if (currentId.equals("esikatselu")) {
                return;
            }
            int currentIndex = children.indexOf(getChildById(currentId));
            if (nextIndex == currentIndex) {
                return;
            }
        }
        throw new ElementNotFound(nextId);

    }
}
