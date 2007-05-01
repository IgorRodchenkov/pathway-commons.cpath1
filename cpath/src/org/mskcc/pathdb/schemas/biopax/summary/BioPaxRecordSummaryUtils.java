// $Id: BioPaxRecordSummaryUtils.java,v 1.49 2007-05-01 15:29:59 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * This class contains some utility methods
 * used primarily by BioPaxRecord.jsp
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class BioPaxRecordSummaryUtils {
    /**
     * Names longer than this will be truncated.
     */
    public static final int NAME_LENGTH = 30;

    private static int maxLength = NAME_LENGTH;
    private static Logger log = Logger.getLogger(BioPaxRecordSummaryUtils.class);

    /**
     * Gets the BioPax Header String to render.
     *
     * @param biopaxRecordSummary BioPaxRecordSummary Object.
     * @return String HTML String.
     * @throws IllegalArgumentException Illegal Argument Specified.
     */
    public static String getBioPaxRecordHeaderString
            (BioPaxRecordSummary biopaxRecordSummary)
            throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null) {
            throw new IllegalArgumentException
                    ("BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString() "
                            + "argument is null");
        }

        // used to make type more readable
        BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();

        // get type
        String type = biopaxRecordSummary.getType();

        // build up label
        String label = biopaxRecordSummary.getLabel();
        if (label != null) {
            if (type != null) {
                label = entityTypeMap.get(type) + ":  " + label;
            }
        } else {
            // cannot do anything without a label
            return null;
        }

        // outta here
        return label;
    }

    /**
     * Gets the BioPax Data Source String to render.
     *
     * @param biopaxRecordSummary BioPaxRecordSummary Object.
     * @return String HTML String.
     * @throws IllegalArgumentException Illegal Argument Specified.
     */
    public static String getBioPaxRecordDataSourceString
            (BioPaxRecordSummary biopaxRecordSummary)
            throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null) {
            throw new IllegalArgumentException
                    ("BioPaxRecordSummaryUtils.getBioPaxRecordDataSource"
                            + "String() argument is null");
        }

        // string to return
        String dataSource = biopaxRecordSummary.getDataSource();

        // outta here
        return (dataSource != null && dataSource.length() > 0)
                ? dataSource : null;
    }

    /**
     * Gets the BioPax Availability String to render.
     *
     * @param biopaxRecordSummary BioPaxRecordSummary Object.
     * @return String HTML String.
     * @throws IllegalArgumentException Illegal Argument Specified.
     */
    public static String getBioPaxRecordAvailabilityString
            (BioPaxRecordSummary biopaxRecordSummary)
            throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null) {
            throw new IllegalArgumentException
                    ("BioPaxRecordSummaryUtils.getBioPaxRecord"
                            + "AvailabilityString() argument is null");
        }

        // string to return
        String availability = biopaxRecordSummary.getAvailability();

        // outta here
        return (availability != null && availability.length() > 0)
                ? availability : null;
    }

    /**
     * Creates an HTML Link to the Specified BioPaxRecordSummary Object.
     *
     * @param entitySummary      ParticipantSummaryComponent Object.
     * @param interactionSummary Interaction Summary Object.
     * @return HTML String.
     */
    public static String createEntityLink(BioPaxRecordSummary entitySummary,
            InteractionSummary interactionSummary) {
        return createComponentLink(entitySummary, interactionSummary);
    }

    /**
     * Creates an HTML Link to the Specified BioPaxRecordSummary Object.
     *
     * @param entitySummary ParticipantSummaryComponent Object.
     * @return HTML String.
     */
    public static String createEntityLink(BioPaxRecordSummary entitySummary) {
        return createComponentLink(entitySummary, null);
    }

    /**
     * Creates an HTML Link to the Specified BioPaxRecordSummary Object.
     *
     * @param entitySummary ParticipantSummaryComponent Object.
     * @param max max length of name
     * @return HTML String.
     */
    public static String createEntityLink(BioPaxRecordSummary entitySummary, int max) {
        maxLength = max;
        String html = createComponentLink(entitySummary, null);
        maxLength = NAME_LENGTH;
        return html;
    }

    /**
     * Creates an HTML Link to the Specified Component Object.
     *
     * @param component          ParticipantSummaryComponent Object.
     * @param interactionSummary Interaction Summary Object.
     * @return HTML String.
     */
    private static String createComponentLink(BioPaxRecordSummary component,
            InteractionSummary interactionSummary) {
        String label = component.getLabel();
        boolean isTransport = false;
        ParticipantSummaryComponent participant = null;

        //  Determine if we have any special cases to deal with.
        if (component instanceof ParticipantSummaryComponent) {
            participant = (ParticipantSummaryComponent) component;
        }

        if (participant != null) {
            if (interactionSummary != null) {
                isTransport = isTransport(interactionSummary);
            }
        }

        //  Start HTML A Link Tag.
        StringBuffer buf = new StringBuffer("<a href=\"record2.do?id="
                + component.getRecordID());

        //  Create JavaScript for MouseOver Pop-Up Box
        buf.append("\" onmouseover=\"return overlib('");

        //  Create Header for Pop-Up Box
        buf.append("<DIV CLASS=popup>");
        buf.append("<DIV CLASS=popup_caption>");
        String truncatedName = truncateLongName(label, maxLength);
        buf.append(truncatedName);
        String celluarLocation = "";
        if (participant != null) {
            if (participant.getCellularLocation() != null) {
                celluarLocation = participant.getCellularLocation();
                buf.append(" in <SPAN CLASS=popup_organism>" + participant.getCellularLocation()
                        + "</SPAN>");
            }
        }
        int lengthOfHeader = truncatedName.length() + celluarLocation.length();
        if (lengthOfHeader < NAME_LENGTH) {
            lengthOfHeader = NAME_LENGTH;
        }
        buf.append("</DIV>");
        buf.append("<DIV CLASS=popup_text>");

        StringBuffer detailsBuf = new StringBuffer();
        //  Add Synonyms to Pop-Up Box
        addSynonyms(component, lengthOfHeader, detailsBuf);

        //  Add Features to Pop-Up Box
        if (participant != null) {
            // commented out for now;  sequence features are no longer shown in pop-up box
            // addFeatures(participant, detailsBuf);
        }
        addComponents(component, lengthOfHeader, detailsBuf);

        if (detailsBuf.length() == 0) {
            buf.append("No synonyms specified");
        } else {
            buf.append(detailsBuf.toString());
        }

        buf.append("</DIV>");
        buf.append("</DIV>");
        buf.append("', FULLHTML, WRAP, CELLPAD, 5, OFFSETY, 0");
        buf.append("); return true;\" onmouseout=\"return nd();\">");

        //  Output Component Name and end A Tag.
        buf.append(truncateLongName(label, maxLength));
        buf.append("</a>");

        //  If this is a transport interaction, show cellular
        //  location explicitly
        if (participant != null) {
            if (isTransport && participant.getCellularLocation() != null) {
                buf.append(" (in " + participant.getCellularLocation() + ")");
            }
        }

        //  Output features next to component name
        if (participant != null) {
            String featuresStr = getFeatures(participant);
            buf.append  (featuresStr);
        }
        return buf.toString();
    }

    /**
     * Adds SubComponents of the current Node.
     * This is currently only applied when we are dealing with Complexes,
     * but this might become more general in the future.
     *
     * @param participant ParticipantSummaryComponent Object.
     * @param buf         StringBuffer Object.
     */
    private static void addComponents(BioPaxRecordSummary participant,
            int lengthOfHeader, StringBuffer buf) {
        ArrayList componentList = participant.getComponentList();
        if (componentList != null && componentList.size() > 0) {
            buf.append("<P>Complex contains the following molecules:");
            buf.append("<UL>");
            for (int i = 0; i < componentList.size(); i++) {
                BioPaxRecordSummary child =
                        (BioPaxRecordSummary) componentList.get(i);
                buf.append("<LI>" + truncateLongName(child.getLabel(), lengthOfHeader)
                        + "</LI>");
            }
            buf.append("</UL>");
        }
    }

    /**
     * Automatically Truncates Long Names.
     *
     * @param name Name.
     * @return Truncated name.
     */
    private static String truncateLongName(String name, int nameLength) {
        if (name != null) {
            if (name.length() > nameLength) {
                name = name.substring(0, nameLength - 3) + "...";
            }
        }
        return entityFilter(name);
    }

    /**
     * Adds Feature List.
     *
     * @param component ParticipantSummaryComponent Object.
     */
    private static String getFeatures(ParticipantSummaryComponent component) {
        StringBuffer buf = new StringBuffer();
        if (component.getFeatureList() != null
                && component.getFeatureList().size() > 0) {
            buf.append (" (");
            ArrayList<BioPaxFeature> featureList = component.getFeatureList();

            //  First categorize all features, based on feature term
            HashMap featureMap = categorizeFeatures(featureList);

            //  Then, output all features w/ location(s)
            Set termSet = featureMap.keySet();
            Iterator iterator = termSet.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String term = (String) iterator.next();
                ArrayList tempList = (ArrayList) featureMap.get(term);

                if (tempList.size() > 1) {
                    buf.append(tempList.size()+"-");
                }
                buf.append(term);

                //  output location(s) in superscript.
                StringBuffer locBuffer = new StringBuffer();
                for (int j=0; j<tempList.size(); j++) {
                    BioPaxFeature feature = (BioPaxFeature) tempList.get(j);
                    if (feature.getIntervalBegin() != null
                            && feature.getIntervalEnd() != null) {
                        locBuffer.append (" " + feature.getIntervalBegin()
                            + " - " + feature.getIntervalEnd());
                    } else if (feature.getPosition() != null) {
                        locBuffer.append(" " + feature.getPosition());
                    }
                }
                if (locBuffer.length() > 0) {
                    buf.append("<sup>");
                    buf.append(locBuffer.toString());
                    buf.append("</sup>");
                }
                if (i < featureMap.size() -1) {
                    buf.append (", ");
                }
                i++;
            }
            buf.append (")");
        }
        return buf.toString();
    }

    private static HashMap categorizeFeatures (ArrayList<BioPaxFeature> featureList) {
        HashMap featureMap = new HashMap();
        //  Categorize list by feature terms
        for (int i = 0; i < featureList.size(); i++) {
            BioPaxFeature feature = featureList.get(i);
            String term = feature.getTerm();
            if (featureMap.containsKey(term)) {
                ArrayList tempList = (ArrayList) featureMap.get(term);
                tempList.add(feature);
            } else {
                ArrayList tempList = new ArrayList();
                tempList.add(feature);
                featureMap.put(term, tempList);
            }
        }
        return featureMap;
    }

    /**
     * Adds Feature List.
     *
     * @param component ParticipantSummaryComponent Object.
     * @param buf       HTML StringBuffer Object.
     */
    private static void addFeatures(ParticipantSummaryComponent component,
            StringBuffer buf) {
        if (component.getFeatureList() != null
                && component.getFeatureList().size() > 0) {
            buf.append("Sequence Features:  <ul>");
            ArrayList<BioPaxFeature> featureList = component.getFeatureList();
            for (int i = 0; i < featureList.size(); i++) {
                BioPaxFeature feature = featureList.get(i);
                String featureTerm = feature.getTerm();
                buf.append("<li>" + featureTerm);
                if (feature.getIntervalBegin() != null
                        && feature.getIntervalEnd() != null) {
                    buf.append (" @location:  " + feature.getIntervalBegin()
                        + " - " + feature.getIntervalEnd());
                }
                else if (feature.getPosition() != null) {
                    buf.append(" @location:  " + feature.getPosition() + "</li>");
                }
            }
            buf.append("</ul>");
        }
    }

    /**
     * Adds Synonym List.
     *
     * @param component ParticipantSummaryComponent Object.
     * @param buf       HTML StringBuffer Object.
     */
    private static void addSynonyms (BioPaxRecordSummary component, int lengthOfHeader,
            StringBuffer buf) {
        List synList = component.getSynonyms();
        if (synList != null && synList.size() > 0) {
            buf.append("Also known as:  <ul>");
            for (int i = 0; i < synList.size(); i++) {
                String synonym = (String) synList.get(i);
                synonym = truncateLongName(synonym, lengthOfHeader);
                buf.append("<li>" + synonym + "</li>");
            }
            buf.append("</ul>");
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

        //  Add special case handling.
        //  If we have a Biochemical reaction with left/right, and the left/right refer
        //  to different cellular locations, we assume this is a transport reaction too.
        if (summary instanceof ConversionInteractionSummary) {
            ConversionInteractionSummary conversionSummary = (ConversionInteractionSummary)
                    summary;
            List leftSide = conversionSummary.getLeftSideComponents();
            List rightSide = conversionSummary.getRightSideComponents();
            if (leftSide != null && rightSide != null) {
                if (leftSide.size() == 1 && rightSide.size() ==1) {
                    ParticipantSummaryComponent leftComponent = (ParticipantSummaryComponent)
                            leftSide.get(0);
                    ParticipantSummaryComponent rightComponent = (ParticipantSummaryComponent)
                            rightSide.get(0);
                    String leftLocation = leftComponent.getCellularLocation();
                    String rightLocation = rightComponent.getCellularLocation();
                    if (leftLocation != null && rightLocation != null) {
                        if (!leftLocation.toLowerCase().equals(rightLocation.toLowerCase())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Replaces Various Characters with their HTML Entities.
     */
    private static String entityFilter(String str) {
        if (str != null) {
            str = str.replaceAll("\'", "&rsquo;");
            str = str.replaceAll("\"", "&quot;");
        }
        return str;
    }
}
