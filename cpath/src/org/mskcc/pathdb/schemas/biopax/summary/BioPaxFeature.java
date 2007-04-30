package org.mskcc.pathdb.schemas.biopax.summary;

/**
 * Encapsulate a BioPAX Feature.
 *
 * @author Ethan Cerami.
 */
public class BioPaxFeature {
    /**
     * Phosphorylated Keyword.
     */
    private static final String PHOSPHORYLATED = "phosphorylated";

    /**
     * Ubiquitinated Keyword.
     */
    private static final String UBIQUITINATED = "ubiquitinated";

    /**
     * Acetylated Keyword.
     */
    private static final String ACETYLATED = "acetylated";

    /**
     * Sumoylated Keyword.
     */
    private static final String SUMOYLATED = "sumoylated";

    /**
     * Phosphorylation Feature; start pattern.
     */
    private static final String PHOSPHORYLATION_FEATURE = "phosph";

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

    private String term;
    private String position;
    private String intervalBegin;
    private String intervalEnd;

    /**
     * Gets the Feature Term (major terms will be normalized)
     * @return feature term.
     */
    public String getTerm () {
        return term;
    }

    /**
     * Sets the Feature Term.
     * @param term feature term.
     */
    public void setTerm (String term) {
        this.term = getNormalizedTerm(term);
    }

    /**
     * Gets sequence position.
     * @return sequence position.
     */
    public String getPosition () {
        return position;
    }

    /**
     * Sets sequence position.
     * @param position sequence position.
     */
    public void setPosition (String position) {
        this.position = position;
    }

    /**
     * Gets begin interval.
     * @return begin interval.
     */
    public String getIntervalBegin () {
        return intervalBegin;
    }

    /**
     * Sets the begin interval.
     * @param intervalBegin begin interval.
     */
    public void setIntervalBegin (String intervalBegin) {
        this.intervalBegin = intervalBegin;
    }

    /**
     * Gets the end interval.
     * @return end interval
     */
    public String getIntervalEnd () {
        return intervalEnd;
    }

    /**
     * Sets the end interval.
     * @param intervalEnd end interval.
     */
    public void setIntervalEnd (String intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    /**
     * Determines if the specified component has the specified target feature.
     *
     */
    private String getNormalizedTerm (String feature) {
        feature = feature.toLowerCase();
        feature = entityFilter(feature);
        if (feature.indexOf(PHOSPHORYLATION_FEATURE) > -1) {
            return PHOSPHORYLATED;
        } else if (feature.indexOf(ACETYLATION_FEATURE) > -1) {
            return ACETYLATED;
        } else if (feature.indexOf(SUMOYLATION_FEATURE) > -1) {
            return SUMOYLATED;
        } else if (feature.indexOf(UBIQUITINATION_FEATURE) > -1) {
            return UBIQUITINATED;
        } else {
            return feature;
        }
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