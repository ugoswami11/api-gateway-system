package com.gateway.gatewayservice.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent object creation
    }

    /*
     * JWT Secret Key
     * In production this should come from:
     * ENV variables / Vault / Secret Manager
     */
    public static final String SECRET_KEY =
            "mySuperSecretKeyForJwtAuthentication123456";

    /*
     * JWT Expiration Time
     * 1 hour = 3600000 milliseconds
     */
    public static final long JWT_EXPIRATION = 3600000;

    /*
     * Authorization Header Name
     */
    public static final String AUTH_HEADER = "Authorization";

    /*
     * Bearer Token Prefix
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /*
     * Public Authentication Endpoints
     */
    public static final String LOGIN_PATH = "/auth/login";
    public static final String REGISTER_PATH = "/auth/register";

}