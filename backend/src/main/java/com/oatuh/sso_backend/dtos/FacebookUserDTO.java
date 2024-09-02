package com.oatuh.sso_backend.dtos;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookUserDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("picture")
    private Map<String, Object> picture;

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, Object> getPicture() {
        return picture;
    }
 
    

    public void setPicture(Map<String, Object> picture) {
        this.picture = picture;
    }

    // facebook contiene il nome ed il cognome nella stessa parte quindi per separarli ci servirà 
    //questa funzione cosi poi da poterlo salvare nel nostro utente generico
    
    public String[] getNameParts() {
        if (name == null || name.isEmpty()) {
            return new String[] { "", "" };
        }
        String[] parts = name.split(" ", 2);
        return parts.length == 2 ? parts : new String[] { parts[0], "" };
    }
    
    //dato che facebook ritorna le immagini di profilo come json annidati siamo costretti a creare una funzione apposita
    //per estrarre l'immagine
    public String getPictureUrl() {
        if (picture != null && picture.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) picture.get("data");
            if (data.containsKey("url")) {
                return (String) data.get("url");
            }
        }
        return null;
    }
}

