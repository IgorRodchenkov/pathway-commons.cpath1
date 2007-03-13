package org.mskcc.pathdb.tool;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Attribute;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.bb.sql.dao.DaoBBGene;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.bb.sql.dao.DaoBBPathway;
import org.mskcc.pathdb.bb.sql.dao.DaoBBInternalLink;
import org.mskcc.pathdb.bb.model.BBGeneRecord;
import org.mskcc.pathdb.bb.model.BBPathwayRecord;
import org.mskcc.pathdb.bb.model.BBInternalLinkRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

/**
 * Hacky Class to Load Biocarta Files from the NCI/Nature Pathway Interaction Database (PID).
 *
 * @author Ethan Cerami
 */
public class LoadBioCartaPid {

    /**
     * Constructor.
     * @param fileName          File Name.
     * @throws IOException      File IO Exception.
     * @throws JDOMException    XML Error.
     * @throws DaoException     Database Access Error.
     */
    public LoadBioCartaPid (String fileName) throws IOException, JDOMException, DaoException {
        //  Parse XML
        Reader reader = new FileReader(fileName);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(reader);

        //  Get Root Element
        Element root = doc.getRootElement();

        //  Extract Pathway Name and ID
        String pathwayId = null;
        XPath xpath = XPath.newInstance("Model/PathwayList/Pathway");
        List pathwayList = xpath.selectNodes(root);
        if (pathwayList != null && pathwayList.size() > 0) {
            Element pathway = (Element) pathwayList.get(0);
            Attribute id = pathway.getAttribute("id");
            String pathwayName = pathway.getChildTextNormalize("LongName");
			String pathwayShortName = pathway.getChildTextNormalize("ShortName");
            pathwayId = id.getValue();
            //  Add Pathway Info to BB_Pathway table
            if (pathwayName != null) {
                DaoBBPathway daoBbPathway = new DaoBBPathway();
                BBPathwayRecord pathwayRecord = new BBPathwayRecord(pathwayId,
																	pathwayName,
																	"BioCarta",
																	"http://www.biocarta.com/pathfiles/h_" + pathwayShortName + ".asp");
                daoBbPathway.addRecord(pathwayRecord);
            }
            System.out.println("Got Pathway:  " + pathwayName + " [" + pathwayId + "]");
        }

        //  Extract all molecules
        xpath = XPath.newInstance("Model/MoleculeList/Molecule");
        List moleculeList = xpath.selectNodes(root);
        ArrayList<BBInternalLinkRecord> links = new ArrayList<BBInternalLinkRecord>();
        for (int i=0; i<moleculeList.size(); i++) {
            Element e = (Element) moleculeList.get(i);
            Attribute moleculeType = e.getAttribute("molecule_type");

            //  Filter for molecules of type protein
            if (moleculeType != null && moleculeType.getValue().equals("protein")) {
                List nameList = e.getChildren("Name");
                String entrezGene = null;
                String officialName = null;
                for (int j=0; j<nameList.size(); j++) {
                    Element name = (Element) nameList.get(j);
                    Attribute type = name.getAttribute("name_type");
                    Attribute value = name.getAttribute("value");
                    if (type != null) {
                        //  Extract LL (Entrez Gene ID)
                        if (type.getValue().equals("LL")) {
                            entrezGene = value.getValue();
                        //  Extract Official Gene Name
                        } else if (type.getValue().equals("OF")) {
                            officialName = value.getValue();
                        }
                    }
                }
                if (entrezGene != null && officialName != null) {
                    //  Check BB_Gene for existing Entrez Gene Entrez
                    DaoBBGene daoBbGene = new DaoBBGene();
                    BBGeneRecord geneRecord = daoBbGene.getBBGene(entrezGene);
                    System.out.println("    " + officialName + " [" + entrezGene + "]");
                    //  If an entry does not exist, add it to BB_Gene table
                    if (geneRecord == null) {
                        geneRecord = new BBGeneRecord(entrezGene, officialName);
                        daoBbGene.addRecord(geneRecord);
                    }
                    //  Add links from Entrez Gene to BB_Pathway table
                    BBInternalLinkRecord bbLinkRecord = new BBInternalLinkRecord();
                    bbLinkRecord.setEntrezGeneID(entrezGene);
                    bbLinkRecord.setPathwayID(pathwayId);
                    links.add(bbLinkRecord);
                }
            }
        }
        DaoBBInternalLink daoBbLink = new DaoBBInternalLink();
        daoBbLink.addRecords(links);
    }

    /**
     * Main Method.
     *
     * @param argv Command Line Arguments.
     */
    public static void main (String[] argv) throws IOException, JDOMException, DaoException {
        //  Get file name
        String fileName = argv[0];
        LoadBioCartaPid loader = new LoadBioCartaPid(fileName);
    }
}
