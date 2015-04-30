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
	<div class="grid__col grid__col--7-of-12">
		<h1 class="text-muted">Help</h1>
		<h3 id="saving" class="text-muted">Saving bookmarks</h3>
		<p class="lead"><small>
		You can use <a href="http://en.wikipedia.org/wiki/Bookmarklet">bookmarklets</a> for bookmarking pages to <span class="logo">tlrl</span>.
		</small></p>
		<ul style="font-size: 1.1em; font-weight: 300;">
			<li class="tag"><a href="">bookmark</a> will bookmark the current page</li>
			<li class="tag"><a href="">bookmark with tags</a> same as above with option to enter tags</li>
			<li class="tag"><a href="">read later</a> will bookmark the current page and add it to your read later list</li>
			<li class="tag"><a href="">read later with tags</a> same as above with option to enter tags</li>
		</ul>  

		<h3 id="importing" class="text-muted">Importing bookmarks</h3>
		<p class="lead"><small>
		<br/>
		<br/>
		<br/>
		<br/>
		</small></p>
				
		<h3 id="managing" class="text-muted">Managing bookmarks</h3>
		<p class="lead"><small>
		<br/>
		<br/>
		<br/>
		<br/>
		</small></p>

		<h3 id="account" class="text-muted">Account settings</h3>
		<p class="lead"><small>
		<br/>
		<br/>
		<br/>
		<br/>
		</small></p>
	</div>
	<div class="grid__col grid__col--3-of-12">
		<br/>
		<ol class="text-muted">
			<li><a href="#saving">Saving bookmarks</a></li>
			<li><a href="#importing">Importing bookmarks</a></li>
			<li><a href="#managing">Managing bookmarks</a></li>
			<li><a href="#account">Account settings</a></li>
		</ol>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>

<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--10-of-12"></div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>
<%@ include file="/WEB-INF/views/shared/_footer-wrapper.jsp" %>
</body>
</html>