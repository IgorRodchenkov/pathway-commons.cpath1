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
package org.mskcc.pathdb.model;

/**
 * JavaBean to Encapsulate an Organism Record.
 *
 * @author Ethan Cerami
 */
public class Organism {
    private int taxonomyId;
    private String speciesName;
    private String commonName;
    private int numInteractions;

    /**
     * Constructor.
     *
     * @param taxonomyId  TaxonomyID.
     * @param speciesName SpeciesName.
     * @param commonName  CommonName.
     */
    public Organism(int taxonomyId, String speciesName,
            String commonName) {
        this.taxonomyId = taxonomyId;
        this.speciesName = speciesName;
        this.commonName = commonName;
        this.numInteractions = 0;
    }

    /**
     * Gets NCBI Taxonomy ID.
     *
     * @return Taxonomy Identifier.
     */
    public int getTaxonomyId() {
        return taxonomyId;
    }

    /**
     * Sets NCBI TaxonomyID.
     *
     * @param taxonomyId Taxonomy Identifier.
     */
    public void setTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    /**
     * Gets Species Name.
     *
     * @return Species Name.
     */
    public String getSpeciesName() {
        return speciesName;
    }

    /**
     * Sets Species Name.
     *
     * @param speciesName Species Name.
     */
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    /**
     * Gets Common Name of Organism.
     *
     * @return Common Name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets Common Name of Organism.
     *
     * @param commonName Common Name.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Gets Number of Interactions.
     *
     * @return Number of Interactions.
     */
    public int getNumInteractions() {
        return numInteractions;
    }

    /**
     * Sets Number of Interactions.
     *
     * @param numInteractions Number of Interactions.
     */
    public void setNumInteractions(int numInteractions) {
        this.numInteractions = numInteractions;
    }
}