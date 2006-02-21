// $Id: AdminImportData.java,v 1.13 2006-02-21 22:51:09 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.action.admin;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.form.FileUploadForm;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.task.ImportRecordTask;
import org.mskcc.pathdb.task.ImportReferencesTask;
import org.mskcc.pathdb.util.xml.XmlValidator;
import org.mskcc.pathdb.xdebug.XDebug;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Administrative Action for importing data files into cPath.
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
    public ActionForward adminExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        xdebug.logMsg(this, "Importing Data to cPath");
        FileUploadForm importForm = (FileUploadForm) form;
        FormFile file = importForm.getFile();
        if (file != null && file.getFileSize() > 0) {
            importFile(xdebug, file, request);
        } else {
            this.setUserMessage(request, "You must select a file for import.");
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private void importFile(XDebug xdebug, FormFile file,
            HttpServletRequest request) throws IOException, DaoException,
            SAXException, MarshalException, ValidationException {
        xdebug.logMsg(this, "Found File Name:  " + file.getFileName());
        xdebug.logMsg(this, "Found File Size:  " + file.getFileSize());

        String data = new String(file.getFileData());
        String fileName = file.getFileName();
        if (fileName.trim().endsWith("xml") || fileName.endsWith("psi")) {
            //  Assume this is PSI
            importPsi(fileName, data, xdebug, request);
        } else if (fileName.trim().endsWith("owl")) {
            importDataFile(fileName, data, XmlRecordType.BIO_PAX, xdebug,
                    request);
        } else {
            //  Assume this is a list of external refs.
            importRefs(data, xdebug, request);
        }

        //  Set Auto-Update Flag
        request.setAttribute(BaseAction.PAGE_AUTO_UPDATE,
                BaseAction.YES);
    }

    private void importPsi(String fileName, String data, XDebug xdebug,
            HttpServletRequest request) throws SAXException, IOException,
            DaoException, ValidationException, MarshalException {
        xdebug.logMsg(this, "Validating XML File");
        //  Try to unmarshal document via Castor
        try {
            StringReader strReader = new StringReader(data);
            EntrySet.unmarshalEntrySet(strReader);
        } catch (MarshalException e) {
            //  If marshalling fails, validate and get more user-friendly
            //  error message with error location.
            XmlValidator validator = new XmlValidator();
            ArrayList errors = validator.validate(data);
            if (errors != null && errors.size() > 0) {
                SAXParseException exc = (SAXParseException) errors.get(0);
                throw exc;
            } else {
                throw e;
            }
        }
        importDataFile(fileName, data, XmlRecordType.PSI_MI, xdebug, request);
    }

    private void importDataFile(String fileName, String data,
            XmlRecordType xmlType, XDebug xdebug, HttpServletRequest request)
            throws DaoException {
        DaoImport dbImport = new DaoImport();
        xdebug.logMsg(this, "Importing File Type:  " + xmlType
                + " to cPath IMPORT table");
        long importId = dbImport.addRecord(fileName, xmlType, data);
        xdebug.logMsg(this, "Import ID:  " + importId);
        ImportRecordTask task = new ImportRecordTask(importId, true, false,
                false);
        task.start();
        this.setUserMessage(request, "Import Task is now running.");
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
