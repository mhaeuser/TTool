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
 * Class SecurityGenerationForTMAP
 * 
 * Creation: 28/09/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 28/09/2023
 */
 
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaSecret;
import avatartranslator.AvatarSpecification;
import avatartranslator.toproverif.AVATAR2ProVerif;
import common.ConfigurationTTool;
import launcher.RshClient;
import myutil.TraceManager;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifSpec;
import tmltranslator.*;
import tmltranslator.toavatarsec.TML2Avatar;
import ui.TGComponent;

import java.io.Reader;
import java.util.*;


public class SecurityGenerationForTMAP implements Runnable {
    private String appName;
    private TMLMapping<?> tmap;
    private String encComp;
    private String overhead;
    private String decComp;
    private Map<String, List<String>> selectedCPUTasks;
    private int channelIndex = 0;

    private AVATAR2ProVerif avatar2proverif;

    private Map<String, Integer> channelIndexMap = new HashMap<String, Integer>();

    private Map<String, List<HSMChannel>> hsmChannelMap = new HashMap<String, List<HSMChannel>>();

    private Map<String, String> taskHSMMap = new HashMap<String, String>();
    private List<String> hsmTasks = new ArrayList<String>();

    private Map<String, SecurityPattern> channelSecMap = new HashMap<String, SecurityPattern>();
    private TMLMapping<?> newMap;

    public SecurityGenerationForTMAP(String appName, TMLMapping<?> tmap, String encComp, String overhead, String decComp, Map<String, List<String>> selectedCPUTasks) {
        this.appName = appName;
        this.tmap = tmap;
        this.newMap = tmap;
        this.overhead = overhead;
        this.decComp = decComp;
        this.encComp = encComp;
        this.selectedCPUTasks = selectedCPUTasks;
    }
    
    public void proverifAnalysis(TMLMapping<?> tmap, List<String> nonAuthChans, List<String> nonConfChans, boolean checkAuthProverif) {
        if (tmap == null) {
            TraceManager.addDev("No mapping");
            return;
        }

        //Perform ProVerif Analysis

        Object o = null;
        if (tmap.getTMLModeling().getReference() instanceof TGComponent) {
            o = ((TGComponent)(tmap.getTMLModeling().getReference())).getTDiagramPanel().tp;
        }

        TML2Avatar t2a = new TML2Avatar(newMap, false, true, o);
        AvatarSpecification avatarspec = t2a.generateAvatarSpec("1", checkAuthProverif);
        if (avatarspec == null) {
            TraceManager.addDev("No avatar spec");
            return;
        }

        avatar2proverif = new AVATAR2ProVerif(avatarspec);
        try {
            ProVerifSpec proverif = avatar2proverif.generateProVerif(true, true, 3, true, true);
            //warnings = avatar2proverif.getWarnings();

            if (!avatar2proverif.saveInFile("pvspec")) {
                return;
            }

            RshClient rshc = new RshClient(ConfigurationTTool.ProVerifVerifierHost);

            rshc.setCmd(ConfigurationTTool.ProVerifVerifierPath + " -in pitype pvspec");
            rshc.sendExecuteCommandRequest();
            Reader data = rshc.getDataReaderFromProcess();


            ProVerifOutputAnalyzer pvoa = avatar2proverif.getOutputAnalyzer();
            pvoa.analyzeOutput(data, true);

            if (pvoa.getResults().size() == 0) {
                TraceManager.addDev("SECGEN ERROR: No security results");
            }


            Map<AvatarPragmaSecret, ProVerifQueryResult> confResults = pvoa.getConfidentialityResults();

            for (AvatarPragmaSecret pragma : confResults.keySet()) {
                TraceManager.addDev("SECGEN: Pragma " + pragma);
                if (confResults.get(pragma).isProved() && !confResults.get(pragma).isSatisfied()) {
                    nonConfChans.add(pragma.getArg().getBlock().getName() + "__" + pragma.getArg().getName());
                    TraceManager.addDev("SECGEN:" + pragma.getArg().getBlock().getName() + "." + pragma.getArg().getName() + " is not secret");

                    TMLChannel chan = tmap.getTMLModeling().getChannelByShortName(pragma.getArg().getName().replaceAll("_chData", ""));

                    if (chan == null) {
                        chan = tmap.getTMLModeling().getChannelByOriginPortName(pragma.getArg().getName().replaceAll("_chData", ""));
                    }

                    if (chan == null) {
                        TraceManager.addDev("SECGEN: NULL Channel");
                        continue;
                    }

                    if (chan.isBasicChannel()) {
                        TraceManager.addDev("SECGEN: Channel added to nonConfCh");
                        nonConfChans.add(chan.getOriginTask().getName() + "__" + pragma.getArg().getName());

                    } else {
                        for (TMLTask originTask : chan.getOriginTasks()) {
                            nonConfChans.add(originTask.getName() + "__" + pragma.getArg().getName());
                        }
                    }
                }
            }

            Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authResults = pvoa.getAuthenticityResults();
            for (AvatarPragmaAuthenticity pragma : authResults.keySet()) {
                if (authResults.get(pragma).isProved() && !authResults.get(pragma).isSatisfied()) {
                    nonAuthChans.add(pragma.getAttrA().getAttribute().getBlock().getName() + "__" + pragma.getAttrA().getAttribute().getName().replaceAll("_chData", ""));
                    nonAuthChans.add(pragma.getAttrB().getAttribute().getBlock().getName() + "__" + pragma.getAttrB().getAttribute().getName().replaceAll("_chData", ""));
                }
            }

        } catch (Exception e) {
            System.out.println("SECGEN EXCEPTION " + e);

        }
    }

