package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

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

    public static final class ApplicationOptionOid extends Base<String> {
        private ApplicationOptionOid(String value) {
            super(value);
        }

        public static ApplicationOptionOid of(String value) {
            return new ApplicationOptionOid(value);
        }
    }

    public static final class AsciiCountryCode extends Base<String> {
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
}
