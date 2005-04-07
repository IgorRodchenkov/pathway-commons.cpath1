package org.mskcc.pathdb.biopax;

import org.jdom.Namespace;

public class RdfConstants {

    /**
     * RDF ID Attribute
     */
    public final static String ID_ATTRIBUTE = "ID";
    
    /**
     * RDF Resource Attribute
     */
    public final static String RESOURCE_ATTRIBUTE = "resource";

    /**
     * RDF Namespace URI
     */
    public final static String RDF_NAMESPACE_URI =
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * RDF Namespace Prefix
     */
    public final static String RDF_NAMESPACE_PREFIX = "rdf";

    /**
     * RDF Namespace Object.
     */
    public final static Namespace RDF_NAMESPACE = Namespace.getNamespace
            (RdfConstants.RDF_NAMESPACE_PREFIX, RdfConstants.RDF_NAMESPACE_URI);
}