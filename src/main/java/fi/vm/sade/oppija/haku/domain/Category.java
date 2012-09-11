package fi.vm.sade.oppija.haku.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/1210:28 AM}
 * @since 1.1
 */
public class Category extends Titled {

    private transient Category next;
    private transient Category prev;

    public Category(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public void setNext(Category element) {
        this.next = element;
    }

    public void setPrev(Category prev) {
        this.prev = prev;
    }

    public void initChain(Category prev) {
        if (prev != null) {
            setPrev(prev);
            prev.setNext(this);
        }
    }

    @JsonIgnore
    public boolean isHasNext() {
        return next != null;
    }

    @JsonIgnore
    public boolean isHasPrev() {
        return prev != null;
    }

    @JsonIgnore
    public Category getNext() {
        return next;
    }

    @JsonIgnore
    public Category getPrev() {
        return prev;
    }

    public Link asLink() {
        return new Link(title, id);
    }
}
