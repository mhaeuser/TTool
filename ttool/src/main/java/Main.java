/**
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * <p>
 * ludovic.apvrille AT enst.fr
 * <p>
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * <p>
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * <p>
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
 * <p>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 * <p>
 * /**
 * Class Main
 * starts the main Windows and a project manager
 * Creation: 01/12/2003
 *
 * @version 1.0 21/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

import cli.Interpreter;
import cli.InterpreterOutputInterface;
import com.microsoft.z3.Version;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import launcher.RTLLauncher;
import ui.ColorManager;
import ui.MainGUI;
import ui.TToolUpdater;
import ui.util.DefaultText;
import ui.util.IconManager;
import ui.window.JStartingWindow;
import myutil.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;

//import java.io.*;

public class Main implements ActionListener, InterpreterOutputInterface{
    public boolean finish = false;
    public static Main main = new Main();
    public static boolean systemc = true;
    public static boolean lotos = true;
    public static boolean proactive = false;
    public static boolean tpn = false;
    public static boolean os = false;
    public static boolean uppaal = true;
    public static boolean nc = true; // Network calculus
    public static boolean avatar = true; // avatar profile
    public static boolean proverif = true;
    public static boolean experimental = false;
    public static boolean avataronly = false;
    public static boolean turtle = false;
    public static boolean openLast = false;
    public static boolean interact = false;
    public static boolean picoZebre = false;

    public Main() {

    }

    public static void main(String[] args) {


        TraceManager.devPolicy = TraceManager.TO_DEVNULL;

        System.out.println("\n*** TTool version: " + DefaultText.getFullVersion() + " ***\n");


        // Read the image data and display the splash screen
        // -------------------------------------------------


        new Timer(2500, main).start();

        JStartingWindow splashFrame = null;
        //System.out.println("hello");
        URL imageURL = Main.class.getResource("ui/util/starting_logo.png");
        Image img;
        //System.out.println("helli");
        if (imageURL != null) {

            img = (new ImageIcon(imageURL)).getImage();
            splashFrame = JStartingWindow.splash(img, "Loading TTool's elements");
            //splashFrame.setLicenceMessage("An open-source toolkit from:");
            if (splashFrame != null) {
                splashFrame.setMiddleMessage("version " + DefaultText.getVersion());
            }
            //System.out.println("helly");
        } else {
            System.err.println("Starting image not found");
            System.exit(0);
        }


        //new Timer(2500, main).start();


        // Starting window

        // Checking Java version
        if (splashFrame != null) {
            splashFrame.setMessage("Checking Java version");
        }

        String s =  System.getProperty("java.vendor").toLowerCase();
        //System.out.println("Full version:" + s);
        if (!s.contains("oracle")) {
            System.out.println("** WARNING: you seem to use a Java Virtual Machine not provided by Oracle **.\n " +
                    "The use of, e.g., openjdk is strongly discouraged since several graphical bugs have been reported\n");
        }


        // setting default language
        if (splashFrame != null) {
            splashFrame.setMessage("Setting language");
        }
        Locale.setDefault(new Locale("en"));

        boolean startLauncher = true;

        // Setting certificates
	/*String trustStore = System.getProperty("javax.net.ssl.trustStore");
	if (trustStore == null) {
	    System.setProperty("javax.net.ssl.trustStore", "cacerts.jks");
	    } */

        // Analyzing arguments
        String config = "config.xml";
        startLauncher = true;
        for (int i = 0; i < args.length; i++) {
            /*if (args[i].compareTo("-systemc") == 0) {
              systemc = true;
              System.out.println("SystemC features activated - these are beta features that are meant to be used only for research purpose");
              }*/
            if (args[i].compareTo("-lotos") == 0) {
                lotos = true;
                //System.out.println("LOTOS features activated");
            }
            if (args[i].compareTo("-nolotos") == 0) {
                lotos = false;
                //System.out.println("LOTOS features activated");
            }
            if (args[i].compareTo("-launcher") == 0) {
                startLauncher = true;
            }
            if (args[i].compareTo("-nolauncher") == 0) {
                startLauncher = false;
            }
            if (args[i].compareTo("-diplodocus") == 0) {
                systemc = true;
                lotos = true;
                //System.out.println("Diplodocus features activated");
            }
            if (args[i].compareTo("-experimental") == 0) {
                experimental = true;
                System.out.println("Experimental features activated");
            }
            if (args[i].compareTo("-nodiplodocus") == 0) {
                systemc = false;
                //System.out.println("Diplodocus features deactivated");
            }
            if (args[i].compareTo("-interact") == 0) {
                interact = true;
                //System.out.println("Diplodocus features deactivated");
            }
            if (args[i].compareTo("-nointeract") == 0) {
                interact = false;
                //System.out.println("Diplodocus features deactivated");
            }
            if (args[i].compareTo("-proactive") == 0) {
                proactive = true;
                lotos = true;
                //System.out.println("Proactive features activated - these are beta features that are meant to be used only for research purpose");
            }
            if (args[i].compareTo("-tpn") == 0) {
                tpn = true;
                System.out.println("TPN features activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-nonc") == 0) {
                nc = false;
                //System.out.println("Network calculus features unactivated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-avatar") == 0) {
                avatar = true;
                //System.out.println("AVATAR activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-noavatar") == 0) {
                avatar = false;
                //System.out.println("AVATAR unactivated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-avataronly") == 0) {
                avataronly = true;
                System.out.println("Only AVATAR is activated");
            }
            if (args[i].compareTo("-proverif") == 0) {
                proverif = true;
                //System.out.println("ProVerif activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-noproverif") == 0) {
                proverif = false;
                //System.out.println("ProVerif unactivated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-os") == 0) {
                os = true;
                System.out.println("TURTLE-OS features activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-uppaal") == 0) {
                uppaal = true;
                //System.out.println("UPPAAL features activated - these are beta features that are meant to be used only for research purpose");
            }
            if (args[i].compareTo("-nouppaal") == 0) {
                uppaal = false;
                //System.out.println("UPPAAL features activated - these are beta features that are meant to be used only for research purpose");
            }

            if (args[i].compareTo("-config") == 0) {
                config = args[Math.min(args.length - 1, i + 1)];
            }


	     if (args[i].compareTo("-picoZebre") == 0) {
		 picoZebre = true;
		 System.out.println("You like playing with animal °°. enjoy!\n");
            }

            if (args[i].compareTo("-debug") == 0) {
                TraceManager.devPolicy = TraceManager.TO_CONSOLE;
            }

            if (args[i].compareTo("-nocolor") == 0) {
                ColorManager.noColor();
            }

            if (args[i].compareTo("-turtle") == 0) {
                turtle = true;
            }

            if (args[i].compareTo("-openlast") == 0) {
                openLast = true;
            }

        }

        // Icons
        if (splashFrame != null) {
            splashFrame.setMessage("Loading images");
        }
        IconManager.loadImg();

        // Loading configuration
        if (splashFrame != null) {
            splashFrame.setMessage("Loading configuration file: " + config);
        }


        //ConfigurationTTool.makeDefaultConfiguration();
        try {
            ConfigurationTTool.loadConfiguration(config, systemc);
            SpecConfigTTool.setBasicConfigFile(config);
        } catch (Exception e) {
            System.out.println("Couldn't load configuration from file: " + config);
        }
        TraceManager.addDev("\nConfiguration:\n--------------");
        TraceManager.addDev(ConfigurationTTool.getConfiguration(systemc));
        TraceManager.addDev("\nDebugging trace:\n----------------");

        TraceManager.addDev("\nPreparing plugins\n");
        if (splashFrame != null) {
            splashFrame.setMessage("Preparing plugins");
        }
        PluginManager.pluginManager = new PluginManager();
        PluginManager.pluginManager.preparePlugins(ConfigurationTTool.PLUGIN_PATH, ConfigurationTTool.PLUGIN, ConfigurationTTool.PLUGIN_PKG);

        if (ConfigurationTTool.LauncherPort.length() > 0) {
            try {
                int port = Integer.decode(ConfigurationTTool.LauncherPort).intValue();
                launcher.RshClient.PORT_NUMBER = port;
                launcher.RshServer.PORT_NUMBER = port;
                TraceManager.addDev("Port number set to: " + port);
            } catch (Exception e) {
                TraceManager.addError("Wrong port number:" + ConfigurationTTool.LauncherPort);
            }
        }

        if (startLauncher) {
            Thread t = new Thread(new RTLLauncher());
            t.start();
        }



        // making main window
        if (splashFrame != null) {
            splashFrame.setMessage("Creating main window");
        }
        MainGUI mainGUI = new MainGUI(openLast, turtle, systemc, lotos, proactive, tpn, os, uppaal, nc, avatar, proverif, avataronly, experimental, picoZebre);
        if (splashFrame != null) {
            splashFrame.setMessage("Building graphical components");
        }
        mainGUI.build();

        // loading configuration

        // starting application
