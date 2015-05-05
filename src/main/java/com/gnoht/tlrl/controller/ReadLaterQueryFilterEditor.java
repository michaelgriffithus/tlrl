package com.gnoht.tlrl.controller;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * {@link PropertyEditor} that resolves ReadLater query filters from HTTP request
 * parameter to {@link ReadLaterQueryFilter} object. 
 */
public class ReadLaterQueryFilterEditor extends PropertyEditorSupport {

	/**
	 * Creates a {@link ReadLaterQueryFilter} from an HTTP request parameter. 
	 * The request parameter is a comma separated list, (e.g, private,untagged, ..) 
	 * of filters. This editor does no validation and invalid parameters are 
	 * ignored by ReadLaterQueryFilter.
	 * 
	 * @return returns the {@link ReadLaterQueryFilter} instance 
	 */
	@Override
	public void setAsText(String value) throws IllegalArgumentException {
		ReadLaterQueryFilter readLaterQueryFilter = new ReadLaterQueryFilter();
		if(value != null) {
			readLaterQueryFilter = new ReadLaterQueryFilter(value.split(","));
		}
		setValue(readLaterQueryFilter);
	}

	@Override
	public String getAsText() {
		return super.getAsText();
	}
	
}
