package com.dms.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dms.app.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
	
  public List<Project>	findAllByOrderByProjectIdDesc();

}