// DB: Useless
//        if (mainGUI == null) {
//            ErrorGUI.exit(ErrorGUI.GUI);
//        }

        if (splashFrame != null) {
            splashFrame.setMessage("Starting TTool ...");
        }
        main.waitFinish();
        mainGUI.start();

        // Dispose the splash screen
        // -------------------------
        if (splashFrame != null) {
            splashFrame.dispose();
        }

        // interact
        Interpreter interpret = new Interpreter(null, new Main(), true);
        Thread t = new Thread(interpret);
        interpret.setTToolStarted(true);
        interpret.setMGUI(mainGUI);
        t.start();


        // Checking for update
        //TToolUpdater tu = new TToolUpdater(mainGUI.frame);
        //tu.start();
    }

    public synchronized void waitFinish() {
        while (finish == false) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }
    }

    public synchronized void okFinish() {
        finish = true;
        notifyAll();
    }

    public void actionPerformed(ActionEvent evt) {
        main.okFinish();
    }


    public static void testMatrix() {

        //int[] numbers = {42, 18, 12,-12};
        //System.out.println("GCD=" + MyMath.gcd(numbers));


        /*String[] names = {"a", "b", "e", "ae4"};
          int [] line0 ={-1, 1, 0, 0};
          int [] line1 ={0, 0,-1, 1};
          int [] line2 ={-4, 4, -1, 1};
          int [] line3 ={1, -1, 0, 0};
          int [] line4 ={0, 0, 1, -1};
          int [] line4_fake ={10, 10, 11, -11};


          int [] line0b ={-1, 1, 1, -1};
          int [] line1b ={1, -1,-1, 1};
          int [] line2b ={0, 0, 1, 0};
          int [] line3b ={1, 0, 0, -1};
          int [] line4b ={-1, 0, 0, 1};

          IntMatrix myMat = new IntMatrix(4, 4);
          myMat.setNamesOfLine(names);


          myMat.setLineValues(0, line0);
          myMat.setLineValues(1, line1);
          myMat.setLineValues(2, line2);
          myMat.setLineValues(3, line3);

          System.out.println("mat=\n" + myMat.toString() + "\n\n");

          myMat.addLine(line4_fake, "duplicate-be");
          myMat.addLine(line4, "be");

          System.out.println("mat=\n" + myMat.toString() + "\n\n");

          myMat.removeLine(4);


          System.out.println("mat=\n" + myMat.toString() + "\n\n");
          myMat.Farkas();
          System.out.println("mat=\n" + myMat.toString() + "\n\n");

          myMat = new IntMatrix(5, 4);


          myMat.setLineValues(0, line0b);
          myMat.setLineValues(1, line1b);
          myMat.setLineValues(2, line2b);
          myMat.setLineValues(3, line3b);
          myMat.setLineValues(4, line4b);
          System.out.println("matb=\n" + myMat.toString() + "\n\n");
          myMat.Farkas();
          System.out.println("matb=\n" + myMat.toString() + "\n\n");*/

    }

    public static void testBoolExpr() {


        evalBool("t or f");

        evalBool("(t) or f");

        evalBool("(0==0)");


        System.exit(-1);
    }

    public static void evalBool(String s) {
        BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
        System.out.println("->Result of " + s + " =" + bee.getResultOf(s));
        if (bee.hasError()) {
            System.out.println("Error = " + bee.getFullError());
        }
        System.out.println("\n\n");
    }

    public void print(String s) {
        System.out.println(s);
    }

    public void printError(String s) {
        System.err.println(s);
    }

    public void exit(int status) {
        System.exit(status);
    }

} // Class Main
