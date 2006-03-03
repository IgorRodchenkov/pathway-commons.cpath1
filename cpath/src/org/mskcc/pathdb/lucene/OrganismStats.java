// $Id: OrganismStats.java,v 1.16 2006-03-03 18:55:59 cerami Exp $
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
package org.mskcc.pathdb.lucene;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.cache.EhCache;

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

    /**
     * Gets All Organisms Sorted by Name.
     *
     * @return ArrayList of Organism Objects.
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     * @throws CacheException Cache Error.
     */
    public ArrayList getOrganismsSortedByName() throws DaoException,
            QueryException, IOException, CacheException {
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.MEMORY_CACHE);
        Element element = cache.get
                (EhCache.KEY_ORGANISM_LIST_SORTED_BY_NAME);
        if (element != null) {
            return (ArrayList) element.getValue();
        } else {
            ArrayList list = lookUpOrganisms(cache, 0);
            return list;

        }
    }

    /**
     * Gets All Organisms Sorted by Number of Interactions.
     *
     * @return ArrayList of Organism Objects.
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     * @throws CacheException Cache Error.
     */
    public ArrayList getOrganismsSortedByNumInteractions() throws DaoException,
            QueryException, IOException, CacheException {
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.MEMORY_CACHE);
        Element element = cache.get
                (EhCache.KEY_ORGANISM_LIST_SORTED_BY_NUM_ENTITIES);
        if (element != null) {
            return (ArrayList) element.getValue();
        } else {
            ArrayList list = lookUpOrganisms(cache, 1);
            return list;

        }
    }

    /**
     * Resets Organism Stats.
     *
     * @throws DaoException   Data Access Error.
     * @throws QueryException Query Error.
     * @throws IOException    Input / Output Error.
     * @throws CacheException Cache Error.
     */
    public void resetStats() throws DaoException, IOException,
            QueryException, CacheException {
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.MEMORY_CACHE);
        this.lookUpOrganisms(cache, 0);
    }

    private ArrayList lookUpOrganisms(Cache cache, int type)
            throws DaoException, QueryException {
        LuceneReader indexer = new LuceneReader();
        try {
            DaoOrganism dao = new DaoOrganism();
            ArrayList listSortedByName = dao.getAllOrganisms();
            for (int i = 0; i < listSortedByName.size(); i++) {
                Organism organism = (Organism) listSortedByName.get(i);
                String query = new String(LuceneConfig.FIELD_ORGANISM
                        + ":" + organism.getTaxonomyId());
                Hits hits = indexer.executeQuery(query);
                organism.setNumInteractions(hits.length());
            }

            //  Clone and Sort by Number of Interactions
            ArrayList listSortedByNumEntities = (ArrayList)
                    listSortedByName.clone();
            Collections.sort(listSortedByNumEntities,
                    new SortByInteractionCount());

            Element e0 = new Element
                    (EhCache.KEY_ORGANISM_LIST_SORTED_BY_NAME,
                            listSortedByName);
            Element e1 = new Element
                    (EhCache.KEY_ORGANISM_LIST_SORTED_BY_NUM_ENTITIES,
                            listSortedByNumEntities);
            cache.put(e0);
            cache.put(e1);
            if (type == 0) {
                return listSortedByName;
            } else {
                return listSortedByNumEntities;
            }
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
