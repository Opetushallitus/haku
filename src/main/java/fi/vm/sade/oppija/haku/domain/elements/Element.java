package fi.vm.sade.oppija.haku.domain.elements;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.vm.sade.oppija.haku.domain.Attribute;
import fi.vm.sade.oppija.haku.domain.questions.*;
import fi.vm.sade.oppija.haku.domain.rules.EnablingSubmitRule;

import java.util.*;

/**
 * @author jukka
 * @version 9/7/1210:29 AM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = Attachment.class),
                @JsonSubTypes.Type(value = CheckBox.class),
                @JsonSubTypes.Type(value = DropdownSelect.class),
                @JsonSubTypes.Type(value = MultiSelect.class),
                @JsonSubTypes.Type(value = Option.class),
                @JsonSubTypes.Type(value = Radio.class),
                @JsonSubTypes.Type(value = TextArea.class),
                @JsonSubTypes.Type(value = Attachment.class),
                @JsonSubTypes.Type(value = Navigation.class),
                @JsonSubTypes.Type(value = Link.class),
                @JsonSubTypes.Type(value = QuestionGroup.class),
                @JsonSubTypes.Type(value = TextQuestion.class),
                @JsonSubTypes.Type(value = Category.class),
                @JsonSubTypes.Type(value = EnablingSubmitRule.class)
        }
)
public abstract class Element {

    final String id;
    transient String type = this.getClass().getSimpleName();
    String help;
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
        return Collections.unmodifiableSet(attributes);
    }

    public void setHelp(final String help) {
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
            attrStr.append(attribute.getKey());
            attrStr.append("=\"");
            attrStr.append(attribute.getValue());
            attrStr.append("\" ");
        }
        return attrStr.toString();
    }

}
