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
import java.io.IOException;
import org.jdom.JDOMException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.BioPaxControlTypeMap;

/**
 * This class contains some utility methods
 * used primarily by PathwayInteractionTable and PathwayChildNodeTable classes.
 *
 * @author Benjamin Gross.
 */
public class InteractionSummaryUtils {

    /**
     * Gets Interaction Summary string.
     *
     * @param recordID long
     * @return String
     * @throws DaoException
     * @throws IOException
     * @throws InteractionSummaryException
     * @throws JDOMException
     */
    public static String getInteractionSummary(long recordID)
            throws DaoException, IOException, InteractionSummaryException, JDOMException {
        // get interaction parser
        InteractionParser interactionParser = new InteractionParser(recordID);

        // get physical interacittion
        InteractionSummary interactionSummary = interactionParser.getInteractionSummary();
        return (interactionSummary == null) ? null : createInteractionSummaryString(interactionSummary);
    }

    /**
     * Creates the interaction summary string.
     *
     * @param interactionSummary PhysicalInteractiong
     * @return String
     * @throws DaoException
     * @throws IOException
     * @throws InteractionSummaryException
     * @throws JDOMException
     */
    public static String createInteractionSummaryString(InteractionSummary interactionSummary)
            throws DaoException, IOException, InteractionSummaryException, JDOMException {

        int lc, cnt;
        ArrayList components;
        String summaryString = "";

        // left side
        components = interactionSummary.getLeftSideComponents();
		if (components != null){
			cnt = components.size();
			for (lc = 0; lc < cnt; lc++){
				InteractionSummaryComponent summaryComponent = (InteractionSummaryComponent)components.get(lc);
				summaryString += "<a href=\"record.do?id=" + String.valueOf(summaryComponent.getRecordID()) +
					"\">" + summaryComponent.getName() + "</a>";
				// add location:feature string
				String summaryFeatureString = createSummaryFeatureString(summaryComponent);
				if (summaryFeatureString != null){
					summaryString += summaryFeatureString;
				}
				// add summary detail string - see function definition for more info
				String summaryDetailString = createSummaryDetailString(summaryComponent.getRecordID());
				if (summaryDetailString != null){
					summaryString += summaryDetailString;
				}
				// add separator between participants
				summaryString += (lc < cnt-1) ? createSeparatorString(interactionSummary) : " ";
			}
		}

		// operator
		String operatorString = createOperatorString(interactionSummary);
		if (operatorString != null){
			summaryString += (" " + operatorString + " ");
		}

		// right side
		components = interactionSummary.getRightSideComponents();
		if (components != null){
            cnt = components.size();
            for (lc = 0; lc < cnt; lc++){
                InteractionSummaryComponent summaryComponent = (InteractionSummaryComponent)components.get(lc);
                summaryString += "<a href=\"record.do?id=" + String.valueOf(summaryComponent.getRecordID()) +
                              "\">" + summaryComponent.getName() + "</a>";
                // add location:feature string
				String summaryFeatureString = createSummaryFeatureString(summaryComponent);
				if (summaryFeatureString != null){
					summaryString += summaryFeatureString;
				}
                // add summary detail string - see function definition for more info
				String summaryDetailString = createSummaryDetailString(summaryComponent.getRecordID());
				if (summaryDetailString != null){
					summaryString += summaryDetailString;
				}
                // add separator between participants
                summaryString += (lc < cnt-1) ? createSeparatorString(interactionSummary) : "";
            }
        }

        // outta here
        return summaryString;
    }

    /**
     * Creates operator string for Control or Conversion interaction.
	 * Examples of an operator may be "-->" or "[Activates]"
     *
     * @param interactionSummary InteractionSummary
     * @return String
     */
    public static String createOperatorString(InteractionSummary interactionSummary) {

		// string to return
		String operatorString = null;

		// conversion interaction summary
		if (interactionSummary instanceof ConversionInteractionSummary){
			operatorString = "-->";
		}
		else if (interactionSummary instanceof ControlInteractionSummary){
			String controlType = ((ControlInteractionSummary)interactionSummary).getControlType();
			if (controlType != null){
				BioPaxControlTypeMap controlTypeMap = new BioPaxControlTypeMap();
				operatorString = "[" + controlTypeMap.get(controlType) + "]";
			}
			else{
				operatorString = "[CONTROL-TYPE NOT FOUND]";
			}
		}
		// note physical interactions do not have operators, we do nothing
		//else if (interactionSummary instanceof PhysicalInteractionSummary){
		//}

		// outta here
		return operatorString;
	}

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
     * @throws InteractionSummaryException
     * @throws JDOMException
     */
    public static String createSummaryDetailString(long recordID)
            throws DaoException, IOException, InteractionSummaryException, JDOMException {

        String summaryDetails = getInteractionSummary(recordID);
        return (summaryDetails != null && summaryDetails.length() > 0) ? " (" + summaryDetails + ")" : null;
    }

    /**
     * Creates location:feature string.
     *
     * @param interactionSummaryComponent InteractionSummaryComponent
     * @return String
     */
    public static String createSummaryFeatureString(InteractionSummaryComponent interactionSummaryComponent) {

        // string to return
        String summaryFeatureString = "";

        // get data from component
        String cellularLocation = interactionSummaryComponent.getCellularLocation();
        ArrayList featureList = interactionSummaryComponent.getFeatureList();
        int cnt = featureList.size();

        if (cellularLocation != null && cellularLocation.length() > 0){
            summaryFeatureString = "(" + interactionSummaryComponent.getCellularLocation() + ":";
        }

        // process feature list
        if (cnt > 0){
            if (summaryFeatureString.length() == 0){
                summaryFeatureString = "(:";
            }
            for (int lc = 0; lc < cnt; lc++){
                String feature = (String)featureList.get(lc);
                if (lc == 0){
                    summaryFeatureString += feature;
                }
                else{
                    summaryFeatureString += ", " + feature;
                }
            }

        }

        // cap off the string
        if (summaryFeatureString.length() > 0){
            summaryFeatureString += ")";
        }

        // outta here
        return summaryFeatureString;
    }

    /**
     * Creates a separator string between interactions participants
	 * on one side of an operator.  Currently ' + ' is used for
	 * conversion and control interactions and ' ' for physical interactions.
	 *
     * @param interactionSummary InteractionSummary
     * @return String
     */
    public static String createSeparatorString(InteractionSummary interactionSummary) {

		// string to return
		String separatorString = null;

        if (interactionSummary instanceof PhysicalInteractionSummary){
			separatorString = " ";
		}
		else{
			separatorString = " + ";
		}

		// outta here
		return separatorString;
	}
}
