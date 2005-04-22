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
package org.mskcc.pathdb.lucene;

import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Encapsulates Stats on All Organisms in cPath.
 *
 * @author Ethan Cerami
 */
public class OrganismStats {
    private static ArrayList organismListSortedByName;
    private static ArrayList organismListSortedByNumInteractions;

    /**
     * Gets All Organisms Sorted by Name.
     *
     * @return ArrayList of Organism Objects.
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     */
    public ArrayList getOrganismsSortedByName() throws DaoException,
            QueryException, IOException {
        if (organismListSortedByName == null) {
            lookUpOrganisms();
        }
        return organismListSortedByName;
    }

    /**
     * Gets All Organisms Sorted by Number of Interactions.
     *
     * @return ArrayList of Organism Objects.
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     */
    public ArrayList getOrganismsSortedByNumInteractions() throws DaoException,
            QueryException, IOException {
        if (organismListSortedByNumInteractions == null) {
            lookUpOrganisms();
        }
        return organismListSortedByNumInteractions;
    }

    /**
     * Restets Organism Stats.
     *
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     */
    public void resetStats() throws DaoException, IOException,
            QueryException {
        organismListSortedByNumInteractions = null;
        organismListSortedByName = null;
        this.getOrganismsSortedByName();
        this.getOrganismsSortedByNumInteractions();
    }

    private void lookUpOrganisms() throws DaoException, QueryException {
        LuceneReader indexer = new LuceneReader();
        try {
            DaoOrganism dao = new DaoOrganism();
            organismListSortedByName = dao.getAllOrganisms();
            for (int i = 0; i < organismListSortedByName.size(); i++) {
                Organism organism = (Organism) organismListSortedByName.get(i);
                String query = new String(LuceneConfig.FIELD_ORGANISM
                        + ":" + organism.getTaxonomyId());
                Hits hits = indexer.executeQuery(query);
                organism.setNumInteractions(hits.length());
            }

            //  Clone and Sort by Number of Interactions
            organismListSortedByNumInteractions = (ArrayList)
                    organismListSortedByName.clone();
            Collections.sort(organismListSortedByNumInteractions,
                    new SortByInteractionCount());
        } finally {
            //  Make sure to always close the LuceneReader
            indexer.close();
        }
    }
}

/**
 * Organism Sorter.  Sorts By Number of Interactions.
 *
 * @author Ethan Cerami
 */
class SortByInteractionCount implements Comparator {

    /**
     * Compares Two Organisms.
     *
     * @param o1 Organism 1.
     * @param o2 Organism 2.
     * @return integer indicating results of comparison.
     */
    public int compare(Object o1, Object o2) {
        Organism organism1 = (Organism) o1;
        Organism organism2 = (Organism) o2;
        int count1 = organism1.getNumInteractions();
        int count2 = organism2.getNumInteractions();
        int compare = count1 - count2;
        if (compare == 0) {
            String name1 = organism1.getSpeciesName();
            String name2 = organism2.getSpeciesName();
            return name1.compareTo(name2);
        }
        return compare;
    }
}

/**
 * Organism Sorter.  Sorts By Organism Name.
 *
 * @author Ethan Cerami
 */
class SortByName implements Comparator {

    /**
     * Compares Two Organisms.
     *
     * @param o1 Organism 1.
     * @param o2 Organism 2.
     * @return integer indicating results of comparison.
     */
    public int compare(Object o1, Object o2) {
        Organism organism1 = (Organism) o1;
        Organism organism2 = (Organism) o2;
        String name1 = organism1.getSpeciesName();
        String name2 = organism2.getSpeciesName();
        return name1.compareTo(name2);
    }
}
