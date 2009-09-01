package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.model.CPathRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Collection;

/**
 * Export File Utility Class.
 */
public class ExportFileUtil {
    public static final int GSEA_GENE_SYMBOL_OUTPUT = 1;
    public static final int GSEA_ENTREZ_GENE_ID_OUTPUT = 2;
    public static final int PC_OUTPUT = 3;
    public static final int SIF_OUTPUT = 4;
    public static final int TAB_DELIM_EDGE_OUTPUT = 5;
    public static final int TAB_DELIM_NODE_OUTPUT = 6;
    public static final int BIOPAX_OUTPUT = 7;
    public static final int GSEA_OUTPUT = 8;

    private File exportDir;
	private static final String EDGE_ATTRIBUTE_FILE_SUFFIX = "-edge-attributes";
	private static final String NODE_ATTRIBUTE_FILE_SUFFIX = "-node-attributes";

	private static final String GSEA_GENE_SYMBOL_FILE_SUFFIX = "-gene-symbol";
	private static final String GSEA_ENTREZ_GENE_ID_SUFFIX = "-entrez-gene-id";

    private final static String TAB = "\t";
    private static final String TAB_DELIM_EDGE_OUTPUT_HEADER = ("CPATH_RECORD_ID_A" + TAB +
                                                                "INTERACTION_TYPE" + TAB +
                                                                "CPATH_RECORD_ID_B" + TAB +
                                                                "GENE_SYMBOL_A" + TAB +
																"GENE_SYMBOL_B" + TAB +
																"INTERACTION_DATA_SOURCE" + TAB +
																"INTERACTION_PUBMED_ID");
    private static final String TAB_DELIM_NODE_OUTPUT_HEADER = ("CPATH_RECORD_ID" + TAB +
																"GENE_SYMBOL" + TAB +
																"UNIPROT_ACCESSION" + TAB +
																"ENTREZ_GENE_ID" + TAB +
																"CHEBI_ID" + TAB +
																"NODE_TYPE" + TAB +
																"NCBI_TAX_ID");

    //  HashMap that will contain multiple open file writers
    private HashMap<String, FileWriter> fileWriters = new HashMap <String, FileWriter>();

    /**
     * Constructor.
     * @param exportDir Directory where all export files will go.
     */
    public ExportFileUtil (File exportDir) {
        this.exportDir = exportDir;
        
		if (!exportDir.exists()) {
			exportDir.mkdir();
        }

        initDir (GSEA_GENE_SYMBOL_OUTPUT);  // gene symbol and entrez gene id go into same directory
        initDir (PC_OUTPUT);
        initDir (SIF_OUTPUT);
        initDir (TAB_DELIM_EDGE_OUTPUT); // edge and node go into same subdir
        initDir (BIOPAX_OUTPUT);
    }

    /**
	 * Given output format id, return equivalent string.
	 *
	 * @param outputFormat int
	 * @return String
	 */
   	public String getOutputFormatString(int outputFormat) {
		if (outputFormat == GSEA_GENE_SYMBOL_OUTPUT) {
			return "GSEA_GENE_SYMBOL_OUTPUT";
		}
		else if (outputFormat == GSEA_ENTREZ_GENE_ID_OUTPUT) {
			return "GSEA_ENTREZ_GENE_ID_OUTPUT";
		}
		else if (outputFormat == PC_OUTPUT) {
			return "PC_OUTPUT";
		}
		else if (outputFormat == SIF_OUTPUT) {
			return "SIF_OUTPUT";
		}
		else if (outputFormat == TAB_DELIM_EDGE_OUTPUT) {
			return "TAB_DELIM_EDGE_OUTPUT";
		}
		else if (outputFormat == TAB_DELIM_NODE_OUTPUT) {
			return "TAB_DELIM_NODE_OUTPUT";
		}
		else if (outputFormat == BIOPAX_OUTPUT) {
			return "BIOPAX_OUTPUT";
		}
		else {
			return "UNKNOWN_OUTPUT";
		}
	}

