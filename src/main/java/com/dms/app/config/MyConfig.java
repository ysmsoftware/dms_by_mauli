package com.dms.app.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dms.app.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {
	private static final Set<String> ALLOWED_HTTP_METHODS = new LinkedHashSet<>(
			Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"));
	
	@Autowired
	AuthenticationSuccessHandler successHandler;
	
	@Autowired
	UserRepository userRepository;
	
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public FilterRegistrationBean<OncePerRequestFilter> unsupportedHttpMethodFilter() {
		OncePerRequestFilter filter = new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				if (!ALLOWED_HTTP_METHODS.contains(request.getMethod())) {
					response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
					response.setHeader("Allow", String.join(", ", ALLOWED_HTTP_METHODS));
					response.setContentType("text/plain;charset=UTF-8");
					response.getWriter().write("Method Not Allowed");
					return;
				}

				filterChain.doFilter(request, response);
			}
		};

		FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registration.addUrlPatterns("/*");
		return registration;
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	    daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
	    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	    
	    return daoAuthenticationProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/user/**").hasRole("USER")
		.antMatchers("/**").permitAll()		
		.and().formLogin().loginPage("/login").successHandler(successHandler).loginProcessingUrl("/dologin")		
		.and().csrf().disable().headers()	
		.defaultsDisabled()
        .cacheControl();
		
        //http.sessionManagement().maximumSessions(1);		
	}	
		
}