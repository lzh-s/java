<%@ page language="java" contentType="text/html;charset=UTF-8" import="java.text.*,java.util.*;"%>     
<html>     
<head>     
<title>Show time</title>     
</head>     
<body>   
    <h1>this is a simple test</h1>   
     Hello :      
     <%     
         SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");     
         String str = format.format(new Date());     
      %>     
      <%=str %> 
      
       <%for(int i = 0 ; i < 10; i++)
          {
            out.println(i);
        %>
       <br>
        <%}
        %>    
</body>     
</html> 