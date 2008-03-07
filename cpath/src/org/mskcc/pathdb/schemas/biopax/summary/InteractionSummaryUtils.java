// $Id: InteractionSummaryUtils.java,v 1.42 2008-03-07 14:13:56 grossben Exp $
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
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;

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
		return createInteractionSummaryString(interactionSummary, null);
	}

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
	 * @param bpSummary BioPaxRecordSummary
     * @return List<String>
     */
    public static List<String> createInteractionSummaryStringList(InteractionSummary interactionSummary,
																  BioPaxRecordSummary bpSummary) {

		// list to return
		List<String> toReturn = new ArrayList<String>();

		// get html string
		String htmlString = createInteractionSummaryString(interactionSummary, bpSummary);
		
		// split string by component tags
		String[] components = htmlString.split("<span class='component'>");

		// create List<String> to return)
		for (String component : components) {
			if (component.length() == 0) continue;
			toReturn.add("<span class='component'>" + component);
		}

		// outta here
		return toReturn;
	}

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
	 * @param bpSummary BioPaxRecordSummary
     * @return HTML String
     */
    public static String createInteractionSummaryString(InteractionSummary interactionSummary,
														BioPaxRecordSummary bpSummary) {
        StringBuffer buf = new StringBuffer();

        //  Branch, depending on interaction type.
        if (interactionSummary instanceof ConversionInteractionSummary) {
            createConversionInteractionSummary(interactionSummary, buf);
        } else if (interactionSummary instanceof ControlInteractionSummary) {
            createControlSummary(interactionSummary, true, buf);
        } else if (interactionSummary instanceof PhysicalInteractionSummary) {
            createPhysicalInteractionSummary(interactionSummary, bpSummary, buf);
        } else {
            createInteractionSummary(interactionSummary, true, buf);
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
            createPhysicalInteractionSummary(interactionSummary, null, buf);
        } else {
            createInteractionSummary(interactionSummary, false, buf);
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
            buf.append ("<a href='record2.do?id="
                + interactionSummary.getRecordID()
                + "'>" + interactionSummary.getName() + "</a>");
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
	 * @param bpSummary BioPaxRecordSummary
     * @param buf                HTML String Buffer.
     */
    private static void createPhysicalInteractionSummary(InteractionSummary interactionSummary,
														 BioPaxRecordSummary bpSummary,
														 StringBuffer buf) {
        PhysicalInteractionSummary summary =
                (PhysicalInteractionSummary) interactionSummary;

		List<ParticipantSummaryComponent> participantSummaryComponentList =
			new ArrayList<ParticipantSummaryComponent>();

		// if available, use bpSummary to properly order participants in interaction, 
		// protein whose summary page we are on should go first - see bug #1650
		List<ParticipantSummaryComponent> summaryParticipants = (List<ParticipantSummaryComponent>)summary.getParticipants();
	    if (summaryParticipants != null) {
		    for (ParticipantSummaryComponent component : summaryParticipants) {
				if (bpSummary != null && bpSummary.getName() != null && bpSummary.getName().equals(component.getName())) {
					participantSummaryComponentList.add(0, component);
				}
				else {
					participantSummaryComponentList.add(component);
				}
			}
		}

		if (participantSummaryComponentList.size() == 0) {
            buf.append("[&empty;]");
		}
		else {
			//  Iterate through all participants
			for (int i = 0; i < participantSummaryComponentList.size(); i++) {
				ParticipantSummaryComponent component = participantSummaryComponentList.get(i);
				buf.append(BioPaxRecordSummaryUtils.createEntityLink(component, interactionSummary, bpSummary.getOrganism()));
				if (i < participantSummaryComponentList.size() - 1) {
					buf.append(", ");
				}
			}
		}
    }

    /**
     * Creates an Interaction Summary.
     *
     * @param interactionSummary InteractionSummary Object.
     * @param linkInteractionName     Flag to hyperlink interaction name.
     * @param buf                HTML String Buffer.
     */
    private static void createInteractionSummary(InteractionSummary
            interactionSummary, boolean linkInteractionName, StringBuffer buf) {
        if (linkInteractionName) {
            buf.append("<a href='record2.do?id=" + interactionSummary.getRecordID()
                + "'>");
            if (interactionSummary.getName() != null
                    && interactionSummary.getName().equals(CPathRecord.NA_STRING)) {
                buf.append ("Interaction");
            } else {
                buf.append(interactionSummary.getName());
            }
            buf.append("</a>");
        }
        //  Iterate through all participants
        ArrayList participantList = interactionSummary.getParticipants();
        if (participantList != null) {
            buf.append(" [");
            for (int i = 0; i < participantList.size(); i++) {
                ParticipantSummaryComponent component =
                        (ParticipantSummaryComponent) participantList.get(i);
                buf.append(BioPaxRecordSummaryUtils.createEntityLink
						   (component, interactionSummary, null));
                if (i < participantList.size() - 1) {
                    buf.append(", ");
                }
            }
            buf.append("]");
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
            buf.append("<span class='control_verb'>");
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
            buf.append("</span>");
            buf.append (SPACE + "by" + SPACE);
        }

        //  Iterate through all controllers.
        if (controllerList != null) {
            for (int i = 0; i < controllerList.size(); i++) {
                ParticipantSummaryComponent component =
                        (ParticipantSummaryComponent) controllerList.get(i);
                buf.append(BioPaxRecordSummaryUtils.createEntityLink
						   (component, interactionSummary, null));
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
						   (component, summary, null));
                if (i < list.size() - 1) {
                    buf.append(" + ");
                }
            }
        } else {
            List externalLinks = summary.getExternalLinks();
            //  The code below is required, at least until BioPAX Level 3 supports
            //  a description of transcriptional regulation.
            boolean empty = true;
			if (externalLinks != null) {
				for (int i=0; i<externalLinks.size(); i++) {
					ExternalLinkRecord externalLink = (ExternalLinkRecord) externalLinks.get(i);
					ExternalDatabaseRecord externalDb = externalLink.getExternalDatabase();
					if(externalDb.getMasterTerm().equals("GENE ONTOLOGY")
					   && externalLink.getLinkedToId().equals("0006350")) {
						buf.append ("transcription");
						empty = false;
					}
				}
			}
            if (empty) {
                buf.append ("[&empty;]");
            }
        }
    }
}
