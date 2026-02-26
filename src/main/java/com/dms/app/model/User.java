package com.dms.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="tbl_user")
public class User {	
	
	public User() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private int     id;
		
	@Column(name="user_name")
	private String  name;
	
	@Column(name="mobile_number", unique=true)
	private String  mobile;
	
	@Column(name="user_password")
	private String  password;
	
	@Column(name="user_role")
	private String  role;
	
	@Column(name="user_enabled")
	private boolean enabled;
		
	@Column(name="user_password_read")
	private String  userPasswordRead;
	
	@Column(name="user_photo")
	private String userPhoto;
	
	@Column(name="department_id")
	private int departmentId;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_added_by")
	private int userAddedBy;
	
	@Transient
	private String departmentName;
}