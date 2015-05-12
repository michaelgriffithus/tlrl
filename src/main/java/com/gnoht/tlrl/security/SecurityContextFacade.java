package com.gnoht.tlrl.security;

import com.gnoht.tlrl.domain.User;

public interface SecurityContextFacade {

	User getUserPrincipal();
}
