package org.mskcc.pathdb.service;

import org.mskcc.dataservices.core.DataServiceDescription;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.ArrayList;

/**
 * Programmitcally Registers all CPath Data Services.
 *
 * @author Ethan Cerami
 */
public class RegisterCPathServices {
    private static boolean registrationComplete = false;

    /**
     * Registers all CPath Data Services.
     *
     * @throws DataServiceException Error Registering Services.
     */
    public static void registerServices() throws DataServiceException {
        if (!registrationComplete) {
            DataServiceFactory factory = DataServiceFactory.getInstance();
            ArrayList services = getServices();
            for (int i = 0; i < services.size(); i++) {
                DataServiceDescription ds =
                        (DataServiceDescription) services.get(i);
                factory.registerService(ds);
            }
            registrationComplete = true;
        }
    }

    /**
     * Gets List of all CPath Data Services.
     *
     * @return ArrayList of DataServiceDescription Objects.
     */
    private static ArrayList getServices() {
        String provider = "Memorial Sloan Kettering Cancer Center";
        String version = "1.0";

        ArrayList services = new ArrayList();

        PropertyManager manager = PropertyManager.getInstance();
        String dbLocation = manager.getProperty(PropertyManager.DB_LOCATION);

        String desc = "Reads Interactors from GRID";
        DataServiceDescription dsDesc = new DataServiceDescription
                (CPathConstants.READ_INTERACTORS_FROM_GRID,
                        desc, version, provider,
                        org.mskcc.pathdb.service.ReadInteractorsFromGrid.class,
                        dbLocation);
        services.add(dsDesc);

        desc = "Reads Interactions from GRID";
        dsDesc = new DataServiceDescription
                (CPathConstants.READ_INTERACTIONS_FROM_GRID,
                        desc, version, provider,
                        org.mskcc.pathdb.service.ReadInteractionsFromGrid.class,
                        dbLocation);
        services.add(dsDesc);

        desc = "Writes Interactors to GRID";
        dsDesc = new DataServiceDescription
                (CPathConstants.WRITE_INTERACTORS_TO_GRID,
                        desc, version, provider,
                        org.mskcc.pathdb.service.WriteInteractorsToGrid.class,
                        dbLocation);
        services.add(dsDesc);

        desc = "Writes Interactions to GRID";
        dsDesc = new DataServiceDescription
                (CPathConstants.WRITE_INTERACTIONS_TO_GRID,
                        desc, version, provider,
                        org.mskcc.pathdb.service.WriteInteractionsToGrid.class,
                        dbLocation);
        services.add(dsDesc);
        return services;
    }
}