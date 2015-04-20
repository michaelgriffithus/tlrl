<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>TL;RL</title>
	<link rel="icon" href="/favicon.ico">
	<link type="text/css" href="/static/lib/fontawesome/css/font-awesome.min.css" rel="stylesheet">
	<link type="text/css" href="/static/css/tlrl.css" rel="stylesheet">
</head>
<body>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--10-of-12">
		<h1 class="text-muted">Welcome to <span class="logo">tlrl</span></h1>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--5-of-12">
		<p class="lead">Too long; read later&mdash;a simple bookmarking application. Bookmark a url,
			organize it with tags&mdash;you can even search the contents of your bookmark.
		</p>
		<p class="lead">  	
			See some <a href="/recent">recent</a> bookmarks or what's currently
			<a href="/popular">popular</a> with our users. 
		</p>
		<p class="lead">Sign in to get started.</p>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--5-of-12">
		<sec:authorize access="!isFullyAuthenticated()">hello</sec:authorize>
		<p>
			<a href="<c:url value="/auth/google"/>">
				<img src="<c:url value="/static/img/signin-google.png"/>" style="width: 182px; height: 40px;">
			</a>		
		</p>
	</div>
</div>
</body>
</html>