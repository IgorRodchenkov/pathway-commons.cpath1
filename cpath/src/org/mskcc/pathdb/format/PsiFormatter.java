package org.mskcc.pathdb.format;

import org.mskcc.pathdb.model.ExternalReference;
import org.mskcc.pathdb.model.Interaction;
import org.mskcc.pathdb.model.Protein;
import org.mskcc.pathdb.xml.psi.InteractorList;
import org.mskcc.pathdb.xml.psi.Names;
import org.mskcc.pathdb.xml.psi.Organism;
import org.mskcc.pathdb.xml.psi.PrimaryRef;
import org.mskcc.pathdb.xml.psi.ProteinInteractor;
import org.mskcc.pathdb.xml.psi.SecondaryRef;
import org.mskcc.pathdb.xml.psi.Xref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Converts Internal Object Model to PSI-MI Format.
 *
 * Official version of PSI-MI is available at:
 * http://psidev.sourceforge.net/mi/xml/src/MIF.xsd
 *
 * @author Ethan Cerami
 */
public class PsiFormatter {
    /**
     * ArrayList of Protein-Protein Interactions.
     */
    private ArrayList interactions;

    /**
     * Constructor.
     * @param interactions ArrayList of Interactions.
     */
    public PsiFormatter(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Gets PSI XML.
     * @return Root PSI Element.
     */
    public InteractorList getPsiXml() {
        InteractorList interactorList = setInteractors();
        return interactorList;
    }

    /**
     * Sets all Protein Interactors.
     * @return Castor InteractorList.
     */
    private InteractorList setInteractors() {
        HashMap proteinSet = getNonRedundantProteins();
        InteractorList interactorList = new InteractorList();

        Iterator iterator = proteinSet.values().iterator();
        while (iterator.hasNext()) {
            Protein protein = (Protein) iterator.next();
            ProteinInteractor interactor = new ProteinInteractor();
            setNameId(protein, interactor);
            setOrganism(interactor);
            setExternalRefs(protein, interactor);
            interactorList.addProteinInteractor(interactor);
        }
        return interactorList;
    }

    /**
     * Sets Protein Name and ID.
     * @param protein Protein Object
     * @param interactor Castor Protein Interactor Object.
     */
    private void setNameId(Protein protein, ProteinInteractor interactor) {
        Names names = new Names();
        names.setShortLabel(protein.getOrfName());
        names.setFullName(protein.getDescription());
        interactor.setNames(names);
        interactor.setId(protein.getOrfName());
    }

    /**
     * Sets Protein Organism.
     * @param interactor Castor Protein Interactor Object.
     */
    private void setOrganism(ProteinInteractor interactor) {
        Organism organism = new Organism();
        organism.setNcbiTaxId(4932);
        Names orgNames = new Names();
        orgNames.setShortLabel("baker's yeast");
        orgNames.setFullName("Saccharomyces cerevisiae");
        organism.setNames(orgNames);
        interactor.setOrganism(organism);
    }

    /**
     * Sets Protein External References.
     * @param protein Protein Object.
     * @param interactor Castor Protein Interactor Object.
     */
    private void setExternalRefs(Protein protein,
            ProteinInteractor interactor) {
        ExternalReference refs [] = protein.getExternalRefs();
        if (refs.length > 0) {
            Xref xref = new Xref();
            //  First External Reference becomes the Primary Reference
            PrimaryRef primaryRef = new PrimaryRef();
            primaryRef.setDb(refs[0].getDatabase());
            primaryRef.setId(refs[0].getId());
            xref.setPrimaryRef(primaryRef);

            //  All others become Secondary References
            for (int i = 0; i < refs.length; i++) {
                SecondaryRef secondaryRef = new SecondaryRef();
                secondaryRef.setDb(refs[i].getDatabase());
                secondaryRef.setId(refs[i].getId());
                xref.addSecondaryRef(secondaryRef);
            }
            interactor.setXref(xref);
        }
    }

    /**
     * Gets a complete list of NonRedundant Proteins.
     * @return HashMap of NonRedundant Proteins.
     */
    private HashMap getNonRedundantProteins() {
        HashMap proteins = new HashMap();
        for (int i = 0; i < interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            Protein nodeA = interaction.getNodeA();
            Protein nodeB = interaction.getNodeB();
            addToHashMap(nodeA, proteins);
            addToHashMap(nodeB, proteins);
        }
        return proteins;
    }

    /**
     * Conditionally adds Protein to HashMap.
     * @param protein Protein Object.
     * @param proteins HashMap of NonRedundant Proteins.
     */
    private void addToHashMap(Protein protein, HashMap proteins) {
        String orfName = protein.getOrfName();
        if (!proteins.containsKey(orfName)) {
            proteins.put(orfName, protein);
        }
    }
}