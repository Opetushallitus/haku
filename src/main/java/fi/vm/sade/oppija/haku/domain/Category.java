package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/7/1210:28 AM}
 * @since 1.1
 */
public class Category extends Titled {

    private transient Category next;
    private transient Category prev;

    public Category(final String id, final String title) {
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

    public boolean isHasNext() {
        return next != null;
    }

    public boolean isHasPrev() {
        return prev != null;
    }

    public Category getNext() {
        return next;
    }

    public Category getPrev() {
        return prev;
    }

    public Link asLink() {
        return new Link(title, id);
    }
}
