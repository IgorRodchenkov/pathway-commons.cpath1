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
        assertEquals("interaction_type:Two Hybrid",
                terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE);
        request.setQuery("Two Hybrid");
        request.setOrganism("homo sapiens");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("+(interaction_type:Two Hybrid) "
                + "+organism:homo sapiens", terms);

        request = new ProtocolRequest();
        request.setCommand(ProtocolConstants.COMMAND_GET_BY_PMID);
        request.setQuery("1122334455");
        terms = RequestAdapter.getSearchTerms(request);
        assertEquals("pmid:1122334455", terms);
    }
}
