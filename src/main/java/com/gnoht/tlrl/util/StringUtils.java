package com.gnoht.tlrl.util;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Splitter;

public final class StringUtils {
	
	static final String DEFAULT_LIST_DELIM = ",";
	
	public static List<String> toList(String s) {
		return toList(s, DEFAULT_LIST_DELIM);
	}
	
	public static List<String> toList(String s, String delim) {
		if(s == null) 
			return Collections.emptyList();
		return Splitter.on(delim).splitToList(s);
	}
}
