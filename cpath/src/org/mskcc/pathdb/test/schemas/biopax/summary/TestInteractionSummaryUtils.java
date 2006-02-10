package org.mskcc.pathdb.test.schemas.biopax.summary;

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryParser;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;

/**
 * Tests the InteractionSummaryUtils Class.
 *
 * @author Ethan Cerami
 *
 */
public class TestInteractionSummaryUtils extends TestCase {

    /**
     * Validate the summary of a Physical Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testPhysicalInteractionSummary() throws Exception {
        EntitySummaryParser entityParser = new EntitySummaryParser(17);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        assertEquals ("<a href=\"record.do?id=18\" onmouseover=\"drc('', 'AR in "
            + "<FONT COLOR=LIGHTGREEN>nucleus</FONT>'); return true;\" onmouseout=\"nd(); "
            + "return true;\">AR</a>, <a href=\"record.do?id=19\" onmouseover=\"drc"
            + "('', 'ETV5 in <FONT COLOR=LIGHTGREEN>nucleus</FONT>'); return true;\""
            + " onmouseout=\"nd(); return true;\">ETV5</a>", summary);
    }

    /**
     * Validate the summary of a Conversion Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testConversionInteractionSummary() throws Exception {
        EntitySummaryParser entityParser = new EntitySummaryParser(8);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        assertEquals ("<a href=\"record.do?id=10\" onmouseover=\"drc('Also known as:  "
            + "<UL><LI>ATP:D-glucose 6-phosphotransferase</LI><LI>glucose ATP phosphotransferase"
            + "</LI></UL>', 'a-D-glu in <FONT COLOR=LIGHTGREEN>cytoplasm</FONT>'); "
            + "return true;\" onmouseout=\"nd(); return true;\">a-D-glu</a> + "
            + "<a href=\"record.do?id=14\" onmouseover=\"drc('Also known as:  "
            + "<UL><LI>ATP:D-glucose 6-phosphotransferase</LI><LI>glucose ATP "
            + "phosphotransferase</LI></UL>', 'ATP in <FONT COLOR=LIGHTGREEN>cytoplasm</FONT>'); "
            + "return true;\" onmouseout=\"nd(); return true;\">ATP</a> &rarr; "
            + "<a href=\"record.do?id=11\" onmouseover=\"drc('Also known as:  <UL><LI>"
            + "ATP:D-glucose 6-phosphotransferase</LI><LI>glucose ATP phosphotransferase"
            + "</LI></UL>', 'ADP in <FONT COLOR=LIGHTGREEN>cytoplasm</FONT>'); return "
            + "true;\" onmouseout=\"nd(); return true;\">ADP</a> + <a href=\"record.do?id=12\" "
            + "onmouseover=\"drc('Also known as:  <UL><LI>ATP:D-glucose 6-phosphotransferase"
            + "</LI><LI>glucose ATP phosphotransferase</LI></UL>', 'a-D-glu-6-p in "
            + "<FONT COLOR=LIGHTGREEN>cytoplasm</FONT>'); return true;\" onmouseout=\"nd(); "
            + "return true;\">a-D-glu-6-p</a>", summary);
    }

    /**
     * Validate the summary of a Conversion Interaction (Phosphorylation).
     *
     * @throws Exception All Exceptions.
     */
    public void testPhosphorylationInteractionSummary() throws Exception {
        EntitySummaryParser entityParser = new EntitySummaryParser(23);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        assertTrue (summary.indexOf("AR (Phosphorylated)")>0);
    }

    /**
     * Validate the summary of a Control Interaction.
     *
     * @throws Exception All Exceptions.
     */
    public void testControlInteractionSummary() throws Exception {
        EntitySummaryParser entityParser = new EntitySummaryParser(20);
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        String summary = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
    }
}
