// $Id: ControlInteractionSummary.java,v 1.12 2006-03-02 17:38:54 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// package
package org.mskcc.pathdb.schemas.biopax.summary;

// imports

import java.util.ArrayList;

/**
 * This class contains the information
 * needed to construct a control interaction summary string.
 *
 * @author Benjamin Gross.
 */
public class ControlInteractionSummary extends PhysicalInteractionSummary {

    /**
     * The control type.
     */
    private String controlType;

    /**
     * Our list of controller components.
     */
    private ArrayList controllerComponents = null;

    /**
     * Our list of controlled compontents.
     */
    private ArrayList controlledComponents = null;

    /**
     * Constructor.
     *
     * @param controlType          String
     * @param controllerComponents ArrayList
     * @param controlledComponents ArrayList
     */
    public ControlInteractionSummary(String controlType, ArrayList controllerComponents,
            ArrayList controlledComponents) {

        // init our members
        this.controllerComponents = controllerComponents;
        this.controlledComponents = controlledComponents;
        this.controlType = controlType;
        if (controllerComponents != null) {
            participants.addAll(controllerComponents);
        }
        if (controlledComponents != null) {
            participants.addAll(controlledComponents);
        }
    }

    /**
     * Returns the control type.
     *
     * @return String
     */
    public String getControlType() {
        return controlType;
    }

    /**
     * Returns the ArrayList of Controllers.
     *
     * @return ArrayList of ParticipantSummaryComponents
     */
    public ArrayList getControllers() {
        return controllerComponents;
    }

    /**
     * Returns the ArrayList of Controlled.
     *
     * @return ArrayList of EntitySummary or one of its subclasses
     */
    public ArrayList getControlled() {
        return controlledComponents;
    }
}
