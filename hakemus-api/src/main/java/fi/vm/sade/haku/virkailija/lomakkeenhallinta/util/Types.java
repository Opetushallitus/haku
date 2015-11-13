package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

public final class Types {
    protected static class Base<T> {
        private final T value;

        protected Base(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public String toString() {
            return value.toString();
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
        }

        public static AsciiCountryCode of(String value) {
            if (value.length() != 3) {
                throw new IllegalArgumentException("Country code must be 3 characters long, got '" + value + "'");
            }
            return new AsciiCountryCode(value);
        }
    }
}
