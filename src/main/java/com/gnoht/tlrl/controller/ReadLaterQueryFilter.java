package com.gnoht.tlrl.controller;

import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.SharedStatus;

/**
 * Object holding filter attributes for {@link ReadLater} queries.
 */
public class ReadLaterQueryFilter {

	/** For filtering tagged vs. untagged {@link ReadLater}s */
	private boolean untagged = false;
	
	/** For filtering ReadLater status. Defaults to NA (not applicable) */
	private ReadLaterStatus readLaterStatus = ReadLaterStatus.NA;
	
	/** For filtering ReadLater shared status. Defaults to NA (not applicable) */
	private SharedStatus sharedStatus = SharedStatus.NA;
	
	public ReadLaterQueryFilter() {}
	
	public ReadLaterQueryFilter(String ... filters) {
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
