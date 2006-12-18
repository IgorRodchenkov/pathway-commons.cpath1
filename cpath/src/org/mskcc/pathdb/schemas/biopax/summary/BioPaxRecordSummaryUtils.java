// $Id: BioPaxRecordSummaryUtils.java,v 1.35 2006-12-18 21:44:39 cerami Exp $
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class contains some utility methods
 * used primarily by BioPaxRecord.jsp
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class BioPaxRecordSummaryUtils {
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
     * Sumoylated Keyword.
     */
    private static final String SUMOYLATED = " (Sumoylated)";

    /**
     * Phosphorylation Feature; start pattern.
     */
    private static final String PHOSPHORYLATION_FEATURE = "phosphorylat";

    /**
     * Ubiquitination Feature; start pattern.
     */
    private static final String UBIQUITINATION_FEATURE = "ubiquitinat";

    /**
     * Acetylation Feature; start pattern.
     */
    private static final String ACETYLATION_FEATURE = "acetylat";

    /**
     * Sumoylation Feature; start pattern.
     */
    private static final String SUMOYLATION_FEATURE = "sumoylat";

    /**
     * Names longer than this will be truncated.
     */
    public static final int NAME_LENGTH = 30;

    private static int maxLength = NAME_LENGTH;

    /**
     * No name available.
     */
    private static final String NO_NAME_AVAILABLE = "[No Name Available]";

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

        // build up name
        String name = getBioPaxRecordName(biopaxRecordSummary);
        if (name != null) {
            name += (type != null) ? (" ("
                    + entityTypeMap.get(type) + ")") : "";
        } else {
            // cannot do anything without a name
            return null;
        }

        // get organism
        String organism = biopaxRecordSummary.getOrganism();

        // outta here
        return (organism != null) ? (name + " from " + organism) : name;
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
     * Gets the BioPax Comment String to render.
     *
     * @param biopaxRecordSummary BioPaxRecordSummary Object.
     * @return String HTML String.
     * @throws IllegalArgumentException Illegal Argument Specified.
     */
    public static String getBioPaxRecordCommentString
            (BioPaxRecordSummary biopaxRecordSummary)
            throws IllegalArgumentException {

        // check args
        if (biopaxRecordSummary == null) {
            throw new IllegalArgumentException
                    ("BioPaxRecordSummaryUtils.getBioPaxRecordComment"
                            + "String() argument is null");
        }

        // string to return
        String comment = biopaxRecordSummary.getComment();

        // outta here
        return (comment != null && comment.length() > 0) ? comment : null;
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
        String name = getBioPaxRecordName(component);
        boolean isPhosphorylated = false;
        boolean isUbiquitinated = false;
        boolean isAcetylated = false;
        boolean isSumoylated = false;
        boolean isTransport = false;
        ParticipantSummaryComponent participant = null;

        //  Determine if we have any special cases to deal with.
        if (component instanceof ParticipantSummaryComponent) {
            participant = (ParticipantSummaryComponent) component;
        }

        if (participant != null) {
            isPhosphorylated = hasFeature(participant, PHOSPHORYLATION_FEATURE);
            isUbiquitinated = hasFeature(participant, UBIQUITINATION_FEATURE);
            isAcetylated = hasFeature(participant, ACETYLATION_FEATURE);
            isSumoylated = hasFeature(participant, SUMOYLATION_FEATURE);
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
        String truncatedName = truncateLongName(name, maxLength);
        buf.append(truncatedName);
        String features = getFeatures(isPhosphorylated, isUbiquitinated, isAcetylated,
                isSumoylated);
        buf.append (features);
        String celluarLocation = "";
        if (participant != null) {
            if (participant.getCellularLocation() != null) {
                celluarLocation = participant.getCellularLocation();
                buf.append(" in <SPAN CLASS=popup_organism>" + participant.getCellularLocation()
                        + "</SPAN>");
            }
        }
        int lengthOfHeader = truncatedName.length() + features.length() + celluarLocation.length();
        if (lengthOfHeader < NAME_LENGTH) {
            lengthOfHeader = NAME_LENGTH;
        }
        buf.append("</DIV>");
        buf.append("<DIV CLASS=popup_text>");

        //  Add Synonyms to Pop-Up Box
        addSynonmys(component, lengthOfHeader, buf);

        //  Add Features to Pop-Up Box
        if (participant != null) {
            addFeatures(participant, buf);

            if ((participant.getSynonyms() == null
                    || participant.getSynonyms().size() == 0)
                    && (participant.getFeatureList() == null
                    || participant.getFeatureList().size() == 0)) {
                buf.append("No synonyms or features specified");
            }
            addComponents(participant, lengthOfHeader, buf);
        }

        if (participant == null && (component.getSynonyms() == null
                || component.getSynonyms().size() == 0)) {
            buf.append("No synonyms specified");
        }

        buf.append("</DIV>");
        buf.append("</DIV>");
        buf.append("', FULLHTML, WRAP, CELLPAD, 5, OFFSETY, 0");
        buf.append("); return true;\" onmouseout=\"return nd();\">");

        //  Output Component Name and end A Tag.
        buf.append(truncateLongName(name, maxLength));
        buf.append("</a>");

        //  If this is a transport interaction, show cellular
        //  location explicitly
        if (participant != null) {
            if (isTransport && participant.getCellularLocation() != null) {
                buf.append(" (in " + participant.getCellularLocation() + ")");
            }
        }

        //  If component is phosphorylated, show explicitly
        String featuresStr = getFeatures(isPhosphorylated, isUbiquitinated, isAcetylated,
                isSumoylated);
        buf.append  (featuresStr);
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
    private static void addComponents(ParticipantSummaryComponent participant,
            int lengthOfHeader, StringBuffer buf) {
        ArrayList componentList = participant.getComponentList();
        if (componentList != null && componentList.size() > 0) {
            buf.append("<P>Complex contains the following molecules:");
            buf.append("<UL>");
            for (int i = 0; i < componentList.size(); i++) {
                BioPaxRecordSummary child =
                        (BioPaxRecordSummary) componentList.get(i);
                buf.append("<LI>" + truncateLongName(child.getName(), lengthOfHeader)
                        + "</LI>");
            }
            buf.append("</UL>");
        }
    }

    /**
     * Gets the BioPax Record Name.
     * <p/>
     * (use short name or name or shortest synonyms)
     *
     * @param biopaxRecordSummary BioPaxRecordSummary
     * @return String
     */
    public static String getBioPaxRecordName
            (BioPaxRecordSummary biopaxRecordSummary) {

        // name to return
        String name;

        // try short name
        name = biopaxRecordSummary.getShortName();
        if (name != null && name.length() > 0) {
            return name;
        }

        // try name
        name = biopaxRecordSummary.getName();
        if (name != null && name.length() > 0) {
            if (name.startsWith("UniProt:")) {
                StringTokenizer tokenizer = new StringTokenizer(name, " ");
                return (String) tokenizer.nextElement();
            } else {
                return name;
            }
        }

        // get shortest synonym
        int shortestSynonymIndex = -1;
        List list = biopaxRecordSummary.getSynonyms();
        if (list != null && list.size() > 0) {
            int minLength = -1;
            for (int lc = 0; lc < list.size(); lc++) {
                String synonym = (String) list.get(lc);
                if (minLength == -1 || synonym.length() < minLength) {
                    minLength = synonym.length();
                    shortestSynonymIndex = lc;
                }
            }
        } else {
            return NO_NAME_AVAILABLE;
        }

        // set name to return
        if (shortestSynonymIndex > -1) {
            name = (String) list.get(shortestSynonymIndex);

            // we are using synonym as name, remove synonym from list
            list.remove(shortestSynonymIndex);
            biopaxRecordSummary.setSynonyms(list);
        }

        // outta here
        return name;
    }

    /**
     * Appends Features, such as Phosphorylated, Ubiquitinated or Acetylated.
     *
     * @param phosphorylated isPhosphorylated.
     * @param ubiquitinated
     * @param acetylated
     */
    private static String getFeatures(boolean phosphorylated,
            boolean ubiquitinated,
            boolean acetylated,
            boolean sumoylated) {
        StringBuffer buf = new StringBuffer();
        if (phosphorylated) {
            buf.append(PHOSPHORYLATED);
        }
        if (ubiquitinated) {
            buf.append(UBIQUITINATED);
        }
        if (acetylated) {
            buf.append(ACETYLATED);
        }
        if (sumoylated) {
            buf.append(SUMOYLATED);
        }
        return buf.toString();
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
     * @param buf       HTML StringBuffer Object.
     */
    private static void addFeatures(ParticipantSummaryComponent component,
            StringBuffer buf) {
        if (component.getFeatureList() != null
                && component.getFeatureList().size() > 0) {
            buf.append("<P>Features:<UL>");
            ArrayList featureList = component.getFeatureList();
            for (int i = 0; i < featureList.size(); i++) {
                String feature = (String) featureList.get(i);
                feature = entityFilter(feature);
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
    private static void addSynonmys(BioPaxRecordSummary component, int lengthOfHeader,
            StringBuffer buf) {
        List synList = component.getSynonyms();
        if (synList != null && synList.size() > 0) {
            buf.append("Also known as:  <UL>");
            for (int i = 0; i < synList.size(); i++) {
                String synonym = (String) synList.get(i);
                synonym = truncateLongName(synonym, lengthOfHeader);
                buf.append("<LI>" + synonym + "</LI>");
            }
            buf.append("</UL>");
        }
    }

    /**
     * Replaces Various Characters with their HTML Entities.
     */
    private static String entityFilter(String str) {
        str = str.replaceAll("\'", "&rsquo;");
        str = str.replaceAll("\"", "&quot;");
        return str;
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
        return false;
    }

    /**
     * Determines if the specified component has the specified target feature.
     *
     * @param component ParticipantSummaryComponent Object.
     * @return boolean value.
     */
    private static boolean hasFeature(ParticipantSummaryComponent component,
            String featureTarget) {
        if (component.getFeatureList() != null
                && component.getFeatureList().size() > 0) {
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
