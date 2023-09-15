package tmltranslator.patternhandling;

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
import ui.TGConnectingPoint;
import ui.TGConnector;
import ui.tmlad.TGConnectorTMLAD;
import ui.tmlad.TMLADChoice;
import ui.tmlad.TMLADDecrypt;
import ui.tmlad.TMLADEncrypt;
import ui.tmlad.TMLADReadChannel;
import ui.tmlad.TMLADReadRequestArg;
import ui.tmlad.TMLADSendRequest;
import ui.tmlad.TMLADStartState;
import ui.tmlad.TMLADStopState;
import ui.tmlad.TMLADWriteChannel;
import ui.tmlad.TMLActivityDiagramPanel;

import java.awt.*;
import java.io.Reader;
import java.util.List;
import java.util.*;


public class SecurityGenerationForTMAP implements Runnable {
    String name;
    TMLMapping<?> map;
    String encComp;
    String overhead;
    String decComp;
    Map<String, List<String>> selectedCPUTasks;
    boolean autoConf;
    boolean autoWeakAuth;
    boolean autoStrongAuth;
    int channelIndex = 0;

    AVATAR2ProVerif avatar2proverif;
    AvatarSpecification avatarspec;
    ProVerifSpec proverif;
    Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();

    Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();

    Map<String, Integer> channelIndexMap = new HashMap<String, Integer>();

    Map<String, List<HSMChannel>> hsmChannelMap = new HashMap<String, List<HSMChannel>>();

    Map<String, String> taskHSMMap = new HashMap<String, String>();
    List<String> hsmTasks = new ArrayList<String>();

    Map<String, String> channelSecMap = new HashMap<String, String>();
    TMLMapping<?> newMap;

    public SecurityGenerationForTMAP(String name, TMLMapping<?> map, String encComp, String overhead, String decComp, boolean autoConf, boolean autoWeakAuth, boolean autoStrongAuth, Map<String, List<String>> selectedCPUTasks) {

        this.name = name;
        this.map = map;
        this.newMap = map;
        this.overhead = overhead;
        this.decComp = decComp;
        this.autoConf = autoConf;
        this.autoWeakAuth = autoWeakAuth;
        this.autoStrongAuth = autoStrongAuth;
        this.selectedCPUTasks = selectedCPUTasks;
    }
    
