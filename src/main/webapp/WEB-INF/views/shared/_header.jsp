<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--10-of-12">
		<div class="top-nav">
			<a class="logo" href="/"><i class="fa fa-lg fa-bookmark-o"></i> tlrl</a>
			<sec:authorize access="hasRole('ROLE_USER')">
			(<a href="/@<sec:authentication property="name"/>"><sec:authentication property="name"/></a>)
			</sec:authorize>
			<span class="pull-right">
				<a href="/recent">recent</a> &middot; 
				<a href="/popular">popular</a>
				<span class="spacer">|</span>
				<sec:authorize access="hasRole('ROLE_USER')">
					my <a href="/@<sec:authentication property="name"/>">bookmarks</a>
					<span class="spacer">|</span>
					<a href="/signout" target="_self">sign out <i class="fa fa-sign-out"></i></a>					
				</sec:authorize>
				<sec:authorize access="!hasRole('ROLE_USER')">
					<a href="/" target="_self">sign in <i class="fa fa-sign-in"></i></a>					
				</sec:authorize>				
			</span>
		</div>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>
