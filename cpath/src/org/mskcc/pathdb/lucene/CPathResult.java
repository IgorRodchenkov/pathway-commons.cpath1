package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.model.CPathRecord;

/**
 * Enscapsulates a CPath Record with its Lucene Score.
 *
 * @author Ethan Cerami
 */
public class CPathResult {
    private CPathRecord record;
    private float score;

    /**
     * Gets CPath Record.
     * @return CPath Record.
     */
    public CPathRecord getRecord() {
        return record;
    }

    /**
     * Sets CPath Record.
     * @param record CPath Record.
     */
    public void setRecord(CPathRecord record) {
        this.record = record;
    }

    /**
     * Gets Lucene Score.
     * @return score.
     */
    public float getScore() {
        return score;
    }

    /**
     * Sets Lucene Score.
     * @param score Lucene Score.
     */
    public void setScore(float score) {
        this.score = score;
    }
}