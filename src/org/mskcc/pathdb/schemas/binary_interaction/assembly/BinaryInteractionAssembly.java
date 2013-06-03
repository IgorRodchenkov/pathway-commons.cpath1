// $Id: BinaryInteractionAssembly.java,v 1.2 2009-05-17 23:40:32 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2008 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
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
package org.mskcc.pathdb.schemas.binary_interaction.assembly;

// imports
import java.util.List;
import java.io.IOException;

/**
 * Interface to BinaryInteractionAssembly.
 *
 * @author Benjamin Gross.
 */
public interface BinaryInteractionAssembly {

    /**
     * Gets binary interactions string.
     *
     * @return String
	 * @throws IOException
     */
    public String getBinaryInteractionString() throws IOException;

    /**
     * Our implementation of BinaryInteractionAssembly.getBinaryInteractionString().
     *
     * @return List<String> - size 2, first string is edges, second string is nodes
	 * @throws IOException
     */
    public List<String> getExtendedBinaryInteractionStrings() throws IOException;
}