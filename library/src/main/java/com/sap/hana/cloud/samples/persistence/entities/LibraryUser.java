package com.sap.hana.cloud.samples.persistence.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "T_LibraryUser")
@NamedQueries({
@NamedQuery(name = "getUserById", query = "select u from LibraryUser u where u.userId = :userId")
})
public class LibraryUser implements Serializable {

	private static final long serialVersionUID = -2231449309291612084L;

	@Id
	private String userId;

	@Basic
	private String imgSrc;

	@Basic
	private String firstName;

	@Basic
	private String lastName;

	@Basic
	private String displayName;

	@Basic
	private String email;

	@Basic
	private String phone;

	@Basic
	private String address;

	@Basic
	private Gender gender;

	@Basic
	private Set<String> roles = new HashSet<String>();

	// Needed for JPA
	public LibraryUser(){

	}

	public LibraryUser(String userId){
		this.userId = userId;
	}

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public void mergeWith (LibraryUser userFromidentityService) {

		boolean isNameChanged = false;

		if (!this.firstName.equals(userFromidentityService.getFirstName())) {
			isNameChanged = true;
			this.firstName = userFromidentityService.getFirstName();
		}

		if (!this.lastName.equals(userFromidentityService.getLastName())) {
			isNameChanged = true;
			this.lastName = userFromidentityService.getLastName();
		}

		if (isNameChanged) {
			this.displayName = this.firstName + " " + this.lastName;
		}

		if (!this.email.equals(userFromidentityService.getEmail())) {
			this.email = userFromidentityService.getEmail();
		}

		if (!this.roles.equals(userFromidentityService.getRoles())) {
			this.roles = userFromidentityService.getRoles();
		}
	}

	@Override
	public String toString() {
		return "LibraryUser [userId=" + userId + ", imgSrc=" + imgSrc
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", displayName=" + displayName + ", email=" + email
				+ ", phone=" + phone + ", address=" + address + ", gender="
				+ gender + ", roles=" + roles + "]";
	}

	
}
