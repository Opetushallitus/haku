package fi.vm.sade.oppija.haku.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jukka
 * @version 9/7/1210:29 AM}
 * @since 1.1
 */
public abstract class Element {

    final String id;
    String type = this.getClass().getSimpleName();
    List<Element> children = new ArrayList<Element>();
    Set<Attribute> attributes = new HashSet<Attribute>();
    String help = "";

    protected Element(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<Element> getChildren() {
        return children;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public String getHelp() {
        return help;
    }

    public void addChild(Element child) {
        this.children.add(child);
    }


}
