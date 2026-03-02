package com.dms.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dms.app.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

	@Query("select u from Department u where lower(u.departmentName) = lower(:departmentName)")
	Department getDepartmentByDepartmentName(String departmentName);
}