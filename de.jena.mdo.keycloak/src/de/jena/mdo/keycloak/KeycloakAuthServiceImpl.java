/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.jena.mdo.keycloak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.AccessTokenResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.jena.mdo.keycloak.api.KeycloakAuthService;

/**
 * 
 * @author ilenia
 * @since Jan 19, 2023
 */
@Component(name = "KeycloakAuthService")
public class KeycloakAuthServiceImpl implements KeycloakAuthService {
	
	// create a new instance based on the configuration defined in keycloak.json
	private AuthzClient authzClient;
	private AccessTokenResponse token;
	
	@Activate 
	public void activate() {
//		TODO this should be substituted with the actual client config from the city
		try(InputStream configStream = new FileInputStream(new File("./config/keycloak.json"))) {
			authzClient = AuthzClient.create(configStream);	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see de.jena.mdo.keycloak.api.KeycloakAuthService#getBase64TokenString()
	 */
	@Override
	public String getBase64TokenString() {
		if(token == null) {
			token = obtainAccessToken();
		}
		if(isTokenValid(token.getToken())) {
			return encodeTokenString(token.getToken());
		}
		else if(isTokenValid(token.getRefreshToken())) {
			return encodeTokenString(token.getRefreshToken());
		}
		else {
			token = obtainAccessToken();
		}
		return encodeTokenString(token.getToken());
	}
	
	private String encodeTokenString(String tokenString) {
		return Base64.encodeBase64String(tokenString.getBytes());
	}

	
	private AccessTokenResponse obtainAccessToken() {
		return authzClient.obtainAccessToken();
	}

	private boolean isTokenValid(String tokenStr) {
		try {
			authzClient.authorization(tokenStr).authorize();
			return true;
		} catch(Exception e) {
			return false;
		}		
	}
}
