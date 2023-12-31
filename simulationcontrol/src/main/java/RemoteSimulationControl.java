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




import remotesimulation.CommandParser;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
* Class TIFTranslator
* Linecommand application for translating TIF to other languages
* Creation: 29/06/2007
* @version 1.0 29/06/2007
* @author Ludovic APVRILLE
 */
public class RemoteSimulationControl extends Thread  {
	
    public static int port = 3490; 
	public static String host = "localhost";
	public static RemoteConnection rc;
	
	public static boolean mygo = true;
	
	public boolean go = true;
	
	public RemoteSimulationControl() {
	}
	
	public static void printCopyright() {
		System.out.println("RemoteSimulationControl: (C) Institut Mines Telecom / Telecom Paris, Ludovic Apvrille, Ludovic.Apvrille@telecom-paris.fr");
		System.out.println("RemoteSimulationControl is released under a CECILL License. See http://www.cecill.info/index.en.html");
		System.out.println("For more information on TTool related technologies, please consult https://ttool.telecom-paris.fr");

		System.out.println("Enjoy!!!\n");
	}
	
	public static void printUsage() {
		System.out.println("RemoteSimulationControl: usage");
		System.out.println("RemoteSimulationControl <options>");
		System.out.println("<options> are optional. There might be : -host, -port, or -help");
		System.out.println("host: name on the host on which the simulator runs (default: localhost)");
		System.out.println("port: port on which the simulator accepts commands (default: 3490). Must be a positive integer value");
	}
	
	public static void printSeparator() {
		System.out.println("-------------------------------------------------------------");
	}
	
	public static void printEndSeparator() {
		System.out.println("-------------------------------------------------------------\n");
	}
	
	public static void printHelp() {
		System.out.println("\nHelp: ");
		printSeparator();
		System.out.println("help: prints that help");
		System.out.println("help <string cmd>: prints the help on a given command");
		System.out.println("list: lists all commands");
		System.out.println("quit: quits this simulation remote controller");
		printEndSeparator();
	}
	
	public static void printHelp(CommandParser cp, String cmd) {
		System.out.println("\nHelp on command " + cmd + ": ");
		printSeparator();
		System.out.println(cp.getHelp(cmd));
		printEndSeparator();
	}
	
	public static boolean analyseArgs(String [] args) {
		String tmp;
		int i;
		
		System.out.println("Reading arguments");
		
		for(i=0; i<args.length; i++) {
			tmp = args[i].toUpperCase();
			if (tmp.equals("-HELP")) {
				printUsage();
			} else if (tmp.equals("-HOST")) {
				if (i+1 == args.length) {
					System.out.println("Missing parameter for option \"-host\". Aborting");
					return false;
				}
				host = args[i+1];
				i++;
			} else if (tmp.equals("-PORT")) {
				if (i+1 == args.length) {
					System.out.println("Missing parameter for option \"-port\". Aborting");
					return false;
				}
				try {
					port = Integer.decode(args[i+1]).intValue();
				} catch (Exception e) {
					System.out.println("Wrong parameter (" + args[i+1] + ") for option \"-port\". Aborting");
					return false;
				}
				if (port < 1) {
					System.out.println("Wrong parameter (" + args[i+1] + ") for option \"-port\". Aborting");
					return false;
				}
				i++;
			} else {
				System.out.println("Unknown option: " + args[i] + ". Aborting");
				return false;
			}
		}
		
		return true;
	}
	
    public static void tryToConnect(RemoteConnection rc) {
		
		while(true) {
			System.out.println("\nTrying to connect on host: " + host + " on port: " + port);
			try {
				rc.connect();
				return ;
			} catch (RemoteConnectionException rce) {
				System.out.println("Exception: " + rce.getMessage());
				System.out.println("Could not connect to host: " + host + " on port: " + port + ". Retrying soon.");
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
		}
	}
	
    public static void main(String[] args) {
		String s;
		
		printCopyright();
		
		if (!analyseArgs(args)) {
			printUsage();
			return;
		}
		
		rc = new RemoteConnection(host, port);
		
		mygo = true;
		
		while(mygo) {
			tryToConnect(rc);
			System.out.println("Connected on host: " + host + " on port: " + port);
			
			// Create two threads: one for reading, one for writting
			RemoteSimulationControl rsct = new RemoteSimulationControl();
			rsct.start();
			
			try {
				while(true) {
					s = rc.readOneLine();
					System.out.print("\nFrom server: " + s + "\n>");
					System.out.flush();
				}
			} catch (RemoteConnectionException rce) {
				if (mygo) {
					System.out.println("Exception: " + rce.getMessage());
					System.out.println("Could not read data from host: " + host + " on port: " + port + ". Aborting");
				}
			}
			
			rsct.go = false;
		}
		
	}
	
	// Thread reading from keyboard
	public void run() {
		String tmp;
		int ret;
		
		String input;
		BufferedReader dataIn;
		
		CommandParser cp = new CommandParser();
		
		System.out.println("Type \"help\" for more information on commands");
		System.out.println("Ready to get commands:");
		
		try {
			dataIn = new BufferedReader(new InputStreamReader(System.in));
			while(go) {
				System.out.print(">");
				System.out.flush();
				input = dataIn.readLine();
				if (input.trim().length() == 0) {
				} else if (cp.isQuitCommand(input)) {
						mygo = false;
						try {
							rc.disconnect();
						} catch (RemoteConnectionException rce) {
						}
						System.out.println("bye-bye");
						System.exit(-1);
				} else if (cp.isHelpCommand(input)) {
					tmp = cp.getHelpWithCommand(input);
					if ((tmp != null) && (tmp.length() > 0)) {
						printHelp(cp, tmp);
					} else {
						printHelp();
					}
				} else if (cp.isListCommand(input)) {
					System.out.println("\nAvailable commands:");
					printSeparator();
					System.out.println(cp.getCommandList());
					printEndSeparator();
				} else {
					ret = cp.isAValidCommand(input);
					if (ret > -1) {
						rc.send(cp.transformCommandFromUserToSimulator(input));
					} else {
						if (ret == -1) {
							System.out.println("** wrong command **");
						} else {
							System.out.println("** wrong number / type of arguments **");
						}
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("Exception: " + ioe.getMessage());
		} catch (RemoteConnectionException rce) {
			System.out.println("Exception: " + rce.getMessage());
			System.out.println("Could not send data to host: " + host + " on port: " + port + ". Aborting");
			return;
		}
	}
	
	
} // Class RemoteSimulationControl

