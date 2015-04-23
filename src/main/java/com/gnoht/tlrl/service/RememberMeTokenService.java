package com.gnoht.tlrl.service;

import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.gnoht.tlrl.repository.RememberMeTokenRepository;

/**
 * Service for managing {@link RememberMeTokenRepository} interactions.
 */
public interface RememberMeTokenService 
		extends PersistentTokenRepository {
}
