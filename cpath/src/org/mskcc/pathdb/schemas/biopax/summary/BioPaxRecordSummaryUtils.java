// $Id: BioPaxRecordSummaryUtils.java,v 1.4 2006-01-31 15:43:11 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
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

// package
package org.mskcc.pathdb.schemas.biopax.summary;

// imports
import java.util.List;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;


/**
 * This class contains some utility methods
 * used primarily by BioPaxRecord.jsp
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordSummaryUtils {

    /**
     * Gets the BioPax Header String to render.
	 *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getBioPaxRecordHeaderString(BioPaxRecordSummary biopaxRecordSummary) throws IllegalArgumentException {

		// check args
		if (biopaxRecordSummary == null){
			throw new IllegalArgumentException("BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString() argument is null");
		}

		// used to make type more readable
		BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();

		// get type
		String type = biopaxRecordSummary.getType();

		// build up name
		String name = getBioPaxRecordName(biopaxRecordSummary);
		if (name != null){
			name += (type != null) ? (" (" + entityTypeMap.get(type) +")") : "";
		}
		else{
			// cannot do anything without a name
			return null;
		}

		// get organism
		String organism = biopaxRecordSummary.getOrganism();

		// outta here
		return (organism != null) ? (name + " from " + organism) : name;
	}

    /**
     * Gets the BioPax Synonym String to render.
	 *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getBioPaxRecordSynonymString(BioPaxRecordSummary biopaxRecordSummary) throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null){
            throw new IllegalArgumentException("BioPaxRecordSummaryUtils.getBioPaxRecordSynonymString() argument is null");
        }

        // string to return
        String synonymString = "";

        // get synonym list
        List synonymList = biopaxRecordSummary.getSynonyms();

        // concate them all into one long string
        if (synonymList != null && synonymList.size() > 0) {
            for (int lc = 0; lc < synonymList.size(); lc++) {
                String synonym = (String)synonymList.get(lc);
                synonymString += (synonym + " ");
            }
        }

        // outta here
        return synonymString;
    }

    /**
     * Gets the BioPax Data Source String to render.
	 *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getBioPaxRecordDataSourceString(BioPaxRecordSummary biopaxRecordSummary) throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null){
            throw new IllegalArgumentException("BioPaxRecordSummaryUtils.getBioPaxRecordDataSourceString() argument is null");
        }

        // string to return
        String dataSource = biopaxRecordSummary.getDataSource();

        // outta here
        return (dataSource != null && dataSource.length() > 0) ? dataSource : null;
    }

    /**
     * Gets the BioPax Availability String to render.
	 *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getBioPaxRecordAvailabilityString(BioPaxRecordSummary biopaxRecordSummary) throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null){
            throw new IllegalArgumentException("BioPaxRecordSummaryUtils.getBioPaxRecordAvailabilityString() argument is null");
        }

        // string to return
        String availability = biopaxRecordSummary.getAvailability();

        // outta here
        return (availability != null && availability.length() > 0) ? availability : null;
    }

    /**
     * Gets the BioPax Comment String to render.
	 *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getBioPaxRecordCommentString(BioPaxRecordSummary biopaxRecordSummary) throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null){
            throw new IllegalArgumentException("BioPaxRecordSummaryUtils.getBioPaxRecordCommentString() argument is null");
        }

        // string to return
        String comment = biopaxRecordSummary.getComment();

        // outta here
        return (comment != null && comment.length() > 0) ? comment : null;
    }

    /**
     * Gets the BioPax Record Name.
	 *
	 * (use short name or name or shortest synonyms)
     *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     */
    private static String getBioPaxRecordName(BioPaxRecordSummary biopaxRecordSummary) {

		// name to return
		String name;

		// try short name
		name = biopaxRecordSummary.getShortName();
		if (name != null && name.length() > 0) return name;

		// try name
		name = biopaxRecordSummary.getName();
		if (name != null && name.length() > 0) return name;

		// get shortest synonym
		int shortestSynonymIndex = -1;
		List list = biopaxRecordSummary.getSynonyms();
		if (list != null && list.size() > 0){
			int minLength = -1;
			for (int lc = 0; lc < list.size(); lc++){
				String synonym = (String)list.get(lc);
				if (minLength == -1 || synonym.length() < minLength){
					minLength = synonym.length();
					shortestSynonymIndex = lc;
				}
			}
		}
		else{
			return null;
		}

		// set name to return
		if (shortestSynonymIndex > -1){
			name = (String)list.get(shortestSynonymIndex);

			// we are using synonym as name, remove synonym from list
			list.remove(shortestSynonymIndex);
			biopaxRecordSummary.setSynonyms(list);
		}

		// outta here
		return name;
	}
}
