package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import java.util.Iterator;
import java.util.function.Function;

public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {

    default <D> CloseableIterator<D> map(Function<E,D> f) {
        CloseableIterator<E> self = this;
        return new CloseableIterator<D>() {

            @Override
            public boolean hasNext() {
                return self.hasNext();
            }

            @Override
            public D next() {
                return f.apply(self.next());
            }

            @Override
            public void close() throws Exception {
                self.close();
            }
        };
    }
}
