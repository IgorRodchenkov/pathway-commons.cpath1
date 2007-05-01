// $Id: BioPaxControlTypeMap.java,v 1.8 2007-05-01 20:37:04 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
package org.mskcc.pathdb.model;

import java.util.HashMap;

/**
 * Hashmap which maps BioPax Control Type to Plain English.
 *
 * @author Benjamin Gross
 */
public class BioPaxControlTypeMap {
    private static HashMap presentTenseMap;
    private static HashMap pastTenseMap;

    /**
     * Gets a HashMap that maps BioPAX control types to plain english (present tense).
     * For exmple, "INHIBITION" maps to "inhibits" (present tense).
     *
     * @return HashMap, keyed by BioPAX control type.
     */
    public static HashMap getPresentTenseMap() {
        if (presentTenseMap == null) {
            presentTenseMap = new HashMap();
            presentTenseMap.put("INHIBITION", "inhibits");
            presentTenseMap.put("ACTIVATION", "activates");
            presentTenseMap.put("INHIBITION-ALLOSTERIC", "allosterically inhibits");
            presentTenseMap.put("INHIBITION-COMPETITIVE", "competitively inhibits");
            presentTenseMap.put("INHIBITION-IRREVERSIBLE", "irreversibly inhibits");
            presentTenseMap.put("INHIBITION-NONCOMPETITIVE", "noncompetitively inhibits");
            presentTenseMap.put("INHIBITION-OTHER", "inhibits (other)");
            presentTenseMap.put("INHIBITION-UNCOMPETITIVE", "uncompetitively inhibits");
            presentTenseMap.put("INHIBITION-UNKMECH", "inhibits");
            presentTenseMap.put("ACTIVATION-NONALLOSTERIC", "nonallosterically activates");
            presentTenseMap.put("ACTIVATION-ALLOSTERIC", "allosterically activates");
            presentTenseMap.put("ACTIVATION-UNKMECH", "activates");
        }
        return presentTenseMap;
    }

    /**
     * Gets a HashMap that maps BioPAX control types to plain english (past tense).
     * For exmple, "INHIBITION" maps to "inhibited" (past tense).
     *
     * @return HashMap, keyed by BioPAX control type.
     */
    public static HashMap getPastTenseMap() {
        if (pastTenseMap == null) {
            pastTenseMap = new HashMap();
            pastTenseMap.put("INHIBITION", "inhibited");
            pastTenseMap.put("ACTIVATION", "activated");
            pastTenseMap.put("INHIBITION-ALLOSTERIC", "allosterically inhibited");
            pastTenseMap.put("INHIBITION-COMPETITIVE", "competitively inhibited");
            pastTenseMap.put("INHIBITION-IRREVERSIBLE", "irreversibly inhibited");
            pastTenseMap.put("INHIBITION-NONCOMPETITIVE", "noncompetitively inhibited");
            pastTenseMap.put("INHIBITION-OTHER", "inhibited (other)");
            pastTenseMap.put("INHIBITION-UNCOMPETITIVE", "uncompetitively inhibited");
            pastTenseMap.put("INHIBITION-UNKMECH", "inhibited");
            pastTenseMap.put("ACTIVATION-NONALLOSTERIC", "nonallosterically activated");
            pastTenseMap.put("ACTIVATION-ALLOSTERIC", "allosterically activated");
            pastTenseMap.put("ACTIVATION-UNKMECH", "activated");
        }
        return pastTenseMap;
    }

}
