package com.gnoht.tlrl.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.security.OAuthAuthenticationStatus.Code;

public class GoogleApi2 extends DefaultApi20 {

	public static final String AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=%s&redirect_uri=%s";
	public static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";
	public static final String EMPTY_SECRET = "";
	public static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("\"access_token\" : \"([^&\"]+)\"");
	
	@Override
	public String getAccessTokenEndpoint() {
		return "https://accounts.google.com/o/oauth2/token";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		if(config.hasScope()) {
			return String.format(SCOPED_AUTHORIZE_URL, 
					config.getApiKey(), 
					OAuthEncoder.encode(config.getCallback()),
					OAuthEncoder.encode(config.getScope()));
		} else {
			return String.format(AUTHORIZE_URL, config.getApiKey(),
					OAuthEncoder.encode(config.getCallback()));
		}
	}

	@Override
  public AccessTokenExtractor getAccessTokenExtractor() {
		return new AccessTokenExtractor() {
			@Override
			public Token extract(String response) {
				if(response == null || response.isEmpty()) 
					throw new OAuthException("Not a valid OAuthRequest response!");
				
        Matcher matcher = ACCESS_TOKEN_PATTERN.matcher(response);
				if(!matcher.find()) 
					throw new OAuthResponseException("Unable to find access_token.", response);
				
				return new Token(OAuthEncoder.decode(matcher.group(1)), EMPTY_SECRET, response);
			}
		};
  }
	

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}
	
	@Override
  public OAuthService createService(OAuthConfig config) {
      return new GoogleOAuth2Service(this, config);
  }

	/**
	 * {@link OAuthService} implementation specifically for Google OAuth Services.
	 */
	private class GoogleOAuth2Service extends OAuth20AuthenticationService {
		 
		public static final String TOKEN_VALIDATION_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=%s";
		public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String GRANT_TYPE = "grant_type";
    
    @Resource
    private RestTemplate restTemplate;

    public GoogleOAuth2Service(DefaultApi20 api, OAuthConfig config) {
			super(api, config);
		}

    /*
     * (non-Javadoc)
     * @see org.scribe.oauth.OAuthService#getAccessToken(org.scribe.model.Token, org.scribe.model.Verifier)
     */
		@Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
			OAuthRequest request = createPostRequest(api.getAccessTokenEndpoint(), 
					createParams(OAuthConstants.CODE, verifier.getValue(), 
							GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE));
			
      Response response = request.send();
      LOG.debug("Response code={}, message={}, body={}", 
      		response.getCode(), response.getMessage(), response.getBody());
      //extract the token
      return api.getAccessTokenExtractor().extract(response.getBody());
    }
		
		/*
		 * (non-Javadoc)
		 * @see com.gnoht.timeline.core.service.security.OAuthAuthenticationService#authenticate(java.lang.String)
		 */
		@Override
		public OAuthAuthenticationToken authenticate(String verifier) {
			try {
				Token accessToken = getAccessToken(NULL_TOKEN, new Verifier(verifier));
				TokenInfo info = restTemplate.getForObject(new URI(getValidationUrl(accessToken)), TokenInfo.class);
				return new OAuthAuthenticationToken(new OAuthAuthenticationStatus(), info.email);
				
			} catch(OAuthException | RestClientException | URISyntaxException e) {
				String msg = "Unable to get access token";
				LOG.error(msg, e);
				return new OAuthAuthenticationToken(new OAuthAuthenticationStatus(Code.ERROR, msg));
			}
		}
		
		private String getValidationUrl(Token accessToken) {
			return String.format(TOKEN_VALIDATION_URL, accessToken.getToken());
		}

		@Override
		public OAuthAuthenticationToken authenticate(Token verifier) {
			throw new UnsupportedOperationException();
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class TokenInfo {
		@JsonProperty
		String audience;
		@JsonProperty
		String scope;
		@JsonProperty(value="user_id")
		String userid;
		@JsonProperty
		String email;
		@JsonProperty(value="expires_in")
		int expiresIn;
		
		public TokenInfo() {}

		@Override
		public String toString() {
			return "TokenInfo [audience=" + audience + ", scope=" + scope
					+ ", userid=" + userid + ", expiresIn=" + expiresIn + "]";
		}
		
	}
}
