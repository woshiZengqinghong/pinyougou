<%--
  Created by IntelliJ IDEA.
  User: 13790
  Date: 2019/7/10
  Time: 10:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h2>Hello World!</h2>
<h2>用户名：<%=request.getRemoteUser()%></h2>
<a href="http://localhost:8080/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
