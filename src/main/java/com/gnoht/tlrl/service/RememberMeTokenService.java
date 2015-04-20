package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.RememberMeToken;
import com.gnoht.tlrl.repository.RememberMeTokenRepository;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service for managing {@link RememberMeTokenRepository} interactions.
 */
public interface RememberMeTokenService 
		extends ManageableService<Long, RememberMeToken> {
}
