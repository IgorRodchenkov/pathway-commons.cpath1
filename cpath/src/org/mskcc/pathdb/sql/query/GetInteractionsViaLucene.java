/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.sql.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.lucene.LuceneReader;
import org.mskcc.pathdb.lucene.RequestAdapter;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.taglib.Pager;

import java.io.IOException;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
class GetInteractionsViaLucene extends InteractionQuery {
    private String searchTerms;
    private ProtocolRequest request;

    /**
     * Constructor.
     * Only available via Factory Class.
     *
     * @param request ProtocolRequest Object.
     */
    GetInteractionsViaLucene(ProtocolRequest request) {
        this.request = request;
        this.searchTerms = RequestAdapter.getSearchTerms(request);
    }

    /**
     * Executes Query.
     */
    protected XmlAssembly executeSub() throws QueryException,
            IOException, AssemblyException {
        xdebug.logMsg(this, "Getting Interactions via Lucene. "
                + "Using search term(s):  " + searchTerms);
        LuceneReader indexer = new LuceneReader();
        XmlAssembly xmlAssembly;
        Hits hits = executeLuceneSearch(indexer);
        Pager pager = new Pager(request, hits.length());
        long[] cpathIds = extractHits(pager, hits);
        xmlAssembly = createXmlAssembly(cpathIds, hits);
        xmlAssembly.setNumHits(hits.length());
        return xmlAssembly;
    }

    /**
     * Creates XML Assembly.
     */
    private XmlAssembly createXmlAssembly(long[] cpathIds, Hits hits)
            throws AssemblyException {
        XmlAssembly xmlAssembly;
        if (cpathIds != null && cpathIds.length > 0) {
            xmlAssembly = XmlAssemblyFactory.createXmlAssembly(cpathIds,
                    hits.length(), xdebug);
        } else {
            xmlAssembly = XmlAssemblyFactory.createEmptyXmlAssembly(xdebug);
        }
        return xmlAssembly;
    }

    /**
     * Extracts Lucene Hits in Specified Range.
     */
    private long[] extractHits(Pager pager, Hits hits) throws IOException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        long cpathIds[] = new long[size];
        int index = 0;
        xdebug.logMsg(this, "Extracting hits:  " + pager.getStartIndex()
                + " - " + pager.getEndIndex());

        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneConfig.FIELD_INTERACTION_ID);
            cpathIds[index++] = Long.parseLong(field.stringValue());
        }
        return cpathIds;
    }

    /**
     * Executes Lucene Search.
     */
    private Hits executeLuceneSearch(LuceneReader indexer)
            throws QueryException {
        Hits hits = indexer.executeQuery(searchTerms);
        xdebug.logMsg(this, "Total Number of Matching Interactions "
                + "Found:  " + hits.length());
        return hits;
    }
}