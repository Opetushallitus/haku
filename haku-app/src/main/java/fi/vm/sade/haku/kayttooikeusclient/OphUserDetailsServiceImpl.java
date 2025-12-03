package fi.vm.sade.haku.kayttooikeusclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OphUserDetailsServiceImpl implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(OphUserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        Map<String, Object> attributes =  token.getAssertion().getPrincipal().getAttributes();
        log.info("Roles of user {}: {}", attributes.get("oidHenkilo"), attributes.get("roles"));
        attributes.forEach((k, v) -> log.info("Attribute '{}' (type '{}') = '{}'", k, v.getClass().getName(), v));
        List<String> roles = attributes.containsKey("roles") ? (List<String>) attributes.get("roles") : new ArrayList<>();
        return new UserDetailsImpl((String) attributes.get("oidHenkilo"), roles);
    }

    public static final class UserDetailsImpl implements UserDetails {
        private static final long serialVersionUID = 845522107275827768L;

        private final String oidHenkilo;
        private final Collection<SimpleGrantedAuthority> authorities;

        public UserDetailsImpl(String oidHenkilo, List<String> authorities) {
            this.oidHenkilo = oidHenkilo;
            this.authorities = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }

        @Override
        public Collection<SimpleGrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return oidHenkilo;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
