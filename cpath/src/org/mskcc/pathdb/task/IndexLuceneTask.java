package org.mskcc.pathdb.task;

import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Task to Run the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami.
 */
public class IndexLuceneTask extends Task {
    private ProgressMonitor pMonitor;

    /**
     * Constructor.
     * @param verbose Verbose flag.
     */
    public IndexLuceneTask(boolean verbose) {
        super("Run Full Text Indexer");
        this.setVerbose(verbose);
        pMonitor = new ProgressMonitor();
        pMonitor.setCurrentMessage("Running");
    }

    /**
     * Runs the Task.
     */
    public void run() {
        try {
            indexAllPhysicalEntities();
            pMonitor.setCurrentMessage("Indexing Complete -->  Number of "
                    + "Entities Indexed:  " + pMonitor.getCurValue());
        } catch (Exception e) {
            setException(e);
            e.printStackTrace();
        }
    }

    /**
     * Gets the Progress Monitor.
     * @return Progress Monitor object.
     */
    public ProgressMonitor getProgressMonitor() {
        return pMonitor;
    }

    /**
     * Run Full Text Indexing on all Physical Entities.
     * Made public, if you want to run this from the current thread.
     * @throws DaoException Data Accession Exception.
     * @throws IOException Input Output Exception.
     * @throws ImportException Import Exception.
     */
    public void indexAllPhysicalEntities() throws DaoException, IOException,
            ImportException {
        outputMsg("Indexing all Records in Lucene");
        DaoCPath cpath = new DaoCPath();
        pMonitor.setMaxValue(cpath.getNumEntities(CPathRecordType.
                PHYSICAL_ENTITY));
        LuceneIndexer lucene = new LuceneIndexer();
        lucene.initIndex();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from cpath WHERE TYPE=? order by CPATH_ID");
            pstmt.setString(1, CPathRecordType.PHYSICAL_ENTITY.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                indexRecord(cpath, rs, lucene);
            }
            outputMsg("\nIndexing Complete");
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    private void indexRecord(DaoCPath cpath, ResultSet rs, LuceneIndexer lucene)
            throws SQLException, ImportException {
        pMonitor.incrementCurValue();
        CPathRecord record = cpath.extractRecord(rs);
        if (verbose) {
            System.out.print(".");
        }
        lucene.addRecord(record.getName(), record.getDescription(),
                record.getXmlContent(), record.getId());
    }
}