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
@Table(name="tbl_document_view")
public class DocumentView {

	public DocumentView() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="document_view_id")
	private int documentViewId;
	
	@Column(name="document_id")
	private int documentId;
	
	@Column(name="document_revised_id")
	private int documentRevisedId;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="view_datetime")
	private String viewDatetime;
	
	@Transient
	private Document document;
	
	@Transient
	private DocumentRevised documentRevised;
	
	@Transient
	private String userName;
		
}
