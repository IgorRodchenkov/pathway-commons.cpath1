package org.mskcc.pathdb.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.xdebug.SnoopHttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseAction extends Action{

    public ActionForward execute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        XDebug xdebug = new XDebug();
        xdebug.startTimer();
        SnoopHttp snoop = new SnoopHttp(xdebug);
        snoop.process(request, response);
        request.setAttribute("xdebug", xdebug);
        ActionForward forward =
                subExecute (mapping, form, request, response, xdebug);
        xdebug.stopTimer();
        return forward;
    }

    public void setUserMessage (HttpServletRequest request,
            String msg) {
        request.setAttribute("userMsg", msg);
    }

    public abstract ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception;
}
