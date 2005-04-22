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
package org.mskcc.pathdb.test.schemas.psi;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.schemas.psi.PsiUtil;
import org.mskcc.pathdb.sql.transfer.MissingDataException;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.StringWriter;
import java.util.HashMap;

/**
 * Tests the PsiUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestPsiUtil extends TestCase {

    /**
     * Tests the PsiUtil Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testNormalization() throws Exception {
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_mi/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        PsiUtil normalizer = new PsiUtil(new ProgressMonitor());

        EntrySet entrySet = normalizer.getNormalizedDocument(xml, false);
        Entry entry = entrySet.getEntry(0);
        validateAvailability(entry);
        validateExperiments(entry);
        validateInteractors(entry);
        validateInteractions(entry);
        validateInteractionUpdate(entry);
    }

    /**
     * Tests PsiUtil.removeEmptySecondaryRefs()
     *
     * @throws Exception All Exceptions.
     */
    public void testRemovalOfSecondaryRefs() throws Exception {
        // An actual excerpt from HPRD:
        // <xref>
        //   <primaryRef db="HPRD" id="HPRD_00400"/>
        //   <secondaryRef db="PubMed" id="10610782"/>
        //   <secondaryRef db="PubMed" id=" 8648130"/>
        //   <secondaryRef db="PubMed" id=" "/>
        //   <secondaryRef db="PubMed" id=""/>
        //   <secondaryRef db="PubMed" id=" 11367533"/>
        // </xref>
        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb("HPRD");
        primaryRef.setId("HPRD_00400");
        xref.setPrimaryRef(primaryRef);

        DbReferenceType secondaryRefs[] = new DbReferenceType[5];
        DbReferenceType secondaryRef = new DbReferenceType();
        secondaryRef.setDb("PubMed");
        secondaryRef.setId("10610782");
        secondaryRefs[0] = secondaryRef;
        secondaryRef = new DbReferenceType();
        secondaryRef.setDb("PubMed");
        secondaryRef.setId(" 8648130");
        secondaryRefs[1] = secondaryRef;
        secondaryRef = new DbReferenceType();
        secondaryRef.setDb("PubMed");
        secondaryRef.setId(" ");
        secondaryRefs[2] = secondaryRef;
        secondaryRef = new DbReferenceType();
        secondaryRef.setDb("PubMed");
        secondaryRef.setId("");
        secondaryRefs[3] = secondaryRef;
        secondaryRef = new DbReferenceType();
        secondaryRef.setDb("PubMed");
        secondaryRef.setId(" 11367533");
        secondaryRefs[4] = secondaryRef;
        xref.setSecondaryRef(secondaryRefs);

        //  Now remove empty secondary refs
        PsiUtil psiUtil = new PsiUtil(new ProgressMonitor());
        psiUtil.removeEmptySecondaryRefs(xref);

        //  Validate that empty refs are now removed.
        //  Should only be 3 refs remaining.
        assertEquals(3, xref.getSecondaryRefCount());

        //  Validate 0th Element
        secondaryRef = xref.getSecondaryRef(0);
        assertEquals("PubMed", secondaryRef.getDb());
        assertEquals("10610782", secondaryRef.getId());

        //  Validate Last Element
        secondaryRef = xref.getSecondaryRef(2);
        assertEquals("PubMed", secondaryRef.getDb());
        assertEquals("11367533", secondaryRef.getId());
    }

    /**
     * Tests Removal of Version Information and Handling of RefSeq IDs.
     *
     * @throws Exception All Exceptions.
     */
    public void testRemovalOfVersionInformation() throws Exception {
        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb("Ref-Seq");
        primaryRef.setId("NP_000680.2");
        xref.setPrimaryRef(primaryRef);
        ProgressMonitor pMonitor = new ProgressMonitor();
        PsiUtil util = new PsiUtil(pMonitor);
        util.normalizeXrefs(xref);

        primaryRef = xref.getPrimaryRef();
        assertEquals("NP_000680", primaryRef.getId());
        assertEquals("REF_SEQ PROTEIN", primaryRef.getDb());
    }

    /**
     * Tests the addExternalReferences() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testAddXRefs() throws Exception {
        //  Create Initial XRef
        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb("SwissProt");
        primaryRef.setId("AAH08943");
        xref.setPrimaryRef(primaryRef);

        //  Create some new External References that we want to add
        ExternalReference refs[] = new ExternalReference[2];
        refs[0] = new ExternalReference("LocusLink", "ABCDE");
        refs[1] = new ExternalReference("RefSeq", "NP_060241");

        //  Add the New External References to the XRef.
        PsiUtil util = new PsiUtil(null);
        util.addExternalReferences(xref, refs);

        //  Validate that the Xref has the new references
        assertEquals(2, xref.getSecondaryRefCount());
        DbReferenceType x0 = xref.getSecondaryRef(0);
        DbReferenceType x1 = xref.getSecondaryRef(1);

        assertEquals("LocusLink", x0.getDb());
        assertEquals("ABCDE", x0.getId());
        assertEquals("RefSeq", x1.getDb());
        assertEquals("NP_060241", x1.getId());
    }

    private void validateInteractionUpdate(Entry entry) throws Exception {
        PsiUtil util = new PsiUtil(new ProgressMonitor());
        HashMap idMap = new HashMap();
        idMap.put("YAL036C", new Long(1));
        idMap.put("YCR038C", new Long(2));
        idMap.put("YDL065C", new Long(3));
        idMap.put("YDR532C", new Long(4));
        idMap.put("YEL061C", new Long(5));
        idMap.put("YBR200W", new Long(6));
        idMap.put("YHR119W", new Long(7));

        InteractionList interactions = entry.getInteractionList();
        util.updateInteractions(interactions, idMap);
        StringWriter writer = new StringWriter();
        interactions.marshal(writer);
        String xml = writer.toString();
        assertTrue(xml.indexOf("<proteinInteractorRef ref=\"2\"/>") > 0);
    }

    /**
     * Verify that  No Global Availability Item exist,
     * and that all availability references have been replaced
     * with redundant copies.
     */
    private void validateAvailability(Entry entry) {
        AvailabilityList list = entry.getAvailabilityList();
        assertTrue(list == null);
        InteractionList interactionList = entry.getInteractionList();
        InteractionElementType interaction =
                interactionList.getInteraction(0);
        InteractionElementTypeChoice choice =
                interaction.getInteractionElementTypeChoice();
        AvailabilityType avail = choice.getAvailabilityDescription();
        String content = avail.getContent();
        assertEquals("This data is free to all.", content);

        interaction = interactionList.getInteraction(1);
        choice = interaction.getInteractionElementTypeChoice();
        avail = choice.getAvailabilityDescription();
        content = avail.getContent();
        assertEquals("This data is free to all.", content);

    }

    /**
     * Verify that  No Global Experimental Items exist,
     * and that all experimental references have been replaced
     * with redundant copies.
     */
    private void validateExperiments(Entry entry) {
        ExperimentList1 list = entry.getExperimentList1();
        assertTrue(list == null);
        InteractionList interactionList = entry.getInteractionList();
        InteractionElementType interaction =
                interactionList.getInteraction(0);
        ExperimentList expList = interaction.getExperimentList();
        ExperimentListItem expItem = expList.getExperimentListItem(0);
        ExperimentType experiment = expItem.getExperimentDescription();
        CvType cv = experiment.getInteractionDetection();
        String shortLabel = cv.getNames().getShortLabel();
        assertEquals("classical two hybrid", shortLabel);
    }


    /**
     * Verify that interactions no longer contain protein interactors, but
     * that they do contain protein interactor refs.
     */
    private void validateInteractions(Entry entry) {
        InteractionList interactionList = entry.getInteractionList();
        for (int i = 0; i < interactionList.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            ParticipantList participantList = interaction.getParticipantList();
            for (int j = 0; j < participantList.getProteinParticipantCount();
                 j++) {
                ProteinParticipantType proteinParticipant =
                        participantList.getProteinParticipant(j);
                ProteinParticipantTypeChoice choice =
                        proteinParticipant.getProteinParticipantTypeChoice();
                ProteinInteractorType protein = choice.getProteinInteractor();
                assertTrue(protein == null);
                RefType proteinRef = choice.getProteinInteractorRef();
                assertTrue(proteinRef != null);
            }
        }
    }

    /**
     * Verify that only one YCR038C interactor exists.
     *
     * @param entry
     */
    private void validateInteractors(Entry entry) throws MissingDataException {
        PsiUtil util = new PsiUtil(new ProgressMonitor());
        int counter = 0;
        InteractorList interactorList = entry.getInteractorList();
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = protein.getId();
            if (id.equals("YCR038C")) {
                counter++;
                ExternalReference refs[] = util.extractRefs(protein);
                assertEquals("Entrez GI", refs[0].getDatabase());
            }
        }
        assertEquals(1, counter);
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test the PSI-MI Utility Class";
    }
}