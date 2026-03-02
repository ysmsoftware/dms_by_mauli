package com.dms.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="tbl_document_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocumentType {

	public DocumentType() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="document_type_id")
	private int documentTypeId;
		
	@Column(name="category_name")
	private int categoryName;
	
	@Column(name="document_type_name")
	private String documentTypeName;
}
