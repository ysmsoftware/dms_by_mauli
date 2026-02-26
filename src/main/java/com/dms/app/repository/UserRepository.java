package com.dms.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dms.app.model.User;


public interface UserRepository extends JpaRepository<User,Integer> {
	
	@Query("select u from User u where u.mobile = :mobile")
	public User getUserByUserName(@Param("mobile") String mobile);
	
	@Query("select u.name from User u where u.id = :userId")
	public String getUserNameByUseId(@Param("userId") int userId);
	
	@Transactional
	@Modifying
	@Query(value="update tbl_user set user_enabled = :enabled where user_id = :id", nativeQuery = true) 
	public int updateUserStatus(@Param("enabled") boolean enabled, @Param("id") int id);
	
	@Transactional
	@Modifying
	@Query("update User set userPhoto = :imageName where id = :userId")
	public void uploadProfilePicture(int userId, String imageName);
}