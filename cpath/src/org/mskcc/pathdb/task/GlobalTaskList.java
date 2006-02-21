// $Id: GlobalTaskList.java,v 1.7 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.task;

import java.util.ArrayList;

/**
 * Contains a global list of all active/inactive tasks.
 * Uses the Singleton Pattern.
 *
 * @author Ethan Cerami.
 */
public class GlobalTaskList {
    private static GlobalTaskList globalTaskList;
    private ArrayList tasks;

    /**
     * Private Constructor.
     */
    private GlobalTaskList() {
        tasks = new ArrayList();
    }

    /**
     * Gets Instance of the GlobalTaskList Singelton object.
     *
     * @return GlobalTaskList Object.
     */
    public static GlobalTaskList getInstance() {
        if (globalTaskList == null) {
            globalTaskList = new GlobalTaskList();
        }
        return globalTaskList;
    }

    /**
     * Gets list of all active/inactive tasks.
     *
     * @return ArrayList of Task Objects.
     */
    public ArrayList getTaskList() {
        return tasks;
    }

    /**
     * Removes Task at specified index.
     *
     * @param index Index value.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Returns True if One or More Tasks are Currently Active.
     *
     * @return true or false;
     */
    public boolean oneOrMoreTasksAreActive() {
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = (Task) tasks.get(i);
                if (task.isAlive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds New Task to Task List.
     *
     * @param task Task Object.
     */
    void addTask(Task task) {
        tasks.add(task);
    }
}
