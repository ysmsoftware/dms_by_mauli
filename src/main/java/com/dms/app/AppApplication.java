package com.dms.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
	
	

	// spring boot 2.x
		/*
		 * @Bean public ServletWebServerFactory servletContainer() {
		 * TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
		 * 
		 * @Override protected void postProcessContext(Context context) {
		 * SecurityConstraint securityConstraint = new SecurityConstraint();
		 * securityConstraint.setUserConstraint("CONFIDENTIAL"); SecurityCollection
		 * collection = new SecurityCollection(); collection.addPattern("/*");
		 * securityConstraint.addCollection(collection);
		 * context.addConstraint(securityConstraint); } };
		 * tomcat.addAdditionalTomcatConnectors(redirectConnector()); return tomcat; }
		 * 
		 * private Connector redirectConnector() { Connector connector = new
		 * Connector("org.apache.coyote.http11.Http11NioProtocol");
		 * connector.setScheme("http"); connector.setPort(80);
		 * connector.setSecure(false); connector.setRedirectPort(443); return connector;
		 * }
		 */
}
