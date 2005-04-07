About these documents:

biopax-level1.owl           BioPAX OWL Ontology, Level 1 from biopax.org
biopax-level2.owl           BioPAX OWL Ontology, Level 2, from biopax.org

biopax1_sample1.owl         A "legal" Level 1 Document, from biopax.org
                            File use an import to import the BioPAX Level 1
                            ontology file.  Verified to open in Protege.

biopax1_sample2.owl         An "illegal" Level 1 Document.  This file is
                            identical to biopax1_sample1.owl, except that
                            PHYSICAL-ENTITY element references a non-existent
                            smallMolecule. This error should be caught by
                            BioPaxUtil, and there is a JUnit test to verify it.

biopax1_sample3.owl         An "illegal" Level 1 Document.  This file is
                            identical to biopax1_sample1.owl, except that
                            it contains two redundant RDF IDs.  This error
                            should be caught by BioPAxUtil, and there is
                            a JUnit test to verify it.

biopax1_sample4.owl         An "illegal" Level 1 Document.  This file is
                            identical to biopax1_sample1.owl, except that
                            it references a fictional "GLUE" database, which
                            does not exist in cPath.  This error should be
                            caught by BioPAxUtil, and there is a JUnit test
                            to verify it.