    public void proverifAnalysis(TMLMapping<?> map, List<String> nonAuthChans, List<String> nonConfChans) {
        if (map == null) {
            TraceManager.addDev("No mapping");
            return;
        }

        //Perform ProVerif Analysis

        Object o = null;
        if (map.getTMLModeling().getReference() instanceof TGComponent) {
            o = ((TGComponent)(map.getTMLModeling().getReference())).getTDiagramPanel().tp;
        }

        TML2Avatar t2a = new TML2Avatar(newMap, false, true, o);
        AvatarSpecification avatarspec = t2a.generateAvatarSpec("1", this.autoWeakAuth||this.autoStrongAuth);
        if (avatarspec == null) {
            TraceManager.addDev("No avatar spec");
            return;
        }

        avatar2proverif = new AVATAR2ProVerif(avatarspec);
        try {
            proverif = avatar2proverif.generateProVerif(true, true, 3, true, true);
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

                    TMLChannel chan = map.getTMLModeling().getChannelByShortName(pragma.getArg().getName().replaceAll("_chData", ""));

                    if (chan == null) {
                        chan = map.getTMLModeling().getChannelByOriginPortName(pragma.getArg().getName().replaceAll("_chData", ""));
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
            //       TraceManager.addDev("nonConfChans " + nonConfChans);
            //     TraceManager.addDev("nonauthchans " + nonAuthChans);
            //   TraceManager.addDev("all results displayed");

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
        return map;
    }

    public boolean portInTask(TMLTask task, String portName) {
        TMLActivity adTask = task.getActivityDiagram();
        for (TMLActivityElement elem : adTask.getElements()) {
            if (elem instanceof TMLWriteChannel) {
                TMLWriteChannel writeChannel = (TMLWriteChannel) elem;
                for (int i = 0; i < writeChannel.getNbOfChannels(); i++) {
                    if (writeChannel.getChannel(i).getName().equals(portName)) {
                        return true;
                    }
                }
            } else if (elem instanceof TMLReadChannel) {
                TMLReadChannel readChannel = (TMLReadChannel) elem;
                for (int i = 0; i < readChannel.getNbOfChannels(); i++) {
                    if (readChannel.getChannel(i).getName().equals(portName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void run() {
        String title = "";
        // oldmodel = map.getTMLModeling();
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

        //TraceManager.addDev("mapping " + map.getSummaryTaskMapping());

        //   Map<String, HSMChannel> secChannels = new HashMap<String, HSMChannel>();
        //Map<String, HSMChannel> hsmChannels = new HashMap<String, HSMChannel>();


        for (String cpuName : selectedCPUTasks.keySet()) {
            for (String task : selectedCPUTasks.get(cpuName)) {
                hsmTasks.add(task);
                taskHSMMap.put(task, cpuName);
            }
            hsmChannelMap.put(cpuName, new ArrayList<HSMChannel>());

        }


        //Proverif Analysis channels
        List<String> nonAuthChans = new ArrayList<String>();
        List<String> nonConfChans = new ArrayList<String>();

        proverifAnalysis(map, nonAuthChans, nonConfChans);

        TMLModeling<?> tmlmodel = map.getTMLModeling();
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
        //        TMLComponentDesignPanel tmlcdp = map.getTMLCDesignPanel();

        //TMLComponentTaskDiagramPanel tcdp = tmlcdp.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        //newarch.renameMapping(tabName, tabName + "_" + name);

        for (TMLTask task : map.getTMLModeling().getTasks()) {
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
        //ToSecure keeps a map of origin task: {dest task} for which security operators need to be added
        //ToSecureRev keeps a map of dest task: {origin task} for which security operators need to be added

        //SecOutChannels are channels which need symmetric encryption operators added
        //SecInChannels are channels which need sym decryption operators added
        //nonceInChannels are channels which need to send a nonce before receiving the channel data
        //nonceOutChannels are channels which need to receive a nonce before sending the channel data
        //macInChannels are channels which need verifymac operators added
        //macOutChannels are channels which need mac operators added

        //hsmSecInChannels need to send data to the hsm to decrypt after receiving channel data
        //hsmSecOutChannels need to send data to the hsm to encrypt before sending channel data

        //With the proverif results, check which channels need to be secured
        for (TMLTask task : map.getTMLModeling().getTasks()) {
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
                    if (nonAuthChans.contains(chan.getDestinationTask().getName().split("__")[1] + "__" + chan.getName())) {
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
                        if (nonAuthChans.contains(destTask.getName().split("__")[1] + "__" + chan.getName())) {
                            nonAuth = true;
                        }

                    }
                    //When port names are different

                }

                String secName = chan.getName().split("__")[chan.getName().split("__").length - 1];

                for (String chanName : portNames) {
                    //Classify channels based on the type of security requirements and unsatisfied properties
                    if (chan.isBasicChannel()) {
                        if (chan.checkConf && autoConf && nonConf) {
                            toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                            if (chan.checkAuth && autoStrongAuth) {
                                if (!toSecureRev.get(chan.getDestinationTask()).contains(chan.getOriginTask())) {
                                    toSecureRev.get(chan.getDestinationTask()).add(chan.getOriginTask());
                                }
                            }
                            if (hsmTasks.contains(chan.getOriginTask().getName().split("__")[1])) {
                                channelSecMap.put(chanName, "hsmSec_" + secName);
                                if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName) && portInTask(chan.getOriginTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getOriginTask().getName().split("__")[1], HSMChannel.SENC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

                                    if (chan.checkAuth && autoStrongAuth) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secOutChannels.get(chan.getOriginTask()).contains(chanName)) {
                                    secOutChannels.get(chan.getOriginTask()).add(chanName);
                                    channelSecMap.put(chanName, "autoEncrypt_" + secName);
                                    if (chan.checkAuth && autoStrongAuth) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                    }
                                }
                            }

                            if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])) {
                                if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName) && portInTask(chan.getDestinationTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.checkAuth && autoStrongAuth) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
                                    secInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.checkAuth && autoStrongAuth) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                    }
                                }
                            }

                        } else if (chan.checkAuth && autoWeakAuth && nonAuth) {
                            toSecure.get(chan.getOriginTask()).add(chan.getDestinationTask());
                            if (autoStrongAuth) {
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
                                channelSecMap.put(chanName, "hsmSec_" + secName);
                                if (!hsmSecOutChannels.get(chan.getOriginTask()).contains(chanName) && portInTask(chan.getOriginTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getOriginTask().getName().split("__")[1], HSMChannel.MAC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getOriginTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecOutChannels.get(chan.getOriginTask()).add(chanName);

                                    if (autoStrongAuth) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!macInChannels.get(chan.getOriginTask()).contains(chanName)) {
                                    macOutChannels.get(chan.getOriginTask()).add(chanName);
                                    channelSecMap.put(chanName, "autoEncrypt_" + secName);
                                    if (autoStrongAuth) {
                                        nonceOutChannels.get(chan.getOriginTask()).add(chanName);
                                    }
                                }
                            }

                            if (hsmTasks.contains(chan.getDestinationTask().getName().split("__")[1])) {
                                if (!hsmSecInChannels.get(chan.getDestinationTask()).contains(chanName) && portInTask(chan.getDestinationTask(), chanName)) {
                                    HSMChannel hsmchan = new HSMChannel(chanName, chan.getDestinationTask().getName().split("__")[1], HSMChannel.DEC);
                                    hsmChannelMap.get(taskHSMMap.get(chan.getDestinationTask().getName().split("__")[1])).add(hsmchan);
                                    hsmSecInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.checkAuth && autoStrongAuth) {
                                        nonceInChannels.get(chan.getDestinationTask()).add(chanName);
                                        hsmchan.nonceName = "nonce_" + chan.getDestinationTask().getName().split("__")[1] + "_" + chan.getOriginTask().getName().split("__")[1];
                                    }
                                }
                            } else {
                                if (!secInChannels.get(chan.getDestinationTask()).contains(chanName)) {
                                    secInChannels.get(chan.getDestinationTask()).add(chanName);
                                    if (chan.checkAuth && autoStrongAuth) {
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
                                if (chan.checkConf && autoConf && nonConf) {
                                    toSecure.get(orig).add(dest);
								/*if (chan.checkAuth && autoStrongAuth) {
								  if (!toSecureRev.get(dest).contains(orig)) {
								  toSecureRev.get(dest).add(orig);
								  }
								  }*/
                                    if (hsmTasks.contains(orig.getName().split("__")[1])) {
                                        channelSecMap.put(chanName, "hsmSec_" + secName);
                                        if (!hsmSecOutChannels.get(orig).contains(chanName) && portInTask(orig, chanName)) {
                                            HSMChannel hsmchan = new HSMChannel(chanName, orig.getName().split("__")[1], HSMChannel.SENC);
                                            hsmChannelMap.get(taskHSMMap.get(orig.getName().split("__")[1])).add(hsmchan);
                                            hsmSecOutChannels.get(orig).add(chanName);

										/*	if (chan.checkAuth && autoStrongAuth) {
											nonceOutChannels.get(orig).add(chanName);
											hsmchan.nonceName="nonce_" + dest.getName().split("__")[1] + "_" + orig.getName().split("__")[1];
											}*/
                                        }
                                    } else {
                                        if (!secOutChannels.get(orig).contains(chanName)) {
                                            secOutChannels.get(orig).add(chanName);
                                            channelSecMap.put(chanName, "autoEncrypt_" + secName);
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
										/*	if (chan.checkAuth && autoStrongAuth) {
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

                                } else if (chan.checkAuth && autoWeakAuth && nonAuth) {
                                    toSecure.get(orig).add(dest);
								/*	if (autoStrongAuth) {
								/*  		if (chan.getOriginTask().getReferenceObject() instanceof TMLCPrimitiveComponent && chan.getDestinationTask().getReferenceObject() instanceof TMLCPrimitiveComponent) {*/
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
                                        channelSecMap.put(chanName, "hsmSec_" + secName);
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
                                            channelSecMap.put(chanName, "autoEncrypt_" + secName);
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
        TraceManager.addDev("secChannelMap" + channelSecMap);

        TraceManager.addDev("macoutchans " + macOutChannels);
        TraceManager.addDev("macinchans " + macInChannels);
        TraceManager.addDev("nonsecin " + secInChannels);
        TraceManager.addDev("nonsecout " + secOutChannels);
        TraceManager.addDev("noncein " + nonceInChannels);
        TraceManager.addDev("nonceout " + nonceOutChannels);
        TraceManager.addDev("hsmsecin " + hsmSecInChannels);
        TraceManager.addDev("hsmsecout " + hsmSecOutChannels);
        TraceManager.addDev("tosecrev " + toSecureRev);

        //Add a HSM Task for each selected CPU on the component diagram, add associated channels, etc
        for (String cpuName : selectedCPUTasks.keySet()) {
            TMLTask hsm = new TMLTask("HSM_" + cpuName, map.getTMLModeling().getTasks().get(0).getReferenceObject(), null);

            TMLAttribute index = new TMLAttribute("channelIndex", new TMLType(TMLType.NATURAL), "0");
            hsm.addAttribute(index);
            tmlmodel.addTask(hsm);

            //Find all associated tasks
            List<TMLTask> comps = new ArrayList<TMLTask>();
            //Find the component to add a HSM to

            for (TMLTask task : tmlmodel.getTasks()) {
                for (String compName : selectedCPUTasks.get(cpuName)) {
                    if (task.getName().equals(compName)) {
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
                    if (!hsmChan.task.equals(comp.getName())) {
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
                        request.addOriginTask(hsm);
                        request.setDestinationTask(comp);
                        hsm.setRequest(request);
                        comp.setRequest(request);
                        request.addParam(new TMLType(TMLType.NATURAL));
                        tmlmodel.addRequest(request);
                    } else {
                        TMLChannel channel = new TMLChannel(hsmChan.name, hsm.getReferenceObject());
                        channel.setOriginTask(hsm);
                        channel.setDestinationTask(comp);
                        channel.setPorts(new TMLPort(channel.getName(), channel.getReferenceObject()), new TMLPort(channel.getName(), channel.getReferenceObject()));
                        hsm.addWriteTMLChannel(channel);
                        hsm.addTMLChannel(channel); /// IN TMLChannel and WriteTMLChannel ??
                        comp.addReadTMLChannel(channel);
                        comp.addTMLChannel(channel);
                        tmlmodel.addChannel(channel);
                    }
                }
            }
        }


        for (String cpuName : selectedCPUTasks.keySet()) {
            buildHSMActivityDiagram(cpuName);
            //Add a private bus to Hardware Accelerator with the task for hsm

            //Find the CPU the task is mapped to
            TMLArchitecture arch = map.getArch();
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
            TMLTask task = map.getTaskByName("HSM_" + cpuName);
            if (task != null) {
                map.addTaskToHwExecutionNode(task, hwa);
            }
            //Add bus connecting the cpu and HWA
            HwBus bus = new HwBus("HSMBus_" + cpuName);
            bus.privacy = HwBus.BUS_PRIVATE;
            arch.addHwNode(bus);

            //Connect Bus and CPU
            HwLink linkCPUWithBus = new HwLink("link_" + cpu.getName() + "_to_" + bus.getName());
            linkCPUWithBus.bus = bus;
            linkCPUWithBus.hwnode = cpu;
            arch.addHwLink(linkCPUWithBus);

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
                    channel.setOriginTask(task);
                    channel.setDestinationTask(task2);
                    channel.setPorts(new TMLPort(channel.getName(), channel.getReferenceObject()), new TMLPort(channel.getName(), channel.getReferenceObject()));
                    task.addWriteTMLChannel(channel);
                    task.addTMLChannel(channel); /// IN TMLChannel and WriteTMLChannel ??
                    task2.addReadTMLChannel(channel);
                    task2.addTMLChannel(channel);
                    tmlmodel.addChannel(channel);
                    
                    List<TMLChannel> chans2 = tmlmodel.getChannelsFromMe(task2);
                    int count_chans = 0;
                    HwMemory memToPutChannel = null;
                    for (TMLChannel chan : chans2) {
                        if (chan.isCheckAuthChannel()) {
                            TMLChannel sameChannel = map.getChannelByName(chan.getName().split("__")[1]);
                            HwMemory memoryOfChannel = map.getMemoryOfChannel(sameChannel);
                            if (memoryOfChannel != null) {
                                count_chans += 1;
                                if (count_chans == 1) {
                                    memToPutChannel = memoryOfChannel;
                                    //map.addCommToHwCommNode(channel, memToPutChannel);
                                }
                                for (HwCommunicationNode mappedNode : map.getAllCommunicationNodesOfChannel(sameChannel)) {
                                    if (!(mappedNode instanceof HwMemory)) {
                                        map.addCommToHwCommNode(channel, mappedNode);
                                    }
                                }
                            }
                        }
                    }
                    if (count_chans > 0) {
                        map.addCommToHwCommNode(channel, memToPutChannel);
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
            // Get start state position, shift everything down
            int xpos = 0;
            int ypos = 0;
            
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
                            if (writeChannel.getChannel(i).getName().equals(channel) && writeChannel.securityPattern == null) {
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                for (TMLActivityElement elem : channelInstances) {
                    
                    //Add encryption operator
                    SecurityPattern secPattern = new SecurityPattern(channelSecMap.get(channel), SecurityPattern.SYMMETRIC_ENC_PATTERN, overhead, "", encComp, decComp, "", "", "");
                    secPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    TMLExecC encC = new TMLExecC(channelSecMap.get(channel), taskAD.getReferenceObject());
                    encC.securityPattern = secPattern;
                    TMLActivityElement prevElem = taskAD.getPrevious(elem);
                    if (nonceOutChannels.get(task).contains(channel)) {
                        //Receive any nonces if ensuring authenticity
                        TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                        //System.out.println("tmlc " + tmlc);
                        //					System.out.println("Checking "+ tmlc.getDestinationTask() + " " + tmlc.getOriginTask());
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            rd.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            rd.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        rd.securityPattern = secPatternNonce;                     
                        prevElem.setNewNext(elem, rd);
                        rd.addNext(elem);
                        taskAD.addElement(rd);
                        //Move encryption operator after receive nonce component
                        if (tmlc != null) {
                            secPattern.nonce = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
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
                                if (channel.equals(wChannel.getChannel(i).getName()) && wChannel.securityPattern == null) {
                                    wChannel.securityPattern = secPattern;
                                    wChannel.setEncForm(true);
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
                            if (writeChannel.getChannel(i).getName().equals(channel) && writeChannel.securityPattern == null) {
                                fromStart = taskAD.getPrevious(elem);
                                if (fromStart != null) {
                                    channelInstances.add(elem);
                                }
                            }
                        }
                    }
                }
                for (TMLActivityElement elem : channelInstances) {
                    SecurityPattern secPattern = new SecurityPattern(channelSecMap.get(channel), SecurityPattern.MAC_PATTERN, overhead, overhead, encComp, decComp, "", "", "");
                    secPattern.setProcess(SecurityPattern.ENCRYPTION_PROCESS);
                    TMLExecC encC = new TMLExecC(channelSecMap.get(channel), taskAD.getReferenceObject());

                    TMLActivityElement prevElem = taskAD.getPrevious(elem);
                    if (nonceOutChannels.get(task).contains(channel)) {
                        //If we need to receive a nonce
                        TMLReadChannel rd = new TMLReadChannel("", taskAD.getReferenceObject());
                        //Receive any nonces if ensuring authenticity
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            rd.setName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            rd.setName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        SecurityPattern secPatternNonce = new SecurityPattern("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1], SecurityPattern.NONCE_PATTERN, overhead, "", encComp, decComp, "", "", "");
                        rd.securityPattern = secPatternNonce;
                        prevElem.setNewNext(elem, rd);
                        rd.addNext(elem);
                        taskAD.addElement(rd);
                        if (tmlc != null) {
                            secPattern.nonce = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
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
                                if (channel.equals(wChannel.getChannel(i).getName()) && wChannel.securityPattern == null) {
                                    wChannel.securityPattern = secPattern; 
                                    wChannel.setEncForm(true);
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
                            if (writeChannel.getChannel(i).getName().equals(channel) && writeChannel.securityPattern == null) {
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
                    String chanName = writeChannel.getChannel(0).getName();
                    TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
                    if (tmlc == null) {
                        tmlc = tmlmodel.getChannelByOriginPortName(channel);
                    }
                    if (tmlc == null) {
                        continue;
                    }
                    writeChannel.setSecurityContext(channelSecMap.get(chanName));
                    writeChannel.setEncForm(true);
                    xpos = chan.getX();
                    ypos = chan.getY();
                    fromStart = tad.findTGConnectorEndingAt(chan.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();

                    int yShift = 50;

                    TMLSendRequest req = new TMLSendRequest("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]), taskAD.getReferenceObject());
                    //TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    //req.setRequestName("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]));

                    req.setParam(Integer.toString(channelIndexMap.get(chanName)), 0);
                    tad.addComponent(req, xpos, ypos + yShift, false, true);

                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));
                    //tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add connection
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));

                    yShift += 50;
                    //Add write channel operator
                    TMLWriteChannel wr = new TMLWriteChannel("data_" + chanName + "_" + task.getName().split("__")[1], taskAD.getReferenceObject());
                    wr.setEncForm(false);
                    wr.setSecurityContext(channelSecMap.get(chanName));
                    tad.addComponent(wr, xpos, ypos + yShift, false, true);

                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));

                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    TMLADWriteChannel wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    //Receive any nonces if ensuring authenticity
                    if (nonceOutChannels.get(task).contains(channel)) {
                        //Read nonce from rec task
                        yShift += 50;

                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());
                        rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        if (matches.size() > 0) {
                            rd.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            rd.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        rd.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(rd, xpos, ypos + yShift, false, true);
                        fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                        fromStart = new TGConnectorTMLAD(rd.getX(), rd.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(rd.getTGConnectingPointAtIndex(1));

                        //Also send nonce to hsm
                        yShift += 50;
                        wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        wr2.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
                        wr2.setSecurityContext(channelSecMap.get(chanName));
                        tad.addComponent(wr2, xpos, ypos + yShift, false, true);

                        TGConnectorTMLAD tmp = new TGConnectorTMLAD(wr2.getX(), wr2.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, rd.getTGConnectingPointAtIndex(1), wr2.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);

                        fromStart.setP2(wr2.getTGConnectingPointAtIndex(0));

                        fromStart = new TGConnectorTMLAD(rd.getX(), rd.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        fromStart.setP1(wr2.getTGConnectingPointAtIndex(1));


                        yShift += 60;
                    }

                    //Read channel operator to receive hsm data
                    yShift += 60;
                    TMLADReadChannel rd2 = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd2.setChannelName("retData_" + chanName + "_" + task.getName().split("__")[1]);
                    rd2.setSecurityContext(channelSecMap.get(chanName));
                    tad.addComponent(rd2, xpos, ypos + yShift, false, true);

                    fromStart.setP2(rd2.getTGConnectingPointAtIndex(0));
                    yShift += 50;

                    //Add connector
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(rd2.getTGConnectingPointAtIndex(1));
                    yShift += 50;

                    //Direct the last TGConnector back to the start of the write channel operator
                    fromStart.setP2(point);
                }
            }

            for (String channel : hsmSecInChannels.get(task)) {
                //System.out.println("Checking hsmsecinchannel " + channel + " " + task.getName());
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                //TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                //TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                //System.out.println("matches " + channelInstances);
                for (TGComponent chan : channelInstances) {
                    TMLADReadChannel readChannel = (TMLADReadChannel) chan;
                    String chanName = readChannel.getChannelName();
                    TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + chanName);
                    if (tmlc == null) {
                        tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                    }
                    if (tmlc == null) {
                        continue;
                    }
                    readChannel.setSecurityContext(channelSecMap.get(chanName));
                    readChannel.setEncForm(true);
                    xpos = chan.getX() + 1;
                    ypos = chan.getY() + 1;
                    fromStart = tad.findTGConnectorStartingAt(chan.getTGConnectingPointAtIndex(1));
                    point = fromStart.getTGConnectingPointP2();

                    int yShift = 50;


                    TMLADSendRequest req = new TMLADSendRequest(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    req.setRequestName("startHSM_" + taskHSMMap.get(task.getName().split("__")[1]));


                    req.setParam(0, Integer.toString(channelIndexMap.get(chanName)));
                    req.makeValue();
                    tad.addComponent(req, xpos, ypos + yShift, false, true);

                    fromStart.setP2(req.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add connection
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(req.getTGConnectingPointAtIndex(1));
                    TMLADWriteChannel wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    yShift += 50;
                    //Add write channel operator
                    wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    wr.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
                    wr.setSecurityContext(channelSecMap.get(chanName));
                    wr.makeValue();
                    tad.addComponent(wr, xpos, ypos + yShift, false, true);

                    //Add connector between request and write
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, xpos, ypos, false, true);

                    //Add connector between write and ???
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));

                    //If needed, forge nonce, send it to receiving task
                    TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    TMLADWriteChannel wr3 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    TMLADWriteChannel wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);

                    if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        yShift += 60;
                        nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = SecurityPattern.NONCE_PATTERN;
                        nonce.message_overhead = overhead;
                        nonce.encTime = encComp;
                        nonce.decTime = decComp;
                        tad.addComponent(nonce, xpos, ypos + yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift += 50;

                        wr3 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr3.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            wr3.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr3.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr3, xpos, ypos + yShift, false, true);

                        wr3.makeValue();
                        TGConnector tmp = new TGConnectorTMLAD(wr3.getX(), wr3.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr3.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);

                        //Also send nonce to hsm
                        yShift += 50;
                        wr2 = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        wr2.setChannelName("data_" + chanName + "_" + task.getName().split("__")[1]);
                        wr2.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr2, xpos, ypos + yShift, false, true);

                        tmp = new TGConnectorTMLAD(wr2.getX(), wr2.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr3.getTGConnectingPointAtIndex(1), wr2.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);

                        fromStart = new TGConnectorTMLAD(wr2.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr2.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr2.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                    }


                    //Add read channel operator

                    yShift += 60;
                    TMLADReadChannel rd = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("retData_" + chanName + "_" + task.getName().split("__")[1]);
                    rd.setSecurityContext(channelSecMap.get(chanName));
                    rd.setEncForm(false);
                    tad.addComponent(rd, xpos, ypos + yShift, false, true);

                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                    yShift += 50;

                    //Add connector
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    tad.addComponent(fromStart, xpos, ypos, false, true);
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    yShift += 50;

                    //Direct the last TGConnector back to the start of the operator after the read channel
                    fromStart.setP2(point);

                }
            }
            for (String channel : macInChannels.get(task)) {
                //Add decryptmac after readchannel
                int yShift = 50;
                Set<TGComponent> channelInstances = new HashSet<TGComponent>();
                TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                if (tmlc == null) {
                    tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        TMLADReadChannel readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }
                for (TGComponent comp : channelInstances) {
                    yShift = 50;
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next = conn.getTGConnectingPointP2();
                    xpos = fromStart.getX();
                    ypos = fromStart.getY();

                    TMLADReadChannel readChannel = (TMLADReadChannel) comp;
                    readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
                    tad.repaint();

                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    //Create nonce and send it
                    if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        TMLADEncrypt nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = SecurityPattern.NONCE_PATTERN;
                        nonce.message_overhead = overhead;
                        nonce.encTime = encComp;
                        nonce.decTime = decComp;
                        tad.addComponent(nonce, xpos, ypos + yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift += 50;
                        wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            wr.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr, xpos, ypos + yShift, false, true);
                        wr.makeValue();
                        TGConnector tmp = new TGConnectorTMLAD(wr.getX(), wr.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);
                        fromStart = new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                        yShift += 40;
                    }

                    //Add decryption operator if it does not already exist
                    xpos = conn.getX();
                    ypos = conn.getY();

                    TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = channelSecMap.get(readChannel.getChannelName());
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift += 60;
                    conn = new TGConnectorTMLAD(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));
                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false, true);
                    //Shift everything down
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADReadChannel) {
                            readChannel = (TMLADReadChannel) tg;
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
                                readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
                                readChannel.setEncForm(true);

                            }
                        }
                    }
                }
            }
            for (String channel : secInChannels.get(task)) {
                TraceManager.addDev("securing channel " + channel);
                int yShift = 20;

                TGConnector conn = new TGConnectorTMLAD(0, 0, 0, 0, 0, 0, false, null, tad, null, null, new Vector<Point>());
                TGConnectingPoint next = new TGConnectingPoint(null, 0, 0, false, false);
                //Find read channel operator
                TMLChannel tmlc = tmlmodel.getChannelByName(title + "__" + channel);
                if (tmlc == null) {
                    tmlc = tmlmodel.getChannelByDestinationPortName(channel);
                }
                if (tmlc == null) {
                    continue;
                }
                TMLADReadChannel readChannel = new TMLADReadChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                HashSet<TGComponent> channelInstances = new HashSet<TGComponent>();
                for (TGComponent tg : tad.getComponentList()) {
                    if (tg instanceof TMLADReadChannel) {
                        readChannel = (TMLADReadChannel) tg;
                        if (readChannel.getChannelName().equals(channel) && readChannel.getSecurityContext().equals("")) {
                            fromStart = tad.findTGConnectorEndingAt(tg.getTGConnectingPointAtIndex(0));
                            if (fromStart != null) {
                                channelInstances.add(tg);
                            }
                        }
                    }
                }

                for (TGComponent comp : channelInstances) {
                    yShift = 30;
                    readChannel = (TMLADReadChannel) comp;
                    fromStart = tad.findTGConnectorEndingAt(comp.getTGConnectingPointAtIndex(0));
                    point = fromStart.getTGConnectingPointP2();
                    conn = tad.findTGConnectorStartingAt(comp.getTGConnectingPointAtIndex(1));
                    next = conn.getTGConnectingPointP2();
                    xpos = comp.getX();
                    ypos = comp.getY();
                    TMLADWriteChannel wr = new TMLADWriteChannel(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    TMLADEncrypt nonce = new TMLADEncrypt(0, 0, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    if (nonceInChannels.get(task).contains(channel)) {
                        //Create a nonce operator and a write channel operator
                        nonce = new TMLADEncrypt(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        nonce.securityContext = "nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1];
                        nonce.type = SecurityPattern.NONCE_PATTERN;
                        nonce.message_overhead = overhead;
                        nonce.encTime = encComp;
                        nonce.decTime = decComp;
                        tad.addComponent(nonce, xpos, ypos + yShift, false, true);
                        fromStart.setP2(nonce.getTGConnectingPointAtIndex(0));
                        yShift += 50;
                        wr = new TMLADWriteChannel(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                        //Send nonce along channel, the newly created nonce channel or an existing channel with the matching sender and receiver
                        //Find matching channels
                        List<TMLChannel> matches = tmlmodel.getChannels(tmlc.getDestinationTask(), tmlc.getOriginTask());

                        if (matches.size() > 0) {
                            wr.setChannelName(matches.get(0).getName().replaceAll(title + "__", ""));
                        } else {
                            wr.setChannelName("nonceCh" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        }
                        yShift += 40;
                        //send the nonce along the channel
                        wr.setSecurityContext("nonce_" + tmlc.getDestinationTask().getName().split("__")[1] + "_" + tmlc.getOriginTask().getName().split("__")[1]);
                        tad.addComponent(wr, xpos, ypos + yShift, false, true);
                        wr.makeValue();
                        TGConnector tmp = new TGConnectorTMLAD(wr.getX(), wr.getY() + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, nonce.getTGConnectingPointAtIndex(1), wr.getTGConnectingPointAtIndex(0), new Vector<Point>());
                        tad.addComponent(tmp, xpos, ypos, false, true);
                        fromStart = new TGConnectorTMLAD(wr.getX(), wr.getY(), tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, wr.getTGConnectingPointAtIndex(1), null, new Vector<Point>());
                        tad.addComponent(fromStart, xpos, ypos, false, true);
                        //Connect created write channel operator to start of read channel operator
                        fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                        fromStart.setP2(point);
                    }
                    //tad.repaint();

                    //Now add the decrypt operator
                    yShift += 40;
                    readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
                    readChannel.setEncForm(true);
                    tad.repaint();
                    //Add decryption operator if it does not already exist
                    xpos = readChannel.getX();
                    ypos = readChannel.getY();
                    TMLADDecrypt dec = new TMLADDecrypt(xpos + 10, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = channelSecMap.get(readChannel.getChannelName());
                    tad.addComponent(dec, dec.getX(), dec.getY(), false, true);
                    conn.setP2(dec.getTGConnectingPointAtIndex(0));
                    yShift += 60;
                    conn = new TGConnectorTMLAD(xpos, ypos + yShift, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, dec.getTGConnectingPointAtIndex(1), next, new Vector<Point>());
                    conn.setP1(dec.getTGConnectingPointAtIndex(1));

                    conn.setP2(next);
                    tad.addComponent(conn, conn.getX(), conn.getY(), false, true);
                    //Shift everything down
                    for (TGComponent tg : tad.getComponentList()) {
                        if (tg instanceof TMLADReadChannel) {
                            readChannel = (TMLADReadChannel) tg;
                            if (channel.equals(readChannel.getChannelName()) && readChannel.getSecurityContext().equals("")) {
                                readChannel.setSecurityContext(channelSecMap.get(readChannel.getChannelName()));
                                readChannel.setEncForm(true);

                            }
                        }
                    }
                }
            }
        }
        return;
    }

    public void buildHSMActivityDiagram(String cpuName) {
        int xpos = 0;
        int ypos = 0;
        TMLStartState fromStart;
        //Build HSM Activity diagram
        TMLTask task = map.getTaskByName("HSM_" + cpuName);
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

        TMLActionState req = new TMLActionState(cpuName, taskAD.getReferenceObject());
        req.setAction(cpuName);
        TMLADReadRequestArg req = new TMLADReadRequestArg(300, 100, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
        tad.addComponent(req, 300, 100, false, true);
        req.setParam(0, "channelIndex");
        req.makeValue();

        //Connect start and readrequest
        fromStart.setP1(start.getTGConnectingPointAtIndex(0));
        fromStart.setP2(req.getTGConnectingPointAtIndex(0));
        tad.addComponent(fromStart, 300, 200, false, true);

        TMLChoice choice = new TMLChoice(cpuName, taskAD.getReferenceObject());
        TMLADChoice choice = new TMLADChoice(300, 200, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
        tad.addComponent(choice, 300, 200, false, true);

        //Connect readrequest and choice
        fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
        fromStart.setP1(req.getTGConnectingPointAtIndex(1));
        fromStart.setP2(choice.getTGConnectingPointAtIndex(0));
        tad.addComponent(fromStart, 300, 200, false, true);

        int xc = 150;
        //Allows 9 channels max to simplify the diagram

        //If more than 3 channels, build 2 levels of choices

        if (hsmChannelMap.get(cpuName).size() > 3) {
            TMLADChoice choice2 = new TMLADChoice(xc, 400, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
            int i = 0;
            for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
                if (i > 8) {
                    break;
                }
                if (i % 3 == 0) {
                    //Add a new choice every third channel
                    choice2 = new TMLADChoice(xc, 250, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(choice2, xc, 400, false, true);
                    //Connect new choice operator to top choice
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(choice.getTGConnectingPointAtIndex(i / 3 + 1));
                    fromStart.setP2(choice2.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);
                }
                TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                rd.setChannelName("data_" + ch.name + "_" + ch.task);
                rd.setSecurityContext(channelSecMap.get(ch.name));
                tad.addComponent(rd, xc, 300, false, true);
                //Connect choice and readchannel
                fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                fromStart.setP1(choice2.getTGConnectingPointAtIndex(i % 3 + 1));
                fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                tad.addComponent(fromStart, 300, 200, false, true);
                TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                wr.setChannelName("retData_" + ch.name + "_" + ch.task);
                tad.addComponent(wr, xc, 600, false, true);
                wr.setSecurityContext(channelSecMap.get(ch.name));

                if (ch.secType == HSMChannel.DEC) {
                    TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = channelSecMap.get(ch.name);
                    tad.addComponent(dec, xc, 500, false, true);
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Connect encrypt and writechannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);
                } else {
                    TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    enc.securityContext = channelSecMap.get(ch.name);
                    if (ch.secType == HSMChannel.SENC) {
                        enc.type = SecurityPattern.SYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.AENC) {
                        enc.type = SecurityPattern.ASYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.MAC) {
                        enc.type = SecurityPattern.MAC_PATTERN;
                    } else if (ch.secType == HSMChannel.NONCE) {
                        enc.type = SecurityPattern.NONCE_PATTERN;
                    }

                    enc.message_overhead = overhead;
                    enc.encTime = encComp;
                    enc.decTime = decComp;
                    enc.nonce = ch.nonceName;
                    tad.addComponent(enc, xc, 500, false, true);

                    //Connect encrypt and readchannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Connect encrypt and writechannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Add Stop
                    TMLADStopState stop = new TMLADStopState(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(stop, xc, 700, false, true);

                    //Connext stop and write channel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);
                }
                xc += 300;
                i++;
            }
        } else {

            int i = 1;
            for (HSMChannel ch : hsmChannelMap.get(cpuName)) {
                //Add guard as channelindex
                choice.setGuard("[channelIndex==" + channelIndexMap.get(ch.name) + "]", i - 1);

                TMLADReadChannel rd = new TMLADReadChannel(xc, 300, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                rd.setChannelName("data_" + ch.name + "_" + ch.task);
                rd.setSecurityContext(channelSecMap.get(ch.name));
                tad.addComponent(rd, xc, 300, false, true);

                //Recieve plaintext data if encrypting
                if (ch.secType != HSMChannel.DEC) {
                    rd.setEncForm(false);
                }

                //Connect choice and readchannel
                fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                fromStart.setP1(choice.getTGConnectingPointAtIndex(i));
                fromStart.setP2(rd.getTGConnectingPointAtIndex(0));
                tad.addComponent(fromStart, xc, 300, false, true);

                fromStart = new TGConnectorTMLAD(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                fromStart.setP1(rd.getTGConnectingPointAtIndex(1));

                //If needed, receive nonce from task
                if (!ch.nonceName.equals("")) {

                    tad.addComponent(fromStart, 300, 200, false, true);

                    rd = new TMLADReadChannel(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    rd.setChannelName("data_" + ch.name + "_" + ch.task);
                    rd.setSecurityContext(ch.nonceName);
                    tad.addComponent(rd, xc, 350, false, true);

                    fromStart.setP2(rd.getTGConnectingPointAtIndex(0));

                    fromStart = new TGConnectorTMLAD(xc, 350, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));

                }

                //Send data back to task
                TMLADWriteChannel wr = new TMLADWriteChannel(xc, 600, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                wr.setChannelName("retData_" + ch.name + "_" + ch.task);

                //Return plaintext data if decrypting
                if (ch.secType == HSMChannel.DEC) {
                    wr.setEncForm(false);
                }

                tad.addComponent(wr, xc, 600, false, true);
                wr.setSecurityContext(channelSecMap.get(ch.name));

                if (ch.secType == HSMChannel.DEC) {
                    //Add Decrypt operator
                    TMLADDecrypt dec = new TMLADDecrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    dec.securityContext = channelSecMap.get(ch.name);
                    tad.addComponent(dec, xc, 500, false, true);

                    //Connect decrypt and readchannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(dec.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Connect encrypt and writechannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(dec.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Add Stop
                    TMLADStopState stop = new TMLADStopState(xc, 700, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(stop, xc, 700, false, true);

                    //Connect stop and write channel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                } else {
                    TMLADEncrypt enc = new TMLADEncrypt(xc, 500, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    enc.securityContext = channelSecMap.get(ch.name);
                    if (ch.secType == HSMChannel.SENC) {
                        enc.type = SecurityPattern.SYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.AENC) {
                        enc.type = SecurityPattern.ASYMMETRIC_ENC_PATTERN;
                    } else if (ch.secType == HSMChannel.MAC) {
                        enc.type = SecurityPattern.MAC_PATTERN;
                    } else if (ch.secType == HSMChannel.NONCE) {
                        enc.type = SecurityPattern.NONCE_PATTERN;
                    }

                    enc.message_overhead = overhead;
                    enc.encTime = encComp;
                    enc.decTime = decComp;
                    tad.addComponent(enc, xc, 500, false, true);

                    //Connect encrypt and readchannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(rd.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(enc.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Connect encrypt and writechannel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(enc.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(wr.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);

                    //Add Stop
                    TMLADStopState stop = new TMLADStopState(xc, 700, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad);
                    tad.addComponent(stop, xc, 700, false, true);

                    //Connect stop and write channel
                    fromStart = new TGConnectorTMLAD(xpos, ypos, tad.getMinX(), tad.getMaxX(), tad.getMinY(), tad.getMaxY(), false, null, tad, null, null, new Vector<Point>());
                    fromStart.setP1(wr.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                    tad.addComponent(fromStart, 300, 200, false, true);
                }
                xc += 300;
                i++;
            }

        }
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