    /**
     * Cloes all open file descriptors.
     * @throws java.io.IOException IO Error.
     */
    public void closeAllOpenFileDescriptors() throws IOException {
        Collection<FileWriter> fds = fileWriters.values();
        for (FileWriter fileWriter:  fds) {
            fileWriter.close();
			fileWriters.remove(fileWriter);
        }
		fileWriters = new HashMap<String, FileWriter>();
    }
 
    /**
     * Initializes output directories.  This method creates a structure like so:
     * - xxxx
     * ---- by_species
     * ---- by_source
     */
    public File initDir (int outputFormat) {
        File targetDir = getFormatSpecificDir(outputFormat);
        File newDir = new File (exportDir, targetDir.getName());

        //  create the xxxx base directory
        if (!newDir.exists()) {
			newDir.mkdir();
        }

			
        // create the xxxx/by_species directory
        File bySpeciesDir = getBySpeciesDir (outputFormat);
        if (!bySpeciesDir.exists()) {
			bySpeciesDir.mkdir();
        }


        //  create the xxxx/by_source directory
        File byDataSourceDir = getBySourceDir (outputFormat);
        if (!byDataSourceDir.exists()) {
			byDataSourceDir.mkdir();
        }


		// outta here
        return newDir;
    }
    /**
     * Gets the file extension for the specified outputFormat.
     * @param outputFormat  Output Format Index.
     * @return file extension, e.g. ".txt";
     */
    private String getFileExtension (int outputFormat) {
        if (outputFormat == ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT) {
            return ".gmt";
        } else if (outputFormat == ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT) {
            return ".gmt";
        } else if (outputFormat == ExportFileUtil.PC_OUTPUT) {
            return ".txt";
        } else if (outputFormat == ExportFileUtil.SIF_OUTPUT) {
            return ".sif";
        } else if (outputFormat == ExportFileUtil.BIOPAX_OUTPUT) {
            return ".owl";
        } else {
            return ".txt";
        }
    }

    /**
     * Appends to a Data Source File.
     */
    public void appendToDataSourceFile (String line, String dbTerm, int outputFormat)
        throws IOException {
        String fdKey = outputFormat + dbTerm;
		fdKey += getKey(outputFormat);
        String fileExtension = getFileExtension (outputFormat);
        FileWriter writer = fileWriters.get(fdKey);
        File dir = getBySourceDir (outputFormat);
        if (writer == null) {
			String fileName = dbTerm.toLowerCase() + getKey(outputFormat) + fileExtension;
			fileName = fileName.replaceAll("_", "-");
			File dataSourceFile = new File(dir, fileName);
			boolean fileExists = dataSourceFile.exists();
			boolean writeHeader = (!fileExists || (fileExists && dataSourceFile.length() == 0));
            writer = createFileWriter(dataSourceFile);
			if (writeHeader) {
				writeHeader(writer, outputFormat);
			}
            fileWriters.put(fdKey, writer);
        }
        writer.write(line);
		writer.flush();
    }

