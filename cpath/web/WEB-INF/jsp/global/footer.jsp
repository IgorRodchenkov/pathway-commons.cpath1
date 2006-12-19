<%@ page import="org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%
	// get title
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);

	// get tag line
	String footerFile = CPathUIConfig.getPath("footer.jsp");
%>
                </div>
                <!-- End Div:  app -->
            </div>
            <!-- End Div:  bodycol -->
        </td>
    </tr>
</table>

<div id="footer">
  <p>
  <jsp:include page="<%=footerFile%>" flush="true"/>    
  </p>
</div>

<jsp:include page="../global/xdebug.jsp" flush="true" />
</body>
</html>
