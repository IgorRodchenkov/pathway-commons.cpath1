package org.mskcc.pathdb.sql.query;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.util.ArrayList;

public class GetInteractionsByInteractorName extends InteractionQuery {

    /**
     * Constructor.
     * @param interactorName Unique Interactor Name.
     * @throws QueryException Error Performing Query.
     * @throws org.mskcc.dataservices.core.EmptySetException No Results Found.
     */
    public GetInteractionsByInteractorName(String interactorName)
            throws QueryException, EmptySetException {
        logger.info("Executing Interaction Query for:  " + interactorName);
        interactions = new ArrayList();
        try {
            DaoCPath cpath = new DaoCPath();
            CPathRecord record = cpath.getRecordByName(interactorName);
            if (record != null) {
                logger.info("Matching Interactor Found for:  "
                        + record.getDescription());
                entrySet = aggregateXml(record);
                mapToInteractions();
                xml = this.generateXml();
            } else {
                logger.info("No Matching Interactors Found");
                throw new EmptySetException();
            }
        } catch (DaoException e) {
            throw new QueryException("DaoException:  " + e.getMessage(), e);
        } catch (ValidationException e) {
            throw new QueryException("ValidationException:  "
                    + e.getMessage(), e);
        } catch (MarshalException e) {
            throw new QueryException("MarshalException:  " + e.getMessage(), e);
        } catch (MapperException e) {
            throw new QueryException("MapperException:  " + e.getMessage(), e);
        }
    }
}
