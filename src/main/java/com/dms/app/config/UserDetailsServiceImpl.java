package com.dms.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.dms.app.model.User;
import com.dms.app.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User user = userRepository.getUserByUserName(userName);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user");
		}
		
		CustomUserDetails customerUserDetails = new CustomUserDetails(user);
		
		return customerUserDetails;
	}

}
