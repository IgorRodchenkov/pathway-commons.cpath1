package org.mskcc.pathdb.model;

/**
 * JavaBean to Encapsulate an Import Record.
 *
 * @author Ethan Cerami
 */
public class InternalLinkRecord {
    private long id;
    private long cpathIdA;
    private long cpathIdB;

    /**
     * No-arg Constructor.
     */
    public InternalLinkRecord () { }

    /**
     * Constructor.
     * @param cpathIdA CPath ID of Entity A.
     * @param cpathIdB CPath ID of Entity B.
     */
    public InternalLinkRecord (long cpathIdA, long cpathIdB) {
        this.cpathIdA = cpathIdA;
        this.cpathIdB = cpathIdB;
    }

    /**
     * Gets Primary Internal Link ID.
     * @return Primary Internal Link ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets Primary Internal Link ID.
     * @param id Primary Internal Link ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets CPath ID of Entity A.
     * @return cpath ID.
     */
    public long getCpathIdA() {
        return cpathIdA;
    }

    /**
     * Sets CPath ID of Entity A.
     * @param cpathIdA cpathID of Entity A.
     */
    public void setCpathIdA(long cpathIdA) {
        this.cpathIdA = cpathIdA;
    }

    /**
     * Gets CPath ID of Entity B.
     * @return cpath ID.
     */
    public long getCpathIdB() {
        return cpathIdB;
    }

    /**
     * Sets CPath ID of Entity B.
     * @param cpathIdB cpathID of Entity B.
     */
    public void setCpathIdB(long cpathIdB) {
        this.cpathIdB = cpathIdB;
    }
}