package com.dms.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tbl_document_revised")
public class DocumentRevised {

	public DocumentRevised() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="document_revised_id")
	private int documentRevisedId;
	
	//@Column(name="document_id")
	//private int documentId;
	
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "document_id", referencedColumnName = "document_id", nullable = false)
	 private Document document;

	@Column(name="version_type_selection")
	private String versionTypeSelection;
		
	@Column(name="version_number")
	private float versionNumber;
	
	@Column(name="document_name")
	private String documentName;

	@Column(name="document_type_selection")
	private String documentTypeSelection;
	
	@Column(name="note")
	private String note;
	
	@Column(name="document_approval")
	private int documentApproval;
	
	@Column(name="approval_datetime")
	private String approvalDatetime;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_id")
	private int userId;
	
	@Transient
	private String userName;
	
	@Transient
	private int previousDocumentRevisedId;
	
	//@Transient
	//private int documentHeadApproval;

	@Override
	public String toString() {
		return "DocumentRevised [documentRevisedId=" + documentRevisedId + ", document=" + document
				+ ", versionTypeSelection=" + versionTypeSelection + ", versionNumber=" + versionNumber
				+ ", documentName=" + documentName + ", documentTypeSelection=" + documentTypeSelection + ", note="
				+ note + ", documentApproval=" + documentApproval + ", approvalDatetime=" + approvalDatetime
				+ ", createdDatetime=" + createdDatetime + ", userId=" + userId + ", userName=" + userName
				+ ", previousDocumentRevisedId=" + previousDocumentRevisedId + "]";
	}
	
	
}
