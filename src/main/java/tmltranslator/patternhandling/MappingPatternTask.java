/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */


package tmltranslator.patternhandling;
/**
 * Class MappingPatternTask
 * 
 * Creation: 25/10/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 25/10/2023
 */
 
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import tmltranslator.*;

 
public class MappingPatternTask {

    public final static String MAP_TASK_IN_SAME_HW_SEPARATOR = " mapped in the same HW as ";
    public final static String MAP_TASK_IN_NEW_HW_SEPARATOR = " mapped in a new HW linked to ";
    public final static int ORIGIN_PATTERN = 0;
    public final static int ORIGIN_CLONE = 1;
    public final static int ORIGIN_MODEL = 2;

    String taskToMapName;
    String sameHwAs;
    int sameHwAsOrigin;
    String busNameForNewHw;
    int origin;

    public MappingPatternTask(String taskToMapName, int origin) {
        this.taskToMapName = taskToMapName;
        this.origin = origin;
    }

    public String getTaskToMapName() {
        return taskToMapName;
    }

    public String getSameHwAs() {
        return sameHwAs;
    }

    public String getBusNameForNewHw() {
        return busNameForNewHw;
    }

    public int getOrigin() {
        return origin;
    }

    public int getSameHwAsOrigin() {
        return sameHwAsOrigin;
    }

    public void setTaskToMapName(String _taskToMapName) {
        taskToMapName = _taskToMapName;
    }

    public void setSameHwAs(String _sameHwAs, int _sameHwAsOrigin) {
        sameHwAs = _sameHwAs;
        sameHwAsOrigin = _sameHwAsOrigin;
        busNameForNewHw = null;
    }

    public void setBusNameForNewHw(String _busNameForNewHw) {
        busNameForNewHw = _busNameForNewHw;
        sameHwAs = null;
        sameHwAsOrigin = -1;
    }

    public void setOrigin(int _origin) {
        origin = _origin;
    }

    public String getStringDisplay() {
        if (sameHwAs != null) {
            return (taskToMapName + MAP_TASK_IN_SAME_HW_SEPARATOR + sameHwAs);
        } else {
            return (taskToMapName + MAP_TASK_IN_NEW_HW_SEPARATOR + busNameForNewHw);
        }
    }

    public static List<MappingPatternTask> getTasksToMap(LinkedHashMap<String, TaskPattern> _allPatternTasks, List<String> _cloneTasks) {
        List<MappingPatternTask> tasksToMap = new ArrayList<MappingPatternTask>();
        for (String taskPattern : _allPatternTasks.keySet()) {
            MappingPatternTask taskToMap = new MappingPatternTask(taskPattern, ORIGIN_PATTERN);
            tasksToMap.add(taskToMap);
        }
        for (String taskClone : _cloneTasks) {
            MappingPatternTask taskToMap = new MappingPatternTask(taskClone, ORIGIN_CLONE);
            tasksToMap.add(taskToMap);
        }
        return tasksToMap;
    }

    public static List<MappingPatternTask> getTasksLeftToMap(List<MappingPatternTask> _mappedTasks, List<MappingPatternTask> _allTasksToMap) {
        List<MappingPatternTask> tasksLeftToMap = new ArrayList<MappingPatternTask>();
        for (MappingPatternTask taskToMap : _allTasksToMap) {
            boolean isLeftToMap = true;
            for (MappingPatternTask mappedTask : _mappedTasks) {
                if (taskToMap.getTaskToMapName().equals(mappedTask.getTaskToMapName())) {
                    isLeftToMap = false;
                    break;
                }
            }
            if (isLeftToMap) {
                tasksLeftToMap.add(taskToMap);
            }
        }
        return tasksLeftToMap;
    }
    
    // public static List<String> getListTasksToMapWith(List<MappingPatternTask> _mappedTasks, List<String> _cloneTasks) {
    //     List<MappingPatternTask> tasksToMap = new ArrayList<MappingPatternTask>();
    //     for (String taskPattern : _allPatternTasks.keySet()) {
    //         MappingPatternTask taskToMap = new MappingPatternTask(taskPattern, ORIGIN_PATTERN);
    //         tasksToMap.add(taskToMap);
    //     }
    //     for (String taskClone : _cloneTasks) {
    //         MappingPatternTask taskToMap = new MappingPatternTask(taskClone, ORIGIN_CLONE);
    //         tasksToMap.add(taskToMap);
    //     }
    //     return tasksToMap;
    // }
}
