package fi.vm.sade.haku.oppija.lomake.util;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.exception.ElementNotFound;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public final class ElementTree {

    public static final Logger LOGGER = LoggerFactory.getLogger(ElementTree.class);
    private final Element root;

    public ElementTree(final Element root) {
        this.root = root;
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
            // Last phase needs special treatment
            Element nextPhase = null;
            try {
                nextPhase = getChildById(nextId);
            } catch (ElementNotFound enf) {
                // Apparently there's no next phase.
                return true;
            }
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
            if (nextIndex <= currentIndex) {
                return;
            }
        }
        throw new ElementNotFound(nextId);

    }

    public static final Element getFirstChild(final Element element) {
        List<Element> children = element.getChildren();
        if (children.isEmpty()) {
            throw new ResourceNotFoundException("First child not found");
        } else {
            return children.get(0);
        }
    }
}
