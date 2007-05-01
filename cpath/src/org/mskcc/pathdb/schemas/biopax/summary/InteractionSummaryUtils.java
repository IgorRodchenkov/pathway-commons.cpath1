// $Id: InteractionSummaryUtils.java,v 1.31 2007-05-01 21:18:56 cerami Exp $
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
package org.mskcc.pathdb.schemas.biopax.summary;

// imports

import org.mskcc.pathdb.model.BioPaxControlTypeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * This class generates Summary Strings of BioPAX Interaction Objects.
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class InteractionSummaryUtils {

    /**
     * Space Character.
     */
    private static final String SPACE = " ";

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
     * @return HTML String
     */
    public static String createInteractionSummaryString
            (InteractionSummary interactionSummary) {
        StringBuffer buf = new StringBuffer();

        //  Branch, depending on interaction type.
        if (interactionSummary instanceof ConversionInteractionSummary) {
            createConversionInteractionSummary(interactionSummary, buf);
        } else if (interactionSummary instanceof ControlInteractionSummary) {
            createControlSummary(interactionSummary, true, buf);
        } else if (interactionSummary instanceof PhysicalInteractionSummary) {
            createPhysicalInteractionSummary(interactionSummary, buf);
        } else {
            buf.append(interactionSummary.getName());
        }
        return buf.toString();
    }

    /**
     * Creates the interaction summary string (truncated).
     *
     * @param interactionSummary PhysicalInteractiong
     * @return HTML String
     */
    public static String createInteractionSummaryStringTruncated
            (InteractionSummary interactionSummary) {
        StringBuffer buf = new StringBuffer();

        //  Branch, depending on interaction type.
        if (interactionSummary instanceof ConversionInteractionSummary) {
            createConversionInteractionSummary(interactionSummary, buf);
        } else if (interactionSummary instanceof ControlInteractionSummary) {
            createControlSummary(interactionSummary, false, buf);
        } else if (interactionSummary instanceof PhysicalInteractionSummary) {
            createPhysicalInteractionSummary(interactionSummary, buf);
        } else {
            buf.append(interactionSummary.getName());
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

        ArrayList left = summary.getLeftSideComponents();
        ArrayList right = summary.getRightSideComponents();

        if ((left == null || left.size() == 0)
                && (right == null || right.size() ==0)) {
            buf.append (interactionSummary.getName());
        } else {
            createSide(left, interactionSummary, buf);
            buf.append(" &rarr; ");
            createSide(right, interactionSummary, buf);
        }
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
        if (participantList != null) {
            for (int i = 0; i < participantList.size(); i++) {
                ParticipantSummaryComponent component =
                        (ParticipantSummaryComponent) participantList.get(i);
                buf.append(BioPaxRecordSummaryUtils.createEntityLink
                        (component, interactionSummary));
                if (i < participantList.size() - 1) {
                    buf.append(", ");
                }
            }
        } else {
            buf.append("[&empty;]");
        }
    }

    /**
     * Creates a Control Interaction Summary.
     *
     * <P>
     * When verbose is set to true, you get a summary like this:
     * Rapamycin activates [ASK1 ? ASK1 (active)].  This includes the controller
     * and all particpants in the controlled interaction.
     * <P>
     * When verbose is set to false, you get a summary like this:
     * activated by Rapamycin.  This includes the controller only.
     *
     * @param interactionSummary InteractionSummary.
     * @param verbose            Verbose flag.
     * @param buf                HTML String Buffer.
     */
    private static void createControlSummary(InteractionSummary
            interactionSummary, boolean verbose, StringBuffer buf) {
        ControlInteractionSummary summary =
                (ControlInteractionSummary) interactionSummary;
        List controllerList = summary.getControllers();
        String controlType = summary.getControlType();

        if (!verbose) {
            buf.append ("<a href='record2.do?id=" + interactionSummary.getRecordID()
                + "'>");
            if (controlType != null) {
                HashMap map = BioPaxControlTypeMap.getPastTenseMap();
                String controlTypeInEnglish = (String) map.get(controlType);
                if (controlTypeInEnglish != null) {
                    buf.append(controlTypeInEnglish);
                }
            } else {
                buf.append ("controlled by");
            }
            buf.append ("</a>");
            buf.append (SPACE + "by" + SPACE);
        }

        //  Iterate through all controllers.
        if (controllerList != null) {
            for (int i = 0; i < controllerList.size(); i++) {
                ParticipantSummaryComponent component =
                        (ParticipantSummaryComponent) controllerList.get(i);
                buf.append(BioPaxRecordSummaryUtils.createEntityLink
                        (component, interactionSummary));
                if (i < controllerList.size() - 1) {
                    buf.append(", ");
                }
            }
        }
        if (controllerList == null || controllerList.size() ==0) {
            buf.append("<I>" + summary.getName() + "</I>");
        }

        if (verbose) {
            if (controlType != null) {
                HashMap map = BioPaxControlTypeMap.getPresentTenseMap();
                String controlTypeInEnglish = (String) map.get(controlType);
                if (controlTypeInEnglish != null) {
                    buf.append(SPACE + controlTypeInEnglish + SPACE);
                }
            } else {
                buf.append (SPACE + "controls" + SPACE);
            }
        }

        //  Iterate through all controlled elements.
        if (verbose) {
            List controlledList = summary.getControlled();

            if (controlledList != null) {
                if (controlledList.size() == 1) {
                    EntitySummary entitySummary =
                            (EntitySummary) controlledList.get(0);
                    if (entitySummary instanceof InteractionSummary) {
                        InteractionSummary intxnSummary =
                                (InteractionSummary) entitySummary;
                        buf.append("[");
                        buf.append(InteractionSummaryUtils.
                                createInteractionSummaryString(intxnSummary));
                        buf.append("]");
                    } else {
                        buf.append("<I>" + entitySummary.getName() + "</I>");
                    }
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
    private static void createSide(ArrayList list, InteractionSummary summary,
            StringBuffer buf) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ParticipantSummaryComponent component =
                        (ParticipantSummaryComponent) list.get(i);
                buf.append(BioPaxRecordSummaryUtils.createEntityLink
                        (component, summary));
                if (i < list.size() - 1) {
                    buf.append(" + ");
                }
            }
        } else {
            buf.append ("[&empty;]");
        }
    }
}
