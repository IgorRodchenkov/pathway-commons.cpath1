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
package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.mskcc.pathdb.lucene.RequestAdapter;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;

/**
 * Tests the Request Adapter.
 *
 * @author Ethan Cerami
 */
public class TestRequestAdapter extends TestCase {

    /**
     * Tests Request Adapter.
     */
    public void testAdapter() {
        ProtocolRequest request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
        request.setOrganism("562");
        request.setQuery("dna repair");
        String terms = RequestAdapter.getSearchTerms(request);
        assertEquals("+(dna repair) +organism:562", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
        request.setOrganism("562");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("+organism:562", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
        request.setQuery("dna repair");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("dna repair", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
        request.setQuery("dna repair");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("dna repair", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID);
        request.setQuery("12345");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("interactor_id:12345", terms);

        request = new ProtocolRequest();
        request.setCommand
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF);
        request.setQuery("helicase");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("interactor:helicase", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_ORGANISM);
        request.setQuery("562");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("organism:562", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_DATABASE);
        request.setQuery("DIP");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("database:DIP", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE);
        request.setQuery("Two Hybrid");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("experiment_type:Two Hybrid",
                terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE);
        request.setQuery("Two Hybrid");
        request.setOrganism("homo sapiens");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("+(experiment_type:Two Hybrid) "
                + "+organism:homo sapiens", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_PMID);
        request.setQuery("1122334455");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("pmid:1122334455", terms);
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test the RequestAdapter for formulating Lucene queries";
    }
}
