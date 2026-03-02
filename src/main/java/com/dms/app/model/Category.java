package com.dms.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="tbl_category")
public class Category {

	public Category() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="category_id")
	private int categoryId;
		
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="department_id")
	private int departmentId;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_id")
	private int userId;
	
	@Transient
	private String departmentName;
}