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
 * Class PatternConnection
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

 
public class PatternConnection {
    //public static final int NO_AUTHENTICITY = 0; 
    //public static final int WEAK_AUTHENTICITY = 1; 
    //public static final int STRONG_AUTHENTICITY = 2;
    public final static String PORT_CONNECTION_SEPARATOR = " <-> ";
    public final static String NEW_PORT_OPTION = " New port";
    public final static String TASK_CHANNEL_SEPARATOR = "::";

    private String patternTaskName;
    private String patternChannel;
    private String modelTaskName;
    private String modelChannelName;
    private Boolean isNewPort;

    public PatternConnection(String patternTaskName, String patternChannel, String modelTaskName, String modelChannelName, Boolean isNewPort) {
        this.patternTaskName = patternTaskName;
        this.patternChannel = patternChannel;
        this.modelTaskName = modelTaskName;
        this.modelChannelName = modelChannelName;
        this.isNewPort = isNewPort;
    }

    public String getPatternTaskName() {
        return patternTaskName;
    }

    public String getPatternChannel() {
        return patternChannel;
    }

    public String getModelTaskName() {
        return modelTaskName;
    }

    public String getModelChannelName() {
        return modelChannelName;
    }

    public Boolean isNewPort() {
        return isNewPort;
    }

    public void setPatternTaskName(String _patternTaskName) {
        patternTaskName = _patternTaskName;
    }

    public void setPatternChannel(String _patternChannel) {
        patternChannel = _patternChannel;
    }

    public void setModelTaskName(String _modelTaskName) {
        modelTaskName = _modelTaskName;
    }

    public void setModelChannelName(String _modelChannelName) {
        modelChannelName = _modelChannelName;
    }

    public void setIsNewPort(Boolean _isNewPort) {
        isNewPort = _isNewPort;
    }

    public String getStringDisplay() {
        if (isNewPort) {
            return (patternTaskName + TASK_CHANNEL_SEPARATOR + patternChannel + PORT_CONNECTION_SEPARATOR + modelTaskName + TASK_CHANNEL_SEPARATOR+ modelChannelName + NEW_PORT_OPTION);
        } else {
            return (patternTaskName + TASK_CHANNEL_SEPARATOR + patternChannel + PORT_CONNECTION_SEPARATOR + modelTaskName + TASK_CHANNEL_SEPARATOR+ modelChannelName);
        }
        
    }
    
    public boolean isANewChannelRequired(List<PatternConnection> patternConnectionList, LinkedHashMap<String, TaskPattern> patternTasks, List<PatternCloneTask> patternCloneTasks) {
        if (this.isNewPort()) {
            return true;
        } else {
            List<String> similarTasks = new ArrayList<String>();
            for (PatternCloneTask patternCloneTask : patternCloneTasks) {
                if (patternCloneTask.getClonedTask().equals(this.getModelTaskName())) {
                    for (PatternCloneTask patternCloneTask0 : patternCloneTasks) {
                        if (patternCloneTask.getTaskToClone().equals(patternCloneTask0.getTaskToClone())) {
                            similarTasks.add(patternCloneTask0.getClonedTask());
                        }
                    }
                    similarTasks.add(patternCloneTask.getTaskToClone());
                }
            }
            for (PatternConnection patternConnection: patternConnectionList) {
                if (patternConnection != this && this.getModelChannelName().equals(patternConnection.getModelChannelName()) && !this.getModelTaskName().equals(patternConnection.getModelTaskName()) && !patternConnection.isNewPort() && !similarTasks.contains(patternConnection.getModelTaskName())) {
                    for (PortTaskJsonFile portTaskJsonFile : patternTasks.get(this.getPatternTaskName()).getExternalPorts()) {
                        if (this.getPatternChannel().equals(portTaskJsonFile.getName()) && portTaskJsonFile.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}