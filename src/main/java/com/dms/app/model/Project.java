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
@Table(name="tbl_project")
public class Project {
	
	public Project() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="project_id")
	private int projectId;
		
	@Column(name="project_name")
	private String projectName;
	
	@Column(name="about_project")
	private String aboutProject;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_id")
	private int userId;
}