package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mskcc.pathdb.form.FileUploadForm;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.task.ImportRecordTask;
import org.mskcc.pathdb.task.ImportReferencesTask;
import org.mskcc.pathdb.util.XmlValidator;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BaseAction;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Action for importing data files into cPath.
 *
 * @author Ethan Cerami.
 */
public class AdminImportData extends AdminBaseAction {

    /**
     * Executes Action.
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
        xdebug.logMsg(this, "Importing Data to cPath");
        FileUploadForm importForm = (FileUploadForm) form;
        FormFile file = importForm.getFile();
        if (file != null) {
            importFile(xdebug, file, request);
        } else {
            this.setUserMessage(request, "You must select a file for import.");
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private void importFile(XDebug xdebug, FormFile file,
            HttpServletRequest request) throws IOException, DaoException,
            SAXException {
        xdebug.logMsg(this, "Found File Name:  " + file.getFileName());
        xdebug.logMsg(this, "Found File Size:  " + file.getFileSize());

        String data = new String(file.getFileData());
        int index = data.indexOf("<?xml");
        if (index > -1 && index < 100) {
            //  Assume this is PSI
            importPsi(data, xdebug, request);
        } else {
            //  Assume this is a list of external refs.
            importRefs(data, xdebug, request);
        }
    }

    private void importPsi(String data, XDebug xdebug,
            HttpServletRequest request) throws SAXException, IOException,
            DaoException {
        xdebug.logMsg(this, "Importing PSI-MI Data");
        xdebug.logMsg(this, "Validating XML File");
        ArrayList errors = null;
        XmlValidator validator = new XmlValidator();
        errors = validator.validate(data);
        if (errors != null && errors.size() > 0) {
            SAXParseException exc = (SAXParseException) errors.get(0);
            throw exc;
        } else {
            DaoImport dbImport = new DaoImport();
            xdebug.logMsg(this, "Importing File to CPATH IMPORT table");
            long importId = dbImport.addRecord("Web Upload", data);
            xdebug.logMsg(this, "Import ID:  " + importId);
            ImportRecordTask task = new ImportRecordTask(importId, false);
            task.start();
            this.setUserMessage(request, "Import PSI-MI Task is now running.");
        }
    }

    private void importRefs(String data, XDebug xdebug,
            HttpServletRequest request) {
        xdebug.logMsg(this, "Importing External References");
        StringReader reader = new StringReader(data);
        ImportReferencesTask task = new ImportReferencesTask(false, reader);
        task.start();
        this.setUserMessage(request, "Import References Task is now running.");
    }
}