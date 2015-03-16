package fi.vm.sade.hakutest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHelpers {
    public static class Tuple<A, B> {
        A _a;
        B _b;
        public static <A, B> Tuple<A, B> tuple(final A a, final B b) {
            return new Tuple<A, B>() {{
                this._a = a;
                this._b = b;
            }};
        }
    }

    @SafeVarargs
    public static <K, V> Map<K, V> map(final Tuple<K, V>... ts) {
        return new HashMap<K, V>() {{
            for (Tuple<K, V> t : ts) {
                put(t._a, t._b);
            }
        }};
    }

    public static Map<String, String> map(final String... kvs) {
        if (kvs.length % 2 != 0) {
            throw new RuntimeException("Function expects key-value-pairs, must have even number of arguments, got " + kvs.length);
        }
        return new HashMap<String, String>() {{
            for (int i = 0; i < kvs.length; i += 2) {
                put(kvs[i], kvs[i + 1]);
            }
        }};
    }

    public static Map<String, String> mapWithoutNullValues(final String... kvs) {
        if (kvs.length % 2 != 0) {
            throw new RuntimeException("Function expects key-value-pairs, must have even number of arguments, got " + kvs.length);
        }
        return new HashMap<String, String>() {{
            for (int i = 0; i < kvs.length; i += 2) {
                if (kvs[i + 1] != null) {
                    put(kvs[i], kvs[i + 1]);
                }
            }
        }};
    }

    @SafeVarargs
    public static <T> List<T> list(final T... xs) {
        return new ArrayList<T>() {{
            for (T x : xs) {
                add(x);
            }
        }};
    }

}
