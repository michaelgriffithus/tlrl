package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.support.ManageableService;

public interface UserService extends ManageableService<Long, User> {

	User findByEmail(String email);
}
