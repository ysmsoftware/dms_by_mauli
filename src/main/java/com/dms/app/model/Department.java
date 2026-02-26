package com.dms.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="tbl_department")
public class Department {

	public Department() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="department_id")
	private int departmentId;
		
	@Column(name="department_name")
	private String departmentName;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_id")
	private int userId;
	
}
