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
		<h1 class="text-muted">About</h1>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>
<%@ include file="/WEB-INF/views/shared/_about.jsp" %>
<%@ include file="/WEB-INF/views/shared/_footer-wrapper.jsp" %>
</body>
</html>