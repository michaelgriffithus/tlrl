package com.gnoht.tlrl.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.gnoht.tlrl.domain.support.Managed;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Wrapper entity for Spring Security's {@link PersistentRememberMeToken} used
 * in "Remember Me" functionality. 
 */
@Entity(name="remember_me_token")
public class RememberMeToken extends Managed<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String userName;
	private String value;
	@Column(unique=true)
	private String series;
	private Date date;
	
	public RememberMeToken() {}
	
	public RememberMeToken(String userName, String value, String series, Date date) {
		this.userName = userName;
		this.value = value;
		this.series = series;
		this.date = date;
	}
	
	public RememberMeToken(PersistentRememberMeToken token) {
		this(token.getUsername(), token.getTokenValue(), token.getSeries(), token.getDate());
	}
	
	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public String getValue() {
		return value;
	}
	public String getSeries() {
		return series;
	}
	public Date getDate() {
		return date;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("userName", userName)
			.add("series", series)
			.add("date", date);
	}
}
