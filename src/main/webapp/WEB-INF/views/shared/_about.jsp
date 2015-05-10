<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--5-of-12">
		<p class="lead"><s:message code="about.p1"></s:message></p>
		<p class="lead"><s:message code="about.p2"></s:message></p>
		<p class="lead">
			<sec:authorize access="!isAuthenticated()">Sign in to get started. </sec:authorize> 		
		</p>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--5-of-12">
		<p>
		<sec:authorize access="!isAuthenticated()">
			<a href="<c:url value="/auth/google"/>">
				<img src="<c:url value="/static/img/signin-google.png"/>" style="width: 182px; height: 40px;">
			</a>		
		</sec:authorize>
		</p>
	</div>
</div>
    