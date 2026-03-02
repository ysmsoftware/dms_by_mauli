package com.dms.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dms.app.model.Document;
import com.dms.app.model.DocumentData;

public interface DocumentRepository  extends JpaRepository<Document, Integer>  {

	@Query("select u from Document u where u.projectId = :projectId order by u.documentId desc")
	public List<Document> getDocumentByProjectId(@Param("projectId") int projectId);	
	
	@Query(value = "select u.document_id as documentId, u.document_title as documentTitle from tbl_document u where u.project_id = :projectId order by u.document_id desc", nativeQuery = true)
	public List<DocumentData> getDocumentDataByProjectId(@Param("projectId") int projectId);
	
	@Transactional
	@Modifying
	@Query("update Document set lastVersionNumber = :lastVersionNumber where documentId = :documentId")
	public void updateLatedVersionNumber(int documentId, float lastVersionNumber);
	
	@Query("select u from Document u where u.documentApproval = 0 order by u.documentId desc")
	public List<Document> getPendingDocumentList();
	
	@Transactional
	@Modifying
	@Query("update Document d set d.documentApproval = :documentApproval, d.approvalDatetime = :approvalDatetime, d.documentRevisedId = :documentRevisedId, d.userId = :userId  where d.documentId = :documentId ")
	public void updateDocumentApproval(int documentId, int documentApproval, int documentRevisedId, String approvalDatetime, int userId);
}
