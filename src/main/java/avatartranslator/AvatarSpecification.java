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

package avatartranslator;

import avatartranslator.intboolsolver.AvatarIBSolver;
import myutil.NameChecker;
import myutil.TraceManager;
import myutil.intboolsolver.IBSParamSpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Class AvatarSpecification
 * Avatar specification
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public class AvatarSpecification extends AvatarElement implements IBSParamSpec {

    public final static int UPPAAL_MAX_INT = 32767;

    public static String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ",", "!", "?", "{", "}", "|", "&"};
    private final List<AvatarBlock> blocks;
    private final List<AvatarRelation> relations;
    private final List<AvatarAMSInterface> interfaces;
    /**
     * The list of all library functions that can be called.
     */
    private final List<AvatarLibraryFunction> libraryFunctions;
    private final List<AvatarPragma> pragmas; // Security pragmas
    private final List<String> safetyPragmas;
    private final HashMap<String, String> safetyPragmasRefs;
    private final List<AvatarPragmaLatency> latencyPragmas;
    private final List<AvatarConstant> constants;
    private final boolean robustnessMade = false;
    public List<String> checkedIDs;
    private List<AvatarInterfaceRelation> irelations;
    //private AvatarBroadcast broadcast;
    private String applicationCode;
    private Object informationSource; // Element from which the spec has been built
    
    // For JSON return
    private static ArrayList<String> jsonErrors;


    public AvatarSpecification(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        blocks = new LinkedList<>();
        interfaces = new LinkedList<>();
        relations = new LinkedList<>();
        pragmas = new LinkedList<>();
        constants = new LinkedList<>();
        safetyPragmas = new LinkedList<>();
        safetyPragmasRefs = new HashMap<>();
        latencyPragmas = new LinkedList<>();
        this.constants.add(AvatarConstant.FALSE);
        this.constants.add(AvatarConstant.TRUE);
        checkedIDs = new ArrayList<>();
        this.libraryFunctions = new LinkedList<>();
    }

    public AvatarRelation getAvatarRelationWithBlocks (AvatarBlock block1, AvatarBlock block2, boolean synchronous) {
        for(AvatarRelation rel: relations) {
            if ( (block1 == rel.block1) || (block1 == rel.block2) ) {
                if ( (block2 == rel.block1) || (block2 == rel.block2) ) {
                    if (rel.isAsynchronous() == !synchronous) {
                        return rel;
                    }
                }
            }
        }
        return null;
    }

    /*
    * Typical JSON:
    *
    * AI:
{
"blocks": [
{
"name": "Battery",
"attributes": [
{
 "name": "chargeLevel",
 "type": "int"
},
{
 "name": "capacity",
 "type": "int"
}
],
"methods": [
{
 "name": "loadBattery",
 "parameters": [
  {
   "name": "rechargeTime",
   "type": "int"
  }
 ],
 "returnType": "nothing"
 },
 {
 "name": "unloadBattery",
 "parameters": [
  {
  "name": "dischargeThreshold",
  "type": "int"
  }
 ],
 "returnType": "nothing"
 }
],
"signals": [
{
 "name": "batteryLoaded",
 "parameters": [
  {
   "name": "loadAmount",
   "type": "int"
  }
 ],
 "type": "output"
},
{
 "name": "batteryUnloaded",
 "parameters": [
  {
   "name": "unloadAmount",
   "type": "int"
  }
 ],
 "type": "output"
}
]
},
{
"name": "MainEngine",
"attributes": [
{
 "name": "isTurnedOn",
 "type": "boolean"
},
{
 "name": "currentRpm",
 "type": "int"
}
],
"methods": [
{
 "name": "toggleEngine",
 "parameters": [
 {
  "name": "turnOn",
  "type": "boolean"
 }
],
 "returnType": "nothing"
},
{
 "name": "setRpm",
 "parameters": [
  {
   "name": "rpm",
   "type": "int"
 }
],
 "returnType": "nothing"
}
],
"signals": [
{
 "name": "engineToggled",
 "parameters": [
  {
   "name": "turnOn",
   "type": "boolean"
  }
 ],
 "type": "output"
},
{
 "name": "rpmSet",
 "parameters": [
  {
   "name": "rpm",
   "type": "int"
  }
 ],
 "type": "output"
}
]
},
{
"name": "ElectricEngines",
"attributes": [
{
 "name": "hasPower",
 "type": "boolean"
},
{
 "name": "powerLevel",
 "type": "int"
}
],
"methods": [
{
 "name": "togglePower",
 "parameters": [
  {
   "name": "hasPower",
   "type": "boolean"
  }
 ],
 "returnType": "nothing"
},
{
 "name": "setPowerLevel",
 "parameters": [
 {
  "name": "powerLevel",
  "type": "int"
 }
],
 "returnType": "nothing"
}
],
"signals": [
{
 "name": "powerToggled",
 "parameters": [
  {
   "name": "hasPower",
   "type": "boolean"
  }
 ],
 "type": "output"
},
{
 "name": "powerLevelSet",
 "parameters": [
  {
   "name": "powerLevel",
   "type": "int"
  }
 ],
 "type": "output"
}
]
},
{
"name": "SystemController",
"attributes": [
{
 "name": "batteryRechargeTime",
 "type": "int"
},
{
 "name": "dischargeThreshold",
 "type": "int"
}
],
"methods": [
{
 "name": "combineEngines",
 "parameters": [
  {
   "name": "needMorePower",
   "type": "boolean"
  }
 ],
 "returnType": "nothing"
},
{
 "name": "toggleMainEngine",
 "parameters": [
  {
   "name": "turnOn",
   "type": "boolean"
  }
 ],
 "returnType": "nothing"
},
{
 "name": "stopVehicle",
 "parameters": [],
 "returnType": "nothing"
}
],
"signals": [
{
 "name": "enginesCombined",
 "parameters": [
  {
   "name": "needMorePower",
   "type": "boolean"
  }
 ],
 "type": "output"
},
{
 "name": "mainEngineToggled",
 "parameters": [
  {
   "name": "turnOn",
   "type": "boolean"
  }
 ],
 "type": "output"
},
{
 "name": "vehicleStopped",
 "parameters": [],
 "type": "output"
},
{
 "name": "rechargeBattery",
 "parameters": [
  {
   "name": "rechargeTime",
   "type": "int"
  }
 ],
 "type": "input",
 "communicationType": "synchronous"
},
{
 "name": "unloadBattery",
 "parameters": [
  {
   "name": "dischargeThreshold",
   "type": "int"
  }
 ],
 "type": "input",
 "communicationType": "synchronous"
},
{
 "name": "setMainEngineRpm",
 "parameters": [
  {
   "name": "rpm",
   "type": "int"
  }
 ],
 "type": "input",
 "communicationType": "synchronous"
},
{
 "name": "toggleElectricEnginesPower",
 "parameters": [
 {
  "name": "hasPower",
  "type": "boolean"
  }
 ],
 "type": "input",
 "communicationType": "asynchronous"
},
{
 "name": "setElectricEnginesPowerLevel",
 "parameters": [
  {
   "name": "powerLevel",
   "type": "int"
  }
 ],
 "type": "input",
 "communicationType": "asynchronous"
}
]
}
],
"connections": [
{
 "sourceBlock": "Battery",
 "sourceSignal": "batteryLoaded",
 "destinationBlock": "SystemController",
 "destinationSignal": "rechargeBattery",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "Battery",
 "sourceSignal": "batteryUnloaded",
 "destinationBlock": "SystemController",
 "destinationSignal": "unloadBattery",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "MainEngine",
 "sourceSignal": "engineToggled",
 "destinationBlock": "SystemController",
 "destinationSignal": "mainEngineToggled",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "MainEngine",
 "sourceSignal": "rpmSet",
 "destinationBlock": "SystemController",
 "destinationSignal": "setMainEngineRpm",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "ElectricEngines",
 "sourceSignal": "powerToggled",
 "destinationBlock": "SystemController",
 "destinationSignal": "toggleElectricEnginesPower",
 "communicationType": "asynchronous"
},
{
 "sourceBlock": "ElectricEngines",
 "sourceSignal": "powerLevelSet",
 "destinationBlock": "SystemController",
 "destinationSignal": "setElectricEnginesPowerLevel",
 "communicationType": "asynchronous"
},
{
 "sourceBlock": "SystemController",
 "sourceSignal": "rechargeBattery",
 "destinationBlock": "Battery",
 "destinationSignal": "loadBattery",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "SystemController",
 "sourceSignal": "unloadBattery",
 "destinationBlock": "Battery",
 "destinationSignal": "unloadBattery",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "SystemController",
 "destinationBlock": "MainEngine",
 "destinationSignal": "engineToggled",
 "sourceSignal": "mainEngineToggled",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "SystemController",
 "destinationBlock": "MainEngine",
 "destinationSignal": "rpmSet",
 "sourceSignal": "setMainEngineRpm",
 "communicationType": "synchronous"
},
{
 "sourceBlock": "SystemController",
 "destinationBlock": "ElectricEngines",
 "destinationSignal": "powerToggled",
 "sourceSignal": "toggleElectricEnginesPower",
 "communicationType": "asynchronous"
},
{
 "sourceBlock": "SystemController",
 "destinationBlock": "ElectricEngines",
 "destinationSignal": "powerLevelSet",
 "sourceSignal": "setElectricEnginesPowerLevel",
 "communicationType": "asynchronous"
},
{
 "sourceBlock": "SystemController",
 "destinationBlock": "ElectricEngines",
 "destinationSignal": "powerToggled",
 "sourceSignal": "enginesCombined",
 "communicationType": "asynchronous"
}
]
}
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
     */
    public static AvatarSpecification fromJSON(String _spec, String _name, Object _referenceObject, boolean acceptErrors) {
        if (_spec == null) {
            return null;
        }


        AvatarSpecification spec = new AvatarSpecification(_name, _referenceObject);
        jsonErrors = new ArrayList<>();

        int indexStart = _spec.indexOf('{');
        int indexStop = _spec.lastIndexOf('}');

        if ((indexStart == -1) || (indexStop == -1) || (indexStart > indexStop)) {
            throw new org.json.JSONException("Invalid JSON object (start)");
        }

        _spec = _spec.substring(indexStart, indexStop + 1);

        TraceManager.addDev("Cut spec: " + _spec);

        JSONObject mainObject = new JSONObject(_spec);

        JSONArray blocksA = mainObject.getJSONArray("blocks");

        if (blocksA == null) {
            TraceManager.addDev("No blocks in json");
            return spec;
        }

        for (int i = 0; i < blocksA.length(); i++) {
            JSONObject blockO = blocksA.getJSONObject(i);
            String name = spec.removeSpaces(blockO.getString("name"));
            if (name != null) {
                AvatarBlock newBlock = new AvatarBlock(name, spec, _referenceObject);
                spec.addBlock(newBlock);

                try {
                    JSONArray attributesA = blockO.getJSONArray("attributes");
                    if (attributesA != null) {

                        for (int j = 0; j < attributesA.length(); j++) {
                            String nameA = spec.removeSpaces(attributesA.getJSONObject(j).getString("name"));
                            String typeA = attributesA.getJSONObject(j).getString("type");
                            AvatarAttribute aa = new AvatarAttribute(nameA, AvatarType.getType(typeA), newBlock, _referenceObject);
                            newBlock.addAttribute(aa);
                        }
                    }
                } catch (JSONException je) {
                }

                try {
                    JSONArray methodsA = blockO.getJSONArray("methods");
                    for (int j = 0; j < methodsA.length(); j++) {
                        String nameM = spec.removeSpaces(methodsA.getJSONObject(j).getString("name"));
                        AvatarMethod am = new AvatarMethod(nameM, _referenceObject);
                        JSONArray params = methodsA.getJSONObject(j).getJSONArray("parameters");
                        for (int k = 0; k < params.length(); k++) {
                            String nameA = spec.removeSpaces(params.getJSONObject(k).getString("name"));
                            String typeA = params.getJSONObject(k).getString("type");
                            AvatarAttribute aa = new AvatarAttribute(nameA, AvatarType.getType(typeA), newBlock, _referenceObject);
                            am.addParameter(aa);
                        }
                        String returnT = methodsA.getJSONObject(j).getString("returnType");
                        AvatarType at = AvatarType.getType(returnT);
                        if (at != AvatarType.UNDEFINED) {
                            am.addReturnParameter(new AvatarAttribute("returnType", at, newBlock, _referenceObject));
                        }

                        newBlock.addMethod(am);
                    }
                } catch (JSONException je) {
                }

                try {
                    JSONArray signalS = blockO.getJSONArray("signals");
                    for (int j = 0; j < signalS.length(); j++) {
                        String nameS = signalS.getJSONObject(j).getString("signal");
                        if (nameS.startsWith("input")) {
                            nameS = nameS.substring(5).trim();
                            nameS = "in " + nameS;
                        } else if (nameS.startsWith("output")) {
                            nameS = nameS.substring(6).trim();
                            nameS = "out " + nameS;
                        } else {
                            nameS = nameS.substring(6).trim();
                            nameS = "in " + nameS;
                        }

                        AvatarSignal as = AvatarSignal.isAValidSignalThenCreate(nameS, newBlock);

                        /*JSONArray params = signalS.getJSONObject(j).getJSONArray("parameters");
                        for (int k = 0; k < params.length(); k++) {
                            String nameA = spec.removeSpaces(params.getJSONObject(k).getString("name"));
                            String typeA = params.getJSONObject(k).getString("type");
                            AvatarAttribute aa = new AvatarAttribute(nameA, AvatarType.getType(typeA), newBlock, _referenceObject);
                            as.addParameter(aa);
                        }*/

                        if (as != null) {
                            TraceManager.addDev("Added signal: " + nameS);
                            newBlock.addSignal(as);
                        } else {
                            TraceManager.addDev("Invalid signal: " + nameS);
                        }
                    }
                } catch (JSONException je) {
                }

            }
        }

        // Connecting signals with identical names
        HashSet<AvatarSignal> signalSet = new HashSet<>();
        HashSet<AvatarSignal> toBeRemoved = new HashSet<>();


        // We consider blocks one after the others
        // We find signals with the same name in another block
        // We connect them if not yet connected
        // Signals are updated if their attribute list does not work
        // Non connected signals are finally removed



        for(AvatarBlock block: spec.getListOfBlocks()) {
            for (AvatarSignal outSig: block.getSignals()) {
                if (!(signalSet.contains(outSig))) {
                    if (outSig.isOut()) {
                        // We look for a similar signal but out
                        AvatarSignal inSig = spec.getSignalWithNameAndDirection(outSig.getSignalName(), AvatarSignal.IN);
                        if (inSig == null) {
                            toBeRemoved.add(outSig);
                        } else {
                            if (signalSet.contains(inSig)) {
                                toBeRemoved.add(outSig);
                            } else {


                                if (!outSig.isCompatibleWith(inSig)) {
                                    // inSig parameters are used, and the definition of outSig is changed
                                    outSig.getListOfAttributes().clear();
                                    for(AvatarAttribute aa: inSig.getListOfAttributes()) {
                                        outSig.addParameter(aa.clone());
                                    }
                                }

                                AvatarBlock destB = spec.getBlockWithSignal(inSig);
                                if (destB != null) {
                                    AvatarRelation ar = spec.getAvatarRelationWithBlocks(block, destB, true);

                                    if (ar == null) {
                                        ar = new AvatarRelation("relation", block, destB, _referenceObject);
                                        ar.setAsynchronous(false);
                                        spec.addRelation(ar);
                                    }

                                    // Signals can be connected
                                    signalSet.add(outSig);
                                    signalSet.add(inSig);


                                    // If the signals have the same name and are in the same block ,they are renamed
                                    if ((outSig.getSignalName().compareTo(inSig.getSignalName()) == 0) && (destB == block)) {
                                        outSig.setName(outSig.getSignalName() + "_out");
                                        inSig.setName(inSig.getSignalName() + "_in");
                                    }

                                    ar.addSignals(outSig, inSig);

                                    TraceManager.addDev("Connecting " + outSig.getSignalName() + " to " + inSig.getSignalName());
                                } else {
                                    toBeRemoved.add(outSig);
                                }

                            }

                        }
                    }
                }
            }
        }

        if (!acceptErrors) {
            // Identify in signals that are not connected
            for (AvatarBlock block : spec.getListOfBlocks()) {
                for (AvatarSignal inSig : block.getSignals()) {
                    if (!(signalSet.contains(inSig))) {
                        if (inSig.isIn()) {
                            toBeRemoved.add(inSig);
                            jsonErrors.add("In block " + block.getName() + " signal " + inSig.getSignalName() + " was removed because there is" +
                                    " no correponding output signal with the same name");
                        }
                    }
                }
            }

            for (AvatarSignal as : toBeRemoved) {
                for (AvatarBlock block : spec.getListOfBlocks()) {
                    block.removeAvatarSignal(as);
                }
            }
        }


        /*JSONArray connections = null;
        try {
            connections = mainObject.getJSONArray("connections");
        } catch (JSONException je) {
        }

        if (connections == null) {
            //jsonErrors.add("No connections between blocks");
            TraceManager.addDev("No connections in json");
            return spec;
        }

        for (int i = 0; i < connections.length(); i++) {
            JSONObject blockO = connections.getJSONObject(i);

            if (blockO == null) {
                jsonErrors.add("Invalid connection section in JSON");
                continue;
            }

            String srcBlock = spec.removeSpaces(blockO.getString("block1"));
            String sourceSignal = spec.removeSpaces(blockO.getString("sig1"));
            String dstBlock = spec.removeSpaces(blockO.getString("block2"));
            String destinationSignal = spec.removeSpaces(blockO.getString("sig2"));



            AvatarBlock srcB = spec.getBlockWithName(srcBlock);

            if (srcB == null) {
                jsonErrors.add("Block " + srcBlock + " does not exist");
                TraceManager.addDev("Block " + srcBlock + " does not exist");
                continue;
            }

            AvatarSignal srcSig = srcB.getSignalByName(sourceSignal);

            if (srcSig == null) {
                TraceManager.addDev("Signal added as out signal: " + sourceSignal);
                srcSig = new AvatarSignal(sourceSignal, AvatarSignal.OUT, _referenceObject);
                srcB.addSignal(srcSig);
            }

            if (srcSig.isIn()) {
                jsonErrors.add("Signal " + sourceSignal + " in block: " + srcB.getName() + " should be out");
                TraceManager.addDev("Signal " + sourceSignal + " in block: " + srcB.getName() + " should be out");
                continue;
            }


            AvatarBlock destB = spec.getBlockWithName(dstBlock);

            if (destB == null) {
                jsonErrors.add("Block " + dstBlock + " does not exist");
                TraceManager.addDev("Block " + dstBlock + " does not exist");
                continue;

            }

            AvatarSignal dstSig = destB.getSignalByName(destinationSignal);

            if (dstSig == null) {
                TraceManager.addDev("Signal added as in signal: " + destinationSignal);
                dstSig = new AvatarSignal(destinationSignal, AvatarSignal.IN, _referenceObject);
                destB.addSignal(dstSig);
            }

            if (dstSig.isOut()) {
                jsonErrors.add("Signal " + destinationSignal + " in block: " + destB.getName() + " should be in");
                TraceManager.addDev("Signal " + destinationSignal + " in block: " + destB.getName() + " should be in");
                continue;
            }


            if (!srcSig.isCompatibleWith(dstSig)) {
                jsonErrors.add("Signal " + srcSig.getSignalName() + " of block " + srcB.getName() + " and signal " + dstSig.getSignalName() +
                        " of block " + destB.getName() + " " +
                        "cannot be connected " +
                        "because the parameters of these signals are not equal");
                TraceManager.addDev("Signals " + srcSig + " and " + dstSig + " are not compatible");
                continue;
            }

            //String communicationType = spec.removeSpaces(blockO.getString("communicationType"));
            //boolean synchronous = communicationType.compareTo("synchronous") == 0;
            boolean synchronous = true;

            AvatarRelation ar = spec.getAvatarRelationWithBlocks(srcB, destB, synchronous);

            if (ar == null) {
                ar = new AvatarRelation("relation", srcB, destB, _referenceObject);
                ar.setAsynchronous(!synchronous);
                spec.addRelation(ar);
            }

            ar.addSignals(srcSig, dstSig);

        }*/


        return spec;

    }
    
    public static ArrayList<String> getJSONErrors() {
        return jsonErrors;
    }

    public static String removeSpaces(String _input) {
        return _input.trim().replaceAll(" ", "_");
    }

    public List<AvatarLibraryFunction> getListOfLibraryFunctions() {
        return this.libraryFunctions;
    }

    public void addLibraryFunction(AvatarLibraryFunction libraryFunction) {
        this.libraryFunctions.add(libraryFunction);
    }

    // For code generation
    public void addApplicationCode(String _code) {
        if (_code == null) {
            return;
        }
        if (applicationCode == null) {
            applicationCode = _code;
            return;
        }
        applicationCode += _code + "\n";
    }

    public String getApplicationCode() {
        if (applicationCode == null) {
            return "";
        }
        return applicationCode;
    }

    public boolean hasApplicationCode() {
        if (applicationCode == null) {
            return false;
        }
        return (applicationCode.indexOf("__user_init()") != -1);
    }

    public Object getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(Object o) {
        informationSource = o;
    }

    public List<AvatarBlock> getListOfBlocks() {
        return blocks;
    }

    public List<AvatarAMSInterface> getListOfInterfaces() {
        return interfaces;
    }

    public List<AvatarRelation> getRelations() {
        return relations;
    }

    public List<AvatarPragma> getPragmas() {
        return pragmas;
    }

    public List<String> getSafetyPragmas() {
        return safetyPragmas;
    }

    public List<AvatarPragmaLatency> getLatencyPragmas() {
        return latencyPragmas;
    }

    public List<AvatarConstant> getAvatarConstants() {
        return constants;
    }

    public HashMap<String, String> getSafetyPragmasRefs() {
        return safetyPragmasRefs;
    }

    public int getNbOfASMGraphicalElements() {
        int cpt = 0;
        for (AvatarBlock block : blocks) {
            cpt += block.getNbOfASMGraphicalElements();
        }
        return cpt;
    }

    public boolean isASynchronousSignal(AvatarSignal _as) {
        for (AvatarRelation ar : relations) {
            if (ar.containsSignal(_as)) {
                return !(ar.isAsynchronous());
            }
        }

        return false;
    }

    public AvatarSignal getCorrespondingSignal(AvatarSignal _as) {
        for (AvatarRelation ar : relations) {
            if (ar.containsSignal(_as)) {
                int index = ar.hasSignal(_as);
                return ar.getInSignal(index);
            }
        }
        return null;
    }

    //DG
    public boolean ASynchronousExist() {
        List<AvatarRelation> asynchro = getRelations();

        for (AvatarRelation ar : asynchro)
            if (ar.isAsynchronous())
                return true;

        return false;
    }

    public boolean AMSExist() {
        List<AvatarRelation> ams = getRelations();

        for (AvatarRelation ar : ams)
            if (ar.isAMS())
                return true;

        return false;
    }

    // end DG
    public void addBlock(AvatarBlock _block) {
        blocks.add(_block);
    }

    /*public void addBroadcastSignal(AvatarSignal _as) {
      if (!broadcast.containsSignal(_as)) {
      broadcast.addSignal(_as);
      }
      }

      public AvatarBroadcast getBroadcast() {
      return broadcast;
      }*/

    public void addInterface(AvatarAMSInterface _interface) {
        interfaces.add(_interface);
    }

    public void addRelation(AvatarRelation _relation) {
        relations.add(_relation);
    }

    public void addInterfaceRelation(AvatarInterfaceRelation _irelation) {
        irelations.add(_irelation);
    }

    public void addPragma(AvatarPragma _pragma) {
        pragmas.add(_pragma);
    }

    public void addSafetyPragma(String _pragma, String _refPragmas) {
        safetyPragmas.add(_pragma);
        safetyPragmasRefs.put(_pragma, _refPragmas);
    }

    public void addLatencyPragma(AvatarPragmaLatency _pragma) {
        latencyPragmas.add(_pragma);
    }

    public void addConstant(AvatarConstant _constant) {
        //Only add unique constants
        if (this.getAvatarConstantWithName(_constant.getName()) == null) {
            constants.add(_constant);
        }
    }

    @Override
    public String toString() {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("Blocks:\n");
        //TraceManager.addDev("TS Block");
        for (AvatarBlock block : blocks) {
            sb.append("*** " + block.toString() + "\n");
        }
        //TraceManager.addDev("TS Relations");
        sb.append("\nRelations:\n");
        for (AvatarRelation relation : relations) {
            sb.append("Relation:" + relation.toString() + "\n");
        }
        sb.append("\nPragmas:\n");
        for (AvatarPragma pragma : pragmas) {
            sb.append("Pragma:" + pragma.toString() + "\n");
        }
        for (AvatarConstant constant : constants) {
            sb.append("Constant:" + constant.toString() + "\n");
        }

        //TraceManager.addDev("TS All done");

        return sb.toString();
    }

    public String toShortString() {
        //TraceManager.addDev("To Short String");
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("Blocks:\n");
        for (AvatarBlock block : blocks) {
            sb.append("*** " + block.toShortString() + "\n");
        }
        sb.append("\nRelations:\n");
        for (AvatarRelation relation : relations) {
            sb.append("Relation:" + relation.toString() + "\n");
        }
        /*for (AvatarConstant constant: constants){
            sb.append("Constant:" + constant.toString() + "\n");
        }*/

        return sb.toString();
    }

    public AvatarBlock getBlockWithName(String _name) {
        for (AvatarBlock block : blocks) {
            if (block.getName().compareTo(_name) == 0) {
                return block;
            }
        }

        return null;
    }

    public int getBlockIndex(AvatarBlock _block) {
        int cpt = 0;
        for (AvatarBlock block : blocks) {
            if (block == _block) {
                return cpt;
            }
            cpt++;
        }

        return -1;
    }

    public AvatarBlock getBlockWithSignal(String signalName) {
        for(AvatarBlock b: blocks) {
            if (b.getSignalByName(signalName) != null) {
                return b;
            }
        }
        return null;
    }

    public AvatarBlock getBlockWithSignal(AvatarSignal as) {
        for(AvatarBlock b: blocks) {
            if (b.getSignals().contains(as)){
                return b;
            }
        }
        return null;
    }

    public AvatarSignal getSignalWithNameAndDirection(String name, int direction) {
        for(AvatarBlock block: blocks) {
            for(AvatarSignal as: block.getSignals()) {
                if ((as.getSignalName().compareTo(name) == 0) && (as.getInOut() == direction)) {
                    return as;
                }
            }
        }
        return null;
    }

    public AvatarAMSInterface getAMSInterfaceWithName(String _name) {
        for (AvatarAMSInterface interf : interfaces) {
            if (interf.getName().compareTo(_name) == 0) {
                return interf;
            }
        }

        return null;
    }

    public AvatarConstant getAvatarConstantWithName(String _name) {
        for (AvatarConstant constant : constants) {
            if (constant.getName().compareTo(_name) == 0) {
                return constant;
            }
        }

        return null;
    }

    /* Generates the Expression Solvers, returns the AvatarStateMachineElement
     * containing the errors */
    public List<AvatarStateMachineElement> generateAllExpressionSolvers() {
        List<AvatarStateMachineElement> errors = new ArrayList<>();
        AvatarTransition at;
        boolean returnVal;

        //AvatarExpressionSolver.emptyAttributesMap();
        AvatarIBSolver.clearAttributes();

        for (AvatarBlock block : getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();

            for (AvatarStateMachineElement elt : asm.getListOfElements()) {
                if (elt instanceof AvatarTransition) {
                    at = (AvatarTransition) elt;
                    if (at.isGuarded()) {
                        returnVal = at.buildGuardSolver();
                        if (returnVal == false) {
                            errors.add(at);
                        }
                    }
                    if (at.hasDelay()) {
                        returnVal = at.buildDelaySolver();
                        if (returnVal == false) {
                            errors.add(at);
                        }
                    }
                    for (AvatarAction aa : at.getActions()) {
                        if (aa instanceof AvatarActionAssignment) {
                            returnVal = ((AvatarActionAssignment) aa).buildActionSolver(block);
                            if (returnVal == false) {
                                errors.add(elt);
                            }
                        }
                    }
                } else if (elt instanceof AvatarActionOnSignal) {
                    returnVal = ((AvatarActionOnSignal) elt).buildActionSolver(block);
                    if (returnVal == false) {
                        errors.add(elt);
                    }
                }
            }
        }

        return errors;
    }

    public void removeCompositeStates() {
        for (AvatarBlock block : blocks) {
            //TraceManager.addDev("- - - - - - - - Removing composite states of " + block);
            block.getStateMachine().removeCompositeStates(block);
        }
    }

    public void makeFullStates() {
        for (AvatarBlock block : blocks) {
            block.getStateMachine().makeFullStates(block);
        }
    }

    public void removeRandoms() {
        for (AvatarBlock block : blocks) {
            block.getStateMachine().removeRandoms(block);
        }
    }

    public void removeAllDelays() {
        for (AvatarBlock block : blocks) {
            block.getStateMachine().removeAllDelays();
        }
    }

    public void removeTimers() {
        //renameTimers();

        List<AvatarBlock> addedBlocks = new LinkedList<AvatarBlock>();
        for (AvatarBlock block : blocks) {
            if (block.hasTimerAttribute()) {
                block.removeTimers(this, addedBlocks);
            }
        }

        for (int i = 0; i < addedBlocks.size(); i++) {
            addBlock(addedBlocks.get(i));
        }
    }

    public void removeConstants() {
        for (AvatarBlock block : blocks) {
            block.removeConstantAttributes();
        }
    }

    public void sortAttributes() {
        for (AvatarBlock block : blocks) {
            block.sortAttributes();
        }
    }

