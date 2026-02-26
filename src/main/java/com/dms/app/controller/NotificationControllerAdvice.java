package com.dms.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.dms.app.model.Category;
import com.dms.app.model.Document;
import com.dms.app.repository.CategoryRepository;
import com.dms.app.repository.DepartmentRepository;
import com.dms.app.repository.DocumentRepository;
import com.dms.app.repository.DocumentSubTypeRepository;
import com.dms.app.repository.DocumentTypeRepository;
import com.dms.app.repository.ProjectRepository;

@ControllerAdvice
public class NotificationControllerAdvice {
	
	 public NotificationControllerAdvice() {
			
	 }
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private ProjectRepository projectRepository;
		
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	
	@Autowired
	private DocumentSubTypeRepository documentSubTypeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	
	@ModelAttribute
    public void addAttributes(Model model) {
		
	    List<Document> documentList = documentRepository.getPendingDocumentList();		
		model.addAttribute("documentListSize", documentList.size());		
		documentList.forEach(doc->{
			doc.setProjectName(projectRepository.getById(doc.getProjectId()).getProjectName());
			
			Category category = categoryRepository.getById(doc.getDocumentCategory());
			int departmentId = category.getDepartmentId();			
			String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
			
			doc.setCategoryName(category.getCategoryName());
			doc.setDocumentTypeName(documentTypeRepository.getById(doc.getDocumentType()).getDocumentTypeName());
			if(doc.getDocumentSubType() != 0) {
				doc.setDocumentSubTypeName(documentSubTypeRepository.getById(doc.getDocumentSubType()).getDocumentSubTypeName());
			} else {
				doc.setDocumentSubTypeName("-");
			}
			doc.setDepartmentName(departmentName);
			
		});		
        model.addAttribute("documentList", documentList);        
    }
}
