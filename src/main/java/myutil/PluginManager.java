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

import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class PluginManager
 * Creation: 24/05/2017
 * Version 1.0 24/05/2017
 *
 * @author Ludovic APVRILLE
 */
public class PluginManager {
    public static PluginManager pluginManager;
    public static String PLUGIN_PATH = "";

    public ArrayList<Plugin> plugins;

    public PluginManager() {
        plugins = new ArrayList<Plugin>();
    }


    public void preparePlugins(String path, String[] plugins, String[] packages) {
        PLUGIN_PATH = path;
        int cpt = 0;
        for (String s : plugins) {
            createPlugin(s, packages[cpt]);
            cpt++;
        }
    }

    public  ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    public Plugin getPluginAvatarCodeGenerator() {
        for (Plugin plugin : plugins) {
            if (plugin.hasAvatarCodeGenerator()) {
                //TraceManager.addDev("     Found avatar code generation plugin");
                return plugin;
            }
        }
        //TraceManager.addDev("     NOT Found avatar code generation plugin");
        return null;
    }

    public LinkedList<Plugin> getPluginDiplodocusCodeGenerator() {
        LinkedList<Plugin> lplugins = new LinkedList<Plugin>();
        for (Plugin plugin : plugins) {
            if (plugin.hasDiplodocusCodeGenerator()) {
                lplugins.add(plugin);
                //TraceManager.addDev("     Found diplodocus code generator plugin");
            }
        }
        return lplugins;
    }

    public LinkedList<Plugin> getPluginFPGAScheduling() {
        LinkedList<Plugin> lplugins = new LinkedList<Plugin>();
        for (Plugin plugin : plugins) {
            if (plugin.hasFPGAScheduling()) {
                lplugins.add(plugin);
                //TraceManager.addDev("     Found diplodocus code generator plugin");
            }
        }
        return lplugins;
    }

    public LinkedList<Plugin> getPluginGraphicalComponent(String diag) {
        LinkedList<Plugin> lplugins = new LinkedList<Plugin>();
        for (Plugin plugin: plugins) {
            if (plugin.hasGraphicalComponent(diag)) {
                lplugins.add(plugin);
                TraceManager.addDev("     Found graphical plugin: " + plugin.getName());
            }
        }
        return lplugins;
    }

    public void addPlugin(Plugin _plugin) {
        plugins.add(_plugin);
    }

    /*public Plugin getPluginOrCreate(String _name) {
	Plugin plug = getPlugin(_name);
	if (plug != null) {
	    return plug;
	}

	return createPlugin(_name);
	}*/

    public Plugin getPlugin(String _name) {
        for (Plugin plugin : plugins) {
            if (plugin.getName().compareTo(_name) == 0) {
                return plugin;
            }
        }
        return null;
    }

    public Plugin createPlugin(String _name, String packageName) {
        Plugin plugin = new Plugin(PLUGIN_PATH, _name, packageName);
        addPlugin(plugin);
        return plugin;
    }

    public void executeGraphics(Plugin _plugin, String _className, String _methodName, Graphics g) {
        if (_plugin == null) {
            return;
        }

        Method m = _plugin.getMethod(_className, _methodName);
        if (m == null) {
            return;
        }

        try {
            m.invoke(g);
        } catch (Exception e) {
            TraceManager.addDev("Exception occured when executing method " + _methodName);
        }
    }
    
    /*public String executeString(String _pluginName, String _className, String _methodName) {
	Plugin plugin = getPlugin(_pluginName);
	if (plugin == null) {
	    // We must find the package of this plugin
	    plugin = createPlugin(_pluginName);
	    if (plugin == null) {
		return null;
	    }
	}

	return plugin.executeRetStringMethod(_className, _methodName);

	
	}*/


}
