<%@ page import="org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%
	// get title
    String title = (String) request.getAttribute(BaseAction.ATTRIBUTE_TITLE);

	// get tag line
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String maintenanceTagLine = webUIBean.getMaintenanceTagLine();
%>
                </div>
                <!-- End Div:  app -->
            </div>
            <!-- End Div:  bodycol -->
        </td>
    </tr>
</table>

<div id="footer">
  <P><% out.print(maintenanceTagLine); %></P>
  <P>
  <a href="disclaimer.do">Legal Disclaimer / Privacy Notice</a> |
  <a href="http://cbio.mskcc.org">Computational Biology Center</a> |
  <a href="http://www.mskcc.org">Memorial Sloan-Kettering Cancer Center</a>
  </P>
  <P>
  Copyright &#169; 2005 Memorial Sloan-Kettering Cancer Center.
  </P>
</div>

<jsp:include page="../global/xdebug.jsp" flush="true" />
</body>
</html>
