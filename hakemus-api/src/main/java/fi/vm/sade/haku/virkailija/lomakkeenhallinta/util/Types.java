package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

import java.util.Map;

public final class Types {
    protected static class Base<T> {
        private final T value;

        protected Base(T value) {
            if (value == null) {
                throw new IllegalArgumentException("Typed value cannot be initialized to null");
            }
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Base<?> base = (Base<?>) o;

            return !(value != null ? !value.equals(base.value) : base.value != null);

        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    public static final class ApplicationOptionOid extends Oid {
        private ApplicationOptionOid(String value) {
            super(value);
        }

        public static ApplicationOptionOid of(String value) {
            return new ApplicationOptionOid(value);
        }
    }

    public static final class AsciiCountryCode extends SafeString {
        private AsciiCountryCode(String value) {
            super(value);
            if (value.length() != 3) {
                throw new IllegalArgumentException("Country code must be 3 characters long, got '" + value + "'");
            }
        }

        public static AsciiCountryCode of(String value) {
            return new AsciiCountryCode(value);
        }
    }

    public static final class MergedAnswers extends Base<Map<String, String>> {
        private MergedAnswers(Map<String, String> value) {
            super(value);
        }

        public static MergedAnswers of(Map<String, String> value) {
            return new MergedAnswers(value);
        }

        public static MergedAnswers of(Application application) {
            return of(application.getVastauksetMerged());
        }

        public String get(String field) {
            return getValue().get(field);
        }
    }

    public static class SafeString extends Base<String> {
        private SafeString(String value) {
            super(value);
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Safe String cannot be empty");
            }
        }

        public static SafeString of(String value) {
            return new SafeString(value);
        }
    }

    public static class Oid extends SafeString {
        private Oid(String value) {
            super(value);
            if (!value.matches("[0-9]+(.[.0-9]+)*")) {
                throw new IllegalArgumentException("OID must consist of dot-separated integers, got: '" + value + "'");
            }
        }

        public static Oid of(String value) {
            return new Oid(value);
        }
    }
}
