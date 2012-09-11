package fi.vm.sade.oppija.haku.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jukka
 * @version 9/7/1210:29 AM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class Element {

    final String id;

    transient String type = this.getClass().getSimpleName();

    String help = "";

    final List<Element> children = new ArrayList<Element>();
    final Set<Attribute> attributes = new HashSet<Attribute>();


    protected Element(@JsonProperty String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
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

    public Element addChild(Element child) {
        this.children.add(child);
        return this;
    }

    public void addAttribute(final String key, final String value) {
        this.attributes.add(new Attribute(key, value));
    }

    @JsonIgnore
    public String getAttributeString() {
        StringBuilder attrStr = new StringBuilder();
        for (Attribute attribute : attributes) {
            attrStr.append(attribute.key);
            attrStr.append("=\"");
            attrStr.append(attribute.value);
            attrStr.append("\"");
        }
        return attrStr.toString();
    }

}
