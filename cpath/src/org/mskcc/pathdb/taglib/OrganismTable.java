package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.lucene.OrganismStats;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Organism Data Plus Links.
 *
 * @author Ethan Cerami
 */
public class OrganismTable extends HtmlTable {
    /**
     * URL Parameter for Sort Order.
     */
    public static final String SORT_BY_PARAMETER = "sortBy";

    /**
     * Sort By Species Name.
     */
    public static final String SORT_BY_NAME = "name";

    /**
     * Sort By Number of Interactions.
     */
    public static final String SORT_BY_NUM_INTERACTIONS =
            "numInteractions";

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {
        createHeader("Organism Information");
        startTable();
        startRow();
        append("<TH><A HREF='browse.do?" + SORT_BY_PARAMETER + "="
                + SORT_BY_NAME + "'>Species</A></TH>");
        append("<TH><A HREF='browse.do?" + SORT_BY_PARAMETER + "="
                + SORT_BY_NUM_INTERACTIONS + "'>Number of "
                + "Interactions</A></TH>");
        outputRecords();
        endTable();
    }

    /**
     * Output Organism Records.
     */
    private void outputRecords() throws DaoException, IOException,
            QueryException {
        OrganismStats orgStats = new OrganismStats();
        ArrayList records = null;
        String sortOrder = pageContext.getRequest().
                getParameter(SORT_BY_PARAMETER);
        if (sortOrder != null
                && sortOrder.equals(SORT_BY_NUM_INTERACTIONS)) {
            records = orgStats.getOrganismsSortedByNumInteractions();
        } else {
            records = orgStats.getOrganismsSortedByName();
        }
        if (records.size() == 0) {
            startRow();
            append("<TD COLSPAN=5>No Organism Data Available</TD>");
            endRow();
        } else {
            for (int i = 0; i < records.size(); i++) {
                Organism organism = (Organism) records.get(i);
                startRow(i);

                ProtocolRequest request = new ProtocolRequest();
                request.setOrganism(Integer.toString(organism.getTaxonomyId()));
                request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
                request.setFormat(ProtocolConstants.FORMAT_HTML);
                String url = request.getUri();
                outputDataField("<A HREF='" + url + "'>"
                        + organism.getSpeciesName() + "</A>");
                outputDataField(Integer.toString
                        (organism.getNumInteractions()));
                endRow();
            }
        }
    }
}