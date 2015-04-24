<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>TL;RL</title>
	<link type="text/css" href="/static/lib/fontawesome/css/font-awesome.min.css" rel="stylesheet">
	<link type="text/css" href="/static/css/tlrl.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/shared/header.jsp" %>
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--10-of-12">
		<h1 class="text-muted">Sign Up</h1>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>	
<div class="grid">
	<div class="grid__col grid__col--1-of-12"></div>
	<div class="grid__col grid__col--5-of-12">
		<p class="lead">
		Thanks for signing up with <span class="logo">tlrl</span>. 
		We just need to confirm 
		<span class="hl">${user.email}</span> is your correct email. While we're at, let's
		pick a <span class="hl">username</span> as well.<br/>
		</p>
	</div>
	<div class="grid__col grid__col--5-of-12">
		<p>
		<sf:form action="/signup" method="post" modelAttribute="user">
			<c:if test="${!empty requestScope['org.springframework.validation.BindingResult.user'].allErrors}">
				<span class="text-danger"><sf:errors path="*" /></span><br/>
			</c:if>
			<sf:input type="text" path="name" style="width: 200px;"/> 
			<p>
				<button type="submit" class="btn btn-success">Yes that's me</button>
				<button type="button" class="btn btn-default" onclick="cancel(); return true;">No that's not me</button>
			</p>
		</sf:form>
		</p>
	</div>
	<div class="grid__col grid__col--1-of-12"></div>
</div>

	<script type="text/javascript" src="/static/lib/jquery/dist/jquery.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular/angular.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-route/angular-route.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-resource/angular-resource.min.js"></script>
	<script type="text/javascript" src="/static/lib/angular-sanitize/angular-sanitize.min.js"></script>
	<script type="text/javascript" src="/static/lib/moment/min/moment.min.js"></script>
	<script type="text/javascript">
		function cancel() {
			window.location.href = "/signout";
		}
	</script>
</body>
</html>