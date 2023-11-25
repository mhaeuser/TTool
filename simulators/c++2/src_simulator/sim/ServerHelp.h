/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
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
 *
 */

#include <definitions.h>
#include <ServerHelpData.h>

class CommandInfo {
    public:
        std::string fullCmd;
        std::string alias;
        std::string code;
        std::string params;
        std::string description;

        CommandInfo(std::string _fullCmd, std::string _alias, std::string _code, std::string _params, std::string _desc) { // Constructor with parameters
            fullCmd = _fullCmd;
            alias = _alias;
            code = _code;
            params = _params;
            description = _desc;
        }
};

///Class helping print help command and help sever
class ServerHelp {
public:
	///Constructor
	ServerHelp();
	///print the help server
	void printHelpSever();
	///print the help to specific command
	void printHelpCommand(std::string cmd);
	///parse html and getdata
	void parseHtml(std::string filePath);

	bool replace(std::string& str, const std::string& from, const std::string& to);

	std::vector<std::string> splitData(std::string s, std::string delimiter);

    // trim from both ends of string (right then left)
    inline std::string trim(std::string s) {
        std::string t = " \t\n\r\f\v";
        s.erase(s.find_last_not_of(t) + 1);
        s.erase(0, s.find_first_not_of(t));
        return s;
    }

	std::map <std::string, CommandInfo> allCmds;
	std::map <std::string, std::string> aliasMapWithName;
	std::string helpServerContent;

};
