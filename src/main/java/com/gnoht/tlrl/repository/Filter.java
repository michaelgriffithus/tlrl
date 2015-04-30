package com.gnoht.tlrl.repository;

public abstract class Filter<T> {
	
	public Filter(String name, T value) {
		setName(name);
		setValue(value);
	}
	
	public abstract String getName();
	public abstract void setName(String name);
	
	public abstract T getValue();
	public abstract void setValue(T value);
}

