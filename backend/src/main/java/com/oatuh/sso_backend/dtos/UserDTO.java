package com.oatuh.sso_backend.dtos;

public class UserDTO{

	private String name;
	private String surname;
	private String email;
	private String picture;
	private String provider;
	public UserDTO(GoogleUserDTO dto) {
		this.name= dto.getGiven_name();
		this.surname= dto.getFamily_name();
		this.picture = dto.getPicture();
		this.email= dto.getEmail();
	}
	public UserDTO(AzureUserDTO dto) {
		this.name = dto.getGivenName();
		this.surname = dto.getFamilyName();
		this.picture = dto.getPicture();
		this.email = dto.getEmail();
	}
	
	public UserDTO(FacebookUserDTO dto) {
		String[] nameSurname = dto.getNameParts();
		this.name = nameSurname[0];
		this.surname = nameSurname[1];
		this.picture = dto.getPictureUrl();
		this.email = dto.getEmail();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
}
