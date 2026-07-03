package com.example.ordinaMii.Security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String clientId;

    public KeycloakJwtAuthenticationConverter(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        //creo una lista di ruoli
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.addAll(getRealmRoles(jwt));//ruoli del realm
        authorities.addAll(getClientRoles(jwt));

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> getRealmRoles(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //Realm access contiene la mappa dei ruoli nel jwt
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return authorities;
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return authorities;
        }

        //Normalizzo ogni ruolo
        for (Object role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
        }

        return authorities;
    }

    private Collection<GrantedAuthority> getClientRoles(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return authorities;
        }

        Object clientObject = resourceAccess.get(clientId);

        if (!(clientObject instanceof Map<?, ?> clientAccess)) {
            return authorities;
        }

        Object rolesObject = clientAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return authorities;
        }

        for (Object role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
        }

        return authorities;
    }
}