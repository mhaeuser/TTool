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


package help;

import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;


/**
 * Class HelpManager
 * Creation: 28/02/2019
 * Version 2.0 28/02/2019
 *
 * @author Ludovic APVRILLE
 */
public class HelpManager extends HelpEntry {

    private static String PATH_TO_INDEX = "helpTable.txt";
    private static String INIT_CHAR = "-";

    private boolean helpLoaded = false;

    private Vector<HelpEntry> allEntries;


    public HelpManager() {
        linkToParent = null;
    }


    // Returns false in case of failure
    public boolean loadEntries() {
        if (helpLoaded) {
            return true;
        }


        // Setup the root entry
        fillInfos("none Help TTool help");

        //File file = getContent(PATH_TO_INDEX);
        URL url = getURL(PATH_TO_INDEX);

        int lineNb = 0;

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            //TraceManager.addDev("File=" + file);

            HelpEntry currentHelpEntry = this;


            //try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            //try {
            String line;
            lineNb++;
            while ((line = in.readLine()) != null) {
                // while ((line = br.readLine()) != null) {
                //TraceManager.addDev("Reading index line: " + line);
                line = line.trim();
                if (line.length() > 0) {
                    //TraceManager.addDev("Getting number of inits");
                    int nb = getNumberOfInit(line);
                    //TraceManager.addDev("Testing number of inits=" + nb);
                    if (nb > 0) {
                        //TraceManager.addDev("Before adding Entry");
                        currentHelpEntry = addEntry(currentHelpEntry, nb, removeInitChars(line));
                        //TraceManager.addDev("After adding Entry");
                        if (currentHelpEntry == null) {
                            TraceManager.addDev("\nHelp: error when loading help index file at line: " + lineNb + "\n");
                            //System.exit(-1);
                            return false;
                        }
                    }

                }
                lineNb ++;
            }
        } catch (Exception e) {
            TraceManager.addDev("Help: exception when loading help index file at line: " + lineNb + "\n");
            //System.exit(-1);
            return false;
        }

        computeAllEntries();
        helpLoaded = true;

        return true;
    }

    public HelpEntry addEntry(HelpEntry entry, int inHierarchy, String infos) {
        if (entry == null) {
            return null;
        }

        if (inHierarchy < 1) {
            return null;
        }

        // We must locate the corresponding father
        int currentN = entry.getNbInHierarchy();
        HelpEntry father = null;

        // Missing an intermediate section?
        if (inHierarchy > (currentN + 1)) {
            return null;
        }

        //TraceManager.addDev("New node");

        HelpEntry newNode = new HelpEntry();
        boolean ok = newNode.fillInfos(infos);
        if (!ok) {
            TraceManager.addDev("HELP: Not ok");
            return null;
        }

        //TraceManager.addDev("Before child");

        // Child?
        if (inHierarchy == (currentN + 1)) {
            entry.addKid(newNode);
            newNode.linkToParent = entry;
            return newNode;
        }

        //TraceManager.addDev("Before brother");

        // Brother?
        if (inHierarchy == (currentN)) {
            if (entry.getFather() == null) {
                return null;
            }
            entry.getFather().addKid(newNode);
            newNode.linkToParent = entry.getFather();
            return newNode;
        }

        // Next section!
        // We must locate the correct father
        //TraceManager.addDev("Next section");
        father = entry;
        int nbOfAncestors = currentN - inHierarchy + 1;
        //TraceManager.addDev("Next section ancestors:" + nbOfAncestors);
        while (nbOfAncestors > 0) {
            father = father.getFather();
            if (father == null) {
                return null;
            }
            nbOfAncestors--;
        }
        father.addKid(newNode);
        newNode.linkToParent = father;
        return newNode;
    }


    private String removeInitChars(String s) {
        while (s.startsWith(INIT_CHAR)) {
            s = s.substring(1, s.length());
        }
        return s;
    }

    private int getNumberOfInit(String s) {
        int cpt = 0;
        while (s.startsWith(INIT_CHAR)) {
            cpt++;
            s = s.substring(1, s.length());
        }
        return cpt;
    }

    public static URL getURL(String resource) {
        return HelpManager.class.getResource(resource);
    }

    public static File getContent(String resource) {
        try {
            TraceManager.addDev("Getting help resource:" + resource);
            URL url = HelpManager.class.getResource(resource);


            if (url != null) {
                TraceManager.addDev("help url = " + url);
                File myFile = new File(url.toURI());
                return myFile;
            }

            //TraceManager.addDev("NULL URL");
        } catch (Exception e) {
        }
        return null;
    }

    public String printHierarchy() {
        String top = "Help tree\n root has " + getNbOfKids() + " nodes.\n";

        for (HelpEntry he : entries) {
            top += he.printHierarchy(1);
        }
        return top;
    }

    private void computeAllEntries() {
        allEntries = new Vector<>();
        addEntries(allEntries);

    }

    public Vector<HelpEntry> getEntriesWithKeyword(String[] words) {
        Vector<HelpEntry> result = new Vector<>();
        for(HelpEntry he: allEntries) {
            int nb = he.hasSimilarWords(words);
            if (nb > 0) {
                result.add(he);
            }
        }
        return result;
    }

    public HelpEntry getEntryWithMasterKeyword(String word) {

        for(HelpEntry he: allEntries) {
            boolean b = he.hasMasterKeyword(word);
            if (b)
                return he;
        }
        return null;
    }

    public HelpEntry getHelpEntryWithHTMLFile(String pathToHTMLFile) {
        for(HelpEntry he: allEntries) {
            if (he.pathToHTMLFile.compareTo(pathToHTMLFile) == 0) {
                return he;
            }
        }
        return null;
    }
}
