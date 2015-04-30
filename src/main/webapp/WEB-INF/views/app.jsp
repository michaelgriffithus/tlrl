<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" data-ng-app="tlrl">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>TLRL</title>
	<!-- link type="text/css" href="/static/lib/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" -->
	<link type="text/css" href="/static/lib/fontawesome/css/font-awesome.min.css" rel="stylesheet">
	<link type="text/css" href="/static/css/tlrl.css" rel="stylesheet">
	
</head>
<body>
	<div class="container">
		<div data-ng-view></div>
		<div class="grid">
			<div class="grid__col grid__col--1-of-12"></div>
			<div class="grid__col grid__col--10-of-12">
			<x-ng-include src="'/partials/_footer.html'"></x-ng-include>
			</div>
			<div class="grid__col grid__col--1-of-12"></div>
		</div>
	</div>
	<script type="text/javascript" src="/static/lib/jquery/dist/jquery.min.js"></script> 
	<script type="text/javascript" src="/static/lib/angular/angular.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-route/angular-route.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-resource/angular-resource.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-sanitize/angular-sanitize.min.js"></script>
	<script type="text/javascript" src="/static/lib/moment/min/moment.min.js"></script>
	<script type="text/javascript" src="/static/js/modules.js"></script>
	<script type="text/javascript">
	<c:choose>
		<c:when test="${user eq null}">
			var currentUser = null;
		</c:when>
		<c:otherwise>
			var currentUser = {name: "${user.name}"};
		</c:otherwise>
	</c:choose>
	</script>
</body>
</html>