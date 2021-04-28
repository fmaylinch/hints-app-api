package com.codethen.hintsapp.examples.jwt;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;

/**
 * Example of secured endpoints (those with {@link RolesAllowed}).
 * https://quarkus.io/guides/security-jwt
 */
@Path("/secured")
@RequestScoped
public class TokenSecuredResource {

    @Inject
    JsonWebToken jwt;

    @Claim(standard = Claims.birthdate)
    String birthdate;

    @GET()
    @Path("permit-all")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        return getResponseString(ctx) + " --- birthdate: " + birthdate;
    }

    @GET
    @Path("roles-allowed")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + jwt.getClaim("birthdate").toString() + " == " + birthdate;
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (jwt.getName() == null) {
            name = "anonymous";
        } else if (!jwt.getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = jwt.getName();
        }
        return String.format("hello + %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt(jwt));
    }

    /**
     * This way we could get the {@link JsonWebToken} without having to inject it and using {@link RequestScoped}.
     * https://quarkus.io/guides/security-jwt#using-the-jsonwebtoken-and-claim-injection
     *
     * But if we want to inject claims, we need to use {@link RequestScoped}.
     */
    private JsonWebToken getJwt(SecurityContext ctx) {
        final Principal userPrincipal = ctx.getUserPrincipal();
        return (JsonWebToken) userPrincipal;
    }

    private boolean hasJwt(JsonWebToken jwt) {
        return jwt != null && jwt.getClaimNames() != null;
    }
}