package com.prathab.shopping.constants;

import okhttp3.MediaType;

/**
 * Constant class which has all the Jwt constants
 */
public class JwtConstants {
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  /**
   * JWT Constants
   */
  public static final String JWT_SECRET = "secret";
  public static final String JWT_ISSUER = "shopping.com";

  /**
   * JWT Claim Constants
   */
  public static final String JWT_CLAIM_NAME = "name";
  public static final String JWT_CLAIM_MOBILE = "mobile";
  public static final String JWT_CLAIM_USERS_TYPE = "users_type";
}
