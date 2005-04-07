package org.mskcc.pathdb.model;

/**
 * Enumeration of XML Record Types.
 * The list is currently constrained to:  PSI_MI and BIO_PAX.
 *
 * @author Ethan Cerami
 */
public class XmlRecordType {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     *
     * @param name Type Name.
     */
    private XmlRecordType(String name) {
        this.name = name;
    }

    /**
     * Gets Type Name.
     *
     * @return Type Name.
     */
    public String toString() {
        return name;
    }

    /**
     * Get Type by Type Name.
     *
     * @param typeName Type Name, e.g. "PSI_MI", or "BIOPAX".
     * @return correct XmlRecordType.
     */
    public static XmlRecordType getType(String typeName) {
        if (typeName.equals(PSI_MI.toString())) {
            return PSI_MI;
        } else if (typeName.equals(BIO_PAX.toString())) {
            return BIO_PAX;
        } else {
            throw new IllegalArgumentException("No Matching cPath"
                    + "Record types for:  " + typeName);
        }
    }

    /**
     * XmlRecordType Record Type:  PSI_MI.
     */
    public static final XmlRecordType PSI_MI
            = new XmlRecordType("PSI_MI");

    /**
     * CPath Record Type:  BIO_PAX.
     */
    public static final XmlRecordType BIO_PAX
            = new XmlRecordType("BIO_PAX");

}