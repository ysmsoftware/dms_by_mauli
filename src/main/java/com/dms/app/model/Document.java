package com.dms.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tbl_document")
public class Document {

	public Document() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="document_id")
	private int documentId;
	
	@Column(name="document_category")
	private int documentCategory;
	
	@Column(name="document_type")
	private int documentType;
	
	@Column(name="document_sub_type")
	private int documentSubType;
		
	@Column(name="document_title")
	private String documentTitle;
	
	@Column(name="about_document")
	private String aboutDocument;
	
	@Column(name="document_name")
	private String documentName;
	
	@Column(name="document_barcode")
	private String documentBarcode;
	
	@Column(name="document_type_selection")
	private String documentTypeSelection;
		
	@Column(name="version_number")
	private float versionNumber;
	
	@Column(name="last_version_number")
	private float lastVersionNumber;
	
	@Column(name="created_datetime")
	private String createdDatetime;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="project_id")
	private int projectId;
	
	@Column(name="document_revised_id")
	private int documentRevisedId;
	
	@Column(name="note")
	private String note;
	
	@Column(name="document_approval")
	private int documentApproval;
	
	@Column(name="approval_datetime")
	private String approvalDatetime;
	
	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentRevised> documentRevisedList = new ArrayList<DocumentRevised>();

	
	@Transient
	private String userName;
	
	@Transient
	private String categoryName;
	
	@Transient
	private String documentTypeName;
	
	@Transient
	private String documentSubTypeName;
	
	@Transient
	private String departmentName;
	
	@Transient
	private String projectName;

	@Override
	public String toString() {
		return "Document [documentId=" + documentId + ", documentCategory=" + documentCategory + ", documentType="
				+ documentType + ", documentSubType=" + documentSubType + ", documentTitle=" + documentTitle
				+ ", aboutDocument=" + aboutDocument + ", documentName=" + documentName + ", documentBarcode="
				+ documentBarcode + ", documentTypeSelection=" + documentTypeSelection + ", versionNumber="
				+ versionNumber + ", lastVersionNumber=" + lastVersionNumber + ", createdDatetime=" + createdDatetime
				+ ", userId=" + userId + ", projectId=" + projectId + ", documentRevisedId=" + documentRevisedId
				+ ", note=" + note + ", documentApproval=" + documentApproval + ", approvalDatetime=" + approvalDatetime
				+ ", documentRevisedList=" + documentRevisedList + ", userName=" + userName + ", categoryName="
				+ categoryName + ", documentTypeName=" + documentTypeName + ", documentSubTypeName="
				+ documentSubTypeName + ", departmentName=" + departmentName + ", projectName=" + projectName + "]";
	}
	
	
}