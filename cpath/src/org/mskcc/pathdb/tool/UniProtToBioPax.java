package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.util.file.UniProtFileUtil;
import org.mskcc.pathdb.util.file.FileUtil;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level2.*;
import org.biopax.paxtools.model.level3.*;
import org.biopax.paxtools.impl.level2.Level2FactoryImpl;
import org.biopax.paxtools.io.simpleIO.SimpleExporter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for parsing UniProt Flat Text Files, and converting into appropriate
 * BioPAX Physical Entities.
  *
 * @author Ethan Cerami.
 */
public class UniProtToBioPax {

    private static final int RECORDS_PER_BATCH = 1000;

    private Model bpModel;
    private int batchNumber = 0;
	private BioPAXLevel bpLevel;
    private ProgressMonitor pMonitor;
    private int totalNumProteinsProcessed = 0;
    private int numProteinsInCurrentBatch = 0;

    /**
     * Empty Arg Constructor.
     */
    public UniProtToBioPax() {
    }

    /**
     * Constructor.
     *
     * @param pMonitor  Progress Monitor.
	 * @param bpLevel BioPAXLevel.
     */
    public UniProtToBioPax(ProgressMonitor pMonitor, BioPAXLevel bpLevel) {
        this.pMonitor = pMonitor;
		this.bpLevel = bpLevel;
    }

