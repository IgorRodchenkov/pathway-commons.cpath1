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
     * @return ArrayList of Task Objects.
     */
    public ArrayList getTaskList() {
        return tasks;
    }

    /**
     * Removes Task at specified index.
     * @param index Index value.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Adds New Task to Task List.
     * @param task Task Object.
     */
    void addTask(Task task) {
        tasks.add(task);
    }
}