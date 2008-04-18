package org.mskcc.pathdb.util.file;

import java.io.File;

/**
 * Generates organism specific file names, based on UniProt File Names.
 *
 * For example, given the file name:  uniprot_sprot_human.dat.
 * getOrganismSpecificFileName (file, "entrez_gene") will generate
 * the file name:  entrez_gene_human.txt.
 *
 * @author Ethan Cerami 
 */
public class UniProtFileUtil {

    /**
     * Generates an organism specific file name, based on a UniProt File.
     *
     * For example, given the UniProt file:  uniprot_sprot_human.dat.
     * getOrganismSpecificFileName (file, "entrez_gene") will generate
     * the file name:  entrez_gene_human.txt.
     *
     * @param file      UniProt File of the pattern "xxx_xxx_[organism].dat"
     * @param prefix    Prefix of file that you want to generate.
     * @return  new file name of the form:  [prefix]_[organism].txt
     */
    public static File getOrganismSpecificFileName (File file, String prefix) {
        String fileName = file.getName();
        String parts[] = fileName.split("\\.");
        if (parts.length == 2) {
            String firstPart = parts[0];
            parts = firstPart.split("_");
            if (parts.length == 1) {
                throw new IllegalArgumentException ("Illegal file name:  " + fileName);
            } else {
                String organism = parts[parts.length-1];
                return new File (file.getParentFile(), prefix + "_" + organism + ".txt");
            }
        } else {
            throw new IllegalArgumentException ("Illegal file name:  " + fileName);
        }
    }
}