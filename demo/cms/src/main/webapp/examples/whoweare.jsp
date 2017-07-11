<%@ page language="java" %><%
org.apache.wicket.Session wicketSession = null;

try {
    wicketSession = org.apache.wicket.Session.get();
} catch (Exception ignore) {
}

if (wicketSession == null) {
    // wicket session is unavailable, meaning the request is not from CMS-authenticated user!
    response.sendError(403);
    return;
}
%>

<%
org.hippoecm.frontend.session.UserSession userSession = org.hippoecm.frontend.session.UserSession.get();
javax.jcr.Session jcrSession = jcrSession = userSession.getJcrSession();
%>

<html>
<head>
<title>Who we are</title>
</head>
<body>

<h1>Example IFrame Page at <%=request.getContextPath()%><%=request.getServletPath()%></h1>
<hr/>

<p>Hello, <%=jcrSession.getUserID()%>!</p>

<h2>Who we are</h2>
<p>We're somewhat geeky and just a little bit obsessed with open source development. We love Java, foosball and a good community outing.</p>
<p>At a formal presentation, chances are we're wearing an ApacheCon shirt under that button down. We've been known to run a marathon together.</p>
<p>We're probably still talking about AngularJS on the office balcony after hours, and we're always looking for people with the right mix of enthusiasm and nerdiness to join our team.</p>

</body>
</html>