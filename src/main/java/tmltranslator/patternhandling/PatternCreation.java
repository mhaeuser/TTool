package tmltranslator.patternhandling;
/**
 * Class PatternCreation
 * Pattern Creation in separate thread
 * Creation: 18/08/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 18/08/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
public class PatternCreation implements Runnable {
    public final static String MODE_INPUT = "input";
    public final static String MODE_OUTPUT = "output";
    public final static String CHANNEL = "channel";
    public final static String CONFIDENTIALITY = "confidentiality";
    public final static String WITHOUT_CONFIDENTIALITY = "No";
    public final static String WITH_CONFIDENTIALITY = "Yes";
    public final static String AUTHENTICITY = "authenticity";
    public final static String STRONG_AUTHENTICITY = "strong";
    public final static String WEAK_AUTHENTICITY = "weak";
    public final static String WITHOUT_AUTHENTICITY = "No";
    public final static String EVENT = "event";
    public final static String NAME = "name";
    public final static String TYPE = "type";
    public final static String MODE = "mode";
    public final static String TASK = "task";
    public final static String VALUE = "value";
    public final static String EXTERNALPORTS = "externalPorts";
    public final static String INTERNALPORTS = "internalPorts";
    public final static String ATTRIBUTES = "attributes";

	List<String> selectedTasks;
	String patternName;
    String patternsPath;
	TMLMapping<?> tmap;

    public PatternCreation(List<String> _selectedTasks, String _patternName, String _patternsPath, TMLMapping<?> tmap) {
		this.selectedTasks = _selectedTasks;
		this.patternName = _patternName;
		this.patternsPath = _patternsPath;
		this.tmap = tmap;
	}
    
	public void startThread() {
		Thread t = new Thread(this);
		t.start();
		try {
			t.join();
		}
		catch (Exception e) {
			TraceManager.addDev("Error in Pattern Creation Thread");
		}
		return;
	}

    public void run() {
    	TraceManager.addDev("Creating Pattern");
        if (tmap == null) {
            return;
        }

        TMLModeling<?> tmlmNew = tmap.getTMLModeling();
        for (TMLTask task : tmlmNew.getTasks()) {
            String[] taskNameSplit = task.getName().split("__");
            task.setName(taskNameSplit[taskNameSplit.length-1]);
        }
        for (TMLChannel ch : tmlmNew.getChannels()) {
            String[] channelNameSplit = ch.getName().split("__");
            ch.setName(channelNameSplit[channelNameSplit.length-1]);
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            String[] eventNameSplit = evt.getName().split("__");
            evt.setName(eventNameSplit[eventNameSplit.length-1]);
        }
        for (TMLRequest req : tmlmNew.getRequests()) {
            String[] requestNameSplit = req.getName().split("__");
            req.setName(requestNameSplit[requestNameSplit.length-1]);
        }
        if (generateTMLTxt(patternName)) {
            TraceManager.addDev("Done TML generation");
            if (generatePatternFile(patternName)) {
                TraceManager.addDev("Done Pattern JSON File generation");
            } 
        }
        
	}

    @SuppressWarnings("unchecked")
    public boolean generateTMLTxt(String _title) {
        TMLMappingTextSpecification<Class<?>> spec = new TMLMappingTextSpecification<Class<?>>(_title);
        spec.toTextFormat((TMLMapping<Class<?>>) tmap);
        try {
            String pathNewPattern = patternsPath + patternName + "/";
            Files.createDirectories(Paths.get(pathNewPattern));
            spec.saveFile(pathNewPattern, patternName);
        } catch (Exception e) {
            TraceManager.addError("Files could not be saved: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean generatePatternFile(String _title) {
        List<TMLChannel> listExternalChannels = new ArrayList<TMLChannel>();
        List<TMLEvent> listExternalEvents = new ArrayList<TMLEvent>();
        JSONArray listTasks = new JSONArray();
        try {
            FileWriter file = new FileWriter(patternsPath+patternName+"/"+patternName+".json");
            for (String selectedTask1 : selectedTasks) {
                TMLTask task1 = tmap.getTMLModeling().getTMLTaskByName(selectedTask1);
                if (task1 != null) {
                    JSONArray listAttributes = new JSONArray();
                    JSONArray listExternalPorts = new JSONArray();
                    JSONArray listInternalPorts = new JSONArray();
                    JSONObject joTask = new JSONObject();
                    joTask.put(NAME, task1.getName());
                    for(TMLAttribute attrib : task1.getAttributes()) {
                        listAttributes.put(addAttributeInJsonFile(attrib));
                    }
                    joTask.put(ATTRIBUTES, listAttributes);
                    for (int i=0; i < task1.getReadChannels().size(); i++) {
                        for (int j=0 ; j < task1.getReadChannels().get(i).getNbOfChannels(); j++) {
                            Boolean channelCheck = false;
                            TMLChannel ch = task1.getReadChannels().get(i).getChannel(j);
                            if (!listExternalChannels.contains(ch)) {
                                for (String selectedTask2 : selectedTasks) {
                                    if (selectedTask1 != selectedTask2) {
                                        TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                        if (task2 != null) {
                                            channelCheck = isWriteChannelOfTask(ch, task2);
                                            if (channelCheck) {
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!channelCheck) {
                                    listExternalPorts.put(addChannelInJsonFile(ch, task1, MODE_INPUT));
                                    listExternalChannels.add(ch);
                                } else {
                                    listInternalPorts.put(addChannelInJsonFile(ch, task1, MODE_INPUT));
                                }
                            }
                        }
                    }

                    for (int i=0; i < task1.getWriteChannels().size(); i++) {
                        for (int j=0 ; j < task1.getWriteChannels().get(i).getNbOfChannels(); j++) {
                            Boolean channelCheck = false;
                            TMLChannel ch = task1.getWriteChannels().get(i).getChannel(j);
                            if (!listExternalChannels.contains(ch)) {
                                for (String selectedTask2 : selectedTasks) {
                                    if (selectedTask1 != selectedTask2) {
                                        TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                        if (task2 != null) {
                                            channelCheck = isReadChannelOfTask(ch, task2);
                                            if (channelCheck) {
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!channelCheck) {
                                    listExternalPorts.put(addChannelInJsonFile(ch, task1, MODE_OUTPUT));
                                    listExternalChannels.add(ch);
                                } else {
                                    listInternalPorts.put(addChannelInJsonFile(ch, task1, MODE_OUTPUT));
                                }
                            }
                        }
                    }
                    
                    for (int i=0; i < task1.getSendEvents().size(); i++) {
                        TMLEvent event = task1.getSendEvents().get(i).getEvent();
                        Boolean eventCheck = false;
                        if(!listExternalEvents.contains(event)) {
                            for (String selectedTask2 : selectedTasks) {
                                if (selectedTask1 != selectedTask2) {
                                    TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                    if (task2 != null) {
                                        eventCheck = isWaitEventOfTask(event, task2);
                                        if (eventCheck) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!eventCheck) {
                                listExternalPorts.put(addEventInJsonFile(event, task1, MODE_OUTPUT));
                                listExternalEvents.add(event);
                            } else {
                                listInternalPorts.put(addEventInJsonFile(event, task1, MODE_OUTPUT));
                            }
                            if (task1.getSendEvents().get(i).getEvents() != null) {
                                for (int j=0 ; j < task1.getSendEvents().get(i).getEvents().size(); j++) {
                                    eventCheck = false;
                                    event = task1.getSendEvents().get(i).getEvents().get(j);
                                    for (String selectedTask2 : selectedTasks) {
                                        if (selectedTask1 != selectedTask2) {
                                            TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                            if (task2 != null) {
                                                eventCheck = isWaitEventOfTask(event, task2);
                                                if (eventCheck) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!eventCheck) {
                                        listExternalPorts.put(addEventInJsonFile(event, task1, MODE_OUTPUT));
                                        listExternalEvents.add(event);
                                    } else {
                                        listInternalPorts.put(addEventInJsonFile(event, task1, MODE_OUTPUT));
                                    }
                                }
                            }
                        }
                    }


                    for (int i=0; i < task1.getWaitEvents().size(); i++) {
                        TMLEvent event = task1.getWaitEvents().get(i).getEvent();
                        Boolean eventCheck = false;
                        if(!listExternalEvents.contains(event)) {
                            for (String selectedTask2 : selectedTasks) {
                                if (selectedTask1 != selectedTask2) {
                                    TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                    if (task2 != null) {
                                        eventCheck = isSendEventOfTask(event, task2);
                                        if (eventCheck) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!eventCheck) {
                                listExternalPorts.put(addEventInJsonFile(event, task1, MODE_INPUT));
                                listExternalEvents.add(event);
                            }  else {
                                listInternalPorts.put(addEventInJsonFile(event, task1, MODE_INPUT));
                            }
                            if (task1.getWaitEvents().get(i).getEvents() != null) {
                                for (int j=0 ; j < task1.getWaitEvents().get(i).getEvents().size(); j++) {
                                    eventCheck = false;
                                    event = task1.getWaitEvents().get(i).getEvents().get(j);
                                    for (String selectedTask2 : selectedTasks) {
                                        if (selectedTask1 != selectedTask2) {
                                            TMLTask task2 = tmap.getTMLModeling().getTMLTaskByName(selectedTask2);
                                            if (task2 != null) {
                                                eventCheck = isSendEventOfTask(event, task2);
                                                if (eventCheck) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!eventCheck) {
                                        listExternalPorts.put(addEventInJsonFile(event, task1, MODE_INPUT));
                                        listExternalEvents.add(event);
                                    } else {
                                        listInternalPorts.put(addEventInJsonFile(event, task1, MODE_INPUT));
                                    }
                                }
                            }
                        }
                    }
                    joTask.put(EXTERNALPORTS, listExternalPorts);
                    joTask.put(INTERNALPORTS, listInternalPorts);
                    listTasks.put(joTask);
                }       
            }
            file.write(listTasks.toString(1));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /*
        try {
            
            file.write(jo.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } */

        return true;
    }

    JSONObject addChannelInJsonFile(TMLChannel ch, TMLTask task, String mode) {
        JSONObject jo = new JSONObject();
        try {
            jo.put(NAME, ch.getName());
            jo.put(TASK, task.getName());
            jo.put(TYPE, CHANNEL);
            jo.put(MODE, mode);
            jo.put(CONFIDENTIALITY, WITHOUT_CONFIDENTIALITY);
            jo.put(AUTHENTICITY, WITHOUT_AUTHENTICITY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    JSONObject addEventInJsonFile(TMLEvent evt, TMLTask task, String mode) {
        JSONObject jo = new JSONObject();
        try {
            jo.put(NAME, evt.getName());
            jo.put(TASK, task.getName());
            jo.put(TYPE, EVENT);
            jo.put(MODE, mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {
            file.write(jo.toString());
            //file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return jo;
    }

    JSONObject addAttributeInJsonFile(TMLAttribute attrib) {
        JSONObject jo = new JSONObject();
        try {
            jo.put(NAME, attrib.getName());
            jo.put(TYPE, attrib.getType());
            jo.put(VALUE, attrib.getInitialValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    

    List<TMLChannel> getListChannelsBetweenTwoTasks(TMLTask originTask, TMLTask destinationTask) {
        List<TMLChannel> channels = new ArrayList<TMLChannel>();
        if (originTask.getWriteChannels().size() > 0) {
            for (int i=0; i < destinationTask.getReadChannels().size(); i++) {
                for (int j=0 ; j < destinationTask.getReadChannels().get(i).getNbOfChannels(); j++) {
                    for (int k=0; k < originTask.getWriteChannels().size(); k++) {
                        for (int l=0; l < originTask.getWriteChannels().get(k).getNbOfChannels(); l++) {
                            if (originTask.getWriteChannels().get(k).getChannel(l) == destinationTask.getReadChannels().get(i).getChannel(j)) {
                                TMLChannel channel = destinationTask.getReadChannels().get(i).getChannel(j);
                                if (!channels.contains(channel)) {
                                    channels.add(channel);
                                }
                            }
                        }
                    }
                } 
            }
        }
        return channels;
    }

    Boolean isReadChannelOfTask(TMLChannel channel, TMLTask destinationTask) {
        for (int i=0; i < destinationTask.getReadChannels().size(); i++) {
            for (int j=0 ; j < destinationTask.getReadChannels().get(i).getNbOfChannels(); j++) {
                if (channel == destinationTask.getReadChannels().get(i).getChannel(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    Boolean isWriteChannelOfTask(TMLChannel channel, TMLTask originTask) {
        for (int i=0; i < originTask.getWriteChannels().size(); i++) {
            for (int j=0 ; j < originTask.getWriteChannels().get(i).getNbOfChannels(); j++) {
                if (channel == originTask.getWriteChannels().get(i).getChannel(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    Boolean isSendEventOfTask(TMLEvent event, TMLTask originTask) {
        for (int i=0; i < originTask.getSendEvents().size(); i++) {
            if (event == originTask.getSendEvents().get(i).getEvent()) {
                return true;
            }
            if (originTask.getSendEvents().get(i).getEvents() != null) {
                for (int j=0 ; j < originTask.getSendEvents().get(i).getEvents().size(); j++) {
                    if (event == originTask.getSendEvents().get(i).getEvents().get(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    Boolean isWaitEventOfTask(TMLEvent event, TMLTask destinationTask) {
        for (int i=0; i < destinationTask.getWaitEvents().size(); i++) {
            if (event == destinationTask.getWaitEvents().get(i).getEvent()) {
                return true;
            }
            if (destinationTask.getWaitEvents().get(i).getEvents() != null) {
                for (int j=0 ; j < destinationTask.getWaitEvents().get(i).getEvents().size(); j++) {
                    if (event == destinationTask.getWaitEvents().get(i).getEvents().get(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
        
}
