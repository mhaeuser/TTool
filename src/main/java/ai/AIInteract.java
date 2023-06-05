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


package ai;


import myutil.AIInterfaceException;
import myutil.GraphicLib;
import myutil.TraceManager;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Class AIInteract
 *
 * Creation: 02/06/2023
 * @version 1.0 02/06/2023
 * @author Ludovic APVRILLE
 */


public abstract class AIInteract implements Runnable {

    public static String[] REQUEST_TYPES = {"Simple chat", "Identify blocks"};
    public static String[] SHORT_REQUEST_TYPES = {"sc", "ib"};
    public static String[] REQUEST_CLASSES = {"AIChat", "AIBlock"};

    protected AIChatData chatData;


    public AIInteract(AIChatData _chatData) {
        chatData = _chatData;
    }

    public abstract void internalRequest();
    public abstract Object applyAnswer(Object input);

    public final void makeRequest(String _data) {
        chatData.lastQuestion = _data;
        startRunning();
    }

    public void run() {
        internalRequest();
        stopRunning();
    }

    public void startRunning() {
        if (chatData.feedback != null) {
            chatData.feedback.setRunning(true);
        }
        chatData.t = new Thread(this);
        chatData.t.start();
    }

    public void stopRunning() {
        TraceManager.addDev("stop running");
        if (chatData.feedback != null) {
            chatData.feedback.setRunning(false);
        }
        if (chatData.t != null) {
            chatData.t.interrupt();
        }
        if (chatData.feedback != null) {
            chatData.feedback.setAnswerText(chatData.lastAnswer);
        }
        chatData.t = null;
    }

    // Returns true if there was no problem ; false otherwise
    public boolean makeQuestion(String _question) {
        if (chatData.feedback != null) {
            chatData.feedback.addToChat(_question, true);
        }
        try {
            if (chatData.feedback != null) {
                chatData.feedback.addInformation("Connecting, waiting for answer\n");
            }
            chatData.lastAnswer = chatData.aiinterface.chat(_question, true, true);
        } catch (AIInterfaceException aiie) {
            if (chatData.feedback != null) {
                chatData.feedback.addError(aiie.getMessage());
            }
            return false;
        }
        if (chatData.feedback != null) {
            chatData.feedback.addInformation("Got answer from ai.\n");
            chatData.feedback.addToChat(chatData.lastAnswer, false);
        }

        return true;
    }

    public String extractJSON() {
        if (chatData == null) {
            return null;
        }

        if (chatData.lastAnswer == null) {
            return null;
        }

        int begin =  chatData.lastAnswer.indexOf("{");
        int end =  chatData.lastAnswer.lastIndexOf("}");

        if (begin == -1 || end == -1) {
            return null;
        }

        if (begin > end) {
            return null;
        }

        return chatData.lastAnswer.substring(begin, end+1);

    }



    public static int getIndexOfShortType(String _text) {
        for(int i=0; i<SHORT_REQUEST_TYPES.length; i++) {
            if (SHORT_REQUEST_TYPES[i].compareTo(_text) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static AIInteract getAIFromShortType(int _type, AIChatData _chatData) {
        return getInstance("ai." + REQUEST_CLASSES[_type], _chatData);
    }

    public static AIInteract getInstance(String className, AIChatData chatData) {
        TraceManager.addDev("Looking for class: " + className);
        try {
            Class<?> clazz = Class.forName(className);
            if (AIInteract.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getConstructor(AIChatData.class);
                TraceManager.addDev("Constructor:" + constructor.getName());
                return (AIInteract) constructor.newInstance(chatData);
            }
        } catch (ClassNotFoundException e) {
            TraceManager.addDev("The specified class was not found.");
        } catch (NoSuchMethodException e) {
            TraceManager.addDev("The specified class does not have a constructor with a single AIChatData parameter.");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            TraceManager.addDev("An error occurred during instance creation.");
        }

        return null;
    }
	
    
}
