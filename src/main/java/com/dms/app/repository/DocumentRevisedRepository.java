package com.dms.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dms.app.model.DocumentRevised;

public interface DocumentRevisedRepository  extends JpaRepository<DocumentRevised, Integer>  {

	@Query("select u from DocumentRevised u where u.document.documentId = :documentId order by u.documentRevisedId desc")
	public List<DocumentRevised> getRevisedDocumentByDocumentId(@Param("documentId") int documentId);
	
	//public DocumentRevised findFirstByDocumentIdOrderByDocumentRevisedIdDesc(int documentId);
	
	@Query("select u from DocumentRevised u where u.document.documentId = :documentId and u.documentApproval = 1")
	public DocumentRevised getLatestRevisedDocument(int documentId);
	
	@Transactional
	@Modifying
	@Query("update DocumentRevised u set u.documentApproval = 0 where u.documentRevisedId = :documentRevisedId")
	public void updateRevisedDocumentApprovalToZero(int documentRevisedId);
	
	@Query("SELECT u FROM DocumentRevised u WHERE u.document.documentId IN (SELECT d.documentId FROM Document d WHERE d.projectId  = :projectId) order by u.documentRevisedId desc")
	public List<DocumentRevised> getRevisedDocumentByProjectId(@Param("projectId") int projectId);


}