    public TMLMapping<?> startThread() {
        Thread t = new Thread(this);
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            TraceManager.addDev("SECGEN. Error in Security Generation Thread");
            System.out.println("SECGEN. Error in Security Generation Thread");
        }
        return tmap;
    }

    public boolean portInTask(TMLTask task, String portName) {
        TMLActivity adTask = task.getActivityDiagram();
        for (TMLActivityElement elem : adTask.getElements()) {
            if (elem instanceof TMLWriteChannel) {
                TMLWriteChannel writeChannel = (TMLWriteChannel) elem;
                for (int i = 0; i < writeChannel.getNbOfChannels(); i++) {
                    if (writeChannel.getChannel(i).getName().replaceAll(appName + "__", "").equals(portName)) {
                        return true;
                    }
                }
            } else if (elem instanceof TMLReadChannel) {
                TMLReadChannel readChannel = (TMLReadChannel) elem;
                for (int i = 0; i < readChannel.getNbOfChannels(); i++) {
                    if (readChannel.getChannel(i).getName().replaceAll(appName + "__", "").equals(portName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void run() {
        String title = appName;
        // oldmodel = tmap.getTMLModeling();
        Map<TMLTask, HashSet<TMLTask>> toSecure = new HashMap<TMLTask, HashSet<TMLTask>>();
        Map<TMLTask, HashSet<TMLTask>> toSecureRev = new HashMap<TMLTask, HashSet<TMLTask>>();
        Map<TMLTask, HashSet<String>> secOutChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> secInChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> nonceOutChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> nonceInChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> macOutChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> macInChannels = new HashMap<TMLTask, HashSet<String>>();

        Map<TMLTask, HashSet<String>> hsmSecInChannels = new HashMap<TMLTask, HashSet<String>>();
        Map<TMLTask, HashSet<String>> hsmSecOutChannels = new HashMap<TMLTask, HashSet<String>>();

        //TraceManager.addDev("mapping " + tmap.getSummaryTaskMapping());

        //   Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();
        //Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();


        for (String cpuName : selectedCPUTasks.keySet()) {
            for (String task : selectedCPUTasks.get(cpuName)) {
                hsmTasks.add(task);
                taskHSMMap.put(task, cpuName);
            }
            hsmChannelMap.put(cpuName, new ArrayList<HSMChannel>());

        }

        TMLModeling<?> tmlmodel = tmap.getTMLModeling();
        //Proverif Analysis channels
        List<String> nonAuthChans = new ArrayList<String>();
        List<String> nonConfChans = new ArrayList<String>();
        boolean checkAuthProverif = false;
        for (TMLChannel ch : tmlmodel.getChannels()) {
            if (ch.isEnsureWeakAuth() || ch.isEnsureStrongAuth()) {
                checkAuthProverif = true;
                break;
            }
        }
        proverifAnalysis(tmap, nonAuthChans, nonConfChans, checkAuthProverif);

        
        List<TMLChannel> channels = tmlmodel.getChannels();
        for (TMLChannel channel : channels) {
            for (TMLPortWithSecurityInformation p : channel.ports) {
                channel.checkConf = channel.checkConf || p.getCheckConf();
                channel.checkAuth = channel.checkAuth || p.getCheckAuth();
            }
        }

        //System.out.println("Nonauth " + nonAuthChans);
        //System.out.println("NonConf " + nonConfChans);

        //Create clone of Component Diagram + Activity diagrams to secure
        //        TMLComponentDesignPanel tmlcdp = tmap.getTMLCDesignPanel();

        //TMLComponentTaskDiagramPanel tcdp = tmlcdp.tmlctdp;
        //Create clone of architecture panel and tmap tasks to it
        //newarch.renameMapping(tabName, tabName + "_" + name);

        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
            HashSet<String> tmp = new HashSet<String>();
            HashSet<String> tmp2 = new HashSet<String>();
            HashSet<TMLTask> tmp3 = new HashSet<TMLTask>();
            HashSet<TMLTask> tmp4 = new HashSet<TMLTask>();
            HashSet<String> tmp5 = new HashSet<String>();
            HashSet<String> tmp6 = new HashSet<String>();
            HashSet<String> tmp7 = new HashSet<String>();
            HashSet<String> tmp8 = new HashSet<String>();
            HashSet<String> tmp9 = new HashSet<String>();
            HashSet<String> tmp10 = new HashSet<String>();


            secInChannels.put(task, tmp);
            secOutChannels.put(task, tmp2);
            toSecure.put(task, tmp3);
            toSecureRev.put(task, tmp4);
            nonceInChannels.put(task, tmp5);
            nonceOutChannels.put(task, tmp6);
            macInChannels.put(task, tmp7);
            macOutChannels.put(task, tmp8);

            hsmSecInChannels.put(task, tmp9);
            hsmSecOutChannels.put(task, tmp10);

        }
        //ToSecure keeps a tmap of origin task: {dest task} for which security operators need to be added
        //ToSecureRev keeps a tmap of dest task: {origin task} for which security operators need to be added

        //SecOutChannels are channels which need symmetric encryption operators added
        //SecInChannels are channels which need sym decryption operators added
        //nonceInChannels are channels which need to send a nonce before receiving the channel data
        //nonceOutChannels are channels which need to receive a nonce before sending the channel data
        //macInChannels are channels which need verifymac operators added
        //macOutChannels are channels which need mac operators added

        //hsmSecInChannels need to send data to the hsm to decrypt after receiving channel data
        //hsmSecOutChannels need to send data to the hsm to encrypt before sending channel data

        //With the proverif results, check which channels need to be secured
        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
            //Check if all channel operators are secured
            TMLActivity taskAD = task.getActivityDiagram(); //FIXME getActivityDiagramName( task ) )
            if (taskAD == null) {
                TraceManager.addDev("SECGEN. Null Activity Diagram for " + task.getName());
                continue;
            }

            for (TMLChannel chan : tmlmodel.getChannels(task)) {
                //Origin and Destination ports can have different names. Therefore, we need to put both names in the list of channels to secure.
                List<String> portNames = new ArrayList<String>();
                boolean nonConf = false;
                boolean nonAuth = false;

                if (chan.isBasicChannel()) {
                    portNames.add(chan.getOriginPort().getName());
                    portNames.add(chan.getDestinationPort().getName());
                    if (nonConfChans.contains(chan.getOriginTask().getName().split("__")[1] + "__" + chan.getOriginPort().getName() + "_chData")) {
                        nonConf = true;
                        TraceManager.addDev("SECGEN. non conf basic ch = true");
                    }

                    if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + title + "__" + chan.getDestinationPort().getName())) {
                        nonAuth = true;
                    }

                    //When port names are different
                    if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + chan.getDestinationPort().getName())) {
                        nonAuth = true;
                    }
                } else {
                    for (TMLPort port : chan.getOriginPorts()) {
                        for (TMLTask origTask : chan.getOriginTasks()) {
                            if (nonConfChans.contains(origTask.getName().split("__")[1] + "__" + port.getName() + "_chData")) {
                                nonConf = true;
                                TraceManager.addDev("SECGEN. non conf port ch = true");
                            }
                        }
                        portNames.add(port.getName());
                    }


                    for (TMLTask destTask : chan.getDestinationTasks()) {
                        for (TMLPort port : chan.getDestinationPorts()) {
                            if (nonAuthChans.contains(destTask.getName().split("__")[1] + "__" + title + "__" + port.getName())) {
                                nonAuth = true;
                            }
                            if (!portNames.contains(port.getName())) {
                                portNames.add(port.getName());
                            }
                        }
                        //System.out.println(destTask.getName().split("__")[1] + "__" + chan.getName());
                        if (nonAuthChans.contains(destTask.getName().split("__")[1] + "__" + chan.getDestinationPort().getName())) {
                            nonAuth = true;
                        }

                    }
                    //When port names are different

                }

                String secName = chan.getName().split("__")[chan.getName().split("__").length - 1];

                for (String chanName : portNames) {
                    //Classify channels based on the type of security requirements and unsatisfied properties
                    if (chan.isBasicChannel()) {
                        if (chan.isEnsureConf() && nonConf) {
                            toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                            if (chan.isEnsureStrongAuth()) {
                                if (!toSecureRev.get(chan.getDestinationTask()).contains(chan.getOriginTask())) {
                                    toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                }
                            }
                            if (hsmTasks.contains(chan.getOriginTask().getName().split("__")[1])) {
                                SecurityPattern secPattern = new SecurityPattern("hsmSec_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                secPattern.originTask = "HSM_" + taskHSMMap.get(chan.getOriginTask().getName().replaceAll(title + "__", ""));
                                channelSecMap.put(chanName, secPattern);
                                if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName) && portInTask(chan.getOriginTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getOriginTask().getName().split("__")[1], HSMChannel.SENC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

                                    if (chan.isEnsureStrongAuth()) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secOutChannels.get(chan.getOriginTask()).contains(chanName)) {
                                    secOutChannels.get(chan.getOriginTask()).add(chanName);
                                    SecurityPattern secPattern = new SecurityPattern("autoEncrypt_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                    secPattern.originTask = chan.getOriginTask().getName().replaceAll(title + "__", "");
                                    channelSecMap.put(chanName, secPattern);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                    }
                                }
                            }

                            if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])) {
                                if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName) && portInTask(chan.getDestinationTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
                                    secInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                    }
                                }
                            }

                        } else if (chan.isEnsureWeakAuth() && nonAuth) {
                            toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                            if (chan.isEnsureStrongAuth()) {
                                if (!toSecureRev.get(chan.getDestinationTask()).contains(chan.getOriginTask())) {
                                    toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                }
                                /*}
                                  else {
                                  TMLChannel chantmp = oldmodel.getChannelByShortName("__"+chan.getName());
                                  System.out.println("Channel found "+ chantmp);
                                  }*/

                            }
                            if (hsmTasks.contains(chan.getOriginTask().getName().split("__")[1])) {
                                SecurityPattern secPattern = new SecurityPattern("hsmSec_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                secPattern.originTask = "HSM_" + taskHSMMap.get(chan.getOriginTask().getName().replaceAll(title + "__", ""));
                                channelSecMap.put(chanName, secPattern);
                                if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName) && portInTask(chan.getOriginTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getOriginTask().getName().split("__")[1], HSMChannel.MAC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

                                    if (chan.isEnsureStrongAuth()) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!macInChannels.get(chan.getOriginTask()).contains(chanName)) {
                                    macOutChannels.get(chan.getOriginTask()).add(chanName);
                                    SecurityPattern secPattern = new SecurityPattern("autoEncrypt_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                    secPattern.originTask = chan.getOriginTask().getName().replaceAll(title + "__", "");
                                    channelSecMap.put(chanName, secPattern);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                    }
                                }
                            }

                            if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])) {
                                if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName) && portInTask(chan.getDestinationTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
                                    secInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.isEnsureStrongAuth()) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                    }
                                }
                            }
                        }
                    } else {
                        //Ignore strong authenticity for fork and join channels
                        //Only add hsm channel for own port
                        for (TMLTask orig : chan.getOriginTasks()) {
                            for (TMLTask dest : chan.getDestinationTasks()) {
                                if (chan.isEnsureConf() && nonConf) {
                                    toSecure.get(orig).add(dest);
                                /*if (chan.checkAuth && autoStrongAuth) {
                                  if (!toSecureRev.get(dest).contains(orig)) {
                                  toSecureRev.get(dest).add(orig);
                                  }
                                  }*/
                                    if (hsmTasks.contains(orig.getName().split("__")[1])) {
                                        SecurityPattern secPattern = new SecurityPattern("hsmSec_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                        secPattern.originTask = "HSM_" + taskHSMMap.get(orig.getName().replaceAll(title + "__", ""));
                                        channelSecMap.put(chanName, secPattern);
                                        if (!hsmSecOutChannels.get(orig).contains(chanName) && portInTask(orig, chanName)) {
                                            HSMChannel hsmchan = new HSMChannel(chanName, orig.getName().split("__")[1], HSMChannel.SENC);
                                            hsmChannelMap.get(taskHSMMap.get(orig.getName().split("__")[1])).add(hsmchan);
                                            hsmSecOutChannels.get(orig).add(chanName);

                                        /*    if (chan.checkAuth && autoStrongAuth) {
                                            nonceOutChannels.get(orig).add(chanName);
                                            hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
                                            }*/
                                        }
                                    } else {
                                        if (!secOutChannels.get(orig).contains(chanName)) {
                                            secOutChannels.get(orig).add(chanName);
                                            SecurityPattern secPattern = new SecurityPattern("autoEncrypt_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                            secPattern.originTask = orig.getName().replaceAll(title + "__", "");
                                            channelSecMap.put(chanName, secPattern);
                                        /* if (chan.checkAuth && autoStrongAuth) {
                                           nonceOutChannels.get(orig).add(chanName);
                                           }*/
                                        }
                                    }

                                    if (hsmTasks.contains(dest.getName().split("__")[1])) {
                                        if (!hsmSecInChannels.get(dest).contains(chanName) && portInTask(dest, chanName)) {
                                            HSMChannel hsmchan = new HSMChannel(chanName, dest.getName().split("__")[1], HSMChannel.DEC);
                                            hsmChannelMap.get(taskHSMMap.get(dest.getName().split("__")[1])).add(hsmchan);
                                            hsmSecInChannels.get(dest).add(chanName);
                                        /*    if (chan.checkAuth && autoStrongAuth) {
                                            nonceInChannels.get(dest).add(chanName);
                                            hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
                                            }*/
                                        }
                                    } else {
                                        if (!secInChannels.get(dest).contains(chanName)) {
                                            secInChannels.get(dest).add(chanName);
                                        /*if (chan.checkAuth && autoStrongAuth) {
                                          nonceInChannels.get(dest).add(chanName);
                                          }*/
                                        }
                                    }

                                } else if (chan.isEnsureWeakAuth() && nonAuth) {
                                    toSecure.get(orig).add(dest);
                                /*    if (autoStrongAuth) {
                                /*          if (chan.getOriginTask().getReferenceObject() instanceof TMLCPrimitiveComponent && chan.getDestinationTask().getReferenceObject() instanceof TMLCPrimitiveComponent) {*/
                                /*if (!toSecureRev.get(dest).contains(orig)) {
                                  toSecureRev.get(dest).add(orig);
                                  }*/
                                /*}
                                  else {
                                  TMLChannel chantmp = oldmodel.getChannelByShortName("__"+chan.getName());
                                  System.out.println("Channel found "+ chantmp);
                                  }

                                  }*/
                                    if (hsmTasks.contains(orig.getName().split("__")[1])) {
                                        SecurityPattern secPattern = new SecurityPattern("hsmSec_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                        secPattern.originTask = "HSM_" + taskHSMMap.get(orig.getName().replaceAll(title + "__", ""));
                                        channelSecMap.put(chanName, secPattern);
                                        if (!hsmSecOutChannels.get(orig).contains(chanName) && portInTask(orig, chanName)) {
                                            HSMChannel hsmchan = new HSMChannel(chanName, orig.getName().split("__")[1], HSMChannel.MAC);
                                            hsmChannelMap.get(taskHSMMap.get(orig.getName().split("__")[1])).add(hsmchan);
                                            hsmSecOutChannels.get(orig).add(chanName);

                                        /*if (autoStrongAuth) {
                                          nonceOutChannels.get(orig).add(chanName);
                                          hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
                                          }*/
                                        }
                                    } else {
                                        if (!macInChannels.get(orig).contains(chanName)) {
                                            macOutChannels.get(orig).add(chanName);
                                            SecurityPattern secPattern = new SecurityPattern("autoEncrypt_" + secName, SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                                            secPattern.originTask = orig.getName().replaceAll(title + "__", "");
                                            channelSecMap.put(chanName, secPattern);
                                        /*   if (autoStrongAuth) {
                                             nonceOutChannels.get(orig).add(chanName);
                                             }*/
                                        }
                                    }

                                    if (hsmTasks.contains(dest.getName().split("__")[1])) {

                                        if (!hsmSecInChannels.get(dest).contains(chanName) && portInTask(dest, chanName)) {
                                            HSMChannel hsmchan = new HSMChannel(chanName, dest.getName().split("__")[1], HSMChannel.DEC);
                                            hsmChannelMap.get(taskHSMMap.get(dest.getName().split("__")[1])).add(hsmchan);
                                            hsmSecInChannels.get(dest).add(chanName);
                                        /*if (chan.checkAuth && autoStrongAuth) {
                                          nonceInChannels.get(dest).add(chanName);
                                          hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
                                          }*/
                                        }
                                    } else {
                                        if (!secInChannels.get(dest).contains(chanName)) {
                                            secInChannels.get(dest).add(chanName);
                                        /*if (chan.checkAuth && autoStrongAuth) {
                                          nonceInChannels.get(dest).add(chanName);
                                          }*/
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        // System.out.println("hsmchannelmap" + hsmChannelMap);
        for (String chSec : channelSecMap.keySet()) {
            TraceManager.addDev("secChannelMap: key=" + chSec + " value= " + channelSecMap.get(chSec).name);
        }
        for (TMLTask tk : macOutChannels.keySet()) {
            TraceManager.addDev("macoutchans: key= " + tk.getName() + " value= " + macOutChannels.get(tk));
        }
        for (TMLTask tk : macInChannels.keySet()) {
            TraceManager.addDev("macinchans: key= " + tk.getName() + " value= " + macInChannels.get(tk));
        }
        for (TMLTask tk : secInChannels.keySet()) {
            TraceManager.addDev("nonsecin: key= " + tk.getName() + " value= " + secInChannels.get(tk));
        }
        for (TMLTask tk : secOutChannels.keySet()) {
            TraceManager.addDev("nonsecout: key= " + tk.getName() + " value= " + secOutChannels.get(tk));
        }
        for (TMLTask tk : nonceInChannels.keySet()) {
            TraceManager.addDev("noncein: key= " + tk.getName() + " value= " + nonceInChannels.get(tk));
        }
        for (TMLTask tk : nonceOutChannels.keySet()) {
            TraceManager.addDev("nonceout: key= " + tk.getName() + " value= " + nonceOutChannels.get(tk));
        }
        for (TMLTask tk : hsmSecInChannels.keySet()) {
            TraceManager.addDev("hsmsecin: key= " + tk.getName() + " value= " + hsmSecInChannels.get(tk));
        }
        for (TMLTask tk : hsmSecOutChannels.keySet()) {
            TraceManager.addDev("hsmsecout: key= " + tk.getName() + " value= " + hsmSecOutChannels.get(tk));
        }
        for (TMLTask tk : toSecureRev.keySet()) {
            TraceManager.addDev("tosecrev: key= " + tk.getName() + " value= " + toSecureRev.get(tk));
        }
        //Add a HSM Task for each selected CPU on the component diagram, add associated channels, etc
        Map<String, List<TMLChannel>> hsmChannelsToMap = new HashMap<String, List<TMLChannel>>();
        //Map<TMLChannel, Set<TMLChannel>> mapHSMChannelsInSameBusesAs = new HashMap<TMLChannel, Set<TMLChannel>>();
        for (String cpuName : selectedCPUTasks.keySet()) {
            TMLTask hsm = new TMLTask("HSM_" + cpuName, tmap.getTMLModeling().getTasks().get(0).getReferenceObject(), null);

            TMLAttribute index = new TMLAttribute("channelIndex", new TMLType(TMLType.NATURAL), "0");
            hsm.addAttribute(index);
            tmlmodel.addTask(hsm);

            //Find all associated tasks
            List<TMLTask> comps = new ArrayList<TMLTask>();
            //Find the component to add a HSM to

            for (TMLTask task : tmlmodel.getTasks()) {
                for (String compName : selectedCPUTasks.get(cpuName)) {
                    if (task.getName().replaceAll(title + "__", "").equals(compName)) {
                        comps.add(task);
                        break;
                    }
                }
            }
            if (comps.size() == 0) {
                //System.out.println("No Components found");
                continue;
            }

            for (TMLTask comp : comps) {

                //Map<String, HSMChannel> compChannels = new HashMap<String, HSMChannel>();
                //String compName = comp.getValue();

                List<ChannelData> hsmChans = new ArrayList<ChannelData>();
                ChannelData chd = new ChannelData("startHSM_" + cpuName, false, false);
                hsmChans.add(chd);
                for (HSMChannel hsmChan : hsmChannelMap.get(cpuName)) {
                    if (!hsmChan.task.equals(comp.getName().replaceAll(title + "__", ""))) {
                        continue;
                    }
                    if (!channelIndexMap.containsKey(hsmChan.name)) {
                        channelIndexMap.put(hsmChan.name, channelIndex);
                        channelIndex++;
                    }
                    chd = new ChannelData("data_" + hsmChan.name + "_" + hsmChan.task, false, true);
                    hsmChans.add(chd);
                    chd = new ChannelData("retData_" + hsmChan.name + "_" + hsmChan.task, true, true);
                    hsmChans.add(chd);
                }
                for (ChannelData hsmChan : hsmChans) {
                    if (!hsmChan.isChan) {
                        TMLRequest request = new TMLRequest(hsmChan.name, hsm.getReferenceObject());
                        if (hsmChan.isOrigin) {
                            request.addOriginTask(hsm);
                            request.setDestinationTask(comp);
                        } else {
                            request.addOriginTask(comp);
                            request.setDestinationTask(hsm);
                        }
                        hsm.setRequest(request);
                        comp.setRequest(request);
                        request.addParam(new TMLType(TMLType.NATURAL));
                        tmlmodel.addRequest(request);
                    } else {
                        TMLChannel channel = new TMLChannel(hsmChan.name, hsm.getReferenceObject());
                        channel.setPorts(new TMLPort(channel.getName(), channel.getReferenceObject()), new TMLPort(channel.getName(), channel.getReferenceObject()));
                        if (hsmChan.isOrigin) {
                            channel.setOriginTask(hsm);
                            channel.setDestinationTask(comp);
                            comp.addReadTMLChannel(channel);
                            hsm.addWriteTMLChannel(channel);
                        } else {
                            channel.setOriginTask(comp);
                            channel.setDestinationTask(hsm);
                            hsm.addReadTMLChannel(channel);
                            comp.addWriteTMLChannel(channel);
                        }
                        
                        if (hsmChannelsToMap.containsKey(cpuName)) {
                            if (!hsmChannelsToMap.get(cpuName).contains(channel)) {
                                hsmChannelsToMap.get(cpuName).add(channel);
                            }
                        } else {
                            List<TMLChannel> hsmChannels = new ArrayList<TMLChannel>();
                            
                            hsmChannels.add(channel);
                            hsmChannelsToMap.put(cpuName, hsmChannels);
                        }

                        /*Set<TMLChannel> channelsWithSameMapping = new HashSet<TMLChannel>();
                        for (TMLReadChannel rdCh: comp.getReadChannels()) {
                            for(int i=0; i<rdCh.getNbOfChannels(); i++) {
                                if (rdCh.getChannel(i).isEnsureConf() || rdCh.getChannel(i).isEnsureWeakAuth()) {
                                    if (!channelsWithSameMapping.contains(rdCh.getChannel(i))) {
                                        channelsWithSameMapping.add(rdCh.getChannel(i));
                                    }
                                }
                            }
                        }

                        for (TMLWriteChannel wrCh: comp.getWriteChannels()) {
                            for(int i=0; i<wrCh.getNbOfChannels(); i++) {
                                if (wrCh.getChannel(i).isEnsureConf() || wrCh.getChannel(i).isEnsureWeakAuth()) {
                                    if (!channelsWithSameMapping.contains(wrCh.getChannel(i))) { 
                                        channelsWithSameMapping.add(wrCh.getChannel(i));
                                    }
                                }
                            }
                        } 
                        if (mapHSMChannelsInSameBusesAs.containsKey(channel)) {
                            mapHSMChannelsInSameBusesAs.get(channel).addAll(channelsWithSameMapping);
                        } else {
                            mapHSMChannelsInSameBusesAs.put(channel, channelsWithSameMapping);
                        }*/
                        
                        //hsm.addTMLChannel(channel); /// IN TMLChannel and WriteTMLChannel ??
                        
                        //comp.addTMLChannel(channel);
                        tmlmodel.addChannel(channel);
                    }
                }
            }
        }

        /*for (TMLChannel ch : mapHSMChannelsInSameBusesAs.keySet()) {
            for (TMLChannel chAs : mapHSMChannelsInSameBusesAs.get(ch)) {
                for (HwCommunicationNode comm : tmap.getAllCommunicationNodesOfChannel(chAs)) {
                    if (comm instanceof HwBus) {
                        if (!tmap.isCommNodeMappedOn(ch, comm)) {
                            tmap.addCommToHwCommNode(ch, comm);
                        }
                    }
                }
            }
        }*/

        for (TMLTask task : toSecureRev.keySet()) {
            // TraceManager.addDev("Adding nonces to " + task.getName());
            List<TMLChannel> chans = tmlmodel.getChannelsFromMe(task);
            for (TMLTask task2 : toSecureRev.get(task)) {
                boolean addChan = true;
                for (TMLChannel chan : chans) {
                    if (chan.getDestinationTask() == task2) {
                        addChan = false;
                    }
                }
                if (addChan) {
                    TMLChannel channel = new TMLChannel("nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1], task.getReferenceObject());
                    if (tmlmodel.getChannelByName(channel.getName()) == null) {
                        if (hsmTasks.contains(task.getName().replaceAll(title + "__", ""))) {
                            channel.setOriginTask(tmap.getTaskByName("HSM_" + taskHSMMap.get(task.getName().replaceAll(title + "__", ""))));
                            tmap.getTaskByName("HSM_" + taskHSMMap.get(task.getName().replaceAll(title + "__", ""))).addWriteTMLChannel(channel);
                        } else {
                            channel.setOriginTask(task);
                            task.addWriteTMLChannel(channel);
                        }

                        if (hsmTasks.contains(task2.getName().replaceAll(title + "__", ""))) {
                            channel.setDestinationTask(tmap.getTaskByName("HSM_" + taskHSMMap.get(task2.getName().replaceAll(title + "__", ""))));
                            tmap.getTaskByName("HSM_" + taskHSMMap.get(task2.getName().replaceAll(title + "__", ""))).addReadTMLChannel(channel);
                        } else {
                            channel.setDestinationTask(task2);
                            task2.addReadTMLChannel(channel);
                        }
                        
                        channel.setPorts(new TMLPort(channel.getName(), channel.getReferenceObject()), new TMLPort(channel.getName(), channel.getReferenceObject()));
                        tmlmodel.addChannel(channel);
                    }
                    
                }
            }
        }

        for (String cpuName : selectedCPUTasks.keySet()) {          
            buildHSMActivityDiagram(cpuName);
            //Add a private bus to Hardware Accelerator with the task for hsm

            //Find the CPU the task is mapped to
            TMLArchitecture arch = tmap.getArch();
            HwCPU cpu = arch.getHwCPUByName(cpuName);
            
            if (cpu == null) {
                return;
            }

            //Add new memory
            HwMemory mem = new HwMemory("HSMMemory_" + cpuName);
            arch.addHwNode(mem);
            
            //Add Hardware Accelerator
            HwA hwa = new HwA("HSM_" + cpuName);
            arch.addHwNode(hwa);
            
            //Add hsm task to hwa
            TMLTask task = tmap.getTaskByName("HSM_" + cpuName);
            if (task != null) {
                tmap.addTaskToHwExecutionNode(task, hwa);
            }
            //Add bus to be connecting to the HWA
            HwBus bus = new HwBus("HSMBus_" + cpuName);
            bus.privacy = HwBus.BUS_PRIVATE;
            arch.addHwNode(bus);

            //Add bridge to be connecting to the 2 buses
            HwBridge bridge = new HwBridge("HSMBridge_" + cpuName);
            arch.addHwNode(bridge);
            
            //get bus connected to CPU
            HwBus busOfCPU = null;
            for (HwLink link : tmap.getArch().getHwLinks()) {
                if (link.hwnode == cpu) {
                    busOfCPU = link.bus;
                    break;
                }
            }

            for (TMLChannel ch : hsmChannelsToMap.get(cpuName)) {
                tmap.addCommToHwCommNode(ch, mem);
                tmap.addCommToHwCommNode(ch, bus);
                tmap.addCommToHwCommNode(ch, busOfCPU);
            }

            //Connect new Bus and Bridge
            HwLink linkBridgeWithNewBus = new HwLink("link_" + bridge.getName() + "_to_" + bus.getName());
            linkBridgeWithNewBus.bus = bus;
            linkBridgeWithNewBus.hwnode = bridge;
            arch.addHwLink(linkBridgeWithNewBus);

            //Connect the Bus of CPU and Bridge
            if (busOfCPU != null) {
                HwLink linkBridgeWithBus = new HwLink("link_" + bridge.getName() + "_to_" + busOfCPU.getName());
                linkBridgeWithBus.bus = busOfCPU;
                linkBridgeWithBus.hwnode = bridge;
                arch.addHwLink(linkBridgeWithBus);
            }

            //Connect Bus and HWA
            HwLink linkHWAWithBus = new HwLink("link_" + hwa.getName() + "_to_" + bus.getName());
            linkHWAWithBus.bus = bus;
            linkHWAWithBus.hwnode = hwa;
            arch.addHwLink(linkHWAWithBus);

            //Connect Bus and Memory
            HwLink linkMemoryWithBus = new HwLink("link_" + mem.getName() + "_to_" + bus.getName());
            linkMemoryWithBus.bus = bus;
            linkMemoryWithBus.hwnode = mem;
            arch.addHwLink(linkMemoryWithBus);
        }

        for (TMLTask task : toSecureRev.keySet()) {
            // TraceManager.addDev("Adding nonces to " + task.getName());

            for (TMLTask task2 : toSecureRev.get(task)) {
                TMLChannel channel = tmlmodel.getChannelByName("nonceCh" + task.getName().split("__")[1] + "_" + task2.getName().split("__")[1]);
                
                List<TMLChannel> chans, chans2;
                
                chans = tmlmodel.getChannelsToMe(task);
                HwMemory memToPutChannel = null;
                for (TMLChannel chan : chans) {
                    if (chan.isCheckAuthChannel()) {
                        for (HwCommunicationNode mappedNode : tmap.getAllCommunicationNodesOfChannel(chan)) {
                            if (!(mappedNode instanceof HwMemory)) {
                                if (!tmap.isCommNodeMappedOn(channel, mappedNode)) {
                                    tmap.addCommToHwCommNode(channel, mappedNode);
                                }
                            }
                        }
                        HwMemory memoryOfChannel = tmap.getMemoryOfChannel(chan);
                        if (memoryOfChannel != null) {
                            memToPutChannel = memoryOfChannel;
                        }
                    }
                }
                
                if (hsmTasks.contains(task.getName().replaceAll(title + "__", ""))) {
                    chans = tmlmodel.getChannelsFromMe(tmap.getTaskByName("HSM_" + taskHSMMap.get(task.getName().replaceAll(title + "__", ""))));
                    for (TMLChannel chan : chans) {
                        for (HwCommunicationNode mappedNode : tmap.getAllCommunicationNodesOfChannel(chan)) {
                            if (!(mappedNode instanceof HwMemory)) {
                                if (!tmap.isCommNodeMappedOn(channel, mappedNode)) {
                                    tmap.addCommToHwCommNode(channel, mappedNode);
                                }
                            }
                        }
                        HwMemory memoryOfChannel = tmap.getMemoryOfChannel(chan);
                        if (memoryOfChannel != null) {
                            memToPutChannel = memoryOfChannel;
                        }
                    }
                }
                if (memToPutChannel != null) {
                    if (!tmap.isCommNodeMappedOn(channel, memToPutChannel)) {
                        tmap.addCommToHwCommNode(channel, memToPutChannel);
                    }
                }
                chans2 = tmlmodel.getChannelsFromMe(task2);
                for (TMLChannel chan2 : chans2) {
                    if (chan2.isCheckAuthChannel()) {
                        for (HwCommunicationNode mappedNode : tmap.getAllCommunicationNodesOfChannel(chan2)) {
                            if (!(mappedNode instanceof HwMemory)) {
                                if (!tmap.isCommNodeMappedOn(channel, mappedNode)) {
                                    tmap.addCommToHwCommNode(channel, mappedNode);
                                }
                            }
                        }
                    }
                }

                if (hsmTasks.contains(task2.getName().replaceAll(title + "__", ""))) {
                    chans2 = tmlmodel.getChannelsToMe(tmap.getTaskByName("HSM_" + taskHSMMap.get(task2.getName().replaceAll(title + "__", ""))));
                    for (TMLChannel chan2 : chans2) {
                        for (HwCommunicationNode mappedNode : tmap.getAllCommunicationNodesOfChannel(chan2)) {
                            if (!(mappedNode instanceof HwMemory)) {
                                if (!tmap.isCommNodeMappedOn(channel, mappedNode)) {
                                    tmap.addCommToHwCommNode(channel, mappedNode);
                                }
                            }
                        }
                    }
                }
            }
        }


        //  }
        //Add encryption/nonces to activity diagram
        for (TMLTask task : toSecure.keySet()) {
            TraceManager.addDev("Securing task " + task.getName());
            TMLActivity taskAD = task.getActivityDiagram();
            if (taskAD == null) {
                continue;
            }
            
            TMLActivityElement fromStart = taskAD.getFirst();
            //TGConnectingPoint point = new TGConnectingPoint(null, 0, 0, false, false);

            //Find states immediately before the write channel operator
            //For each occurence of a write channel operator, add encryption/nonces before it


            for (String channel : secOutChannels.get(task)) {
                Set<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                if (tmlc == null) {
                    //Find by origin port instead
                    tmlc = tmlmodel.getChannelByOriginPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLWriteChannel) {
                        TMLWriteChannel writeChannel = (TMLWriteChannel) elem;
                        for (int i = 0; i < writeChannel.getNbOfChannels(); i++) {
                            if (writeChannel.getChannel(i).getName().equals(tmlc.getName()) && writeChannel.securityPattern == null) {
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                for (TMLActivityElement elem : channelInstances) {
                    
                    //Add encryption operator
                    TMLExecC encC = new TMLExecC(channelSecMap.get(channel).name, taskAD.getReferenceObject());
                    encC.securityPattern = new SecurityPattern(channelSecMap.get(channel));
                    encC.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    encC.setAction(Integer.toString(channelSecMap.get(channel).encTime));
                    tmlmodel.addSecurityPattern(channelSecMap.get(channel));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(channel))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(channel)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(channel)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(channel), listTask);
                    }
                    
                    TMLActivityElement prevElem = taskAD.getPrevious(elem);
                    if (nonceOutChannels.get(task).contains(channel)) {
                        SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        if (tmlc != null) {
                            encC.securityPattern.nonce = secPatternNonce.getName();
                        }
                        boolean addNewReadNonce = true;
                        for (TMLActivityElement elemT : taskAD.getElements()) {
                            if (elemT instanceof TMLReadChannel) {
                                TMLReadChannel readElem = (TMLReadChannel) elemT;
                                if (readElem.securityPattern != null) {
                                    if(readElem.securityPattern.getName().equals(secPatternNonce.getName())) {
                                        addNewReadNonce = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (addNewReadNonce) {
                            //Receive any nonces if ensuring authenticity
                            TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                            //System.out.println("tmlc " + tmlc);
                            //System.out.println("Checking "+ tmlc.getDestinationTask() + " " + tmlc.getOriginTask());
                            List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                            if (matches.size() > 0) {
                                rd.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                                if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                                } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                                }
                            } else {
                                rd.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                                if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                                } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                                }
                            }
                            secPatternNonce.originTask = rd.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                            rd.securityPattern = secPatternNonce;  
                            rd.setNbOfSamples("1");                   
                            TMLActivityElement nextFirst = taskAD.getFirst().getNextElement(0);
                            taskAD.getFirst().setNewNext(nextFirst, rd);
                            rd.addNext(nextFirst);
                            taskAD.addElement(rd);
                            //Move encryption operator after receive nonce component
                            
                        }
                    }
                    prevElem = taskAD.getPrevious(elem);
                    prevElem.setNewNext(elem, encC);
                    encC.addNext(elem);
                    taskAD.addElement(encC);

                    //Add security contexts to write channels
                    for (TMLActivityElement elemAC : taskAD.getElements()) {
                        if (elemAC instanceof TMLWriteChannel) {
                            TMLWriteChannel wChannel = (TMLWriteChannel) elemAC;
                            for (int i=0; i < wChannel.getNbOfChannels(); i++) {
                                if (channel.equals(wChannel.getChannel(i).getName().replaceAll(title + "__", "")) && wChannel.securityPattern == null) {
                                    wChannel.securityPattern = channelSecMap.get(channel);
                                    // wChannel.setEncForm(true);
                                }
                            }
                        }
                    }
                }
            }

            for (String channel : macOutChannels.get(task)) {
                //Add MAC before writechannel
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                //First, find the connector that points to it. We will add the encryption, nonce operators directly before the write channel operator
                if (tmlc == null) {
                    tmlc = tmlmodel.getChannelByOriginPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                Set<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLWriteChannel) {
                        TMLWriteChannel writeChannel = (TMLWriteChannel) elem;
                        for (int i=0; i < writeChannel.getNbOfChannels(); i++) {
                            if (writeChannel.getChannel(i).getName().equals(tmlc.getName()) && writeChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                for (TMLActivityElement elem : channelInstances) {
                    //SecurityPattern secPattern = new SecurityPattern(channelSecMap.get(channel), SecurityPattern.MAC_PATTERN, overhead, overhead, encComp, decComp, "", "", "");
                    channelSecMap.get(channel).type = SecurityPattern.MAC_PATTERN;
                    TMLExecC encC = new TMLExecC(channelSecMap.get(channel).name, taskAD.getReferenceObject());
                    encC.securityPattern = new SecurityPattern(channelSecMap.get(channel));
                    encC.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    encC.setAction(Integer.toString(channelSecMap.get(channel).encTime));
                    tmlmodel.addSecurityPattern(channelSecMap.get(channel));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(channel))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(channel)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(channel)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(channel), listTask);
                    }

                    TMLActivityElement prevElem = taskAD.getPrevious(elem);
                    if (nonceOutChannels.get(task).contains(channel)) {
                        SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        if (tmlc != null) {
                            encC.securityPattern.nonce = secPatternNonce.getName();
                        }
                        boolean addNewReadNonce = true;
                        for (TMLActivityElement elemT : taskAD.getElements()) {
                            if (elemT instanceof TMLReadChannel) {
                                TMLReadChannel readElem = (TMLReadChannel) elemT;
                                if (readElem.securityPattern != null) {
                                    if(readElem.securityPattern.getName().equals(secPatternNonce.getName())) {
                                        addNewReadNonce = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (addNewReadNonce) {
                            //If we need to receive a nonce
                            TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                            //Receive any nonces if ensuring authenticity
                            List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());
                            if (matches.size() > 0) {
                                rd.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                                if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                                } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                                }
                            } else {
                                rd.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                                if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                                } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                    rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                                }
                            }
                            secPatternNonce.originTask = rd.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                            rd.securityPattern = secPatternNonce;
                            rd.setNbOfSamples("1");
                            TMLActivityElement nextFirst = taskAD.getFirst().getNextElement(0);
                            taskAD.getFirst().setNewNext(nextFirst, rd);
                            rd.addNext(nextFirst);
                            taskAD.addElement(rd);
                            
                        }
                    }
                    //Add encryption operator
                    prevElem = taskAD.getPrevious(elem);
                    prevElem.setNewNext(elem, encC);
                    encC.addNext(elem);
                    taskAD.addElement(encC);
                    //Add security contexts to write channels
                    for (TMLActivityElement elemA : taskAD.getElements()) {
                        if (elemA instanceof TMLWriteChannel) {
                            TMLWriteChannel wChannel = (TMLWriteChannel) elemA;
                            for (int i=0; i < wChannel.getNbOfChannels(); i++) {
                                if (channel.equals(wChannel.getChannel(i).getName().replaceAll(title + "__", "")) && wChannel.securityPattern == null) {
                                    wChannel.securityPattern = channelSecMap.get(channel); 
                                    // wChannel.setEncForm(true);
                                }
                            }
                        }
                    }
                }
            }
            for (String channel : hsmSecOutChannels.get(task)) {
                Set<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLWriteChannel) {
                        TMLWriteChannel writeChannel = (TMLWriteChannel) elem;
                        for (int i=0; i < writeChannel.getNbOfChannels(); i++) {
                            if (writeChannel.getChannel(i).getName().replaceAll(title + "__", "").equals(channel) && writeChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                for (TMLActivityElement chan : channelInstances) {
                    TMLWriteChannel writeChannel = (TMLWriteChannel) chan;
                    String chanName = writeChannel.getChannel(0).getName().replaceAll(title + "__", "");
                    TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
                    
                    if (tmlc == null) {
                        tmlc = tmlmodel.getChannelByOriginPortName(channel);
                    }
                    if (tmlc == null) {
                        continue;
                    }
                    writeChannel.securityPattern = channelSecMap.get(channel);
                    // writeChannel.setEncForm(true);
                    fromStart = taskAD.getPrevious(chan);

                    TMLSendRequest reqSend = new TMLSendRequest("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]), taskAD.getReferenceObject());
                    
                    TMLRequest req = tmlmodel.getRequestByName(reqSend.getName());
                    if (req != null) {
                        reqSend.setRequest(req);
                    }

                    if (reqSend.getNbOfParams() > 0) {
                        reqSend.setParam(Integer.toString(channelIndexMap.get(chanName)), 0);
                    } else {
                        reqSend.addParam(Integer.toString(channelIndexMap.get(chanName)));
                    }
                    
                    fromStart.setNewNext(chan, reqSend);
                    taskAD.addElement(reqSend);
                    //Add write channel operator
                    TMLWriteChannel wr = new TMLWriteChannel("data_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                    if (tmlmodel.getChannelByName(wr.getName()) != null) {
                        wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                    } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                        wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                    }
                    wr.setNbOfSamples("1"); 
                    // wr.setEncForm(false);
                    //wr.securityPattern = channelSecMap.get(chanName);
                    taskAD.addElement(wr);
                    reqSend.addNext(wr);
                    wr.addNext(chan);
                    //Receive any nonces if ensuring authenticity
                    /* if (nonceOutChannels.get(task).contains(channel)) {
                        //Read nonce from rec task

                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());
                        TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                        if (matches.size() > 0) {
                            rd.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                            if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                            } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                            }
                        } else {
                            rd.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                            if (tmlmodel.getChannelByName(rd.getName()) != null) {
                                rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                            } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                                rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                            }
                        }
                        SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        secPatternNonce.originTask = rd.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                        rd.securityPattern = secPatternNonce;
                        rd.setNbOfSamples("1"); 
                        taskAD.addElement(rd);
                        fromStart.setNewNext(reqSend, rd);
                        rd.addNext(reqSend);
                        //wr.setNewNext(chan, rd);

                        TMLWriteChannel wr2 = new TMLWriteChannel("data_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                        if (tmlmodel.getChannelByName(wr2.getName()) != null) {
                            wr2.addChannel(tmlmodel.getChannelByName(wr2.getName()));
                        } else if (tmlmodel.getChannelByShortName(wr2.getName()) != null) {
                            wr2.addChannel(tmlmodel.getChannelByShortName(wr2.getName()));
                        }
                        wr2.setNbOfSamples("1");
                        wr2.securityPattern = secPatternNonce;
                        taskAD.addElement(wr2);
                        reqSend.setNewNext(wr, wr2);
                        wr2.addNext(wr);
                    } */

                    //Read channel operator to receive hsm data
                    TMLReadChannel rd2 = new TMLReadChannel("retData_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                    if (tmlmodel.getChannelByName(rd2.getName()) != null) {
                        rd2.addChannel(tmlmodel.getChannelByName(rd2.getName()));
                    } else if (tmlmodel.getChannelByShortName(rd2.getName()) != null) {
                        rd2.addChannel(tmlmodel.getChannelByShortName(rd2.getName()));
                    }
                    rd2.securityPattern = channelSecMap.get(chanName);
                    rd2.setNbOfSamples("1"); 
                    taskAD.addElement(rd2);

                    fromStart = taskAD.getPrevious(chan);
                    fromStart.setNewNext(chan, rd2);
                    rd2.addNext(chan);
                }
            }

            for (String channel : hsmSecInChannels.get(task)) {
                //System.out.println("Checking hsmsecinchannel " + channel + " " + task.getName());
                Set<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                //TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                //TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLReadChannel) {
                        TMLReadChannel readChannel = (TMLReadChannel) elem;
                        for (int i=0; i<readChannel.getNbOfChannels(); i++) {
                            if (readChannel.getChannel(i).getName().replaceAll(title + "__", "").equals(channel) && readChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                //System.out.println("matches " + channelInstances);
                for (TMLActivityElement chan : channelInstances) {
                    TMLReadChannel readChannel = (TMLReadChannel) chan;
                    String chanName = readChannel.getChannel(0).getName().replaceAll(title + "__", "");
                    TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
                    if (tmlc == null) {
                        tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                    }
                    if (tmlc == null) {
                        continue;
                    }
                    readChannel.securityPattern = channelSecMap.get(chanName);
                    // readChannel.setEncForm(true);
                
                    fromStart = taskAD.getPrevious(chan);
                    TMLActivityElement nextReadCh = chan.getNextElement(0);
                   
                    TMLSendRequest reqSend = new TMLSendRequest("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]), taskAD.getReferenceObject());
                    TMLRequest req = tmlmodel.getRequestByName(reqSend.getName());
                    if (req != null) {
                        reqSend.setRequest(req);
                    }
                    if (reqSend.getNbOfParams() > 0) {
                        reqSend.setParam(Integer.toString(channelIndexMap.get(chanName)), 0);
                    } else {
                        reqSend.addParam(Integer.toString(channelIndexMap.get(chanName)));
                    }
                    taskAD.addElement(reqSend);
                    fromStart.setNewNext(chan, reqSend);
                    reqSend.addNext(chan);

                    //Add write channel operator
                    TMLWriteChannel wr = new TMLWriteChannel("data_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                    if (tmlmodel.getChannelByName(wr.getName()) != null) {
                        wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                    } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                        wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                    }
                    wr.securityPattern = channelSecMap.get(chanName);
                    wr.setNbOfSamples("1");
                    taskAD.addElement(wr);

                    //Add connector between request and write
                    chan.setNewNext(nextReadCh, wr);
                    
                
                    /*if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        TMLExecC nonce = new TMLExecC("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], taskAD.getReferenceObject());
                        SecurityPattern secNonce = new SecurityPattern(nonce.getName(), SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        
                        nonce.securityPattern = secNonce;
                        nonce.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                        nonce.setAction(Integer.toString(secNonce.encTime));
                        
                        tmlmodel.addSecurityPattern(secNonce);
                        if (tmlmodel.securityTaskMap.containsKey(secNonce)) {
                            if (!tmlmodel.securityTaskMap.get(secNonce).contains(task)) {
                                tmlmodel.securityTaskMap.get(secNonce).add(task);
                            }
                        } else {
                            List<TMLTask> listTask = new ArrayList<TMLTask>();
                            listTask.add(task);
                            tmlmodel.securityTaskMap.put(secNonce, listTask);
                        }
                        
                        taskAD.addElement(nonce);
                        fromStart.setNewNext(chan, nonce);

                        TMLWriteChannel wr3 = new TMLWriteChannel("", taskAD.getReferenceObject());
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr3.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                            if (tmlmodel.getChannelByName(wr3.getName()) != null) {
                                wr3.addChannel(tmlmodel.getChannelByName(wr3.getName()));
                            } else if (tmlmodel.getChannelByShortName(wr3.getName()) != null) {
                                wr3.addChannel(tmlmodel.getChannelByShortName(wr3.getName()));
                            }
                        } else {
                            wr3.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                            if (tmlmodel.getChannelByName(wr3.getName()) != null) {
                                wr3.addChannel(tmlmodel.getChannelByName(wr3.getName()));
                            } else if (tmlmodel.getChannelByShortName(wr3.getName()) != null) {
                                wr3.addChannel(tmlmodel.getChannelByShortName(wr3.getName()));
                            }
                        }
                        //send the nonce along the channel
                        secNonce.originTask = wr3.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                        wr3.securityPattern = secNonce;
                        
                        wr3.setNbOfSamples("1");
                        taskAD.addElement(wr3);
                        nonce.addNext(wr3);
                        wr3.addNext(chan);

                        //Also send nonce to hsm
                        TMLWriteChannel wr2 = new TMLWriteChannel("data_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                        if (tmlmodel.getChannelByName(wr2.getName()) != null) {
                            wr2.addChannel(tmlmodel.getChannelByName(wr2.getName()));
                        } else if (tmlmodel.getChannelByShortName(wr2.getName()) != null) {
                            wr2.addChannel(tmlmodel.getChannelByShortName(wr2.getName()));
                        }
                        wr2.securityPattern =  secNonce;
                        wr2.setNbOfSamples("1");
                        taskAD.addElement(wr2);

                        reqSend.setNewNext(wr, wr2);
                        wr2.addNext(wr);
                    }*/


                    //Add read channel operator

                    TMLReadChannel rd = new TMLReadChannel("retData_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                    if (tmlmodel.getChannelByName(rd.getName()) != null) {
                        rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                    } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                        rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                    }
                    //rd.securityPattern = channelSecMap.get(chanName);
                    rd.setNbOfSamples("1"); 
                    // rd.setEncForm(false);
                    taskAD.addElement(rd);

                    wr.addNext(rd);
                    rd.addNext(nextReadCh);

                }
            }
            for (String channel : macInChannels.get(task)) {
                //Add decryptmac after readchannel
                Set<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                //Find read channel operator
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                if (tmlc == null) {
                    tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLReadChannel) {
                        TMLReadChannel readChannel = (TMLReadChannel) elem;
                        for (int i = 0; i<readChannel.getNbOfChannels(); i++) {
                            if (readChannel.getChannel(i).getName().equals(tmlc.getName()) && readChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        } 
                    }
                }
                for (TMLActivityElement elem : channelInstances) {
                    fromStart = taskAD.getPrevious(elem);

                    TMLReadChannel readChannel = (TMLReadChannel) elem;
                    readChannel.securityPattern = channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", ""));
                    //Create nonce and send it
                    if (nonceInChannels.get(task).contains(channel)) {
                        TMLExecC nonce = new TMLExecC("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], taskAD.getReferenceObject());
                        SecurityPattern secNonce = new SecurityPattern(nonce.getName(), SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        nonce.securityPattern = secNonce;
                        nonce.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                        nonce.setAction(Integer.toString(secNonce.encTime));
                        boolean addNewExecCNonce = true;
                        for (TMLActivityElement elemT : taskAD.getElements()) {
                            if (elemT instanceof TMLExecC) {
                                TMLExecC exeCElem = (TMLExecC) elemT;
                                if (exeCElem.securityPattern != null) {
                                    if(exeCElem.securityPattern.getName().equals(secNonce.getName())) {
                                        addNewExecCNonce = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (addNewExecCNonce) {
                            //Create a nonce operator and a write channel operator
                            tmlmodel.addSecurityPattern(secNonce);
                            if (tmlmodel.securityTaskMap.containsKey(secNonce)) {
                                if (!tmlmodel.securityTaskMap.get(secNonce).contains(task)) {
                                    tmlmodel.securityTaskMap.get(secNonce).add(task);
                                }
                            } else {
                                List<TMLTask> listTask = new ArrayList<TMLTask>();
                                listTask.add(task);
                                tmlmodel.securityTaskMap.put(secNonce, listTask);
                            }
                            TMLActivityElement first = taskAD.getFirst();
                            TMLActivityElement nextFirst = first.getNextElement(0);
                            taskAD.addElement(nonce);
                            first.setNewNext(nextFirst, nonce);
                            TMLWriteChannel wr = new TMLWriteChannel("", taskAD.getReferenceObject());
                            //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                            //Find matching channels
                            List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                            if (matches.size() > 0) {
                                wr.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                                if (tmlmodel.getChannelByName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                                } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                                }
                            } else {
                                wr.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                                if (tmlmodel.getChannelByName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                                } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                                }
                            }
                            secNonce.originTask = wr.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                            //send the nonce along the channel
                            wr.setNbOfSamples("1");
                            wr.securityPattern = secNonce;
                            taskAD.addElement(wr);
                            nonce.addNext(wr);
                            wr.addNext(nextFirst);
                        }
                    }

                    //Add decryption operator if it does not already exist
                    TMLExecC dec = new TMLExecC(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", "")).name, taskAD.getReferenceObject());
                    dec.securityPattern = new SecurityPattern(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", "")));
                    dec.setAction(Integer.toString(dec.securityPattern.encTime));
                    tmlmodel.addSecurityPattern(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", "")));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", "")))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", ""))).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", ""))).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(readChannel.getChannel(0).getName().replaceAll(title + "__", "")), listTask);
                    }
                    dec.securityPattern.setProcess(SecurityPattern.DECRYPTION_PROCESS);
                    taskAD.addElement(dec);
                    dec.addNext(readChannel.getNextElement(0));
                    readChannel.setNewNext(readChannel.getNextElement(0), dec);
                    //Shift everything down
                    for (TMLActivityElement elemA : taskAD.getElements()) {
                        if (elemA instanceof TMLReadChannel) {
                            TMLReadChannel rdOfSameCh = (TMLReadChannel) elemA;
                            for (int i=0; i<rdOfSameCh.getNbOfChannels(); i++) {
                                if (channel.equals(rdOfSameCh.getChannel(i).getName().replaceAll(title + "__", "")) && rdOfSameCh.securityPattern == null) {
                                    rdOfSameCh.securityPattern = channelSecMap.get(channel);
                                    // rdOfSameCh.setEncForm(true);
                                }
                            }
                        }
                    }
                }
            }
            for (String channel : secInChannels.get(task)) {
                TraceManager.addDev("securing channel " + channel);
                //Find read channel operator
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                if (tmlc == null) {
                    tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                HashSet<TMLActivityElement> channelInstances = new HashSet<TMLActivityElement>();
                for (TMLActivityElement elem : taskAD.getElements()) {
                    if (elem instanceof TMLReadChannel) {
                        TMLReadChannel readChannel = (TMLReadChannel) elem;
                        for (int i=0; i<readChannel.getNbOfChannels(); i++) {
                            if (readChannel.getChannel(i).getName().equals(tmlc.getName()) && readChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }

                for (TMLActivityElement elem : channelInstances) {
                    TMLReadChannel readChannel = (TMLReadChannel) elem;
                    fromStart = taskAD.getPrevious(elem);
                    if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        TMLExecC nonce = new TMLExecC("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], taskAD.getReferenceObject());
                        SecurityPattern secNonce = new SecurityPattern(nonce.getName(), SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        nonce.securityPattern = secNonce;
                        nonce.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                        nonce.setAction(Integer.toString(secNonce.encTime));
                        boolean addNewExecCNonce = true;
                        for (TMLActivityElement elemT : taskAD.getElements()) {
                            if (elemT instanceof TMLExecC) {
                                TMLExecC exeCElem = (TMLExecC) elemT;
                                if (exeCElem.securityPattern != null) {
                                    if(exeCElem.securityPattern.getName().equals(secNonce.getName())) {
                                        addNewExecCNonce = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (addNewExecCNonce) {
                            tmlmodel.addSecurityPattern(secNonce);
                            if (tmlmodel.securityTaskMap.containsKey(secNonce)) {
                                if (!tmlmodel.securityTaskMap.get(secNonce).contains(task)) {
                                    tmlmodel.securityTaskMap.get(secNonce).add(task);
                                }
                            } else {
                                List<TMLTask> listTask = new ArrayList<TMLTask>();
                                listTask.add(task);
                                tmlmodel.securityTaskMap.put(secNonce, listTask);
                            }

                            taskAD.addElement(nonce);
                            TMLActivityElement first = taskAD.getFirst();
                            TMLActivityElement nextFirst = first.getNextElement(0);
                            first.setNewNext(nextFirst, nonce);
                            TMLWriteChannel wr = new TMLWriteChannel("", taskAD.getReferenceObject());
                            //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                            //Find matching channels
                            List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                            if (matches.size() > 0) {
                                wr.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                                if (tmlmodel.getChannelByName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                                } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                                }
                            } else {
                                wr.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                                if (tmlmodel.getChannelByName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                                } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                                    wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                                }
                            }
                            secNonce.originTask = wr.getChannel(0).getOriginTask().getName().replaceAll(title + "__", "");
                            //send the nonce along the channel
                            wr.setNbOfSamples("1");
                            wr.securityPattern = secNonce;
                            taskAD.addElement(wr);
                            nonce.addNext(wr);
                            wr.addNext(nextFirst);
                        }
                    }

                    //Now add the decrypt operator
                    String readChShortName = readChannel.getChannel(0).getName().replaceAll(title + "__", "");
                    readChannel.securityPattern = channelSecMap.get(readChShortName);
                    // readChannel.setEncForm(true);
                    //Add decryption operator if it does not already exist
                    TMLExecC dec = new TMLExecC(channelSecMap.get(readChShortName).name, taskAD.getReferenceObject());
                    dec.securityPattern = new SecurityPattern(channelSecMap.get(readChShortName));
                    dec.securityPattern.setProcess(SecurityPattern.DECRYPTION_PROCESS);
                    dec.setAction(Integer.toString(channelSecMap.get(readChShortName).encTime));

                    tmlmodel.addSecurityPattern(channelSecMap.get(readChShortName));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(readChShortName))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(readChShortName)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(readChShortName)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(readChShortName), listTask);
                    }

                    taskAD.addElement(dec);
                    dec.addNext(readChannel.getNextElement(0));
                    readChannel.setNewNext(readChannel.getNextElement(0), dec);
                    //Shift everything down
                    for (TMLActivityElement elemA : taskAD.getElements()) {
                        if (elemA instanceof TMLReadChannel) {
                            TMLReadChannel rdOfSameCh = (TMLReadChannel) elemA;
                            for (int i=0; i < rdOfSameCh.getNbOfChannels(); i++) {
                                String readSameChShortName = rdOfSameCh.getChannel(i).getName().replaceAll(title + "__", "");
                                if (channel.equals(readSameChShortName) && rdOfSameCh.securityPattern == null) {
                                    rdOfSameCh.securityPattern = channelSecMap.get(readSameChShortName);
                                    // rdOfSameCh.setEncForm(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        return;
    }

    public void buildHSMActivityDiagram(String cpuName) {
        TMLModeling<?> tmlmodel = tmap.getTMLModeling();
        //Build HSM Activity diagram
        TMLTask task = tmap.getTaskByName("HSM_" + cpuName);
        TMLActivity taskAD = task.getActivityDiagram();
        if (taskAD == null) {
            return;
        }

        TMLActivityElement start = taskAD.getFirst();
        if (start == null) {
            start = new TMLStartState("start", taskAD.getReferenceObject());
            taskAD.setFirst(start);
        }
        
        //fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());

        if (hsmChannelMap.get(cpuName).size() == 0) {
            TMLStopState stop = new TMLStopState("stop", taskAD.getReferenceObject());
            taskAD.addElement(stop);
            //Connect stop and start
            start.addNext(stop);
            return;
        }
        TMLActionState req = new TMLActionState("action", taskAD.getReferenceObject());
        if (task.getAttributeByName("channelIndex") == null) {
            TMLAttribute attrib = new TMLAttribute("channelIndex", new TMLType(TMLType.NATURAL), "0");
            task.addAttribute(attrib);
        }
        if (task.getAttributeByName("arg0__req") == null) {
            TMLAttribute attrib = new TMLAttribute("arg0__req", new TMLType(TMLType.NATURAL), "0");
            task.addAttribute(attrib);
        }
        req.setAction("channelIndex = arg0__req");
        taskAD.addElement(req);
        //Connect start and readrequest
        start.addNext(req);

        TMLActivityElement lastCurElem = req;
        List<String> readNonces = new ArrayList<String>();
        List<String> writeNonces = new ArrayList<String>();
        for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
            if (!ch.nonceName.equals("")) {
                if (ch.secType == HSMChannel.DEC) {
                    TMLExecC nonce = new TMLExecC("nonce_" + ch.task + "_" + tmlmodel.getChannelByShortName(ch.name).getOriginTask().getName().replaceAll(appName + "__", ""), taskAD.getReferenceObject());
                    SecurityPattern secNonce = new SecurityPattern(nonce.getName(), SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                    nonce.securityPattern = secNonce;
                    nonce.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    nonce.setAction(Integer.toString(secNonce.encTime));
                    if (!writeNonces.contains(nonce.getName())) {
                        tmlmodel.addSecurityPattern(secNonce);
                        if (tmlmodel.securityTaskMap.containsKey(secNonce)) {
                            if (!tmlmodel.securityTaskMap.get(secNonce).contains(task)) {
                                tmlmodel.securityTaskMap.get(secNonce).add(task);
                            }
                        } else {
                            List<TMLTask> listTask = new ArrayList<TMLTask>();
                            listTask.add(task);
                            tmlmodel.securityTaskMap.put(secNonce, listTask);
                        }

                        taskAD.addElement(nonce);

                        if (lastCurElem.getNbNext()>0) {
                            lastCurElem.setNewNext(lastCurElem.getNextElement(0), nonce);
                        } else {
                            lastCurElem.addNext(nonce);
                        }
                        TMLWriteChannel wr = new TMLWriteChannel("", taskAD.getReferenceObject());
                        wr.setName("nonceCh" + ch.task + "_" + tmlmodel.getChannelByShortName(ch.name).getOriginTask().getName().split("__")[1]);
                        if (tmlmodel.getChannelByName(wr.getName()) != null) {
                            wr.addChannel(tmlmodel.getChannelByName(wr.getName()));
                        } else if (tmlmodel.getChannelByShortName(wr.getName()) != null) {
                            wr.addChannel(tmlmodel.getChannelByShortName(wr.getName()));
                        }
                        secNonce.originTask = task.getName();
                        //send the nonce along the channel
                        wr.setNbOfSamples("1");
                        wr.securityPattern = secNonce;
                        taskAD.addElement(wr);
                        nonce.addNext(wr);
                        lastCurElem = wr;
                        writeNonces.add(nonce.getName());
                    }
                } else {
                    SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlmodel.getChannelByShortName(ch.name).getDestinationTask().getName().replaceAll(appName + "__", "") + "_" + ch.task, SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                    if (!readNonces.contains(secPatternNonce.getName())) {
                        //If we need to receive a nonce
                        TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                        //Receive any nonces if ensuring authenticity
                        rd.setName("nonceCh" + tmlmodel.getChannelByShortName(ch.name).getDestinationTask().getName().replaceAll(appName + "__", "") + "_" + ch.task);
                        if (tmlmodel.getChannelByName(rd.getName()) != null) {
                            rd.addChannel(tmlmodel.getChannelByName(rd.getName()));
                        } else if (tmlmodel.getChannelByShortName(rd.getName()) != null) {
                            rd.addChannel(tmlmodel.getChannelByShortName(rd.getName()));
                        }
                        secPatternNonce.originTask = rd.getChannel(0).getOriginTask().getName().replaceAll(appName + "__", "");
                        rd.securityPattern = secPatternNonce;
                        rd.setNbOfSamples("1");
                        if (lastCurElem.getNbNext() > 0) {
                            lastCurElem.setNewNext(lastCurElem.getNextElement(0), rd);
                        } else {
                            lastCurElem.addNext(rd);
                        }
                        lastCurElem = rd;
                        taskAD.addElement(rd);
                        readNonces.add(secPatternNonce.getName());
                    }
                }
            }
        }
       

        TMLChoice choice = new TMLChoice("choice", taskAD.getReferenceObject());
        taskAD.addElement(choice);

        //Connect lastCurElem and choice
        lastCurElem.addNext(choice);

        //Allows 9 channels max to simplify the diagram

        //If more than 3 channels, build 2 levels of choices

        if (hsmChannelMap.get(cpuName).size() > 3) {
            TMLChoice choice2 = new TMLChoice("choice2", taskAD.getReferenceObject());
            int i = 0;
            for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
                if (i > 8) {
                    break;
                }
                if (i % 3 == 0) {
                    //Add a new choice every third channel
                    choice2 = new TMLChoice("choice2", taskAD.getReferenceObject());
                    taskAD.addElement(choice2);
                    //Connect new choice operator to top choice
                    choice.addGuard("");
                    choice.addNext(choice2);
                }
                choice2.addGuard("[channelIndex==" + channelIndexMap.get(ch.name) + "]");

                if (choice.getGuard(choice.getNbGuard()-1).length() == 2) {
                    choice.setGuardAt(choice.getNbGuard()-1,"[(" + choice2.getGuard(choice2.getNbGuard()-1).substring(1, choice2.getGuard(choice2.getNbGuard()-1).length()-1) + ")]");
                } else {
                    choice.setGuardAt(choice.getNbGuard()-1, choice.getGuard(choice.getNbGuard()-1).substring(0, choice.getGuard(choice.getNbGuard()-1).length()-1) + " or (" + choice2.getGuard(choice2.getNbGuard()-1).substring(1, choice2.getGuard(choice2.getNbGuard()-1).length()-1) + ")]");
                }
                
                TMLActivityElement prevRd = choice2;
                //If needed, receive nonce from task
                /*if (!ch.nonceName.equals("")) {
                    //Connect choice and readchannel
                    TMLReadChannel rdNonce = new TMLReadChannel(("data_" + ch.name + "_" + ch.task), taskAD.getReferenceObject());
                    rdNonce.addChannel(tmlmodel.getChannelByName("data_" + ch.name + "_" + ch.task));
                    rdNonce.securityPattern = new SecurityPattern(ch.nonceName, SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                    rdNonce.securityPattern.originTask = rdNonce.getChannel(0).getOriginTask().getName().replaceAll(appName + "__", "");
                    rdNonce.setNbOfSamples("1"); 
                    taskAD.addElement(rdNonce);

                    //choice2.getNextElement(choice2.getNbNext()-1).addNext(rd);
                    choice2.addNext(rdNonce);
                    prevRd = rdNonce;
                }*/
                
                TMLReadChannel rd = new TMLReadChannel("data_" + ch.name + "_" + ch.task, taskAD.getReferenceObject());
                rd.addChannel(tmlmodel.getChannelByName("data_" + ch.name + "_" + ch.task));
                //
                rd.setNbOfSamples("1"); 
                taskAD.addElement(rd);
                prevRd.addNext(rd);
                //Recieve plaintext data if encrypting
                /* if (ch.secType != HSMChannel.DEC) {
                    rd.setEncForm(false);
                } */

                TMLWriteChannel wr = new TMLWriteChannel("retData_" + ch.name + "_" + ch.task, taskAD.getReferenceObject());
                wr.addChannel(tmlmodel.getChannelByName("retData_" + ch.name + "_" + ch.task));
                taskAD.addElement(wr);
                wr.setNbOfSamples("1");
                if (ch.secType == HSMChannel.DEC) {
                    rd.securityPattern = channelSecMap.get(ch.name);
                    TMLExecC dec = new TMLExecC(channelSecMap.get(ch.name).name, taskAD.getReferenceObject());
                    dec.securityPattern = new SecurityPattern(channelSecMap.get(ch.name));
                    dec.securityPattern.setProcess(SecurityPattern.DECRYPTION_PROCESS);
                    dec.setAction(Integer.toString(channelSecMap.get(ch.name).encTime));

                    tmlmodel.addSecurityPattern(channelSecMap.get(ch.name));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(ch.name))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(ch.name), listTask);
                    }

                    taskAD.addElement(dec);
                    rd.addNext(dec);

                    //Connect decrypt and writechannel
                    dec.addNext(wr);
                    //Add Stop
                    TMLStopState stop = new TMLStopState("stop", taskAD.getReferenceObject());
                    taskAD.addElement(stop);

                    //Connext stop and write channel
                    wr.addNext(stop);
                } else {
                    wr.securityPattern = channelSecMap.get(ch.name);
                    TMLExecC enc = new TMLExecC(channelSecMap.get(ch.name).name, taskAD.getReferenceObject());
                    enc.securityPattern = new SecurityPattern(channelSecMap.get(ch.name));
                    enc.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    enc.setAction(Integer.toString(channelSecMap.get(ch.name).encTime));

                    tmlmodel.addSecurityPattern(channelSecMap.get(ch.name));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(ch.name))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(ch.name), listTask);
                    }
                    
                    if (ch.secType == HSMChannel.SENC) {
                        enc.securityPattern.type = SecurityPattern.SYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.AENC) {
                        enc.securityPattern.type = SecurityPattern.ASYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.MAC) {
                        enc.securityPattern.type = SecurityPattern.MAC_PATTERN;
                    } else if (ch.secType == HSMChannel.NONCE) {
                        enc.securityPattern.type = SecurityPattern.NONCE_PATTERN;
                    }

                    enc.securityPattern.overhead = Integer.parseInt(overhead);
                    enc.securityPattern.encTime = Integer.parseInt(encComp);
                    enc.securityPattern.decTime = Integer.parseInt(decComp);
                    enc.securityPattern.nonce = ch.nonceName;
                    taskAD.addElement(enc);

                    //Connect encrypt and readchannel
                    rd.addNext(enc);
                    //Connect encrypt and writechannel
                    enc.addNext(wr);

                    //Add Stop
                    TMLStopState stop = new TMLStopState("stop", taskAD.getReferenceObject());
                    taskAD.addElement(stop);

                    //Connext stop and write channel
                    wr.addNext(stop);
                }
                i++;
            }
        } else {

            for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
                //Add guard as channelindex
                choice.addGuard("[channelIndex==" + channelIndexMap.get(ch.name) + "]");
                
                TMLActivityElement prevRd = choice;
                //If needed, receive nonce from task
                /*if (!ch.nonceName.equals("")) {
                    TMLReadChannel rdNonce = new TMLReadChannel(("data_" + ch.name + "_" + ch.task), taskAD.getReferenceObject());
                    rdNonce.addChannel(tmlmodel.getChannelByName("data_" + ch.name + "_" + ch.task));
                    rdNonce.securityPattern = new SecurityPattern(ch.nonceName, SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                    rdNonce.securityPattern.originTask = rdNonce.getChannel(0).getOriginTask().getName().replaceAll(appName + "__", "");
                    rdNonce.setNbOfSamples("1"); 
                    taskAD.addElement(rdNonce);

                    choice.addNext(rdNonce);
                    prevRd = rdNonce;
                }*/

                TMLReadChannel rd = new TMLReadChannel("data_" + ch.name + "_" + ch.task, taskAD.getReferenceObject());
                rd.addChannel(tmlmodel.getChannelByName("data_" + ch.name + "_" + ch.task));
                
                rd.setNbOfSamples("1"); 
                taskAD.addElement(rd);

                //Recieve plaintext data if encrypting
                /* if (ch.secType != HSMChannel.DEC) {
                    rd.setEncForm(false);
                } */

                //Connect choice and readchannel
                prevRd.addNext(rd);

                //Send data back to task
                TMLWriteChannel wr = new TMLWriteChannel("retData_" + ch.name + "_" + ch.task, taskAD.getReferenceObject());
                wr.addChannel(tmlmodel.getChannelByName("retData_" + ch.name + "_" + ch.task));

                //Return plaintext data if decrypting
                /* if (ch.secType == HSMChannel.DEC) {
                    wr.setEncForm(false);
                } */
                wr.setNbOfSamples("1");
                
                taskAD.addElement(wr);
                if (ch.secType == HSMChannel.DEC) {
                    rd.securityPattern = channelSecMap.get(ch.name);
                    TraceManager.addDev("Add Decrypt operator");
                    //Add Decrypt operator
                    TMLExecC dec = new TMLExecC(channelSecMap.get(ch.name).name, taskAD.getReferenceObject());
                    dec.securityPattern = new SecurityPattern(channelSecMap.get(ch.name));
                    dec.securityPattern.setProcess(SecurityPattern.DECRYPTION_PROCESS);
                    dec.setAction(Integer.toString(channelSecMap.get(ch.name).encTime));

                    tmlmodel.addSecurityPattern(channelSecMap.get(ch.name));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(ch.name))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(ch.name), listTask);
                    }
                    
                    taskAD.addElement(dec);

                    //Connect decrypt and readchannel
                    rd.addNext(dec);

                    //Connect decrypt and writechannel
                    dec.addNext(wr);

                    //Add Stop
                    TMLStopState stop = new TMLStopState("stop", taskAD.getReferenceObject());
                    taskAD.addElement(stop);

                    //Connect stop and write channel
                    wr.addNext(stop);

                } else {
                    wr.securityPattern = channelSecMap.get(ch.name);
                    TraceManager.addDev("Add Encrypt operator");
                    TMLExecC enc = new TMLExecC(channelSecMap.get(ch.name).name, taskAD.getReferenceObject());
                    enc.securityPattern = new SecurityPattern(channelSecMap.get(ch.name));
                    enc.securityPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    enc.setAction(Integer.toString(channelSecMap.get(ch.name).encTime));

                    tmlmodel.addSecurityPattern(channelSecMap.get(ch.name));
                    if (tmlmodel.securityTaskMap.containsKey(channelSecMap.get(ch.name))) {
                        if (!tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).contains(task)) {
                            tmlmodel.securityTaskMap.get(channelSecMap.get(ch.name)).add(task);
                        }
                    } else {
                        List<TMLTask> listTask = new ArrayList<TMLTask>();
                        listTask.add(task);
                        tmlmodel.securityTaskMap.put(channelSecMap.get(ch.name), listTask);
                    }

                    if (ch.secType == HSMChannel.SENC) {
                        enc.securityPattern.type = SecurityPattern.SYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.AENC) {
                        enc.securityPattern.type = SecurityPattern.ASYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.MAC) {
                        enc.securityPattern.type = SecurityPattern.MAC_PATTERN;
                    } else if (ch.secType == HSMChannel.NONCE) {
                        enc.securityPattern.type = SecurityPattern.NONCE_PATTERN;
                    }

                    enc.securityPattern.overhead = Integer.parseInt(overhead);
                    enc.securityPattern.encTime = Integer.parseInt(encComp);
                    enc.securityPattern.decTime = Integer.parseInt(decComp);
                    enc.securityPattern.nonce = ch.nonceName;
                    taskAD.addElement(enc);

                    //Connect encrypt and readchannel
                    rd.addNext(enc);

                    //Connect encrypt and writechannel
                    enc.addNext(wr);

                    //Add Stop
                    TMLStopState stop = new TMLStopState("stop", taskAD.getReferenceObject());
                    taskAD.addElement(stop);
                    //Connect stop and write channel
                    wr.addNext(stop);
                }
            }

        }
    }

    public TMLMapping<?> autoMapKeys() {
        if (tmap == null) {
            return tmap;
        }
        //Find all Security Patterns, if they don't have an associated memory at encrypt and decrypt, tmap them
        TMLModeling<?> tmlm = tmap.getTMLModeling();
        if (tmlm.securityTaskMap == null) {
            return tmap;
        }
        for (SecurityPattern sp : tmlm.securityTaskMap.keySet()) {
            if (sp.type.equals(SecurityPattern.SYMMETRIC_ENC_PATTERN) || sp.type.equals(SecurityPattern.MAC_PATTERN) || sp.type.equals(SecurityPattern.ASYMMETRIC_ENC_PATTERN)) {
                for (TMLTask t : tmlm.securityTaskMap.get(sp)) {
                    HwExecutionNode node1 = tmap.getHwNodeOf(t);
                    boolean taskMappedToCPU = false;
                    if (node1 != null) {
                        if (node1 instanceof HwExecutionNode) {
                            HwExecutionNode cpuNode = node1;
                            taskMappedToCPU = true;
                            boolean keyMappedtoMem = false;
                            HwLink lastLink = null;
                            for (int i=0; i < tmap.getArch().getHwLinks().size(); i++) {
                                HwLink link = tmap.getArch().getHwLinks().get(i);
                                if (!keyMappedtoMem && link.hwnode == node1) {
                                    lastLink = link;
                                    if (link.bus.privacy == 1) {
                                        HwBus curBus = link.bus;
                                        boolean keyFound = false;
                                        HwMemory memNodeToMap = null;
                                        outer:
                                        for (HwLink linkBus : tmap.getArch().getHwLinks()) {
                                            if (linkBus.bus == curBus) {
                                                if (linkBus.hwnode instanceof HwMemory) {
                                                    memNodeToMap = (HwMemory) linkBus.hwnode;
                                                    List<SecurityPattern> keys = tmap.getMappedPatterns(memNodeToMap);
                                                    if (keys.contains(sp)) {
                                                        keyFound = true;
                                                        keyMappedtoMem = true;
                                                        break outer;
                                                    }
                                                }
                                            }
                                        }
                                        if (!keyFound) {
                                            if (memNodeToMap != null) {
                                                TraceManager.addDev("Adding " + sp.name + " key to " + memNodeToMap.getName());
                                                tmap.addSecurityPattern(memNodeToMap, sp);
                                                keyMappedtoMem = true;
                                            } else {
                                                HwMemory newHwMemory = new HwMemory(cpuNode.getName() + "KeysMemory");
                                                TraceManager.addDev("Creating new memory: " + newHwMemory.getName());
                                                tmap.getArch().addHwNode(newHwMemory);
                                                
                                                //Connect Bus and Memory
                                                HwLink linkNewMemWithBus = new HwLink("link_" + newHwMemory.getName() + "_to_" + curBus.getName());
                                                linkNewMemWithBus.setNodes(curBus, newHwMemory);
                                                tmap.getArch().getHwLinks().add(linkNewMemWithBus);
                                                tmap.addSecurityPattern(newHwMemory, sp);
                                                TraceManager.addDev("Adding " + sp.name + " key to " + newHwMemory.getName());
                                                keyMappedtoMem = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (!keyMappedtoMem) {
                                if (lastLink != null) {
                                    HwBus lastBusNode = lastLink.bus;
                                    HwExecutionNode cpuArchiNode = cpuNode;

                                    HwBridge newBridge = new HwBridge(cpuNode.getName() + "KeysBrigde");
                                    tmap.getArch().addHwNode(newBridge);

                                    HwBus newPrivateBus = new HwBus(cpuNode.getName() + "KeysPrivateBus");
                                    newPrivateBus.privacy = HwBus.BUS_PRIVATE;
                                    for (TMLElement elem : tmap.getLisMappedChannels(lastBusNode)) {
                                        if (elem instanceof TMLChannel) {
                                            tmap.addCommToHwCommNode(elem, newPrivateBus);
                                        }
                                    }
                                    tmap.getArch().addHwNode(newPrivateBus);
                                   
                                    HwMemory memNodeToMap = new HwMemory(cpuNode.getName() + "KeysMemory");
                                    TraceManager.addDev("Creating new memory: " + memNodeToMap.getName());
                                    tmap.getArch().addHwNode(memNodeToMap);

                                    tmap.addSecurityPattern(memNodeToMap, sp);
                                    TraceManager.addDev("Adding " + sp.name + " key to " + memNodeToMap.getName());
                                    keyMappedtoMem = true;

                                    //Connect Bus and Memory
                                    HwLink newLinkBusMemory = new HwLink("Link_"+newPrivateBus.getName() + "_" + memNodeToMap.getName());
                                    newLinkBusMemory.setNodes(newPrivateBus, memNodeToMap);
                                    tmap.getArch().addHwLink(newLinkBusMemory);

                                    //Connect new Private Bus and Bridge
                                    HwLink newLinkPrivateBusBridge = new HwLink("Link_"+newPrivateBus.getName() + "_" + newBridge.getName());
                                    newLinkPrivateBusBridge.setNodes(newPrivateBus, newBridge);
                                    tmap.getArch().addHwLink(newLinkPrivateBusBridge);

                                    //Connect Public Bus and Bridge
                                    HwLink newLinkPublicBusBridge = new HwLink("Link_"+lastBusNode.getName() + "_" + newBridge.getName());
                                    newLinkPublicBusBridge.setNodes(lastLink.bus, newBridge);
                                    tmap.getArch().addHwLink(newLinkPublicBusBridge);

                                    //Connect new Private Bus and CPU
                                    HwLink newLinkPrivateBusCPU = new HwLink("Link_"+newPrivateBus.getName() + "_" + cpuArchiNode.getName());
                                    newLinkPrivateBusCPU.setNodes(newPrivateBus, cpuArchiNode);
                                    tmap.getArch().addHwLink(newLinkPrivateBusCPU);


                                    //Disconnect Public Bus and CPU
                                    HwLink linkToRemove = null;
                                    for (HwLink li: tmap.getArch().getHwLinks()) {
                                        if (li.bus == lastLink.bus && li.hwnode == cpuNode) {
                                            TraceManager.addDev("Disconnect :" + li.bus.getName() + " and " + li.hwnode.getName());
                                            linkToRemove = li;
                                            break;
                                        }       
                                    }
                                    if (linkToRemove != null) {
                                        tmap.getArch().getHwLinks().remove(linkToRemove);
                                    }
                                }
                            }
                        }
                    }
                    if (!taskMappedToCPU) {
                        TraceManager.addDev(t.getTaskName() + " has to be mapped to a CPU!");
                    }
                }
            }
        }
        TraceManager.addDev("Mapping finished");
        return tmap;
    }

    class HSMChannel {
        public static final int SENC = 0;
        public static final int NONCE_ENC = 1;
        public static final int MAC = 2;
        public static final int DEC = 3;
        public static final int AENC = 4;
        public static final int NONCE = 5;
        public String name;
        public String task;
        public String securityContext = "";
        public int secType;
        public String nonceName = "";

        public HSMChannel(String n, String t, int type) {
            name = n;
            task = t;
            secType = type;
        }
    }

    class ChannelData {
        public String name;
        public boolean isOrigin;
        public boolean isChan;

        public ChannelData(String n, boolean orig, boolean isCh) {
            name = n;
            isOrigin = orig;
            isChan = isCh;
        }

    }

}
