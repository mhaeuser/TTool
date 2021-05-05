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
#include<ServerHelp.h>
#include<unistd.h>

ServerHelp::ServerHelp() {
    //add all available commands here, when a new command is added it should be added both from here and CommandParser.java
    //active breakpoint
	CommandInfo ab("active-breakpoints", "ab", "20", "<int 0/1 (unactive / active)>", "Active / unactive breakpoints");
	allCmds.insert(std::pair<std::string, CommandInfo>(ab.fullCmd, ab));

	//add-breakpoint
	CommandInfo abp("add-breakpoint", "abp", "11", "<int TaskID, string CommandID", "Set a breakpoint in task which id is the first parameter on the command provided as the second parameter");
    allCmds.insert(std::pair<std::string, CommandInfo>(abp.fullCmd, abp));

	//calculate-latencies
	CommandInfo cl("calculate-latencies", "cl", "23", "<int: Checkpoint 1 id> <int: Checkpoint2 id>", "Calculate latencies between checkpoints");
    allCmds.insert(std::pair<std::string, CommandInfo>(cl.fullCmd, cl));

	//choose-branch
	CommandInfo cb("choose-branch", "cb", "12", "<int: task ID>", "Chooses the branch of the given command of a task");
    allCmds.insert(std::pair<std::string, CommandInfo>(cb.fullCmd, cb));

	//get-breakpoint-list
	CommandInfo gbl("get-breakpoint-list", "gbl", "18", "", "Returns the list of breakpoints currently set");
    allCmds.insert(std::pair<std::string, CommandInfo>(gbl.fullCmd, gbl));

	//get-command-of-task
	CommandInfo gcot("get-command-of-task", "gcot", "14", "", "Returns the current command of the task provided as argument");
    allCmds.insert(std::pair<std::string, CommandInfo>(gcot.fullCmd, gcot));

	//get-benchmark
	CommandInfo gb("get-benchmark", "gb", "10", "<int: 0: show benchmark; 1:save in file>", "Returns information on hardware nodes of the architecture");
    allCmds.insert(std::pair<std::string, CommandInfo>(gb.fullCmd, gb));

	//get-executed-operators
	CommandInfo geo("get-executed-operators", "geo", "21", "", "Returns the list of executed operators");
    allCmds.insert(std::pair<std::string, CommandInfo>(geo.fullCmd, geo));

    //get-hashcode
    CommandInfo gh("get-hashcode", "gh", "19", "", "Returns the hashcode of the tmap under simulation");
    allCmds.insert(std::pair<std::string, CommandInfo>(gh.fullCmd, gh));

    //get-info-on-hw
    CommandInfo gioh("get-info-on-hw", "gioh", "4", "<int: 0: CPU; 1:Bus; 2: Mem; 3: Bridge; 4: Channel; 5: Task> <int: id>", "Returns information on hardware nodes of the architecture");
    allCmds.insert(std::pair<std::string, CommandInfo>(gioh.fullCmd, gioh));

    //get-numer-of-branches
    CommandInfo gnob("get-numer-of-branches", "gnob", "17", "", "Returns the number of branches the current command has");
    allCmds.insert(std::pair<std::string, CommandInfo>(gnob.fullCmd, gnob));

    //get-simulation-time
    CommandInfo time("get-simulation-time", "time", "13", "", "Returns the current absolute time unit of the simulation");
    allCmds.insert(std::pair<std::string, CommandInfo>(time.fullCmd, time));

    //get-variable-of-task
    CommandInfo gvof("get-variable-of-task", "gvof", "3", "", "Returns the value of a variable a a task");
    allCmds.insert(std::pair<std::string, CommandInfo>(gvof.fullCmd, gvof));

    //kill
    CommandInfo kill("kill", "", "0", "", "Terminates the remote simulator");
    allCmds.insert(std::pair<std::string, CommandInfo>(kill.fullCmd, kill));

    //list-transactions
    CommandInfo lt("list-transactions", "lt", "22", "<string: Max. nb of transactions>", "Get the most recent transactions");
    allCmds.insert(std::pair<std::string, CommandInfo>(lt.fullCmd, lt));

    //list-all-transactions-of-a-task
    CommandInfo lat("list-all-transactions-of-a-task", "lat", "25", "<string: Task Name>", "Get all transactions of Task");
    allCmds.insert(std::pair<std::string, CommandInfo>(lat.fullCmd, lat));

    //remove-all-trans
    CommandInfo rmat("remove-all-trans", "rmat", "26", "<int: <YES>: 1, <NO>: 0>", "Remove all the transactions in the past");
    allCmds.insert(std::pair<std::string, CommandInfo>(rmat.fullCmd, rmat));

    //rm-breakpoint
    CommandInfo rmb("rm-breakpoint", "rmb", "16", "<int: task ID>", "Remove a breakpoint in task which id is the first parameter on the command provided as the second parameter");
    allCmds.insert(std::pair<std::string, CommandInfo>(rmb.fullCmd, rmb));

    //reset
    CommandInfo reset("reset", "", "2", "", "Resets the remote simulator");
    allCmds.insert(std::pair<std::string, CommandInfo>(reset.fullCmd, reset));

    //raw-command
    CommandInfo rc("raw-command", "rc", "", "<[string: param #0] [string: param #1] [string: param #2] [string: param #3] [string: param #4]>", "Sends a raw command to the remote simulator");
    allCmds.insert(std::pair<std::string, CommandInfo>(rc.fullCmd, rc));

    //restore-simulation-state-from-file
    CommandInfo rssff("restore-simulation-state-from-file", "rssff", "9", "<string: File name>", "Restores the simulation state from a file");
    allCmds.insert(std::pair<std::string, CommandInfo>(rssff.fullCmd, rssff));

    //run-exploration
    CommandInfo re("run-exploration", "re", "1 7", "[int between 0 and 100 (percentage): Minimum number of explored commands] [int between 0 and 100 (percentage): File name of the resulting graph, with NO extension] <string: null>", "Runs the simulation in exploration mode");
    allCmds.insert(std::pair<std::string, CommandInfo>(re.fullCmd, re));

    //run-to-next-breakpoint
    CommandInfo rtnb("run-to-next-breakpoint", "rtnb", "1 0", "", "Runs the simulation until a breakpoint is met");
    allCmds.insert(std::pair<std::string, CommandInfo>(rtnb.fullCmd, rtnb));

    //run-to-next-breakpoint-max-trans
    CommandInfo rtnbmt("run-to-next-breakpoint-max-trans", "rtnbmt", "1 19", "<int: max nb of transactions>", "Runs the simulation until a breakpoint is met or max number of transactions are executed");
    allCmds.insert(std::pair<std::string, CommandInfo>(rtnbmt.fullCmd, rtnbmt));

    //run-to-next-transfer-on-bus
    CommandInfo rtntob("run-to-next-transfer-on-bus", "rtntob", "1 8", "<int: bus id>", "Runs to the next transfer on bus which id is provided as argument");
    allCmds.insert(std::pair<std::string, CommandInfo>(rtntob.fullCmd, rtntob));

    //run-to-time
    CommandInfo rtt("run-to-time", "rtt", "1 5", "<int: x: time value>", "Runs the simulation until time x is reached");
    allCmds.insert(std::pair<std::string, CommandInfo>(rtt.fullCmd, rtt));

    //run-until-channel-access
    CommandInfo ruca("run-until-channel-access", "ruca", "1 12", "<int: Channel id>", "Run simulation until a operation is performed on the channel which ID is provided as parameter");
    allCmds.insert(std::pair<std::string, CommandInfo>(ruca.fullCmd, ruca));

    //run-until-write-on-channel-access
    CommandInfo ruwca("run-until-write-on-channel-access", "ruwca", "1 17", "<string: Channel name>", "Run simulation until a write operation is performed on the channel which channel name is provided as parameter");
    allCmds.insert(std::pair<std::string, CommandInfo>(ruwca.fullCmd, ruwca));

    //run-until-read-on-channel-access
    CommandInfo rurca("run-until-read-on-channel-access", "rurca", "1 18", "<string: Channel name>", "Run simulation until a read operation is performed on the channel which chanel name is provided as parameter");
    allCmds.insert(std::pair<std::string, CommandInfo>(rurca.fullCmd, rurca));

    //run-until-cpu-executes
    CommandInfo ruce("run-until-cpu-executes", "ruce", "1 9", "<int: CPU id>", "Run simulation until CPU which ID is provided as parameter executes");
    allCmds.insert(std::pair<std::string, CommandInfo>(ruce.fullCmd, ruce));

    //run-until-memory-access
    CommandInfo ruma("run-until-memory-access", "ruma", "1 11", "<int: Memory id>", "Run simulation until the memory which ID is provided as parameter is accessed");
    allCmds.insert(std::pair<std::string, CommandInfo>(ruma.fullCmd, ruma));

    //run-until-task-executes
    CommandInfo rute("run-until-task-executes", "rute", "1 10", "<int: Task id>", "Run simulation until the task which ID is provided as parameter executes");
    allCmds.insert(std::pair<std::string, CommandInfo>(rute.fullCmd, rute));

    //run-x-commands
    CommandInfo rxtu("run-x-commands", "rxtu", "1 6", "<int: nb of commands>", "Runs the simulation for x commands");
    allCmds.insert(std::pair<std::string, CommandInfo>(rxtu.fullCmd, rxtu));

    //run-x-time-units
    CommandInfo rxcomm("run-x-time-units", "rxcomm", "1 4", "<int: nb of time units>", "Runs the simulation for x units of time");
    allCmds.insert(std::pair<std::string, CommandInfo>(rxcomm.fullCmd, rxcomm));

    //run-x-transactions
    CommandInfo rxtr("run-x-transactions", "rxtr", "1 2", "<int: nb of transactions>", "Runs the simulation for x transactions");
    allCmds.insert(std::pair<std::string, CommandInfo>(rxtr.fullCmd, rxtr));

    //save-simulation-state-in-file
    CommandInfo sssif("save-simulation-state-in-file", "sssif", "8", "<string: File name>", "Saves the current simulation state into a file");
    allCmds.insert(std::pair<std::string, CommandInfo>(sssif.fullCmd, sssif));

    //save-trace-in-file
    CommandInfo stif("save-trace-in-file", "stif", "7", "<int: File format: 0-> VCD, 1->HTML, 2->TXT, 3->XML> <string: File name>", "Saves the current trace of the simulation in a VCD, HTML, TXT or XML file");
    allCmds.insert(std::pair<std::string, CommandInfo>(stif.fullCmd, stif));

    //show-timeline-trace
    CommandInfo stlt("show-timeline-trace", "stlt", "7 4", "<string: Task List> <int: Scale idle time: 0 -> no, 1 -> yes>", "Show the current timeline diagram tracein HTML format");
    allCmds.insert(std::pair<std::string, CommandInfo>(stlt.fullCmd, stlt));

    //set-variable
    CommandInfo sv("set-variable", "sv", "5", "<int: task ID> <int: variable ID> <int: variable value>", "Set the value of a variable");
    allCmds.insert(std::pair<std::string, CommandInfo>(sv.fullCmd, sv));

    //stop
    CommandInfo stop("stop", "", "15", "", "Stops the currently running simulation");
    allCmds.insert(std::pair<std::string, CommandInfo>(stop.fullCmd, stop));

    //write-in-channel
    CommandInfo wic("write-in-channel", "wic", "6", "<int: Channel ID> <string: Nb of samples>", "Writes y samples / events to channel / event x");
    allCmds.insert(std::pair<std::string, CommandInfo>(wic.fullCmd, wic));

    //add-virtual-signals
    CommandInfo avs("add-virtual-signals", "avs", "1 16", "<string: Channel name> <string: Nb of samples> <string: value of samples>", "Send virtual events to channel");
    allCmds.insert(std::pair<std::string, CommandInfo>(avs.fullCmd, avs));

    //generate help server content
    helpServerContent = "\n************ Available Commands ************\n";
    std::map<std::string, CommandInfo>::iterator it;
    for (it = allCmds.begin(); it != allCmds.end(); it++) {
        helpServerContent += it->first + "/" + it->second.alias + "\n";
        aliasMapWithName[it->second.alias] = it->first;
    }

    helpServerContent += "********************************************\n\n";
}

void ServerHelp::printHelpSever(){
    std::cout << helpServerContent << std:: endl;
}

void ServerHelp::printHelpCommand(std::string cmd) {
    std::string result = "";
    std::map<std::string, CommandInfo>::iterator it;

    if (!cmd.empty()) {
        std::map<std::string, std::string>::iterator ite;
        ite = aliasMapWithName.find(cmd);
        // in case user using alias for help instead of real command name, we need to find the real name by alias
        if (ite != aliasMapWithName.end()) {
            cmd = ite->second;
        }

        it = allCmds.find(cmd);
        if (it != allCmds.end()) {
            result += "\nHelp on command: " + cmd + "\n";
            result += "-------------------------------------------------------------\n";
            result += cmd + " " + it->second.params + "\n";
            result += it->second.description + "\n";
            result += "alias: " + it->second.alias + "\n";
            result += "code: " + it->second.code + "\n";
            result += "-------------------------------------------------------------\n\n";
        } else {
            result += "Wrong command name!\n";
        }
    }

    std::cout << result << std::endl;
}
