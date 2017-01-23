package com.ibm.webapp.bean;

import org.bson.Document;

import com.ibm.utils.MongoSerializable;

@SuppressWarnings("unused")
public class UserProfle extends MongoSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2482049778664407494L;
	private String userId;
	private String fname;
	private String lname;
	private String password;
	private String role;
	private String regId;
	private String email;

	public UserProfle() {
		super();
	}

	public String getUserId() {
		return (String) get("userId");
	}

	public String getFname() {
		return (String) get("fname");
	}

	public String getLname() {
		return (String) get("lname");
	}

	public String getPassword() {
		return (String) get("password");
	}

	public String getRole() {
		return (String) get("role");
	}

	public String getRegId() {
		return (String) get("regId");
	}

	public String getEmail() {
		return (String) get("email");
	}

	public void setUserId(String userId) {
		this.userId = userId;
		put("userId", userId);
	}

	public void setFname(String fname) {
		this.fname = fname;
		put("fname", fname);
	}

	public void setLname(String lname) {
		this.lname = lname;
		put("lname", lname);
	}

	public void setPassword(String password) {
		this.password = password;
		put("password", password);
	}

	public void setRole(String role) {
		this.role = role;
		put("role", role);
	}

	public void setRegId(String regId) {
		this.regId = regId;
		put("regId", regId);
	}

	public void setEmail(String email) {
		this.email = email;
		put("email", email);
	}

	public void buildInstance(Document doc) {
		super.setInternalFields(doc);
		this.userId = (String) doc.get("userId");
		this.fname = (String) doc.get("fname");
		this.lname = (String) doc.get("lname");
		this.password = (String) doc.get("password");
		this.role = (String) doc.get("role");
		this.regId = (String) doc.get("regId");
		this.email = (String) doc.get("email");
	}

}
