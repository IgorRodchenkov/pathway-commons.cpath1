package org.mskcc.pathdb.form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * Struts ActionForm for uploading files.
 *
 * @author Ethan Cerami
 */
public class FileUploadForm extends ActionForm {
    private FormFile file;

    /**
     * Gets the Form File.
     *
     * @return Form File.
     */
    public FormFile getFile() {
        return file;
    }

    /**
     * Sets the Form File.
     *
     * @param file Form File.
     */
    public void setFile(FormFile file) {
        this.file = file;
    }
}