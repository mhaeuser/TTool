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

    String patternTaskName;
    String patternChannel;
    String modelTaskName;
    String modelChannelName;
    Boolean isNewPort;
    String newNameOfTaskInModel;
    String newNameOfChannelInModel;
    //Boolean isConf;
    //int auth;

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

    public String getNewNameOfTaskInModel() {
        return newNameOfTaskInModel;
    }

    public String getNewNameOfChannelInModel() {
        return newNameOfChannelInModel;
    }

    /*public Boolean isConfidential() {
        return isConf;
    }

    public int getAuthenticity() {
        return auth;
    }*/

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

    public void setNewNameOfTaskInModel(String _newNameOfTaskInModel) {
        newNameOfTaskInModel = _newNameOfTaskInModel;
    }

    public void setNewNameOfChannelInModel(String _newNameOfChannelInModel) {
        newNameOfChannelInModel = _newNameOfChannelInModel;
    }

    /*public void setIsConfidential(Boolean _isConf) {
        isConf = _isConf;
    }

    public void setAuthenticity(int _auth) {
        auth = _auth;
    }*/


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
