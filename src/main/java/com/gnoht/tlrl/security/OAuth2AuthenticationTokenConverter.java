package com.gnoht.tlrl.security;

import java.util.Map;

public interface OAuth2AuthenticationTokenConverter {

	/**
	 * Extracts an {@link OAuth2Authentication} from given OAuth2 token info.
	 * 
	 * @param tokenInfo Token information obtained after an OAuth2 authorization.
	 * @return 
	 */
	OAuth2Authentication extractAuthentication(Map<String, Object> tokenInfo);
}
