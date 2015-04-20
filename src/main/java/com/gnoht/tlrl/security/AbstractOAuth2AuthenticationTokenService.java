package com.gnoht.tlrl.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Base implementation of {@link OAuth2AuthenticationTokenService} that delegates 
 * retrieval of OAuth2 access token with configured {@link OAuth2RestOperations}, and 
 * delegates converting the access token to an {@link Authentication} with configured
 * {@link OAuth2AuthenticationTokenConverter}. 
 * 
 * <p>Much of the logic in this class is borrowed from Spring's {@link RemoteTokenServices} 
 * implementation.
 * 
 * @param <T> type of Authentication to return.
 */
public abstract class AbstractOAuth2AuthenticationTokenService implements
		OAuth2AuthenticationTokenService {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractOAuth2AuthenticationTokenService.class);
	
	final OAuth2RestOperations restTemplate;
	final String baseAuthorizationUrl;
	final String tokenName;
	final String clientId;
	final String clientSecret;
	final OAuth2AuthenticationTokenConverter tokenConverter;
	
	public AbstractOAuth2AuthenticationTokenService(OAuth2RestOperations restTemplate, String url, 
			String clientId, String clientSecret, String tokenName, OAuth2AuthenticationTokenConverter converter) {
		this.baseAuthorizationUrl = url;
		this.tokenName = tokenName;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.tokenConverter = converter;
		this.restTemplate = restTemplate;
		((RestTemplate) this.restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
        @Override // Ignores 400
        public void handleError(ClientHttpResponse response) throws IOException {
          if(response.getRawStatusCode() != 400) {
            super.handleError(response);
          }
        }
      });
	}
	
	/**
	 * Takes given access token and queries provider for additional token info.
	 * 
	 * @param accessToken
	 * @return
	 */
	Map<String, Object> getTokenInfo(OAuth2AccessToken accessToken) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		// provider expects just raw token value
		params.add(tokenName, accessToken.getValue()); 
		
    return restTemplate
    	.exchange(getAuthorizationUrl(accessToken), HttpMethod.POST, 
    		new HttpEntity<>(params, getValidationRequestHeader(clientId, clientSecret)), 
    		new ParameterizedTypeReference<Map<String, Object>>() {})
    	.getBody();
	}

	/**
	 * The final authorization url (e.g. authorization url + access_token). 
	 * 
	 * @param accessToken
	 * @return
	 */
	protected abstract String getAuthorizationUrl(OAuth2AccessToken accessToken);
	
	/**
	 * Builds the request headers needed to perform a validation for given 
	 * access token.
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @return
	 */
	HttpHeaders getValidationRequestHeader(String clientId, String clientSecret) {
		String credentials = String.format("%s:%s", clientId, clientSecret);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + 
				new String(Base64.encode(credentials.getBytes(StandardCharsets.UTF_8))));
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return httpHeaders;
	}
	
	/*
	 * @see com.gnoht.tlrl.security.OAuth2AuthenticationTokenService#getAuthentication()
	 */
	@Override
	public OAuth2Authentication getAuthentication() {
		return getAuthentication(getAccessToken());
	}

	/*
	 * @see com.gnoht.tlrl.security.OAuth2AuthenticationTokenService#getAuthentication(
	 * 	org.springframework.security.oauth2.common.OAuth2AccessToken)
	 */
	@Override
	public OAuth2Authentication getAuthentication(OAuth2AccessToken accessToken) {
		LOG.info("Starting getAuthentication(): accessToken={}", accessToken);
		Map<String, Object> tokenInfo = getTokenInfo(accessToken);
		return tokenConverter.extractAuthentication(tokenInfo);
	}

	/*
	 * @see com.gnoht.tlrl.security.OAuth2AuthenticationTokenService#getAccessToken()
	 */
	@Override
	public OAuth2AccessToken getAccessToken() {
		LOG.info("Starting getAccessToken()");
		return restTemplate.getAccessToken();
	}

	public String getBaseAuthorizationUrl() {
		return baseAuthorizationUrl;
	}
}