//
//    private void renameTimers() {
//        // Check whether timers have the same name in different blocks
//        ArrayList<AvatarAttribute> allTimers = new ArrayList<AvatarAttribute>();
//        for(AvatarBlock block: blocks) {
//            allTimers.clear();
//            block.putAllTimers(allTimers);
//            for(AvatarAttribute att: allTimers) {
//                for(AvatarBlock bl: blocks) {
//                    if (block != bl) {
//                        if (bl.hasTimer(att.getName())) {
//                            // Must change name of timer
//                            TraceManager.addDev("Changing name of Timer:" + att);
//                            att.setName(att.getName() + "__" + block.getName());
//                        }
//                    }
//                }
//            }
//        }
//
//    }

    public void setAttributeOptRatio(int attributeOptRatio) {
        for (AvatarBlock block : blocks) {
            block.setAttributeOptRatio(attributeOptRatio);
        }
    }

    /**
     * Removes all FIFOs by replacing them with
     * synchronous relations and one block per FIFO
     * The size of the infinite fifo is max 1024
     * and min 1
     *
     * @param _maxSizeOfInfiniteFifo : the max size of the infinite fifo
     */
    public void removeFIFOs(int _maxSizeOfInfiniteFifo) {
        List<AvatarRelation> oldOnes = new LinkedList<AvatarRelation>();
        List<AvatarRelation> newOnes = new LinkedList<AvatarRelation>();

        int FIFO_ID = 0;
        for (AvatarRelation ar : relations) {
            if (ar.isAsynchronous()) {
                // Must be removed
                int size = Math.min(_maxSizeOfInfiniteFifo, ar.getSizeOfFIFO());
                //TraceManager.addDev("***************************** Size of FIFO:" + size);
                size = Math.max(1, size);
                FIFO_ID = removeFIFO(ar, size, oldOnes, newOnes, FIFO_ID);
            }
        }

        for (AvatarRelation ar : oldOnes) {
            relations.remove(ar);
        }

        for (AvatarRelation ar : newOnes) {
            relations.add(ar);
        }

    }

    private int removeFIFO(AvatarRelation _ar, int _sizeOfInfiniteFifo, List<AvatarRelation> _oldOnes, List<AvatarRelation> _newOnes, int FIFO_ID) {
        for (int i = 0; i < _ar.nbOfSignals(); i++) {
            //TraceManager.addDev("FIFO for AR: " + _ar.getBlock1().getName() + " --> " + _ar.getBlock2().getName());
            if (_ar.getSignal1(i).isIn()) {
                FIFO_ID = removeFIFO(_ar, _ar.getSignal2(i), _ar.getSignal1(i), _sizeOfInfiniteFifo, _oldOnes, _newOnes, FIFO_ID, _ar.block2,
                        _ar.block1);
            } else {
                FIFO_ID = removeFIFO(_ar, _ar.getSignal1(i), _ar.getSignal2(i), _sizeOfInfiniteFifo, _oldOnes, _newOnes, FIFO_ID, _ar.block1,
                        _ar.block2);
            }
        }
        _oldOnes.add(_ar);
        return FIFO_ID;
    }

    private int removeFIFO(AvatarRelation _ar, AvatarSignal _sig1, AvatarSignal _sig2, int _sizeOfInfiniteFifo, List<AvatarRelation> _oldOnes,
                           List<AvatarRelation> _newOnes, int FIFO_ID, AvatarBlock block1, AvatarBlock block2) {
        // We create the new block, and the new relation towards the new block
        String nameOfBlock = "FIFO_" + _sig1.getName() + "_" + _sig2.getName() + "_" + FIFO_ID;
        AvatarBlock fifoBlock = AvatarBlockTemplate.getFifoBlock(nameOfBlock, this, _ar, _ar.getReferenceObject(), _sig1, _sig2,
                _sizeOfInfiniteFifo, FIFO_ID);
        //fifoBlock.getStateMachine().groupUselessTransitions(fifoBlock);
        blocks.add(fifoBlock);

        // We now need to create the new relation
        AvatarRelation newAR1 = new AvatarRelation("FIFO_write_" + FIFO_ID, block1, fifoBlock, _ar.getReferenceObject());
        newAR1.setAsynchronous(false);
        newAR1.setPrivate(_ar.isPrivate());
        //TraceManager.addDev("FIFO. Connecting " + _sig1.getName() + " to write of FIFO " + fifoBlock.getName());
        newAR1.addSignals(_sig1, fifoBlock.getSignalByName("write"));
        _newOnes.add(newAR1);

        AvatarRelation newAR2 = new AvatarRelation("FIFO_read_" + FIFO_ID, fifoBlock, block2, _ar.getReferenceObject());
        newAR2.setAsynchronous(false);
        newAR2.setPrivate(_ar.isPrivate());
        //TraceManager.addDev("FIFO. Connecting FIFO " + fifoBlock.getName() + " read to " + _sig2.getName());
        newAR2.addSignals(fifoBlock.getSignalByName("read"), _sig2);
        _newOnes.add(newAR2);

        // We also add the query signals to the newAR2 relation


        AvatarSignal queryS = new AvatarSignal("query_FIFO_read_" + FIFO_ID, AvatarSignal.IN, _sig2.getReferenceObject());
        block2.addSignal(queryS);
        AvatarAttribute queryA = new AvatarAttribute("queryA", AvatarType.INTEGER, null, _sig2.getReferenceObject());
        queryS.addParameter(queryA);

        AvatarRelation newAR3 = new AvatarRelation("FIFO_query_" + FIFO_ID, fifoBlock, block2, _ar.getReferenceObject());
        newAR3.setAsynchronous(false);
        newAR3.setPrivate(_ar.isPrivate());
        newAR3.addSignals(fifoBlock.getSignalByName("query"), queryS);
        _newOnes.add(newAR3);

        // Replace query in block2 for _sig2 by readOnSignal with signal queryS. Use the same attribute as before
        block2.replaceQueriesWithReadSignal(_sig2, queryS);


        FIFO_ID++;

        return FIFO_ID;
    }

    public boolean areSynchronized(AvatarSignal as1, AvatarSignal as2) {
        AvatarRelation ar = getAvatarRelationWithSignal(as1);
        if (ar == null) {
            return false;
        }

        int index1 = ar.getIndexOfSignal(as1);
        int index2 = ar.getIndexOfSignal(as2);

        return (index1 == index2);
    }

    public AvatarRelation getAvatarRelationWithSignal(AvatarSignal _as) {
        for (AvatarRelation ar : relations) {
            if (ar.hasSignal(_as) > -1) {
                return ar;
            }
        }
        return null;
    }

    public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
        AvatarStateMachineElement asme;
        for (AvatarBlock block : blocks) {
            asme = block.getStateMachineElementFromReferenceObject(_o);
            if (asme != null) {
                return asme;
            }
        }
        return null;
    }

    public AvatarBlock getBlockFromReferenceObject(Object _o) {
        for (AvatarBlock block : blocks) {
            if (block.containsStateMachineElementWithReferenceObject(_o)) {
                return block;
            }
        }
        return null;
    }

    public AvatarBlock getBlockWithAttribute(String _attributeName) {
        int index;

        for (AvatarBlock block : blocks) {
            index = block.getIndexOfAvatarAttributeWithName(_attributeName);
            if (index > -1) {
                return block;
            }
        }
        return null;
    }

    public void removeElseGuards() {
        for (AvatarBlock block : blocks) {
            removeElseGuards(block.getStateMachine());
        }
        for (AvatarLibraryFunction function : libraryFunctions) {
            removeElseGuards(function.getStateMachine());
        }
    }

    private void removeElseGuards(AvatarStateMachine asm) {
        if (asm == null)
            return;

        for (AvatarStateMachineElement asme : asm.getListOfElements()) {
            if (!(asme instanceof AvatarState))
                continue;

            //TraceManager.addDev("Working with state " + asme.getNiceName());
            for (AvatarStateMachineElement next : asme.getNexts()) {
                if (!(next instanceof AvatarTransition))
                    continue;
                AvatarTransition at = (AvatarTransition) next;
                AvatarGuard ancientGuard = at.getGuard();

                if (ancientGuard == null)
                    continue;

                //TraceManager.addDev("[[[[[[[[[[[[[[[ Guard before: " + ancientGuard.toString() + " type:" + ancientGuard.getClass().toString());
                at.setGuard(ancientGuard.getRealGuard(asme));
                //TraceManager.addDev("]]]]]]]]]]]]]]] Guard after: " + at.getGuard().toString());
            }
        }
    }

    /**
     * Removes all function calls by inlining them.
     */
    public void removeLibraryFunctionCalls() {
        for (AvatarBlock block : this.blocks) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm == null)
                continue;

            asm.removeLibraryFunctionCalls(block);
        }


        //TraceManager.addDev("\n\nNew spec:" + this.toString() + "\n");
    }

    public boolean hasLossyChannel() {
        for (AvatarRelation relation : relations)
            if (relation.isLossy())
                return true;

        return false;
    }

    public void removeEmptyTransitions(boolean _canOptimize) {
        for (AvatarBlock block : this.blocks) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null)
                asm.removeEmptyTransitions(block, _canOptimize);
        }
    }

    public void groupUselessTransitions() {
        for (AvatarBlock block : this.blocks) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null)
                asm.groupUselessTransitions(block);
        }
    }

    public void makeRobustness() {
        //TraceManager.addDev("Make robustness");
        if (robustnessMade) {
            return;
        }

        /*robustnessMade = true;

          TraceManager.addDev("Testing lossy channels");

          if (hasLossyChannel()) {
          TraceManager.addDev("Making robustness");
          int idstate = 0;
          for(AvatarBlock block: blocks) {
          idstate = block.getStateMachine().makeMessageLostRobustness(idstate);
          }

          /*AvatarBlock ab = new AvatarBlock("Robustness__", this.getReferenceObject());
          addBlock(ab);
          AvatarMethod am = new AvatarMethod("messageLost", null);
          ab.addMethod(am);
          AvatarStateMachine asm = ab.getStateMachine();
          AvatarStartState ass = new AvatarStartState("StartState", null);
          asm.addElement(ass);
          asm.setStartState(ass);
          AvatarTransition at = new AvatarTransition("Transition", null);
          asm.addElement(at);
          ass.addNext(at);
          AvatarState state = new AvatarState("MainState", null);
          asm.addElement(state);
          at.addNext(state);

          // Parsing all state machines to add robustness
          AvatarStateMachine sm;
          AvatarActionOnSignal aaos;
          AvatarSignal as;
          AvatarState state0;
          int i;

          for(AvatarRelation ar: relations) {
          if (ar.isAsynchronous() && ar.isLossy()) {
          // Modify the relation
          ar.makeRobustness();
          for(i=0; i<ar.nbOfSignals(); i = i+2) {
          as = ar.getInSignal(i);
          at = new AvatarTransition("TransitionToReceiving", null);
          asm.addElement(at);
          state.addNext(at);
          aaos = new AvatarActionOnSignal("Receiving__" + as.getName(), as, null);
          asm.addElement(aaos);
          at.addNext(aaos);
          at = new AvatarTransition("TransitionToIntermediateState", null);
          asm.addElement(at);
          state0 = new AvatarState("Choice__" + as.getName(), null);
          asm.addElement(state0);
          aaos.addNext(at);
          at.addNext(state0);
          at = new AvatarTransition("TransitionToMainState", null);
          at.addAction("messageLost()");
          asm.addElement(at);
          state0.addNext(at);
          at.addNext(state);

          as = ar.getOutSignal(i+1);
          at = new AvatarTransition("TransitionToSending", null);
          asm.addElement(at);
          aaos = new AvatarActionOnSignal("Sending__" + as.getName(), as, null);
          asm.addElement(aaos);
          state0.addNext(at);
          at.addNext(aaos);
          at = new AvatarTransition("TransitionAfterSending", null);
          asm.addElement(at);
          aaos.addNext(at);
          at.addNext(state);
          }

          }
          }
          }*/
    }

    public AvatarSpecification advancedClone() {
        AvatarSpecification spec = new AvatarSpecification(this.getName(), this.getReferenceObject());
        Map<AvatarBlock, AvatarBlock> correspondenceBlocks = new HashMap<AvatarBlock, AvatarBlock>();

        // Cloning block definition
        for (AvatarBlock block : blocks) {
            AvatarBlock nB = block.advancedClone(spec);
            correspondenceBlocks.put(block, nB);
            spec.addBlock(nB);
        }

        // Handling the clone of fathers
        for (AvatarBlock block : blocks) {
            AvatarBlock father = block.getFather();
            if (father != null) {
                AvatarBlock nb = spec.getBlockWithName(block.getName());
                if (nb != null) {
                    AvatarBlock nf = spec.getBlockWithName(father.getName());
                    if (nf != null) {
                        //TraceManager.addDev("Setting "+ nf.getName() + " as the father of " + nb.getName());
                        nb.setFather(nf);
                    }
                }
            }
        }

        // Cloning asm
        for (AvatarBlock block : blocks) {
            AvatarBlock nb = spec.getBlockWithName(block.getName());
            block.getStateMachine().advancedClone(nb.getStateMachine(), nb);
        }

        // Relations
        for (AvatarRelation relation : relations) {
            AvatarRelation nR = relation.advancedClone(correspondenceBlocks);
            if (nR != null) {
                spec.addRelation(nR);
            }
        }

		/*for(AvatarPragma pragma: pragmas) {
		    AvatarPragma nP = pragma.advancedClone();
		    spec.addPragma(nP);
		    }*/

        for (String safetyPragma : safetyPragmas) {
            spec.addSafetyPragma(safetyPragma, safetyPragmasRefs.get(safetyPragma));
        }

        for (AvatarPragmaLatency latencyPragma : latencyPragmas) {
            spec.addLatencyPragma(latencyPragma);
        }

        for (AvatarConstant constant : constants) {
            AvatarConstant cN = constant.advancedClone();
            spec.addConstant(cN);
        }
        for (String id : checkedIDs) {
            spec.checkedIDs.add(id);
        }

        spec.setInformationSource(getInformationSource());
        spec.addApplicationCode(getApplicationCode());

        return spec;
    }

    public AvatarAttribute getMatchingAttribute(AvatarAttribute aa) {
        for (AvatarBlock block : this.blocks) {
            if (block.getName().compareTo(aa.getBlock().getName()) == 0) {
                return block.getAvatarAttributeWithName(aa.getName());
            }
        }

        return null;
    }

    public AvatarDependencyGraph makeDependencyGraph() {
        return makeDependencyGraph(true);
    }

    public AvatarDependencyGraph makeDependencyGraph(boolean withID) {
        AvatarDependencyGraph adg = new AvatarDependencyGraph();
        adg.buildGraph(this, withID);
        return adg;
    }

    public AvatarCompactDependencyGraph makeCompactDependencyGraph() {
        return makeCompactDependencyGraph(true);
    }

    public AvatarCompactDependencyGraph makeCompactDependencyGraph(boolean withID) {
        AvatarCompactDependencyGraph adg = new AvatarCompactDependencyGraph();
        adg.buildGraph(this, withID);
        return adg;
    }

    public AvatarSpecification simplifyFromDependencies(ArrayList<AvatarElement> eltsOfInterest) {
        AvatarSpecification clonedSpec = advancedClone();
        AvatarDependencyGraph adg = clonedSpec.makeDependencyGraph();
        AvatarDependencyGraph reducedGraph = adg.reduceGraphBefore(eltsOfInterest);
        clonedSpec.reduceFromDependencyGraph(reducedGraph);
        return clonedSpec;
    }

    public boolean isSignalUsed(AvatarSignal _sig) {
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().isSignalUsed(_sig)) {
                return true;
            }
        }

        return false;
    }

    public void removeUselessSignalAssociations() {
        ArrayList<AvatarRelation> mightBeRemoved = new ArrayList<>();
        ArrayList<AvatarSignal> toBeRemoved1 = new ArrayList<>();
        ArrayList<AvatarSignal> toBeRemoved2 = new ArrayList<>();

        for (AvatarRelation rel : relations) {
            // For each signal association, we look for whether it is used or not
            for (int i = 0; i < rel.getSignals1().size(); i++) {
                AvatarSignal sig1 = rel.getSignal1(i);
                if (!isSignalUsed(sig1)) {
                    AvatarSignal sig2 = rel.getSignal2(i);
                    if (!isSignalUsed(sig2)) {
                        // We can remove the signals. We remove its declaration in blocks and we remove the signals from the relation
                        toBeRemoved1.add(sig1);
                        toBeRemoved2.add(sig2);
                        mightBeRemoved.add(rel);
                    }
                }
            }
        }

        // Removing useless signals from blocks
        for (AvatarBlock block : blocks) {
            block.getSignals().removeAll(toBeRemoved1);
            block.getSignals().removeAll(toBeRemoved2);
        }

        // Removing signals from relations, and removing relations if applicable
        for (int cpt = 0; cpt < mightBeRemoved.size(); cpt++) {
            AvatarRelation rel = mightBeRemoved.get(cpt);
            rel.removeAssociation(toBeRemoved1.get(cpt), toBeRemoved2.get(cpt));
            if (rel.getSignals1().size() == 0) {
                relations.remove(rel);
            }
        }

    }

    public void removeEmptyBlocks() {
        // Remove all blocks with no ASM and no signals
        ArrayList<AvatarBlock> toBeRemoved = new ArrayList<>();
        for (AvatarBlock block : blocks) {
            if (block.getStateMachine().isBasicStateMachine()) {
                if (block.getSignals().size() == 0) {
                    toBeRemoved.add(block);
                }
            }
        }

        // If a block has a father in toBeRemoved, then keep the father, and the father of the father, etc.
        for (AvatarBlock block : blocks) {
            AvatarBlock bl = block.getFather();
            while (bl != null) {
                if (toBeRemoved.contains(bl)) {
                    toBeRemoved.remove(bl);
                }
                bl = bl.getFather();
            }
        }


        blocks.removeAll(toBeRemoved);
    }

    /**
     * Removes attributes that are not used
     */
    public void removeUselessAttributes() {
        ArrayList<AvatarBlock> toBeRemoved = new ArrayList<>();
        for (AvatarBlock block : blocks) {
            block.removeUselessAttributes();
        }
    }

    // TO BE COMPLETED
    // We assume the graph has been reduced already to what is necessary:
    // We now need to reduce the avatarspec accordingly
    public void reduceFromDependencyGraph(AvatarDependencyGraph _adg) {

        // We have to update the state machines according to the graph
        for (AvatarBlock block : blocks) {
            //TraceManager.addDev("Handling block " + block.getName());
            AvatarStateMachine asm = block.getStateMachine();
            // We first check if the start is still in the graph
            // If not, the state machine is empty: we just create a stop, and that's it
            AvatarStartState ass = asm.getStartState();
            if (_adg.getFirstStateWithReference(ass) == null) {
                TraceManager.addDev("No start state in " + block.getName());
                asm.makeBasicSM(block);
                block.clearAttributes();
            } else {

                // Otherwise we keep the start and consider all other elements
                // We remove all elements with no correspondence in the graph
                // Then, we redo a valid ASM i.e. all elements with no nexts (apart from states)
                // are given a stop state after

                TraceManager.addDev("Reducing state machine of " + block.getName());

                ArrayList<AvatarElement> toRemove = new ArrayList<>();
                for (AvatarStateMachineElement asme : asm.getListOfElements()) {
                    if (_adg.getFirstStateWithReference(asme) == null) {
                        boolean toBeRemoved = false;
                        if (asme instanceof AvatarTransition) {
                            if (!((AvatarTransition) asme).isEmpty()) {
                                toBeRemoved = true;
                            }
                        } else {
                            toBeRemoved = true;
                        }

                        if (toBeRemoved)
                            toRemove.add(asme);

                    }

                }
                TraceManager.addDev("To remove size: " + toRemove.size() + " size of ASM: " + asm.getListOfElements().size());
                asm.getListOfElements().removeAll(toRemove);
                TraceManager.addDev("Removed. New size of ASM: " + asm.getListOfElements().size());
                asm.makeCorrect(block);
            }
        }


        // Then we can remove useless attributes i.e attributes that are not used


        removeUselessSignalAssociations();

        removeUselessAttributes();

        removeEmptyBlocks();

    }

    public NameChecker.NamedElement[] getSubNamedElements() {
        NameChecker.NamedElement[] lne = new NameChecker.NamedElement[blocks.size()];
        int index = 0;
        for (AvatarBlock bl : blocks) {
            lne[index] = bl;
            index++;
        }
        return lne;
    }

}
