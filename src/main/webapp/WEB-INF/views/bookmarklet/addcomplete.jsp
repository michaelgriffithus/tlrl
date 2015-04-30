<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>TL;RL</title>
	<link type="text/css" href="/static/lib/fontawesome/css/font-awesome.min.css" rel="stylesheet">
	<link type="text/css" href="/static/css/tlrl.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/shared/_header.jsp" %>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--10-of-12">
		<p style="background-color:#fcf8e3;padding: 5px;" class="lead"><a href="${readLater.url}">${readLater.title}</a>
		<br>
		has been saved to your bookmarks!
		</p>
		<br/>
		<p class="text-muted" style="font-weight: 300;"> 
		<b>Why are some pages saved directly from the <i class="logo">tlrl</i> <a href="http://en.wikipedia.org/wiki/Bookmarklet">bookmarklet</a> while others redirected here?</b>
		The website for the page you just bookmarked has a <a href="http://en.wikipedia.org/wiki/Content_Security_Policy">Content Security 
		Policy</a> that prohibits bookmarklets from directly executing external scripts, which <i class="logo">tlrl</i>
		requires to work normally. To get around this restriction, we open up a new tab/window to perform 
		saving of the webpage, then redirect you here when it's complete.
		</p>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>
<h1>
<br/>
<br/>
<br/></h1>
<%@ include file="/WEB-INF/views/shared/_footer-wrapper.jsp" %>
</body>
</html>