package org.mskcc.pathdb.model;

/**
 * Enumeration of CPathRecord Types.
 * This list is currently constrained to:  PHYSICAL_ENTITY, INTERACTION,
 * and PATHWAY.
 *
 * @author Ethan Cerami
 */
public class CPathRecordType {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     * @param name Type Name.
     */
    private CPathRecordType(String name) {
        this.name = name;
    }

    /**
     * Gets Type Name.
     * @return Type Name.
     */
    public String toString() {
        return name;
    }

    /**
     * Get Type by Type Name.
     * @param typeName Type Name, e.g. "PHYSICAL_ENTITY", "INTERACTION"
     * or "PATHWAY".
     * @return correct CPathRecordType.
     */
    public static CPathRecordType getType(String typeName) {
        if (typeName.equals(PHYSICAL_ENTITY.toString())) {
            return PHYSICAL_ENTITY;
        } else if (typeName.equals(INTERACTION.toString())) {
            return INTERACTION;
        } else if (typeName.equals(PATHWAY.toString())) {
            return PATHWAY;
        } else {
            throw new IllegalArgumentException("No Matching cPath"
                    + "Record types for:  " + typeName);
        }
    }

    /**
     * CPath Record Type:  PHYSICAL_ENTITY.
     */
    public static final CPathRecordType PHYSICAL_ENTITY
            = new CPathRecordType("PHYSICAL_ENTITY");

    /**
     * CPath Record Type:  INTERACTION.
     */
    public static final CPathRecordType INTERACTION
            = new CPathRecordType("INTERACTION");

    /**
     * CPath Record Type:  PATHWAY.
     */
    public static final CPathRecordType PATHWAY
            = new CPathRecordType("PATHWAY");

}
