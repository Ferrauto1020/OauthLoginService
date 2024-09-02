package com.oatuh.sso_backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureUserDTO {

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("@odata.context")
    private String odataContext;

    @JsonProperty("givenname")
    private String givenName;

    @JsonProperty("familyname")
    private String familyName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    // Costruttore senza argomenti
    public AzureUserDTO() {}

    // Getters e Setters
    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getOdataContext() {
        return odataContext;
    }

    public void setOdataContext(String odataContext) {
        this.odataContext = odataContext;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "AzureUserDto{" +
                "sub='" + sub + '\'' +
                ", odataContext='" + odataContext + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", email='" + email + '\'' +
                ", locale='" + locale + '\'' +
                ", picture='" + picture + '\'' +
                ", name='" + name + '\'' +
                ", emailVerified=" + emailVerified +
                '}';
    }
}