    /**
     * Appends to a Speces File.
     */
    public void appendToSpeciesFile(String line, int ncbiTaxonomyId, int outputFormat)
            throws IOException, DaoException {
        if (ncbiTaxonomyId == CPathRecord.TAXONOMY_NOT_SPECIFIED) {
            return;
        }
        String fdKey = outputFormat + Integer.toString(ncbiTaxonomyId);
		fdKey += getKey(outputFormat);
        String fileExtension = getFileExtension (outputFormat);
        FileWriter writer = fileWriters.get(fdKey);
        File dir = getBySpeciesDir (outputFormat);
        if (writer == null) {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism organism = daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId);
			// remove all illegal chars from filename
			String regex = "[\\[|\\]|\\(|\\)|\\/|\\\\|\\.|;|\\:|\\<|\\>|\\,| ]";
			String speciesName = organism.getSpeciesName().replaceAll(regex, "-");
			// now remove all strings of two or more - and replace with one -
			speciesName = speciesName.replaceAll("-{2,}", "-");
			// remove trailing -
			if (speciesName.endsWith("-")) {
				speciesName = speciesName.substring(0, speciesName.length()-1);
			}
			String fileName = speciesName.toLowerCase() + getKey(outputFormat) + fileExtension;
			File speciesFile = new File(dir, fileName);
			boolean fileExists = speciesFile.exists();
			boolean writeHeader = (!fileExists || (fileExists && speciesFile.length() == 0));
            writer = createFileWriter(speciesFile);
			if (writeHeader) {
				writeHeader(writer, outputFormat);
			}
            fileWriters.put(fdKey, writer);
        }
        writer.write(line);
		writer.flush();
    }

    /**
     * Gets the format specific base directory.
     * @param outputFormat  Output Format.
     * @return Directory.
     */
    private File getFormatSpecificDir(int outputFormat) {
        if (outputFormat == ExportFileUtil.GSEA_GENE_SYMBOL_OUTPUT) {
            return new File (exportDir, "gsea");
        } else if (outputFormat == ExportFileUtil.GSEA_ENTREZ_GENE_ID_OUTPUT) {
            return new File (exportDir, "gsea");
        } else if (outputFormat == ExportFileUtil.PC_OUTPUT) {
            return new File (exportDir, "gene_sets");
        } else if (outputFormat == ExportFileUtil.SIF_OUTPUT) {
            return new File (exportDir, "sif");
        } else if (outputFormat == ExportFileUtil.TAB_DELIM_EDGE_OUTPUT) {
            return new File (exportDir, "tab_delim_network");
        } else if (outputFormat == ExportFileUtil.TAB_DELIM_NODE_OUTPUT) {
            return new File (exportDir, "tab_delim_network");
        } else if (outputFormat == ExportFileUtil.BIOPAX_OUTPUT) {
            return new File (exportDir, "biopax");
        } else {
            return null;
        }
    }


    /**
     * Gets the by_species directory.
     * @param outputFormat output format.
     * @return the by_species directory.
     */
    private File getBySpeciesDir (int outputFormat) {
        return new File (getFormatSpecificDir(outputFormat), "by_species");
    }

    /**
     * Gets the by_source directory.
     * @param outputFormat output format.
     * @return the by_source directory
     */
    private File getBySourceDir (int outputFormat) {
        return new File (getFormatSpecificDir(outputFormat), "by_source");
    }

	/**
	 * This routine was created to support both edge and node
	 * attribute files and gsea gene symbol and gsea entrez gene id
	 *
	 * @param outputFormat int
	 * @return String
	 */
	private String getKey (int outputFormat) {
		
		if (outputFormat == GSEA_GENE_SYMBOL_OUTPUT) {
			return GSEA_GENE_SYMBOL_FILE_SUFFIX;
		}
		else if (outputFormat == GSEA_ENTREZ_GENE_ID_OUTPUT) {
			return GSEA_ENTREZ_GENE_ID_SUFFIX;
		}
		else if (outputFormat == TAB_DELIM_EDGE_OUTPUT) {
			return EDGE_ATTRIBUTE_FILE_SUFFIX;
		}
		else if (outputFormat == TAB_DELIM_NODE_OUTPUT) {
			return NODE_ATTRIBUTE_FILE_SUFFIX;
		}

		// outta here
		return "";
	}

	/**
	 * Creates a filewriter.
	 *
	 * @param outputFile File
	 * @return FileWriter
	 * @throw IOException
	 */
	private FileWriter createFileWriter(File outputFile) throws IOException {
		
		try {
			return new FileWriter(outputFile, true);
		}
		catch (IOException e) {
			if (e.getMessage().contains("Too many open files")) {
				// tried closing "some" handles and doesn't always work - close all
				closeAllOpenFileDescriptors();
				return new FileWriter(outputFile, true);
			}
			else {
				throw(e);
			}
		}
	}

	/**
	 * Writes header into the given file.
	 *
	 * @param writer FileWriter
	 * @param int outputFormat
	 * @throws IOException
	 */
	private void writeHeader(FileWriter writer, int outputFormat) throws IOException {

		if (outputFormat == TAB_DELIM_EDGE_OUTPUT) {
			writer.write(TAB_DELIM_EDGE_OUTPUT_HEADER + "\n");
			writer.flush();
		}
		else if (outputFormat == TAB_DELIM_NODE_OUTPUT) {
			writer.write(TAB_DELIM_NODE_OUTPUT_HEADER + "\n");
			writer.flush();
		}
	}
}
