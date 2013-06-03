// $Id: BioPaxElementFilter.java,v 1.6 2006-06-09 19:22:03 cerami Exp $
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
 * Examines a BioPAX Document and removes "Black Listed" Elements. All other elements are
 * retained.  The set of "Black Listed" Elements currently contains the following:
 * <UL>
 * <LI>{@link BioPaxConstants#PATHWAY_COMPONENTS_ELEMENT PATHWAY_COMPONENTS_ELEMENT}
 * </UL>
 * <p/>
 * Comment from Ethan Cerami, June 7, 2006:  On why we remove black listed elements.
 * <p/>
 * The get_top_level_pathway_list command in the Web Services API uses XML_ABBREV
 * to retrieve all pathways in the database.  By default, these pathways contain
 * much additional information that is not needed for the command, and may confuse
 * the end user.  For example, the pathway may contains dozens of PATHWAY-COMPONENT
 * elements.  To clean things up, we remove these black listed elements. Note also that
 * those elements which are black listed will not be indexed by lucene either.
 *
 * @author Ethan Cerami
 */
public class BioPaxElementFilter {
    private static HashSet blackListedElements;

    /**
     * Removes all Black Listed Elements.  All other elements are retained.
     *
     * @param e Element Object.
     */
    public static void removeBlackListedElements(Element e) {
        if (blackListedElements == null) {
            initBlackListedElements();
        }
        List children = e.getChildren();
        List scheduledForRemoval = new ArrayList();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            String name = child.getName();
            //  If this is a black listed element, schedule it for removal
            if (blackListedElements.contains(name)) {
                scheduledForRemoval.add(child);
            }
        }
        for (int i = 0; i < scheduledForRemoval.size(); i++) {
            Element child = (Element) scheduledForRemoval.get(i);
            e.removeContent(child);
        }
    }

    private static void initBlackListedElements() {
        blackListedElements = new HashSet();
        blackListedElements.add(BioPaxConstants.PATHWAY_COMPONENTS_ELEMENT);
    }
}