    /**
     * Parses a UniProt File and converts to BioPAX.
     *
     * @param uniProtFile       UniProt Flat File.
     * @return                  Number of physical entity records created.
     * @throws java.io.IOException      Error Reading File.
     * @throws IllegalAccessException   BioPAX Output Error.
     * @throws InvocationTargetException BioPAX Output Error.
     */
    public int convertToBioPax(File uniProtFile) throws IOException,
            IllegalAccessException, InvocationTargetException {
        FileReader reader= null;

		// create a model
		createBPModel();

        try {
            reader = new FileReader (uniProtFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            HashMap dataElements = new HashMap();
            numProteinsInCurrentBatch = 0;
            while (line != null) {
                if (pMonitor != null) {
                    pMonitor.incrementCurValue();
                    ConsoleUtil.showProgress(pMonitor);
                }
                if (line.startsWith ("//")) {
                    StringBuffer deField = (StringBuffer) dataElements.get("DE");

                    StringBuffer id = (StringBuffer) dataElements.get("ID");
                    StringBuffer organismName = (StringBuffer) dataElements.get("OS");
                    StringBuffer organismTaxId = (StringBuffer) dataElements.get("OX");
                    StringBuffer comments = (StringBuffer) dataElements.get("CC");
                    StringBuffer geneName = (StringBuffer) dataElements.get("GN");
                    StringBuffer acNames = (StringBuffer) dataElements.get("AC");
                    StringBuffer xrefs = (StringBuffer) dataElements.get("DR");

                    String idParts[] = id.toString().split("\\s");
                    String shortName = idParts[0];
                    BioPAXElement currentProteinOrER = getPhysicalEntity(shortName);

                    setNameAndSynonyms(currentProteinOrER, deField.toString());
                    setOrganism(organismName.toString(), organismTaxId.toString(), currentProteinOrER);
                    String geneSyns = null;
                    if (geneName != null) {
                        geneSyns= setGeneSymbolAndSynonyms(geneName.toString(), currentProteinOrER);
                    }
                    if (comments != null) {
                        setComments (comments.toString(), geneSyns, currentProteinOrER);
                    }
                    setUniProtAccessionNumbers(acNames.toString(), currentProteinOrER);
                    if (xrefs != null) {
                        setXRefs (xrefs.toString(), currentProteinOrER);
                    }
                    dataElements = new HashMap();
                    numProteinsInCurrentBatch++;
                    
                    if (numProteinsInCurrentBatch >= RECORDS_PER_BATCH) {
                        streamToFile(uniProtFile);
						// create a new model for the next file to dump
						createBPModel();
                    }
                } else {
                    String key = line.substring (0, 2);
                    String data = line.substring(5);
                    if (data.startsWith("-------") ||
                            data.startsWith("Copyrighted") ||
                            data.startsWith("Distributed")) {
                        //  do nothing here...
                    } else {
                        if (dataElements.containsKey(key)) {
                            StringBuffer existingData = (StringBuffer) dataElements.get(key);
                            existingData.append (" " + data);
                        } else {
                            dataElements.put(key, new StringBuffer (data));
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
            if (numProteinsInCurrentBatch > 0) {
                streamToFile(uniProtFile);
				// no need to create new model - we are done processing uniprot file
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return totalNumProteinsProcessed;
    }

    private void setNameAndSynonyms (BioPAXElement currentProteinOrER, String deField) {
        //  With the latest UNIPROT Export, the DE Line contains multiple fields.
        //  For example:
        //  DE   RecName: Full=14-3-3 protein beta/alpha;
        //  DE   AltName: Full=Protein kinase C inhibitor protein 1;
        //  DE            Short=KCIP-1;
        //  DE   AltName: Full=Protein 1054;
        //  We only want DE:  RecName:Full
        String name = null;
        if (deField != null && deField.length() > 0) {
            String deTemp = deField.toString();
            String fields[] = deTemp.split(";");
            for (String field: fields) {
                String parts[] = field.split("=");
                if (parts.length == 2) {
                    String fieldName = parts[0].trim();
                    String fieldValue = parts[1].trim();
                    if (fieldName.length() > 0 && fieldName.equals("RecName: Full")) {
						if (bpLevel == BioPAXLevel.L2) {
							((physicalEntity)currentProteinOrER).setNAME(fieldValue);
						}
						else if (bpLevel == BioPAXLevel.L3) {
							((EntityReference)currentProteinOrER).setStandardName(fieldValue);
						}
                    } else {
						if (bpLevel == BioPAXLevel.L2) {
							((physicalEntity)currentProteinOrER).addSYNONYMS(fieldValue);
						}
						else if (bpLevel == BioPAXLevel.L3) {
							((EntityReference)currentProteinOrER).addName(fieldValue);
						}
                    }
                }
            }
        }
    }

    /**
     * Streams BioPAX to a file via PaxTools.
     */
    private void streamToFile(File uniProtFile) throws IOException,
            IllegalAccessException, InvocationTargetException {
        File bpOutFile = UniProtFileUtil.getOrganismSpecificFileName(uniProtFile,
                "bp_" + batchNumber, "owl");
        FileOutputStream out = new FileOutputStream (bpOutFile);
        SimpleExporter exporter = new SimpleExporter(bpLevel);
        exporter.convertToOWL(bpModel, out);
        out.close();
        totalNumProteinsProcessed += numProteinsInCurrentBatch;
        numProteinsInCurrentBatch = 0;
        batchNumber++;
    }

    /**
     * Sets the Current Organism Information.
     */
    private void setOrganism(String organismName, String organismTaxId, BioPAXElement currentProteinOrER) {
        organismTaxId = organismTaxId.replaceAll(";", "");
        String parts[] = organismTaxId.split("=");
        String taxId = parts[1];
        parts = organismName.split("\\(");
        String name = parts[0].trim();
        String rdfId = "BIO_SOURCE_NCBI_" + taxId;
		BioPAXElement bpSource = getBioSource(rdfId, taxId, name);
		if (bpLevel == BioPAXLevel.L2) {
			((protein)currentProteinOrER).setORGANISM((bioSource)bpSource);
		}
		else if (bpLevel == BioPAXLevel.L3) {
			SequenceEntityReference ser = (SequenceEntityReference)currentProteinOrER;
			ser.setOrganism((BioSource)bpSource);
		}
    }

    /**
     * Sets Multiple Comments.
     */
    private void setComments (String comments, String geneSynonyms, BioPAXElement currentProteinOrER) {
        String commentParts[] = comments.split("-!- ");
        StringBuffer reducedComments = new StringBuffer();
        for (int i=0; i<commentParts.length; i++) {
            String currentComment = commentParts[i];
            //  Filter out the Interaction comments.
            //  We don't want these, as cPath itself will contain the interactions.
            if (!currentComment.startsWith("INTERACTION")) {
                currentComment = currentComment.replaceAll("     ", " ");
                reducedComments.append (currentComment);
            }
        }
        if (geneSynonyms != null && geneSynonyms.length() > 0) {
            reducedComments.append (" GENE SYNONYMS:" + geneSynonyms + ".");
        }
        if (reducedComments.length() > 0) {
            reducedComments.append (" COPYRIGHT:  Protein annotation is derived from the "
                    + "UniProt Consortium (http://www.uniprot.org/).  Distributed under "
                    + "the Creative Commons Attribution-NoDerivs License.");
        }
        HashSet <String> commentSet = new HashSet();
        commentSet.add(reducedComments.toString());
		if (bpLevel == BioPAXLevel.L2) {
			((Level2Element)currentProteinOrER).setCOMMENT(commentSet);
		}
		else if (bpLevel == BioPAXLevel.L3) {
			((Level3Element)currentProteinOrER).setComment(commentSet);
		}
    }

    /**
     * Sets UniProt Accession Numbers (can be 0, 1 or N).
     * However, we only take the 0th element, which is referred in UniProt as the
     * "Primary Accession Number".
     */
    private void setUniProtAccessionNumbers (String acNames, BioPAXElement currentProteinOrER) {
        String acList[] = acNames.split(";");
        if (acList.length > 0) {
			for (String acEntry : acList) {
				String ac = acEntry.trim();
				setUnificationXRef(ExternalDatabaseConstants.UNIPROT, ac, currentProteinOrER);
			}
        }
    }

    /**
     * Sets Multiple Types of XRefs, e.g. Entrez Gene ID and RefSeq.
     */
    private void setXRefs (String acNames, BioPAXElement currentProteinOrER) {
        String xrefList[] = acNames.split("\\.");

        for (int i=0; i<xrefList.length; i++) {
            String xref = xrefList[i].trim();
            if (xref.startsWith("GeneID")) {
                xref = xref.replaceAll("; -.", "");
                String parts[] = xref.split(";");
                String entrezGeneId = parts[1];
                setRelationshipXRef(ExternalDatabaseConstants.ENTREZ_GENE, entrezGeneId, currentProteinOrER);
            } else if (xref.startsWith("RefSeq")) {
                xref = xref.replaceAll("; -.", "");
                String parts[] = xref.split(";");
                String refSeqId = parts[1];
                if (refSeqId.contains(".")) {
                    parts = refSeqId.split("\\.");
                    refSeqId = parts[0];
                }
                setRelationshipXRef(ExternalDatabaseConstants.REF_SEQ, refSeqId, currentProteinOrER);
            }
        }
    }

    /**
     * Sets the HUGO Gene Symbol and Synonyms.
     */
    private String setGeneSymbolAndSynonyms(String geneName, BioPAXElement currentProteinOrER) {
        StringBuffer synBuffer = new StringBuffer();
        String parts[] = geneName.split(";");
        for (int i=0; i<parts.length; i++) {
            String subParts[] = parts[i].split("=");
            // Set HUGO Gene Name
            if (subParts[0].trim().equals("Name")) {
                geneName = subParts[1];
                setRelationshipXRef(ExternalDatabaseConstants.GENE_SYMBOL, geneName, currentProteinOrER);
            } else if (subParts[0].trim().equals("Synonyms")) {
                String synList[] = subParts[1].split(",");
                for (int j=0; j<synList.length; j++) {
                    String currentSynonym = synList[j].trim();
                    synBuffer.append(" " + currentSynonym);
                }
            }
        }
        return synBuffer.toString();
    }

    /**
     * Sets Relationship XRefs.
     */
    private void setRelationshipXRef(String dbName, String id, BioPAXElement currentProteinOrER) {
        id = id.trim();
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
        String rdfId = dbName + "_" +  id;
        if (bpMap.containsKey(rdfId)) {
			if (bpLevel == BioPAXLevel.L2) {
				relationshipXref rXRef = (relationshipXref) bpMap.get(rdfId);
				((physicalEntity)currentProteinOrER).addXREF(rXRef);
			}
			else if (bpLevel == BioPAXLevel.L3) {
				RelationshipXref rXRef = (RelationshipXref) bpMap.get(rdfId);
				((EntityReference)currentProteinOrER).addXref(rXRef);
			}
        } else {
			if (bpLevel == BioPAXLevel.L2) {
				relationshipXref rXRef = (relationshipXref)bpModel.addNew(relationshipXref.class, rdfId);
				rXRef.setDB(dbName);
				rXRef.setID(id);
				((physicalEntity)currentProteinOrER).addXREF(rXRef);
			}
			else if (bpLevel == BioPAXLevel.L3) {
				RelationshipXref rXRef = (RelationshipXref)bpModel.addNew(RelationshipXref.class, rdfId);
				rXRef.setDb(dbName);
				rXRef.setId(id);
				((EntityReference)currentProteinOrER).addXref(rXRef);
			}
        }
    }

    /**
     * Sets Unification XRefs.
     */
    private void setUnificationXRef(String dbName, String id, BioPAXElement currentProteinOrER) {
        id = id.trim();
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
        String rdfId = dbName + "_" +  id;
        if (bpMap.containsKey(rdfId)) {
			if (bpLevel == BioPAXLevel.L2) {
				unificationXref rXRef = (unificationXref) bpMap.get(rdfId);
				((physicalEntity)currentProteinOrER).addXREF(rXRef);
			}
			else if (bpLevel == BioPAXLevel.L3) {
				UnificationXref rXRef = (UnificationXref) bpMap.get(rdfId);
				((EntityReference)currentProteinOrER).addXref(rXRef);
			}
        } else {
			if (bpLevel == BioPAXLevel.L2) {
				unificationXref rXRef = (unificationXref)bpModel.addNew(unificationXref.class, rdfId);
				rXRef.setDB(dbName);
				rXRef.setID(id);
				((physicalEntity)currentProteinOrER).addXREF(rXRef);
			}
			else if (bpLevel == BioPAXLevel.L3) {
				UnificationXref rXRef = (UnificationXref)bpModel.addNew(UnificationXref.class, rdfId);
				rXRef.setDb(dbName);
				rXRef.setId(id);
				((EntityReference)currentProteinOrER).addXref(rXRef);
			}
        }
    }

	/**
	 * Gets a physical entity (or Entity Reference in L3)
	 *
	 * @param shortName String
	 * @return <T extends BioPAXElement>
	 */
	private <T extends BioPAXElement> T getPhysicalEntity(String shortName) {

		if (bpLevel == BioPAXLevel.L2) {
			physicalEntity toReturn = (physicalEntity)bpModel.addNew(protein.class, shortName);
			toReturn.setSHORT_NAME(shortName);
			return (T)toReturn;
		}
		else if (bpLevel == BioPAXLevel.L3) {
			SequenceEntityReference toReturn = (SequenceEntityReference)bpModel.addNew(ProteinReference.class, shortName + "_ER");
			toReturn.setDisplayName(shortName);
			return (T)toReturn;
		}

		// should not get here
		return null;
	}

	/**
	 * Gets a biosource
	 *
	 * @return <T extends BioPAXElement>
	 */
	private <T extends BioPAXElement> T getBioSource(String rdfId, String taxId, String name) {

		// check if biosource already exists
        Map<String, BioPAXElement> bpMap = bpModel.getIdMap();
		if (bpMap.containsKey(rdfId)) {
			return (T)bpMap.get(rdfId);
		}

		if (bpLevel == BioPAXLevel.L2) {
			bioSource toReturn = (bioSource)bpModel.addNew(bioSource.class, rdfId);
			toReturn.setNAME(name);
			unificationXref taxonXref = (unificationXref)bpModel.addNew(unificationXref.class, "TAXON_NCBI_" + taxId);
            taxonXref.setDB("NCBI_taxonomy");
            taxonXref.setID(taxId);
			toReturn.setTAXON_XREF(taxonXref);
			return (T)toReturn;
		}
		else if (bpLevel == BioPAXLevel.L3) {
			BioSource toReturn = (BioSource)bpModel.addNew(BioSource.class, rdfId);
			toReturn.setStandardName(name);
			UnificationXref taxonXref = (UnificationXref)bpModel.addNew(UnificationXref.class, "TAXON_NCBI_" + taxId);
			taxonXref.setDb("NCBI_taxonomy");
            taxonXref.setId(taxId);
			toReturn.setTaxonXref((UnificationXref)taxonXref);
			return (T)toReturn;
		}

		// should not get here
		return null;
	}

	private void createBPModel() {

		if (bpLevel == BioPAXLevel.L2) {
			bpModel = BioPAXLevel.L2.getDefaultFactory().createModel();
		}
		else if (bpLevel == BioPAXLevel.L3) {
			bpModel = BioPAXLevel.L3.getDefaultFactory().createModel();
		}
	}

    /**
     * Command Line Usage.
     * @param args          Must include UniProt File Name.
     * @throws java.io.IOException  IO Error.
     */
    public static void main(String[] args) throws IOException, IllegalAccessException,
            InvocationTargetException {
        if (args.length == 0) {
            System.out.println ("command line usage:  uniprot2biopax.pl<biopax level (2 or 3)> <uniprot_file.dat>");
            System.exit(1);
        }
        ProgressMonitor pMonitor = new ProgressMonitor();
        pMonitor.setConsoleMode(true);

		BioPAXLevel bpLevel = UniProtToBioPax.getBioPAXLevel(args[0]);
        File uniProtFile = new File (args[1]);
        System.out.println ("Reading data from:  " + uniProtFile.getAbsolutePath());
        int numLines = FileUtil.getNumLines(uniProtFile);
        System.out.println (" --> total number of lines:  " + numLines);
        pMonitor.setMaxValue(numLines);
        UniProtToBioPax parser = new UniProtToBioPax(pMonitor, bpLevel);
        int numRecords = parser.convertToBioPax(uniProtFile);
        System.out.println ("Total number of protein records processed:  " + numRecords);
        if (uniProtFile.getParentFile() != null) {
            System.out.println ("All files written to:  "
                    + uniProtFile.getParentFile().getAbsolutePath());
        }
    }

	/**
	 * Converts an string argument to main representing an bp level to a BioPAXLevel type.
	 *
	 * @param bpLevel
	 * @return BioPAXLevel
	 */
	public static BioPAXLevel getBioPAXLevel(String bpLevel) {

		Integer bpLevelArg = null;
		try {
			bpLevelArg = Integer.valueOf(bpLevel);
			if (bpLevelArg != 2 && bpLevelArg != 3) {
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e) {
			System.err.println("Incorrect BioPAX level specified: " + bpLevel + " .  Please select level 2 or 3.");
			System.exit(0);
		}

		// outta here
		return (bpLevelArg == 2) ? BioPAXLevel.L2 : BioPAXLevel.L3;
	}
}
