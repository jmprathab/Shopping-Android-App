package com.prathab.shopping.constants;

/**
 * Constant class which has all the URL endpoints
 */
public final class EndPoints {
  /**
   * Base API URL
   */
  private static final String BASE_URL = "http://192.168.0.16:8080/shopping/api";

  /**
   * Accounts endpoint URL
   */
  public static final String ACCOUNTS_URL = BASE_URL + "/accounts";
  public static final String ACCOUNTS_LOGIN = ACCOUNTS_URL + "/login";
  public static final String ACCOUNTS_CREATE_ACCCOUNT = ACCOUNTS_URL + "/create";

  /**
   * ProductsModel endpoint URL
   */
  public static final String PRODUCTS_URL = BASE_URL + "/products";
}
