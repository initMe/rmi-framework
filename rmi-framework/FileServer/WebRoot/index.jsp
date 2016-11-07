<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  </head>
  
  <body>
  	<center>
  	<div style="border: 1px">
  		测试文件上传
	  	<form action="<%=basePath%>file/upload.do" method="post" enctype="multipart/form-data">
	  		<input type="file" name="file" value="选择文件"/>
	  		<input type="text" name="w" />
	  		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  		<input type="submit" value="提交"/>
	  	</form>
  	</div>
	</center>
  </body>
</html>
