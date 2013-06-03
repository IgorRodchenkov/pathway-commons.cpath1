// $Id: ValidateXmlTask.java,v 1.9 2008-07-01 20:11:31 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.task;

import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.util.xml.XmlValidator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Simple Command Line Tool for Validating XML.
 *
 * @author Ethan Cerami.
 */
public class ValidateXmlTask {
    private File file;

    /**
     * Constructor.
     *
     * @param file XML File.
     */
    public ValidateXmlTask(File file) {
        this.file = file;
    }

    /**
     * Validates the XML File, and outputs error messages to the Standard
     * Console.
     *
     * @param showErrors Show/Hide Error Messages on System.out.
     * @return true if valid;  false otherwise.
     * @throws IOException          Input/Output Exception.
     * @throws SAXException         XML SAX Error.
     * @throws DataServiceException Error Reading Data.
     */
    public boolean validate(boolean showErrors)
            throws IOException, SAXException, DataServiceException {
        System.out.println("Validating XML File:   " + file.getAbsolutePath());
        XmlValidator validator = new XmlValidator();
        ContentReader reader = new ContentReader();
        String xmlStr = reader.retrieveContent(file.getAbsolutePath());
        ArrayList errors = validator.validatePsiMiLevel1(new StringReader(xmlStr));
        if (errors != null && errors.size() > 0) {
            System.out.println("XML File is Invalid.");
            for (int i = 0; i < errors.size(); i++) {
                SAXParseException saxException =
                        (SAXParseException) errors.get(i);
                if (showErrors) {
                    System.out.println("Error #" + i + ":  "
                            + saxException.getMessage() + "  [Line:  "
                            + saxException.getLineNumber() + ", Column:  "
                            + saxException.getColumnNumber());
                }
            }
            return false;
        } else {
            System.out.println("XML File is Valid.");
            return true;
        }
    }
}
