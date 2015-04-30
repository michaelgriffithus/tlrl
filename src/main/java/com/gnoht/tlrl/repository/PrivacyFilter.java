package com.gnoht.tlrl.repository;

public class PrivacyFilter extends Filter<PrivacyFilter.Privacy> {

	private String name;
	private Privacy value;
	
	public PrivacyFilter(String name, Privacy value) {
		super(name, value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Privacy getValue() {
		return value;
	}

	@Override
	public void setValue(Privacy value) {
		this.value = value;
	}

	public static enum Privacy {
		NA,
		PUBLIC,
		PRIVATE
	}
}
