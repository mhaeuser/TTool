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

package avatartranslator.mutation;

import java.util.UUID;

//import myutil.TraceManager;

/**
 * Interface MutationParser
 * Creation: 07/07/2022
 *
 * @author Léon FRENOT
 * @version 1.0 07/07/2022
 */

public interface MutationParser {

    public static final String[] MUTATION_TOKENS = {"ADD", "RM", "REMOVE", "MD", "MODIFY", "SWAP", "ATTACH", "DETACH", "SWAP"};
    public static final String[] ELEMENT_TOKENS = {"ATTRIBUTE", "METHOD", "INPUT", "OUTPUT", "STATE", "ACTION", "RANDOM", "SET", "RESET", "EXPIRE", "TRANSITION", "CONNECTION", "LINK", "SIGNAL", "BLOCK"};
    public static final String[] INOUT_TOKENS = {"INPUT", "OUTPUT"};
    public static final String[] KEYWORD_TOKENS = {"IN", "FROM", "TO", "WITH", "AND", "LATENCY", "AT", "BETWEEN"};
    public static final String[] PUBLIC_TOKENS = {"PUBLIC", "PRIVATE"};
    public static final String[] SYNCH_TOKENS = {"SYNCHRONOUS", "SYNCH", "ASYNCHRONOUS", "ASYNCH"};

    public static final int UNDEFINED_TYPE = -1;
    public static final int NAME_TYPE = 0;
    public static final int UUID_TYPE = 1;

    public static boolean isToken(String str) {
        if (findMutationToken(str).length() > 0) return true;
        if (findElementToken(str).length() > 0) return true;
        if (findInOutToken(str).length() > 0) return true;
        if (findKeywordToken(str).length() > 0) return true;
        return false;
    }

    public static String tokensToString(String[] tokens) {
        int len = tokens.length;

        String out = "[ " + tokens[0];

        for(int i = 1; i < len; i++) {
            out += " ; " + tokens[i];
        }

        out += " ]";

        return out;

    }

    public static String[] tokenise(String _toParse, String tokens) {
        //TraceManager.addDev(_toParse + " ; " + tokens);
        int len = tokens.length();
        String toParse = _toParse;
        for (int index = 0; index < len; index++) {
            String c = String.valueOf(tokens.charAt(index));
            toParse = toParse.replace(c, "~" + c + "~");
        }
        //TraceManager.addDev(toParse);
        String[] out = toParse.split("(~[ ]*)+");
        //TraceManager.addDev(tokensToString(out));
        return out;
    }

    public static String[] tokenise(String toParse) {
        return tokenise(toParse, "(),[] ");
    }

    public static int indexOf(int from, String[] arr, String token) {
        int len = arr.length;
        for (int index = from; index < len; index++) {
            //TraceManager.addDev(token + " =? " + arr[index].toUpperCase());
            if (token.toUpperCase().equals(arr[index].toUpperCase())) return index;
        }
        return -1;
    }

    public static int indexOf(String[] arr, String token) {
        return indexOf(0, arr, token);
    }

    public static boolean isTokenIn(int index, String[] arr, String token) {
        return indexOf(index, arr, token) != -1;
    }

    public static boolean isTokenIn(String[] arr, String token) {
        return isTokenIn(0, arr, token);
    }

    public static boolean isTokenIn(String s, String token) {
        return isTokenIn(tokenise(s.toUpperCase()), token);
    }

    public static String findToken(String[] arr, String[] tokens) {
        for(String token : tokens) {
            if(isTokenIn(arr, token)) return token;
        }
        return "";
    }

    public static String findToken(String str, String[] tokens) {
        return findToken(tokenise(str.toUpperCase()), tokens);
    }

    public static String findMutationToken(String str) {
        return findToken(str, MUTATION_TOKENS);
    }

    public static String findElementToken(String str) {
        return findToken(str, ELEMENT_TOKENS);
    }

    public static String findInOutToken(String[] arr) {
        return findToken(arr, INOUT_TOKENS);
    }

    public static String findInOutToken(String str) {
        return findToken(str, INOUT_TOKENS);
    }

    public static String findKeywordToken(String[] arr) {
        return findToken(arr, KEYWORD_TOKENS);
    }

    public static String findKeywordToken(String str) {
        return findToken(str, KEYWORD_TOKENS);
    }

    public static String findPublicToken(String[] arr) {
        return findToken(arr, PUBLIC_TOKENS);
    }

    public static String findPublicToken(String str) {
        return findToken(str, PUBLIC_TOKENS);
    }

    public static String findSynchToken(String[] arr) {
        return findToken(arr, SYNCH_TOKENS);
    }

    public static String findSynchToken(String str) {
        return findToken(str, SYNCH_TOKENS);
    }

    public static int UUIDType(String _name) {
        if(isUUID(_name)) {
            return UUID_TYPE;
        }
        return NAME_TYPE;
    }

    public static boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String concatenate(int startIndex, int endIndex, String[] arr) {
        String out = arr[startIndex];
        for(int i = startIndex + 1; i < endIndex && i < arr.length; i++) {
            out += " " + arr[i];
        }
        return out;
    }

    public static String concatenate(int startIndex, String[] arr) {
        return concatenate(startIndex, arr.length, arr);
    }

}