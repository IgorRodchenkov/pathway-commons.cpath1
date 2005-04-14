/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.util.xml;

import org.jdom.Text;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Various XML Utility Methods.
 *
 * @author Ethan Cerami
 */
public class XmlUtil {

    /**
     * Normalizes Text.
     * Replaces all whitespace characters with a single whitespace.
     *
     * @param str Text to Normalize.
     * @return Normalized Text.
     */
    public static String normalizeText(String str) {
        Text text = new Text(str);
        return text.getTextNormalize();
    }

    /**
     * Serializes JDOM Element to XML.
     * @param e JDOM Element
     * @return XML String
     */
    public static String serializeToXml(Element e) throws IOException {
        StringWriter writer = new StringWriter();
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        out.output(e, writer);
        return writer.toString();
    }
}