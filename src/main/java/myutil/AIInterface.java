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


package myutil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class AIInterface
 *
 * Creation: 03/04/2023
 * @version 1.0 03/04/2023
 * @author Ludovic APVRILLE
 */
public class AIInterface {
    public final static String URL_OPENAI_COMPLETION = "https://api.openai.com/v1/chat/completions";
    public final static String MODEL_GPT_35 = "gpt-3.5-turbo";

    private final static String NO_URL = "No URL specified";
    private final static String NO_KEY = "No key specified";
    private final static String NO_AI_MODEL = "No ai model specified (e.g. \"gpt-3.5-turbo\")";
    private final static String CONNECTION_PB = "Connection to server failed";
    private final static String DISCONNECTION_PB = "DisConnection to server failed";

    private  String urlText;
    private  String key;
    private  String aiModel;
    private  HttpURLConnection connection;
    private ArrayList<AIKnowledge> knowledge;

    public AIInterface() {
        knowledge = new ArrayList<>();
    }

    public void clearKnowledge() {
        knowledge.clear();
    }

    public void setURL(String _url) {
        urlText = _url;
    }

    public void setKey(String _key) {
        key = _key;
    }

    public void setAIModel(String _aiModel) {
        aiModel = _aiModel;
    }

    private void connect() throws AIInterfaceException {
        if (urlText == null) {
            throw new AIInterfaceException(NO_URL);
        }

        if (key == null) {
            throw new AIInterfaceException(NO_KEY);
        }

        if (aiModel == null) {
            throw new AIInterfaceException(NO_AI_MODEL);
        }

        try {
            if (connection == null) {
                TraceManager.addDev("Connecting to " + urlText);
                connection = (HttpURLConnection) new URL(urlText).openConnection();
                TraceManager.addDev("Connection to " + urlText + " is opened");
                connection.setRequestMethod("POST");
                //connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Authorization", "Bearer " + key);
                connection.setDoOutput(true);
            }

        } catch (Exception e) {
            disconnect();
            throw new AIInterfaceException(CONNECTION_PB);
        }
        TraceManager.addDev("Connection done with key: " + key);
    }

    private void disconnect() throws AIInterfaceException {
        try {
            connection.disconnect();
        } catch (Exception e) {
            connection = null;
            throw new AIInterfaceException(DISCONNECTION_PB);
        }
        connection = null;
    }

    public String chat(String text, boolean useKnowledgeAsInput, boolean useOuputKnowledge) throws AIInterfaceException {
        connect();
        org.json.JSONObject mainObject = new org.json.JSONObject();
        mainObject.put("model", "gpt-3.5-turbo");
        org.json.JSONArray array = new org.json.JSONArray();
        org.json.JSONObject sub = new org.json.JSONObject();
        sub.put("role", "system");
        sub.put("content", "You are a helpful assistant for system engineering.");
        array.put(sub);
        if (useOuputKnowledge) {
            for(AIKnowledge aik: knowledge) {
                sub = new org.json.JSONObject();
                sub.put("role", "user");
                TraceManager.addDev("Putting user knowledge: " + aik.userKnowledge);
                sub.put("content", aik.userKnowledge);
                array.put(sub);
                sub = new org.json.JSONObject();
                TraceManager.addDev("Putting assistant knowledge: " + aik.assistantKnowledge);
                sub.put("role", "assistant");
                sub.put("content", aik.assistantKnowledge);
                array.put(sub);
            }
        }
        sub = new JSONObject();
        sub.put("role", "user");
        sub.put("content", text);
        array.put(sub);
        mainObject.put("messages", array);

        // Sending JSON
        TraceManager.addDev("Sending: " + mainObject.toString());
        sendJSON(mainObject);

        StringBuilder sb = getAnswer();
        TraceManager.addDev("Got answer");
        JSONObject answerObject = new JSONObject(sb.toString());
        JSONArray answerArray = answerObject.getJSONArray("choices");
        JSONObject answerText = answerArray.getJSONObject(0);
        JSONObject messageText = answerText.getJSONObject("message");
        String aiText = messageText.getString("content");

        if (useOuputKnowledge) {
            AIKnowledge addedKnowledge = new AIKnowledge(text, aiText);
            knowledge.add(addedKnowledge);
        }
        //connection = null;
        disconnect();
        return aiText;
    }

    private void sendJSON(org.json.JSONObject _jsonToBeSent) throws AIInterfaceException {
        if (connection == null) {
            throw new AIInterfaceException(CONNECTION_PB);
        }

        try (OutputStream os = connection.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
            osw.write(_jsonToBeSent.toString());
            osw.flush();
        } catch (Exception e) {
            connection = null;
            throw new AIInterfaceException(e.getMessage());
        }

        /*String formData = URLEncoder.encode(_jsonToBeSent.toString(), StandardCharsets.UTF_8);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(formData);
        } catch (Exception e) {
            connection = null;
            throw new AIInterfaceException(e.getMessage());
        }*/
    }

    private StringBuilder getAnswer() throws AIInterfaceException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println("Line read: " + line);
                response.append(line);
            }
        } catch (Exception e) {
            throw new AIInterfaceException(e.getMessage());
        }
        return response;
    }

    public void addKnowledge(String userKnowledge, String assistantKnowledge) {
        knowledge.add(new AIKnowledge(userKnowledge, assistantKnowledge));
    }

    public ArrayList<AIKnowledge> getKnowledge() {
        return knowledge;
    }

    public class AIKnowledge {

        public AIKnowledge(String _userKnowledge, String _assistantKnowledge) {
            userKnowledge = _userKnowledge;
            assistantKnowledge = _assistantKnowledge;
        }

        public String userKnowledge;
        public String assistantKnowledge;
    }



    
}
