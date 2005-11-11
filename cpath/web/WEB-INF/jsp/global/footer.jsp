<%@ page import="org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%
	// get title
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);

	// get tag line
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String tagLine = webUIBean.getHomePageMaintenanceTagLine();
%>
                </div>
                <!-- End Div:  app -->
            </div>
            <!-- End Div:  bodycol -->
        </td>
    </tr>
    <!-- End Body Column -->

    <!-- Start Footer -->
    <tr>
        <% if (title.equals("cPath::Administration")){ %>
		<td></td>
        <td>
		<% } else { %>
	    <td colspan=2 width=100%>
	    <% } %>
            <div/>
            <div/>
            <div/>
            <div id="footer">
	            <div>
                <% out.print(tagLine); %>
                </div>
                <div>
                  <a href="disclaimer.do">Legal Disclaimer / Privacy Notice</a> |
                  <a href="http://cbio.mskcc.org">Computational Biology Center</a> |
                  <a href="http://www.mskcc.org">Memorial Sloan-Kettering Cancer Center</a>
                </div>
                <div>
                  Copyright &#169; 2004 Memorial Sloan-Kettering Cancer Center.
                </div>
            </div>
        </td>
    </tr>
    <!-- End Footer -->
</table>
<!--  End Main Table -->

<jsp:include page="../global/xdebug.jsp" flush="true" />
</body>
</html>
