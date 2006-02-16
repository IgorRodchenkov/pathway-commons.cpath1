// $Id: InteractionSummaryUtils.java,v 1.18 2006-02-16 14:39:21 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// package
package org.mskcc.pathdb.schemas.biopax.summary;

// imports
import java.util.ArrayList;
import java.util.List;
import org.mskcc.pathdb.model.BioPaxControlTypeMap;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

/**
 * This class generates Summary Strings of BioPAX Interaction Objects.
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class InteractionSummaryUtils {
    /**
     * Phosphorylated Keyword.
     */
    private static final String PHOSPHORYLATED = " (Phosphorylated)";

    /**
     * Ubiquitinated Keyword.
     */
    private static final String UBIQUITINATED = " (Ubiquitinated)";

    /**
     * Acetylated Keyword.
     */
    private static final String ACETYLATED = " (Acetylated)";

    /**
     * Phosphorylation Feature.
     */
    private static final String PHOSPHORYLATION_FEATURE = "phosphorylation";

    /**
     * Ubiquitination Feature.
     */
    private static final String UBIQUITINATION_FEATURE = "ubiquitination";

    /**
     * Acetylation Feature.
     */
    private static final String ACETYLATION_FEATURE = "acetylation";

    /**
     * Space Character.
     */
    private static final String SPACE = " ";

    /**
     * Names longer than this will be truncated.
     */
    private static final int NAME_LENGTH = 10;

    /**
     * No name available.
     */
    private static final String NO_NAME_AVAILABLE = "[No Name Available]";

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
     * @return HTML String
     */
    public static String createInteractionSummaryString(InteractionSummary interactionSummary) {
        StringBuffer buf = new StringBuffer();

        //  Branch, depending on interaction type.
        if (interactionSummary instanceof ConversionInteractionSummary) {
            createConversionInteractionSummary(interactionSummary, buf);
        } else if (interactionSummary instanceof ControlInteractionSummary) {
            createControlSummary(interactionSummary, buf);
        } else if (interactionSummary instanceof PhysicalInteractionSummary) {
            createPhysicalInteractionSummary(interactionSummary, buf);
        } else {
            buf.append("Interaction Type Not yet supported!");
        }
        return buf.toString();
    }

    /**
     * Creates a Conversion Interaction Summary.
     *
     * @param interactionSummary InteractionSummary Object.
     * @param buf                HTML String Buffer.
     */
    private static void createConversionInteractionSummary(InteractionSummary
            interactionSummary, StringBuffer buf) {
        ConversionInteractionSummary summary =
                (ConversionInteractionSummary) interactionSummary;

        //  Create left side
        ArrayList left = summary.getLeftSideComponents();
        createSide(left, interactionSummary, buf);

        //  Create rigth side
        ArrayList right = summary.getRightSideComponents();
        buf.append(" &rarr; ");
        createSide(right, interactionSummary, buf);
    }

    /**
     * Creates a Physical Interaction Summary.
     *
     * @param interactionSummary InteractionSummary Object.
     * @param buf                HTML String Buffer.
     */
    private static void createPhysicalInteractionSummary(InteractionSummary
            interactionSummary, StringBuffer buf) {
        PhysicalInteractionSummary summary =
                (PhysicalInteractionSummary) interactionSummary;

        //  Iterate through all participants
        ArrayList participantList = summary.getParticipants();
        for (int i = 0; i < participantList.size(); i++) {
            ParticipantSummaryComponent component = (ParticipantSummaryComponent)
                    participantList.get(i);
            buf.append(createComponentLink(component, interactionSummary));
            if (i < participantList.size() - 1) {
                buf.append(", ");
            }
        }
    }

    /**
     * Creates a Control Interaction Summary.
     *
     * @param interactionSummary InteractionSummary.
     * @param buf                HTML String Buffer.
     */
    private static void createControlSummary(InteractionSummary interactionSummary,
            StringBuffer buf) {
        BioPaxControlTypeMap map = new BioPaxControlTypeMap();
        ControlInteractionSummary summary = (ControlInteractionSummary) interactionSummary;
        List controllerList = summary.getControllers();

        //  Iterate through all controllers.
        for (int i = 0; i < controllerList.size(); i++) {
            ParticipantSummaryComponent component = (ParticipantSummaryComponent)
                    controllerList.get(i);
            buf.append(createComponentLink(component, interactionSummary));
            if (i < controllerList.size() - 1) {
                buf.append(", ");
            }
        }

        //  Output control type in Plain English
        String controlType = summary.getControlType();
        String controlTypeInEnglish = (String) map.get(controlType);
        if (controlTypeInEnglish != null) {
            buf.append(SPACE + controlTypeInEnglish + SPACE);
        }

        //  Iterate through all controlled elements.
        List controlledList = summary.getControlled();

        if (controlledList != null) {
            if (controlledList.size() == 1) {
                EntitySummary entitySummary = (EntitySummary) controlledList.get(0);
                if (entitySummary instanceof InteractionSummary) {
                    InteractionSummary intxnSummary = (InteractionSummary) entitySummary;
                    buf.append("[");
                    buf.append(InteractionSummaryUtils.createInteractionSummaryString
                            (intxnSummary));
                    buf.append("]");
                } else {
                    buf.append(entitySummary.getName());
                }
            }
        }
    }

    /**
     * Create Left/Right Side of a reaction.
     *
     * @param list    List of ParticipantSummaryComponent Objects.
     * @param summary InteractionSummary Object.
     * @param buf     HTML String Buffer.
     */
    private static void createSide(ArrayList list, InteractionSummary summary, StringBuffer buf) {
        for (int i = 0; i < list.size(); i++) {
            ParticipantSummaryComponent component = (ParticipantSummaryComponent)
                    list.get(i);
            buf.append(createComponentLink(component, summary));
            if (i < list.size() - 1) {
                buf.append(" + ");
            }
        }
    }

    /**
     * Creates an HTML Link to the Specified Component Object.
     *
     * @param component ParticipantSummaryComponent Object.
     * @param summary   Interaction Summary Object.
     * @return HTML String.
     */
    private static String createComponentLink(ParticipantSummaryComponent component,
            InteractionSummary summary) {
        String name = determineName(component);

        //  Determine if we have any special cases to deal with.
        boolean isPhosphorylated = hasFeature(component, PHOSPHORYLATION_FEATURE);
        boolean isUbiquitinated = hasFeature(component, UBIQUITINATION_FEATURE);
        boolean isAcetylated = hasFeature(component, ACETYLATION_FEATURE);
        boolean isTransport = isTransport(summary);

        //  Start HTML A Link Tag.
        StringBuffer buf = new StringBuffer("<a href=\"record.do?id=" + component.getRecordID());

        //  Create JavaScript for MouseOver Pop-Up Box
        buf.append("\" onmouseover=\"return overlib('");

        //  Add Synonyms to Pop-Up Box
        addSynonmys(component, buf);

        //  Add Features to Pop-Up Box
        addFeatures(component, buf);

        //  Create Header for Pop-Up Box
        buf.append("', WRAP, CELLPAD, 5, OFFSETY, 0, CAPTION, '");
        buf.append(name);
        appendFeatures(isPhosphorylated, isUbiquitinated, isAcetylated, buf);
        if (component.getCellularLocation() != null) {
            buf.append(" in <FONT COLOR=LIGHTGREEN>" + component.getCellularLocation()
                    + "</FONT>");
        }
        buf.append("'); return true;\" onmouseout=\"return nd();\">");

        //  Output Component Name and end A Tag.
        buf.append(truncateLongName(name));
        buf.append("</a>");

        //  If this is a transport interaction, show cellular location explicitly
        if (isTransport && component.getCellularLocation() != null) {
            buf.append(" (in " + component.getCellularLocation() + ")");
        }

        //  If component is phosphorylated, show explicitly
        appendFeatures(isPhosphorylated, isUbiquitinated, isAcetylated, buf);
        return buf.toString();
    }

    /**
     * Determines a Component Name.
     * The following order of precedence is used to determine a name:
     * <UL>
     * <LI>Name</LI>
     * <LI>Short Name</LI>
     * <LI>First Synonym</LI>
     * </UL>
     * If none of these attributes are available, the name is set to NO_NAME_AVAILABLE.
     * @param component ParticipantSummaryComponent Object.
     * @return Name string.
     */
    private static String determineName (ParticipantSummaryComponent component) {
        String name = component.getName();
        if (name == null) {
            name = component.getShortName();
            if (name == null) {
                List synList = component.getSynonyms();
                if (synList != null && synList.size() > 0) {
                    name = (String) synList.get(0);
                } else {
                    name = NO_NAME_AVAILABLE;
                }
            }
        }
        return name;
    }

    /**
     * Appends Features, such as Phosphorylated, Ubiquitinated or Acetylated.
     * @param phosphorylated isPhosphorylated.
     * @param ubiquitinated
     * @param acetylated
     * @param buf
     */
    private static void appendFeatures(boolean phosphorylated, boolean ubiquitinated,
            boolean acetylated, StringBuffer buf) {
        if (phosphorylated) {
            buf.append(PHOSPHORYLATED);
        }
        if (ubiquitinated) {
            buf.append(UBIQUITINATED);
        }
        if (acetylated) {
            buf.append(ACETYLATED);
        }
    }

    private static String truncateLongName (String name) {
        if (name !=null ) {
            if (name.length() > NAME_LENGTH) {
                return name.substring(0, NAME_LENGTH) + "...";
            }
        }
        return name;
    }

    /**
     * Adds Feature List.
     *
     * @param component ParticipantSummaryComponent Object.
     * @param buf       HTML StringBuffer Object.
     */
    private static void addFeatures(ParticipantSummaryComponent component, StringBuffer buf) {
        if (component.getFeatureList() != null && component.getFeatureList().size() > 0) {
            buf.append("<P>Features:<UL>");
            ArrayList featureList = component.getFeatureList();
            for (int i = 0; i < featureList.size(); i++) {
                String feature = (String) featureList.get(i);
                buf.append("<LI>" + feature + "</LI>");
            }
            buf.append("</UL>");
        }
    }

    /**
     * Adds Synonym List.
     *
     * @param component ParticipantSummaryComponent Object.
     * @param buf       HTML StringBuffer Object.
     */
    private static void addSynonmys(ParticipantSummaryComponent component, StringBuffer buf) {
        List synList = component.getSynonyms();
        if (synList != null && synList.size() > 0) {
            buf.append("Also known as:  <UL>");
            for (int i = 0; i < synList.size(); i++) {
                String synonym = (String) synList.get(i);
                buf.append("<LI>" + synonym + "</LI>");
            }
            buf.append("</UL>");
        }
    }

    /**
     * Determines if the specified interaction is of type:  TRANSPORT.
     *
     * @param summary InteractionSummary Object.
     * @return boolean value.
     */
    private static boolean isTransport(InteractionSummary summary) {
        String interactionType = summary.getSpecificType();
        if (interactionType != null) {
            if (interactionType.equalsIgnoreCase(BioPaxConstants.TRANSPORT)
                    || interactionType.equalsIgnoreCase
                    (BioPaxConstants.TRANSPORT_WITH_BIOCHEMICAL_REACTION)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified component has the specified target feature.
     *
     * @param component ParticipantSummaryComponent Object.
     * @return boolean value.
     */
    private static boolean hasFeature (ParticipantSummaryComponent component, String featureTarget) {
        if (component.getFeatureList() != null && component.getFeatureList().size() > 0) {
            ArrayList featureList = component.getFeatureList();
            for (int i = 0; i < featureList.size(); i++) {
                String feature = (String) featureList.get(i);
                feature = feature.toLowerCase();
                if (feature.indexOf(featureTarget) > -1) {
                    return true;
                }
            }
        }
        return false;
    }
}