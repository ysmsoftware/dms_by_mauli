package com.dms.app.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.dms.app.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("select u from Category u where lower(u.categoryName) = lower(:categoryName)")
	Category getCategoryByCategoryName(String categoryName);
	
	@Transactional
	@Modifying
	@Query("delete from Category  where categoryId = :categoryId")
	public void deleteCategory(int categoryId);
	
	@Query("select u from Category u where u.departmentId = :departmentId")
	List<Category> getCategoryByDepartmentId(int departmentId);
}
