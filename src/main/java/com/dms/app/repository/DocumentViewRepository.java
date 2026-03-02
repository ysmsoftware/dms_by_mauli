package com.dms.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dms.app.model.DocumentView;
import com.dms.app.model.DocumentViewReport;

public interface DocumentViewRepository  extends JpaRepository<DocumentView, Integer> {
	
	@Query(value=" select dv.document_view_id as documentViewId, d.document_id as documentId, u.user_id as userId, d.document_title as documentTitle,  u.user_name as userName, dv.view_datetime as viewDatetime, dr.version_number as versionNumber "
			+ "from tbl_document_view dv, tbl_document_revised dr, tbl_document d, tbl_user u "
			+ "where d.document_id = dv.document_id and u.user_id = dv.user_id and dv.document_revised_id = dr.document_revised_id "
			+ "and dr.document_id = :documentId and u.user_id = :userId order by dv.document_view_id desc ", nativeQuery = true)
	public List<DocumentViewReport> getDocumentViewReportByDocumentIdAndUserId(@Param("documentId") int documentId, @Param("userId") int userId);
	
	@Query(value=" select dv.document_view_id as documentViewId, d.document_title as documentTitle,  u.user_name as userName, dv.view_datetime as viewDatetime, dr.version_number as versionNumber "
			+ "from tbl_document_view dv, tbl_document_revised dr, tbl_document d, tbl_user u "
			+ "where d.document_id = dv.document_id and u.user_id = dv.user_id and dv.document_revised_id = dr.document_revised_id "
			+ "and dr.document_id = :documentId order by dv.document_view_id desc ", nativeQuery = true)
	public List<DocumentViewReport> getDocumentViewReportByDocumentId(@Param("documentId") int documentId);

}
