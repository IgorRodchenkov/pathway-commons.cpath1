package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.model.ImportRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class DownloadSource extends BaseAction {

    /**
     * Downloads Source File.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        String sourceIdStr = request.getParameter("source_id");
        ServletOutputStream realOut = response.getOutputStream();
        long sourceId = -1;
        try {
            sourceId = Long.parseLong(sourceIdStr);
        } catch (NumberFormatException e) {
        }
        if (sourceId >=0) {
            DaoImport dao = new DaoImport();
            ImportRecord importRecord = dao.getRecordById(sourceId);
            response.setContentType("application/x-download");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            response.setHeader("Content-Disposition","inline; filename="
                    + importRecord.getDescription() + ".gz");
            GZIPOutputStream zipOut = new GZIPOutputStream (byteStream);
            OutputStreamWriter tempOut = new OutputStreamWriter(zipOut);
            tempOut.write(importRecord.getData());
            tempOut.close();
            byteStream.writeTo(realOut);
        }
        return null;
    }
}
