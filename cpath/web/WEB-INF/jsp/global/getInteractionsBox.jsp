<%@ page import="java.net.URLEncoder,
                 org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolConstants"%>

<div id="search" class="toolgroup">
    <div class="label">
        <strong>Search</strong>
    </div>
    <div class="body">
        <FORM ACTION="webservice.do" METHOD="GET">
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
        <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
            value="<%= ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME %>"/>
                    <INPUT TYPE="TEXT" name="<%= ProtocolRequest.ARG_QUERY %>"
                        value="" SIZE="10"/>
                    <INPUT TYPE="HIDDEN" name="<%= ProtocolRequest.ARG_FORMAT %>"
                        value="html"/>
                    <INPUT TYPE="SUBMIT" value="Go"/>
        </FORM>
    </div>
</div>