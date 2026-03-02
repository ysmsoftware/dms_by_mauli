package com.dms.app.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.dms.app.model.DocumentType;

public interface DocumentTypeRepository  extends JpaRepository<DocumentType, Integer> {

	public List<DocumentType> findDocumentTypeByCategoryName(int categoryName);
	
	@Query("select u from DocumentType u where u.categoryName = :categoryName")
	public List<DocumentType> getDocumentTypeListByCategoryName(int categoryName);
	
	@Query("select u from DocumentType u where lower(u.documentTypeName) = lower(:documentTypeName)")
	DocumentType getDocumentTypeByDocumentTypeName(String documentTypeName);
	
	@Transactional
	@Modifying
	@Query("delete from DocumentType  where documentTypeId = :documentTypeId")
	public void deleteDocumentType(int documentTypeId);
}
