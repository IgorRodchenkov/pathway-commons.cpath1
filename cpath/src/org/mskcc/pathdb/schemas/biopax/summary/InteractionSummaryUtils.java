// $Id: InteractionSummaryUtils.java,v 1.10 2006-02-10 20:56:46 cerami Exp $
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.jdom.JDOMException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.BioPaxControlTypeMap;

/**
 * This class contains some utility methods
 * used primarily by PathwayInteractionTable and PathwayChildNodeTable classes.
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class InteractionSummaryUtils {
    private static String PHOSPHORYLATED = " (Phosphorylated)";
    private static String PHOSPHORYLATION_FEATURE = "phosphorylation";

    public static String getInteractionSummary(long recordID)
            throws DaoException, IOException, JDOMException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, EntitySummaryException {
        // get interaction parser
        EntitySummaryParser entityParser = new EntitySummaryParser(recordID);

        // get physical interacittion
        InteractionSummary interactionSummary = (InteractionSummary)
                entityParser.getEntitySummary();
        return (interactionSummary == null) ? "Not yet supported:  ID:  " + recordID
                : createInteractionSummaryString(interactionSummary);
    }

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
     * @return HTML String
     */
    public static String createInteractionSummaryString(InteractionSummary interactionSummary) {
            StringBuffer buf = new StringBuffer();
            if (interactionSummary instanceof ConversionInteractionSummary) {
                ConversionInteractionSummary summary =
                        (ConversionInteractionSummary) interactionSummary;
                ArrayList left = summary.getLeftSideComponents();
                ArrayList right = summary.getRightSideComponents();
                createSide(left, buf);
                buf.append (" &rarr; ");
                createSide(right, buf);
            } else if (interactionSummary instanceof PhysicalInteractionSummary) {
                PhysicalInteractionSummary summary =
                        (PhysicalInteractionSummary) interactionSummary;
                ArrayList participantList = summary.getParticipants();
                for (int i=0; i<participantList.size(); i++) {
                    ParticipantSummaryComponent component = (ParticipantSummaryComponent)
                        participantList.get(i);
                    buf.append (createLink(component));
                    if (i < participantList.size() -1) {
                        buf.append (", ");
                    }
                }
            } else {
                buf.append ("Not yet supported!");
            }
            return buf.toString();
    }

    private static void createSide(ArrayList list, StringBuffer buf) {
        for (int i=0; i<list.size(); i++) {
            ParticipantSummaryComponent component = (ParticipantSummaryComponent)
                list.get(i);
            buf.append (createLink(component));
            if (i < list.size() -1) {
                buf.append (" + ");
            }
        }
    }

    private static String createLink (ParticipantSummaryComponent component) {

        String name = component.getName();
        boolean isPhosphorylated = isPhosphorylated (component);

        StringBuffer buf = new StringBuffer("<a href=\"record.do?id=" + component.getRecordID());
        buf.append ("\" onmouseover=\"drc('");
        List synList = component.getSynonyms();
        if (synList != null && synList.size() > 0) {
            buf.append("Also known as:  <UL>");
            for (int i=0; i<synList.size(); i++) {
                String synonym = (String) synList.get(i);
                buf.append("<LI>" + synonym + "</LI>");
            }
            buf.append ("</UL>");
        }
        if (component.getFeatureList() != null && component.getFeatureList().size() > 0) {
            buf.append ("<P>Features:<UL>");
            ArrayList featureList = component.getFeatureList();
            for (int i=0; i<featureList.size(); i++) {
                String feature = (String) featureList.get(i);
                buf.append ("<LI>" + feature + "</LI>");
            }
            buf.append ("</UL>");
        }
        buf.append("', '");
        buf.append (name);
        if (isPhosphorylated) {
            buf.append (PHOSPHORYLATED);
        }
        if (component.getCellularLocation() != null) {
            buf.append (" in <FONT COLOR=LIGHTGREEN>" + component.getCellularLocation()
                + "</FONT>");
        }
        buf.append ("'); return true;\" onmouseout=\"nd(); return true;\">");
        buf.append (name);
        buf.append ("</a>");
        if (isPhosphorylated) {
            buf.append(PHOSPHORYLATED);
        }
        return buf.toString();
    }

    private static boolean isPhosphorylated (ParticipantSummaryComponent component) {
        if (component.getFeatureList() != null && component.getFeatureList().size() > 0) {
            ArrayList featureList = component.getFeatureList();
            for (int i=0; i<featureList.size(); i++) {
                String feature = (String) featureList.get(i);
                feature = feature.toLowerCase();
                if (feature.indexOf(PHOSPHORYLATION_FEATURE) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

//        int lc, cnt;
//        ArrayList components;
//        String summaryString = "";
//
//        // set participants
//        components = (interactionSummary instanceof ControlInteractionSummary) ?
//			((ControlInteractionSummary)interactionSummary).getControllers() :
//			((interactionSummary instanceof ConversionInteractionSummary) ?
//			((ConversionInteractionSummary)interactionSummary).getLeftSideComponents() : interactionSummary.getParticipants());
//		if (components != null){
//			cnt = components.size();
//			for (lc = 0; lc < cnt; lc++){
//				ParticipantSummaryComponent summaryComponent = (ParticipantSummaryComponent)components.get(lc);
//				summaryString += "<a href=\"record.do?id=" + String.valueOf(summaryComponent.getRecordID()) +
//					"\">" + summaryComponent.getName() + "</a>";
//				// add location:feature string
//				String summaryFeatureString = createSummaryFeatureString(summaryComponent);
//				if (summaryFeatureString != null){
//					summaryString += summaryFeatureString;
//				}
//				// add summary detail string - see function definition for more info
//				String summaryDetailString = createSummaryDetailString(summaryComponent.getRecordID());
//				if (summaryDetailString != null){
//					summaryString += summaryDetailString;
//				}
//				// add separator between participants
//				summaryString += (lc < cnt-1) ? createSeparatorString(interactionSummary) : " ";
//			}
//		}
//
//		// operator
//		String operatorString = createOperatorString(interactionSummary);
//		if (operatorString != null){
//			summaryString += (" " + operatorString + " ");
//		}
//
//		// right side
//        components = (interactionSummary instanceof ControlInteractionSummary) ?
//			((ControlInteractionSummary)interactionSummary).getControlled() :
//			((interactionSummary instanceof ConversionInteractionSummary) ?
//			 ((ConversionInteractionSummary)interactionSummary).getRightSideComponents() : null);
//		if (components != null){
//            cnt = components.size();
//            for (lc = 0; lc < cnt; lc++){
//                ParticipantSummaryComponent summaryComponent = (ParticipantSummaryComponent)components.get(lc);
//                summaryString += "<a href=\"record.do?id=" + String.valueOf(summaryComponent.getRecordID()) +
//                              "\">" + summaryComponent.getName() + "</a>";
//                // add location:feature string
//				String summaryFeatureString = createSummaryFeatureString(summaryComponent);
//				if (summaryFeatureString != null){
//					summaryString += summaryFeatureString;
//				}
//                // add summary detail string - see function definition for more info
//				String summaryDetailString = createSummaryDetailString(summaryComponent.getRecordID());
//				if (summaryDetailString != null){
//					summaryString += summaryDetailString;
//				}
//                // add separator between participants
//                summaryString += (lc < cnt-1) ? createSeparatorString(interactionSummary) : "";
//            }
//        }
//
//        // outta here
//        return summaryString;
//    }

//    /**
//     * Creates operator string for Control or Conversion interaction.
//	 * Examples of an operator may be "-->" or "[Activates]"
//     *
//     * @param interactionSummary InteractionSummary
//     * @return String
//     */
//    private static String createOperatorString(InteractionSummary interactionSummary) {
//
//		// string to return
//		String operatorString = null;
//
//		// conversion interaction summary
//		if (interactionSummary instanceof ConversionInteractionSummary){
//			operatorString = "-->";
//		}
//		else if (interactionSummary instanceof ControlInteractionSummary){
//			String controlType = ((ControlInteractionSummary)interactionSummary).getControlType();
//			if (controlType != null){
//				BioPaxControlTypeMap controlTypeMap = new BioPaxControlTypeMap();
//				operatorString = "[" + controlTypeMap.get(controlType) + "]";
//			}
//			else{
//				operatorString = "[CONTROL-TYPE NOT FOUND]";
//			}
//		}
//		// note physical interactions do not have operators, we do nothing
//		//else if (interactionSummary instanceof PhysicalInteractionSummary){
//		//}
//
//		// outta here
//		return operatorString;
//	}

    /**
     * Gets Interaction Summary string.
     *
     * This has been added to augment originally spec'd
     * summary information like 'Phosphorylation' with
     * the actual interaction, like 'Alpha6 --> Alpha6',
     * so we will have 'Phosphorylation: Alpha6 --> Alpha6'
     *
     * @param recordID long
     * @return String
     * @throws DaoException
     * @throws IOException
     * @throws JDOMException
     */
//    private static String createSummaryDetailString(long recordID)
//            throws DaoException, IOException, InteractionSummaryException, JDOMException {
//
//        String summaryDetails = getInteractionSummary(recordID);
//        return (summaryDetails != null && summaryDetails.length() > 0)
//                ? " (" + summaryDetails + ")" : null;
//    }

//    /**
//     * Creates location:feature string.
//     *
//     * @param participantSummaryComponent ParticipantSummaryComponent
//     * @return String
//     */
//    private static String createSummaryFeatureString
//            (ParticipantSummaryComponent participantSummaryComponent) {
//
//        // string to return
//        String summaryFeatureString = "";
//
//        // get data from component
//        String cellularLocation = participantSummaryComponent.getCellularLocation();
//        ArrayList featureList = participantSummaryComponent.getFeatureList();
//        int cnt = (featureList != null) ? featureList.size() : 0;
//
//        if (cellularLocation != null && cellularLocation.length() > 0){
//            summaryFeatureString = "(" + participantSummaryComponent.getCellularLocation() + ":";
//        }
//
//        // process feature list
//        if (cnt > 0){
//            if (summaryFeatureString.length() == 0){
//                summaryFeatureString = "(:";
//            }
//            for (int lc = 0; lc < cnt; lc++){
//                if (featureList != null && featureList.size() > 0){
//                    String feature = (String)featureList.get(lc);
//                    if (feature != null && feature.length() > 0){
//                        if (lc == 0){
//                            summaryFeatureString += feature;
//                        }
//                        else{
//                            summaryFeatureString += ", " + feature;
//                        }
//                    }
//                }
//            }
//
//        }
//
//        // cap off the string
//        if (summaryFeatureString.length() > 0){
//            summaryFeatureString += ")";
//        }
//
//        // outta here
//        return summaryFeatureString;
//    }
//
//    /**
//     * Creates a separator string between interactions participants
//	 * on one side of an operator.  Currently ' + ' is used for
//	 * conversion and control interactions and ' ' for physical interactions.
//	 *
//     * @param interactionSummary InteractionSummary
//     * @return String
//     */
//    private static String createSeparatorString(InteractionSummary interactionSummary) {
//
//		// string to return
//		String separatorString;
//
//        if (interactionSummary instanceof PhysicalInteractionSummary){
//			separatorString = " ";
//		}
//		else{
//			separatorString = " + ";
//		}
//
//		// outta here
//		return separatorString;
//	}
}
