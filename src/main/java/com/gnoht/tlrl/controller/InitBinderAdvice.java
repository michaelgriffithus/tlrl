package com.gnoht.tlrl.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * {@link ControllerAdvice} that registers custom data binders for all
 * controllers under "com.gnoht.tlrl.controller" package.  
 */
@ControllerAdvice(basePackageClasses={InitBinderAdvice.class})
public class InitBinderAdvice {

	private final ReadLaterQueryFilterEditor readLaterQueryFilterEditor;
	
	public InitBinderAdvice() {
		this.readLaterQueryFilterEditor = new ReadLaterQueryFilterEditor();
	}

	@InitBinder
	public void registerCustomEditors(WebDataBinder binder) {
		binder.registerCustomEditor(ReadLaterQueryFilter.class, readLaterQueryFilterEditor);
	}
}
