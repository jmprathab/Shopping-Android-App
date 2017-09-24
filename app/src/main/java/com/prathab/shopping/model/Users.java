package com.prathab.shopping.model;

/**
 * Users class which represents user of the Application
 */
public class Users {
  private String name;
  private String mobile;
  private String email;
  private String password;

  private Users(Builder builder) {
    this.name = builder.name;
    this.mobile = builder.mobile;
    this.email = builder.email;
    this.password = builder.password;
  }

  private Users() {
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public static class UsersTypes {
    public static final String USER = "users";
    public static final String ADMINISTRATOR = "admin";
  }

  public static class Builder {
    private String name;
    private String mobile;
    private String email;
    private String password;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setMobile(String mobile) {
      this.mobile = mobile;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Users build() {
      return new Users(this);
    }
  }
}

