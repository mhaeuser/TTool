# HelpServer

All the commands on server side.

## Rules to add new command
```
- Increase the total number of commands;
- Each command has at least 5 parameters and make sure its order is correct, the parameters separate each other by ";" and use ":=" to assign value:
    - command name:= command name;
    - alias:= command alias;
    - code:= command code;
    - description:= command description;
    - nb of param:= number of params;
- If the nb of param > 0 then add the param types and paramsNames base on the nb of param:
    - params[]: type of params: 0-> optinal, 1-> int, 2-> string ... (see function "getParamString()" in "SimulationCommand.java" for more detail);
    - paramsNames[]: description of the corresponding params;
- After adding new command, run "make internalhelp" and then "make allnotest" to validate new cmd to TTool
```
## Number of commands:= 42

### command #1
- command name:= active-breakpoints;\
alias:= ab;\
code:= 20;\
description:= Active / unactive breakpoints;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= 0/1 (unactive / active);

### command #2
- command name:= add-breakpoint;\
alias:= abp;\
code:= 11;\
description:= Set a breakpoint in task which id is the first parameter on the command provided as the second parameter;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= task ID;\
params[1]:= 0;\
paramNames[1]:= comamnd ID;

### command #3
- command name:= calculate-latencies;\
alias:= cl;\
code:= 23;\
description:= Calculate latencies between checkpoints;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= Checkpoint 1 id;\
params[1]:= 1;\
paramNames[1]:= Checkpoint2 id;

### command #4
- command name:= choose-branch;\
alias:= cb;\
code:= 12;\
description:= Chooses the branch of the given command of a task;\
nb of param:= 3;\
params[0]:= 1;\
paramNames[0]:= task ID;\
params[1]:= 0;\
paramNames[1]:= command ID;\
params[2]:= 0;\
paramNames[2]:= branch ID;

### command #5
- command name:= get-breakpoint-list;\
alias:= gbl;\
code:= 18;\
description:= Returns the list of breakpoints currently set;\
nb of param:= 0;

### command #6
- command name:= get-command-of-task;\
alias:= gcot;\
code:= 14;\
description:= Returns the current command of the task provided as argument;\
nb of param:= 1;\
params[0]:= 0;\
paramNames[0]:= Task id (or "all");

### command #7
- command name:= get-benchmark;\
alias:= gb;\
code:= 10;\
description:= Returns information on hardware nodes of the architecture;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= 0: show benchmark, 1:save in file;\
params[1]:= 0;\
paramNames[1]:= Name of file;

### command #8
- command name:= get-executed-operators;\
alias:= geo;\
code:= 21;\
description:= Returns the list of executed operators;\
nb of param:= 0;

### command #9
- command name:= get-hashcode;\
alias:= gh;\
code:= 19;\
description:= Returns the hashcode of the tmap under simulation;\
nb of param:= 0;

### command #10
- command name:= get-info-on-hw;\
alias:= gioh;\
code:= 4;\
description:= Returns information on hardware nodes of the architecture;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= 0: CPU, 1:Bus, 2: Mem, 3: Bridge, 4: Channel, 5: Task;\
params[1]:= 1;\
paramNames[1]:= id;

### command #11
- command name:= get-numer-of-branches;\
alias:= gnob;\
code:= 17;\
description:= Returns the number of branches the current command has;\
nb of param:= 0;

### command #12
- command name:= get-simulation-time;\
alias:= time;\
code:= 13;\
description:= Returns the current absolute time unit of the simulation;\
nb of param:= 0;

### command #13
- command name:= get-variable-of-task;\
alias:= gvof;\
code:= 3;\
description:= Returns the value of a variable a a task;\
nb of param:= 2;\
params[0]:= 0;\
paramNames[0]:= Task id;\
params[1]:= 0;\
paramNames[1]:= Variable id;

### command #14
- command name:= kill;\
alias:= kill;\
code:= 0;\
description:= Terminates the remote simulator;\
nb of param:= 0;

### command #15
- command name:= list-transactions;\
alias:= lt;\
code:= 22;\
description:= Get the most recent transactions;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= Max. nb of transactions;

### command #16
- command name:= list-all-transactions-of-a-task;\
alias:= lat;\
code:= 25;\
description:= Get all transactions of Task;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= Task Name;

### command #17
- command name:= remove-all-trans;\
alias:= rmat;\
code:= 26;\
description:= Remove all the transactions in the past;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= <YES>: 1, <NO>: 0;

### command #18
- command name:= rm-breakpoint;\
alias:= rmb;\
code:= 16;\
description:= Remove a breakpoint in task which id is the first parameter on the command provided as the second parameter;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= task ID;\
params[1]:= 0;\
paramNames[1]:= comamnd ID;

### command #19
- command name:= reset;\
alias:= reset;\
code:= 2;\
description:= Resets the remote simulator;\
nb of param:= 0;

### command #20
- command name:= raw-command;\
alias:= rc;\
code:= ;\
description:= Sends a raw command to the remote simulator;\
nb of param:= 5;\
params[0]:= 4;\
paramNames[0]:= param #0;\
params[1]:= 4;\
paramNames[1]:= param #1;\
params[2]:= 4;\
paramNames[2]:= param #2;\
params[3]:= 4;\
paramNames[3]:= param #3;\
params[4]:= 4;\
paramNames[4]:= param #4;

