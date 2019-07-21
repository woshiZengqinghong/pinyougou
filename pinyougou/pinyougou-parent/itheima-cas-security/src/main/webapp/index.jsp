<%--
  Created by IntelliJ IDEA.
  User: 13790
  Date: 2019/7/10
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@page language="java" contentType="text/html; charset=utf-8"
        pageEncoding="utf-8"%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h2>订单首页Hello World!</h2>
<h2>用户名security：<%=SecurityContextHolder.getContext().getAuthentication().getName()%></h2>
<a href="/logout/cas">aaaaa</a>
<h2>aaaaa</h2>

<a href="/logout/cas">退出登录</a>
</body>
</html>
