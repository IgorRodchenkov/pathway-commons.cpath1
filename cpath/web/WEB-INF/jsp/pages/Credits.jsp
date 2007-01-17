<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Credits");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<% String appName = CPathUIConfig.getWebUIBean().getApplicationName(); %>
<div>
<h1>Credits</h1>
<p><%= appName %> runs on <a href="http://cbio.mskcc.org/dev_site/cpath/">cPath software</a>,
and was created by:  <a href="http://baderlab.org/">Gary Bader</a>,
<a href="http://cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</a>,
<a href="http://cbio.mskcc.org/people/info/emek_demir.html">Emek Demir</a>,
<a href="http://cbio.mskcc.org/people/info/benjamin_gross.html">Benjamin Gross</a>, and
<a href="http://cbio.mskcc.org/people/info/chris_sander.html">Chris Sander</a>.
</p>

<p><%= appName %> is built entirely on open source software.  We gratefully
acknowledge the following open source projects.</p>

<table>
<tr valign=top>
    <td><a href="http://tomcat.apache.org/">Apache Tomcat</a></td>
    <td>Servlet / Java Server Pages (JSP) Engine.</td>
</tr>
<tr valign=top>
    <td><a href="http://jakarta.apache.org/commons/">Apache Commons</a></td>
    <td>Multiple Apache Commons projects, including DBCP:  database connection pooling services.</td>
</tr>
<tr valign=top>
    <td><a href="http://logging.apache.org/log4j/docs/">Apache Log4j</a></td>
    <td>Java logging framework.  Helps us track down bugs.</td>
</tr>
<tr valign=top>
    <td><a href="http://lucene.apache.org/java/docs/">Apache Lucene</a></td>
    <td>Full-featured text search engine library.  Powers all our search functionality.</td>
</tr>
<tr valign=top>
    <td><a href="http://lucene.apache.org/java/docs/">Apache Struts</a></td>
    <td>Extensible framework for creating Java web applications.  Powers all or our
    web pages.</td>
</tr>
<tr valign=top>
    <td><a href="http://www.hpl.hp.com/personal/jjc/arp/">ARP: Another RDF Parser</a></td>
    <td>RDF/XML Parser.  Parses and validates all our BioPAX files.</td>
</tr>
<tr valign=top>
    <td><a href="http://cruisecontrol.sourceforge.net/">Cruise Control</a></td>
    <td>Framework for running continuous builds.  Re-compiles and re-tests all <%= appName %>
    code after every check in.</td>
</tr>
<tr valign=top>
    <td><a href="http://www.nongnu.org/cvs/">CVS</a></td>
    <td>Version control system.</td>
</tr>
<tr valign=top>
    <td><a href="http://ehcache.sourceforge.net/">EhCache</a></td>
    <td>Java caching library.  Helps keep <%= appName %> running fast.</td>
</tr>
<tr valign=top>
    <td><a href="http://getfirebug.com/">Firebug</a></td>
    <td>A must-have extension to Firefox, used to debug Web 2.0 applications.</td>
</tr>
<tr valign=top>
    <td><a href="http://jdom.org/">JDOM</a></td>
    <td>Our favorite library for parsing XML in Java.</td>
</tr>
<tr valign=top>
    <td><a href="http://junit.org/index.htm">JUnit</a></td>
    <td>Unit test framework for regression testing.</td>
</tr>
<tr valign=top>
    <td><a href="http://www.mysql.com">MySQL</a></td>
    <td>Relational database system.</td>
</tr>
<tr valign=top>
    <td><a href="http://chrispederick.com/work/webdeveloper/">Web Developer Extension</a></td>
    <td>Another must-have extension to Firefox, used to debug HTML, CSS and Javascript.</td>
</tr>
<tr valign=top>
    <td><a href="http://developer.yahoo.com/yui/">Yahoo! UI Library (YUI)</a></td>
    <td>Web 2.0 / AJAX library.  Powers all of our dynamic HTML / AJAX features.
    Nice work, Yahoo!</td>
</tr>
</table>
<p>&nbsp;</p>
<p>
We also gratefully acknowledge <a href="http://andreasviklund.com/">Andreas Viklund</a>,
who provided the <a href="http://andreasviklund.com/templates">open source stylesheets</a>,
used to design the web site.
</p>
<p>&nbsp;</p>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />