package org.mskcc.pathdb.biopax;

import org.jdom.Namespace;

public class OwlConstants {

    /**
     * OWL Namespace URI.
     */
    public static final String OWL_NAMESPACE_URI =
            "http://www.w3.org/2002/07/owl#";

    /**
     * OWL Namespace Prefix
     */
    public static final String OWL_NAMESPACE_PREFIX = "owl";

    /**
     * RDF Namespace Object.
     */
    public final static Namespace OWL_NAMESPACE = Namespace.getNamespace
            (OWL_NAMESPACE_PREFIX, OWL_NAMESPACE_URI);    
}