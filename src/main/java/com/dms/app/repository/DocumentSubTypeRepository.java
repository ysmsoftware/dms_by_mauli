package com.dms.app.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.dms.app.model.DocumentSubType;

public interface DocumentSubTypeRepository  extends JpaRepository<DocumentSubType, Integer> {

	public List<DocumentSubType> findDocumentSubTypeByDocumentTypeName(int documentTypeName);
	
	@Query("select u from DocumentSubType u where u.documentTypeName = :documentTypeName")
	public List<DocumentSubType> getDocumentSubTypeListByDocumentTypeName(int documentTypeName);
	
	@Query("select u from DocumentSubType u where lower(u.documentSubTypeName) = lower(:documentSubTypeName)")
	DocumentSubType getDocumentSubTypeByDocumentTypeName(String documentSubTypeName);
	
	@Transactional
	@Modifying
	@Query("delete from DocumentSubType  where documentSubTypeId = :documentSubTypeId")
	public void deleteDocumentSubType(int documentSubTypeId);
}
