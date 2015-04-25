package com.gnoht.tlrl.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import com.gnoht.tlrl.security.ClientOnlyAuthCodeResourceDetails;
import com.gnoht.tlrl.util.StringUtils;

@Configuration
@EnableOAuth2Client
public class OAuth2SecurityConfig {

	@Resource private Environment env;
	// injected by @EnableOAuth2Client config, per user session
	@Resource private OAuth2ClientContext oauth2ClientContext;
	
	@Bean
	public OAuth2ProtectedResourceDetails googleOAuthResouce() {
		AuthorizationCodeResourceDetails details = new ClientOnlyAuthCodeResourceDetails();
		details.setId("oauth2-google-client");
		details.setClientId(env.getProperty("oauth2.google.client.id"));
		details.setClientSecret(env.getProperty("oauth2.google.client.secret"));
		details.setAccessTokenUri(env.getProperty("oauth2.google.accessTokenUri"));
		details.setUserAuthorizationUri(env.getProperty("oauth2.google.userAuthorizationUri"));
		details.setPreEstablishedRedirectUri(env.getProperty("oauth2.google.preestablished.redirect.url"));
		details.setTokenName(env.getProperty("oauth2.google.authorization.code"));
		details.setScope(StringUtils.toList(env.getProperty("oauth2.google.auth.scope")));
		details.setUseCurrentUri(false);
		details.setAuthenticationScheme(AuthenticationScheme.query);
		details.setClientAuthenticationScheme(AuthenticationScheme.form);
		return details;
	}
	
	@Bean
  public OAuth2RestTemplate googleRestTemplate() {
		return new OAuth2RestTemplate(googleOAuthResouce(), oauth2ClientContext);
  }
}
