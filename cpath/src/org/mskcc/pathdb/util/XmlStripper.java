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
package org.mskcc.pathdb.util;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Utility Class for stripping out all XML Markup from a document,
 * and returning all element and attributes values only.
 *
 * @author Ethan Cerami
 */
public class XmlStripper {

    /**
     * Strips all XML Markup from Document.
     *
     * @param xml      XML Document.
     * @param skipRoot Skips Processing of Root Node.
     * @return String of Tokens.
     * @throws IOException Error in JDOM.
     */
    public static String stripTags(String xml, boolean skipRoot)
            throws IOException {
        StringBuffer textBuffer = new StringBuffer();
        try {
            StringReader reader = new StringReader(xml);
            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom.Document jdomDoc = saxBuilder.build(reader);
            Element element = jdomDoc.getRootElement();
            if (skipRoot) {
                List children = element.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element child = (Element) children.get(i);
                    processElement(child, textBuffer);
                }
            } else {
                processElement(element, textBuffer);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return textBuffer.toString();
    }

    /**
     * Recursive Method for Processing Elements.
     *
     * @param element    Element.
     * @param textBuffer StringBuffer.
     */
    private static void processElement(Element element,
            StringBuffer textBuffer) {
        //  Extract Text
        String text = element.getTextNormalize();
        if (text.length() > 0) {
            appendText(textBuffer, text);
        }

        //  Extract Attribute Values
        List attributes = element.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = (Attribute) attributes.get(i);
            String value = attribute.getValue();
            appendText(textBuffer, value);
        }

        //  Recursively process all children.
        List children = element.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            processElement(child, textBuffer);
        }
    }

    private static void appendText(StringBuffer textBuffer, String text) {
        if (textBuffer.length() > 0) {
            textBuffer.append(" ");
        }
        textBuffer.append(text);
    }
}