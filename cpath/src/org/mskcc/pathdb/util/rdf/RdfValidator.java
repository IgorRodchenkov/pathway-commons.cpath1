// $Id: RdfValidator.java,v 1.6 2006-02-22 22:51:58 grossb Exp $
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
package org.mskcc.pathdb.util.rdf;

import com.hp.hpl.jena.rdf.arp.ARP;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * RDF Validator Utility Class.
 * <p/>
 * Built on ARP (Another RDF Parser).  Full details available online at:
 * http://www.hpl.hp.com/personal/jjc/arp/
 * <p/>
 * ARP is used within Jena, and forms the basis of the W3C RDF Validation
 * service, available online at:  http://www.w3.org/RDF/Validator/.
 *
 * @author Ethan Cerami
 */
public class RdfValidator {
    private RdfErrorHandler errorHandler;

    /**
     * Constructor.
     *
     * @param reader Reader Object.
     * @throws IOException  InputOuput Exception.
     * @throws SAXException XML SAX Parsing Error.
     */
    public RdfValidator(Reader reader) throws IOException, SAXException {
        ARP arp = new ARP();
        arp.setStrictErrorMode();
        errorHandler = new RdfErrorHandler();
        arp.setErrorHandler(errorHandler);
        arp.load(reader);
    }

    /**
     * Returns true if the validator encountered any fatal errors, errors,
     * or warnings.
     *
     * @return true or false.
     */
    public boolean hasErrorsOrWarnings() {
        ArrayList allList = new ArrayList();
        allList.addAll(errorHandler.getFatalErrorList());
        allList.addAll(errorHandler.getErrorList());
        allList.addAll(errorHandler.getWarningList());
        return (allList.size() > 0);
    }

    /**
     * Gets the Error Handler Object.
     * Provides easy access to all fatal errors, errors, and warnings.
     *
     * @return RdfErrorHandler Object.
     */
    public RdfErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns a multi-line String of all fata errors, errors, and warnings
     * (in that order).  Can be presented to the end user.
     *
     * @return multi-line String of error messages.
     */
    public String getReadableErrorList() {
        StringBuffer msg = new StringBuffer();
        ArrayList allList = new ArrayList();
        allList.addAll(errorHandler.getFatalErrorList());
        allList.addAll(errorHandler.getErrorList());
        allList.addAll(errorHandler.getWarningList());

        for (int i = 0; i < allList.size(); i++) {
            SAXParseException exception = (SAXParseException) allList.get(i);
            msg.append(i + ".  ");
            msg.append("RDF Error:  " + exception.getMessage());
            msg.append(" [Line=  " + exception.getLineNumber());
            msg.append(", Column=  " + exception.getColumnNumber());
            msg.append("]\n");
        }
        return msg.toString().trim();
    }
}


