package com.gnoht.tlrl.security;

import java.util.HashMap;
import java.util.Map;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OAuth20AuthenticationService 
		implements OAuthAuthenticationService {

	protected static final Logger LOG =
			LoggerFactory.getLogger(OAuth20AuthenticationService.class);

	protected static final String VERSION = "2.0";
	protected static final Token NULL_TOKEN = null;

	protected DefaultApi20 api;
	protected OAuthConfig config;
	
	public OAuth20AuthenticationService(DefaultApi20 api, OAuthConfig config) {
		this.api = api;
		this.config = config;
	}

	/**
	 * Helper for creating base {@link OAuthRequest} with POST params.
	 * @param url
	 * @param params
	 * @return
	 */
	protected OAuthRequest createPostRequest(String url, Map<String, String> params) {
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
    request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
    request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
    request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
    
    for(String key: params.keySet()) 
    	request.addBodyParameter(key, params.get(key));
    
    return request;
	}
	
	protected Map<String, String> createParams(String ... args) {
		if(args.length % 2 != 0) 
			throw new IllegalArgumentException("params should be in the form name, value. " +
					"It appears one of your params is missing a value");
		
		Map<String, String> params = new HashMap<String, String>();
		for(int i=0; i < args.length; i++) {
			params.put(args[i++], args[i]);
		}
		return params;
	}
	
	/**
	 * Helper for creating a base {@link OAuthRequest} with GET params.
	 * @param url
	 * @param params
	 * @return
	 */
	protected OAuthRequest createRequest(String url, Map<String, String> params) {
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
    request.addQuerystringParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
    request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
    request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
    
    if(config.hasScope()) 
    	request.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
    
    for(String key: params.keySet()) 
    	request.addQuerystringParameter(key, params.get(key));
    
    return request;
	}

	@Override
	public String getAuthorizationUrl(Token arg0) {
		return getAuthorizationUrl();
	}

	@Override
	public Token getRequestToken() {
		throw new UnsupportedOperationException("Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");	
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest request) {
		request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
	}
	
	@Override
	public String getAuthorizationUrl() {
		return api.getAuthorizationUrl(config);
	}
}
