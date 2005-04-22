/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.test.lucene;

import junit.framework.TestCase;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tests the PsiInteractorExtractor.
 *
 * @author Ethan Cerami
 */
public class TestPsiInteractorExtractor extends TestCase {

    /**
     * Tests the Interactor Extractor.
     *
     * @throws Exception All Exceptions.
     */
    public void testExtractor() throws Exception {
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
        EntrySet entrySet = (EntrySet) assembly.getXmlObject();
        PsiInteractorExtractor interactorExtractor = new PsiInteractorExtractor
                (entrySet, "chaperonin +interaction_type:Genetic",
                        new XDebug());
        ArrayList proteins = interactorExtractor.getSortedInteractors();

        assertEquals(1, proteins.size());
        Iterator iterator = proteins.iterator();
        ProteinWithWeight proteinWithWeight = (ProteinWithWeight)
                iterator.next();
        ProteinInteractorType protein = proteinWithWeight.getProtein();
        assertEquals("2", protein.getId());
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can extract interactor data from PSI-MI " +
                "interaction records";
    }
}