### command #21
- command name:= restore-simulation-state-from-file;\
alias:= rssff;\
code:= 9;\
description:= Restores the simulation state from a file;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= File name;

### command #22
- command name:= run-exploration;\
alias:= re;\
code:= 1 7;\
description:= Runs the simulation in exploration mode;\
nb of param:= 3;\
params[0]:= 6;\
paramNames[0]:= Minimum number of explored commands;\
params[1]:= 6;\
paramNames[1]:= Minimum number of explored branches;\
params[2]:= 2;\
paramNames[2]:= File name of the resulting graph, with NO extension;

### command #23
- command name:= run-to-next-breakpoint;\
alias:= rtnb;\
code:= 1 0;\
description:= Runs the simulation until a breakpoint is met;\
nb of param:= 0;

### command #24
- command name:= run-to-next-breakpoint-max-trans;\
alias:= rtnbmt;\
code:= 1 19;\
description:= Runs the simulation until a breakpoint is met or max number of transactions are executed;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= max nb of transactions;

### command #25
- command name:= run-to-next-transfer-on-bus;\
alias:= rtntob;\
code:= 1 8;\
description:= Runs to the next transfer on bus which id is provided as argument;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= bus id;

### command #26
- command name:= run-to-time;\
alias:= rtt;\
code:= 1 5;\
description:= Runs the simulation until time x is reached;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= x: time value;

### command #27
- command name:= run-until-channel-access;\
alias:= ruca;\
code:= 1 12;\
description:= Run simulation until a operation is performed on the channel which ID is provided as parameter;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= Channel id;

### command #28
- command name:= run-until-write-on-channel-access;\
alias:= ruwca;\
code:= 1 17;\
description:= Run simulation until a write operation is performed on the channel which channel name is provided as parameter;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= Channel name;

### command #29
- command name:= run-until-read-on-channel-access;\
alias:= rurca;\
code:= 1 18;\
description:= Run simulation until a read operation is performed on the channel which chanel name is provided as parameter;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= Channel name;

### command #30
- command name:= run-until-cpu-executes;\
alias:= ruce;\
code:= 1 9;\
description:= Run simulation until CPU which ID is provided as parameter executes;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= CPU id;

### command #31
- command name:= run-until-memory-access;\
alias:= ruma;\
code:= 1 11;\
description:= Run simulation until the memory which ID is provided as parameter is accessed;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= Memory id;

### command #32
- command name:= run-until-task-executes;\
alias:= rute;\
code:= 1 10;\
description:= Run simulation until the task which ID is provided as parameter executes;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= Task id;

### command #33
- command name:= run-x-commands;\
alias:= rxcomm;\
code:= 1 4;\
description:= Runs the simulation for x commands;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= nb of commands;

### command #34
- command name:= run-x-time-units;\
alias:= rxtu;\
code:= 1 6;\
description:= Runs the simulation for x units of time;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= nb of time units;

### command #35
- command name:= run-x-transactions;\
alias:= rxtr;\
code:= 1 2;\
description:= Runs the simulation for x transactions;\
nb of param:= 1;\
params[0]:= 1;\
paramNames[0]:= nb of transactions;

### command #36
- command name:= save-simulation-state-in-file;\
alias:= sssif;\
code:= 8;\
description:= Saves the current simulation state into a file;\
nb of param:= 1;\
params[0]:= 2;\
paramNames[0]:= File name;

### command #37
- command name:= save-trace-in-file;\
alias:= stif;\
code:= 7;\
description:= Saves the current trace of the simulation in a VCD, HTML, TXT or XML file;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= File format: 0-> VCD, 1->HTML, 2->TXT, 3->XML;\
params[1]:= 2;\
paramNames[1]:= File name;

### command #38
- command name:= show-timeline-trace;\
alias:= stlt;\
code:= 7 4;\
description:= Show the current timeline diagram tracein HTML format;\
nb of param:= 2;\
params[0]:= 2;\
paramNames[0]:= Task List;\
params[1]:= 1;\
paramNames[1]:= Scale idle time: 0 -> no, 1 -> yes;

### command #39
- command name:= set-variable;\
alias:= sv;\
code:= 5;\
description:= Set the value of a variable;\
nb of param:= 3;\
params[0]:= 1;\
paramNames[0]:= task ID;\
params[1]:= 1;\
paramNames[1]:= variable ID;\
params[2]:= 1;\
paramNames[2]:= variable value;

### command #40
- command name:= stop;\
alias:= stop;\
code:= 15;\
description:= Stops the currently running simulation;\
nb of param:= 0;

### command #41
- command name:= write-in-channel;\
alias:= wic;\
code:= 6;\
description:= Writes y samples / events to channel / event x;\
nb of param:= 2;\
params[0]:= 1;\
paramNames[0]:= Channel ID;\
params[1]:= 2;\
paramNames[1]:= Nb of samples;

### command #42
- command name:= add-virtual-signals;\
alias:= avs;\
code:= 1 16;\
description:= Send virtual events to channel;\
nb of param:= 3;\
params[0]:= 2;\
paramNames[0]:= Channel name;\
params[1]:= 2;\
paramNames[1]:= Nb of samples;\
params[2]:= 2;\
paramNames[2]:= value of samples;
