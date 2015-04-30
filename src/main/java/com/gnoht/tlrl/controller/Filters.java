package com.gnoht.tlrl.controller;

import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.SharedStatus;

public class Filters {

	private boolean untagged = false;
	private ReadLaterStatus readLaterStatus = ReadLaterStatus.NA; 
	private SharedStatus sharedStatus = SharedStatus.NA;
	
	public Filters() {}
	
	public Filters(String ... filters) {
		for(String filter: filters) {
			if(filter.equals("private")) {
				sharedStatus = SharedStatus.PRIVATE;
			} else if(filter.equals("public")) {
				sharedStatus = SharedStatus.PUBLIC;
			} else if(filter.equals("untagged")) {
				untagged = true;
			} else if(filter.equals("unread")) {
				readLaterStatus = ReadLaterStatus.UNREAD;
			}
		}
	}

	public boolean isUntagged() {
		return untagged;
	}
	public SharedStatus getSharedStatus() {
		return sharedStatus;
	}
	public ReadLaterStatus getReadLaterStatus() {
		return readLaterStatus;
	}
}
