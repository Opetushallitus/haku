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
    final String type = this.getClass().getSimpleName();
    String help = "";

    final List<Element> children = new ArrayList<Element>();
    final Set<Attribute> attributes = new HashSet<Attribute>();

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

    public void setHelp(String help) {
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

    public void addChild(Element child) {
        this.children.add(child);
    }

    public void addAttribute(final String key, final String value) {
        this.attributes.add(new Attribute(key, value));
    }


}
