package org.mskcc.pathdb.sql.assembly;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;

/**
 * Factory for instantiaing XmlAssembly objects.
 *
 * @author Ethan Cerami.
 */
public class XmlAssemblyFactory {

    /**
     * Gets an XmlAssembly based on specified cPathId.
     *
     * @param cpathId cPathID must refer to an Interaction record.
     * @param xdebug  XDebug Object
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly getXmlAssembly(long cpathId, XDebug xdebug)
            throws AssemblyException {
        try {
            DaoCPath dao = new DaoCPath();
            CPathRecord record = dao.getRecordById(cpathId);
            checkRecordType(record);
            return createAssemblyInstance(record, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException (e);
        }
    }

    /**
     * Gets an XmlAssembly based on specified list of cPathIds.
     *
     * @param cpathIds Each cPathID must refer to an Interaction record.
     * @param xdebug   XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly getXmlAssembly(long cpathIds[], XDebug xdebug)
            throws AssemblyException {
        try {
            ArrayList records = new ArrayList();
            DaoCPath dao = new DaoCPath();
            for (int i = 0; i < cpathIds.length; i++) {
                CPathRecord record = dao.getRecordById(cpathIds[i]);
                checkRecordType(record);
                records.add(record);
            }
            return createAssemblyInstance(records, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException (e);
        }
    }

    /**
     * Gets an XmlAssembly based on specified cPathRecord.
     *
     * @param interaction CPathRecord must contain an Interaction.
     * @param xdebug      XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly getXmlAssembly(CPathRecord interaction,
            XDebug xdebug) throws AssemblyException {
        checkRecordType(interaction);
        return createAssemblyInstance(interaction, xdebug);
    }

    /**
     * Gets an XmlAssembly based on specified list of cPathRecords.
     *
     * @param interactions An ArrayList of CPathRecord Objects.
     *                     Each CPathRecord object must contain an Interaction.
     * @param xdebug       XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly getXmlAssembly(ArrayList interactions,
            XDebug xdebug) throws AssemblyException {
        for (int i = 0; i < interactions.size(); i++) {
            Object object = interactions.get(i);
            if (object instanceof CPathRecord == false) {
                throw new IllegalArgumentException("ArrayList must "
                        + "contain objects of type:  " +
                        CPathRecord.class.getName());
            } else {
                CPathRecord record = (CPathRecord) object;
                checkRecordType(record);
            }
        }
        return createAssemblyInstance(interactions, xdebug);
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param interaction CPathRecord Interaction Object.
     * @return XmlAssembly Object.
     */
    private static XmlAssembly createAssemblyInstance(CPathRecord interaction,
            XDebug xdebug) throws AssemblyException {
        ArrayList records = new ArrayList();
        records.add(interaction);
        return createAssemblyInstance(records, xdebug);
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param interactions ArrayList of CPathRecord Objects.
     * @return XmlAssembly Object.
     */
    private static XmlAssembly createAssemblyInstance(ArrayList interactions,
            XDebug xdebug) throws AssemblyException {
        // TODO:  When we add support for BioPax, we need to update this.
        XmlAssembly xmlAssembly = new PsiAssembly(interactions, xdebug);
        return xmlAssembly;
    }

    /**
     * Confirms that this is an Interaction Record.
     * If this is not an interaction record, an IllegalArgumentException
     * is thrown.
     *
     * @param record CPathRecord.
     */
    private static void checkRecordType(CPathRecord record) {
        if (!record.getType().equals(CPathRecordType.INTERACTION)) {
            throwIllegalArgument(record);
        }
    }

    /**
     * Throws an IllegalArgumentException with detailed error message.
     *
     * @param record CPathRecord.
     */
    private static void throwIllegalArgument(CPathRecord record) {
        throw new IllegalArgumentException("The specified cpathId:  "
                + record.getId() + " points to a record of type:  "
                + record.getType()
                + ".  It must point to a record of type:  "
                + CPathRecordType.INTERACTION.toString()
                + ".");
    }
}