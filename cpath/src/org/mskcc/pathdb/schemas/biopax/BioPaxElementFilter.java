// $Id: BioPaxElementFilter.java,v 1.4 2006-02-22 22:47:50 grossb Exp $
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
package org.mskcc.pathdb.schemas.biopax;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Examines a BioPAX Document and retains the following core elements:
 * NAME, SHORT-NAME, ORGANISM, COMMENT and XREFs.  All other elements are
 * stripped out.
 *
 * @author Ethan Cerami
 */
public class BioPaxElementFilter {
    private static HashSet coreElements;

    /**
     * Retains the following core elements:  NAME, SHORT-NAME, ORGANISM,
     * COMMENT and XREFs.  All other elements are stripped out.
     *
     * @param e Element Object.
     */
    public static void retainCoreElementsOnly(Element e) {
        if (coreElements == null) {
            initCoreElements();
        }
        List children = e.getChildren();
        List scheduledForRemoval = new ArrayList();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            String name = child.getName();
            //  If this is not a core element, schedule it for removal
            if (!coreElements.contains(name)) {
                scheduledForRemoval.add(child);
            }
        }
        for (int i = 0; i < scheduledForRemoval.size(); i++) {
            Element child = (Element) scheduledForRemoval.get(i);
            e.removeContent(child);
        }
    }

    private static void initCoreElements() {
        coreElements = new HashSet();
        coreElements.add(BioPaxConstants.NAME_ELEMENT);
        coreElements.add(BioPaxConstants.SHORT_NAME_ELEMENT);
        coreElements.add(BioPaxConstants.ORGANISM_ELEMENT);
        coreElements.add(BioPaxConstants.XREF_ELEMENT);
        coreElements.add(BioPaxConstants.COMMENT_ELEMENT);
    }
}
