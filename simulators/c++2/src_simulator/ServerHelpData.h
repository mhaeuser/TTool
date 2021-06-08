#define STRING_HELP " \n\
<!DOCTYPE html> \n\
<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"\" xml:lang=\"\"> \n\
<head> \n\
  <meta charset=\"utf-8\" /> \n\
  <meta name=\"generator\" content=\"pandoc\" /> \n\
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=yes\" /> \n\
  <title>TTool help</title> \n\
  <style> \n\
    code{white-space: pre-wrap;} \n\
    span.smallcaps{font-variant: small-caps;} \n\
    span.underline{text-decoration: underline;} \n\
    div.column{display: inline-block; vertical-align: top; width: 50%;} \n\
    div.hanging-indent{margin-left: 1.5em; text-indent: -1.5em;} \n\
    ul.task-list{list-style: none;} \n\
  </style> \n\
  <!--[if lt IE 9]> \n\
    <script src=\"//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv-printshiv.min.js\"></script> \n\
  <![endif]--> \n\
</head> \n\
<body> \n\
<h1 id=\"helpserver\">HelpServer</h1> \n\
<p>All the commands on server side.</p> \n\
<h2 id=\"rules-to-add-new-command\">Rules to add new command</h2> \n\
<pre><code>- Increase the total number of commands; \n\
- Each command has at least 5 parameters and make sure its order is correct, the parameters separate each other by &quot;;&quot; and use &quot;:=&quot; to assign value: \n\
    - command name:= command name; \n\
    - alias:= command alias; \n\
    - code:= command code; \n\
    - description:= command description; \n\
    - nb of param:= number of params; \n\
- If the nb of param &gt; 0 then add the param types and paramsNames base on the nb of param: \n\
    - params[]: type of params: 0-&gt; optinal, 1-&gt; int, 2-&gt; string ... (see function &quot;getParamString()&quot; in &quot;SimulationCommand.java&quot; for more detail); \n\
    - paramsNames[]: description of the corresponding params; \n\
- After adding new command, run &quot;make internalhelp&quot; and then &quot;make allnotest&quot; to validate new cmd to TTool</code></pre> \n\
<h2 id=\"number-of-commands-42\">Number of commands:= 42</h2> \n\
<h3 id=\"command-1\">command #1</h3> \n\
<ul> \n\
<li>command name:= active-breakpoints;<br /> \n\
alias:= ab;<br /> \n\
code:= 20;<br /> \n\
description:= Active / unactive breakpoints;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= 0/1 (unactive / active);</li> \n\
</ul> \n\
<h3 id=\"command-2\">command #2</h3> \n\
<ul> \n\
<li>command name:= add-breakpoint;<br /> \n\
alias:= abp;<br /> \n\
code:= 11;<br /> \n\
description:= Set a breakpoint in task which id is the first parameter on the command provided as the second parameter;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= task ID;<br /> \n\
params[1]:= 0;<br /> \n\
paramNames[1]:= comamnd ID;</li> \n\
</ul> \n\
<h3 id=\"command-3\">command #3</h3> \n\
<ul> \n\
<li>command name:= calculate-latencies;<br /> \n\
alias:= cl;<br /> \n\
code:= 23;<br /> \n\
description:= Calculate latencies between checkpoints;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= Checkpoint 1 id;<br /> \n\
params[1]:= 1;<br /> \n\
paramNames[1]:= Checkpoint2 id;</li> \n\
</ul> \n\
<h3 id=\"command-4\">command #4</h3> \n\
<ul> \n\
<li>command name:= choose-branch;<br /> \n\
alias:= cb;<br /> \n\
code:= 12;<br /> \n\
description:= Chooses the branch of the given command of a task;<br /> \n\
nb of param:= 3;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= task ID;<br /> \n\
params[1]:= 0;<br /> \n\
paramNames[1]:= command ID;<br /> \n\
params[2]:= 0;<br /> \n\
paramNames[2]:= branch ID;</li> \n\
</ul> \n\
<h3 id=\"command-5\">command #5</h3> \n\
<ul> \n\
<li>command name:= get-breakpoint-list;<br /> \n\
alias:= gbl;<br /> \n\
code:= 18;<br /> \n\
description:= Returns the list of breakpoints currently set;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-6\">command #6</h3> \n\
<ul> \n\
<li>command name:= get-command-of-task;<br /> \n\
alias:= gcot;<br /> \n\
code:= 14;<br /> \n\
description:= Returns the current command of the task provided as argument;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 0;<br /> \n\
paramNames[0]:= Task id (or “all”);</li> \n\
</ul> \n\
<h3 id=\"command-7\">command #7</h3> \n\
<ul> \n\
<li>command name:= get-benchmark;<br /> \n\
alias:= gb;<br /> \n\
code:= 10;<br /> \n\
description:= Returns information on hardware nodes of the architecture;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= 0: show benchmark, 1:save in file;<br /> \n\
params[1]:= 0;<br /> \n\
paramNames[1]:= Name of file;</li> \n\
</ul> \n\
<h3 id=\"command-8\">command #8</h3> \n\
<ul> \n\
<li>command name:= get-executed-operators;<br /> \n\
alias:= geo;<br /> \n\
code:= 21;<br /> \n\
description:= Returns the list of executed operators;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-9\">command #9</h3> \n\
<ul> \n\
<li>command name:= get-hashcode;<br /> \n\
alias:= gh;<br /> \n\
code:= 19;<br /> \n\
description:= Returns the hashcode of the tmap under simulation;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-10\">command #10</h3> \n\
<ul> \n\
<li>command name:= get-info-on-hw;<br /> \n\
alias:= gioh;<br /> \n\
code:= 4;<br /> \n\
description:= Returns information on hardware nodes of the architecture;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= 0: CPU, 1:Bus, 2: Mem, 3: Bridge, 4: Channel, 5: Task;<br /> \n\
params[1]:= 1;<br /> \n\
paramNames[1]:= id;</li> \n\
</ul> \n\
<h3 id=\"command-11\">command #11</h3> \n\
<ul> \n\
<li>command name:= get-numer-of-branches;<br /> \n\
alias:= gnob;<br /> \n\
code:= 17;<br /> \n\
description:= Returns the number of branches the current command has;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-12\">command #12</h3> \n\
<ul> \n\
<li>command name:= get-simulation-time;<br /> \n\
alias:= time;<br /> \n\
code:= 13;<br /> \n\
description:= Returns the current absolute time unit of the simulation;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-13\">command #13</h3> \n\
<ul> \n\
<li>command name:= get-variable-of-task;<br /> \n\
alias:= gvof;<br /> \n\
code:= 3;<br /> \n\
description:= Returns the value of a variable a a task;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 0;<br /> \n\
paramNames[0]:= Task id;<br /> \n\
params[1]:= 0;<br /> \n\
paramNames[1]:= Variable id;</li> \n\
</ul> \n\
<h3 id=\"command-14\">command #14</h3> \n\
<ul> \n\
<li>command name:= kill;<br /> \n\
alias:= kill;<br /> \n\
code:= 0;<br /> \n\
description:= Terminates the remote simulator;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-15\">command #15</h3> \n\
<ul> \n\
<li>command name:= list-transactions;<br /> \n\
alias:= lt;<br /> \n\
code:= 22;<br /> \n\
description:= Get the most recent transactions;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Max. nb of transactions;</li> \n\
</ul> \n\
<h3 id=\"command-16\">command #16</h3> \n\
<ul> \n\
<li>command name:= list-all-transactions-of-a-task;<br /> \n\
alias:= lat;<br /> \n\
code:= 25;<br /> \n\
description:= Get all transactions of Task;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Task Name;</li> \n\
</ul> \n\
<h3 id=\"command-17\">command #17</h3> \n\
<ul> \n\
<li>command name:= remove-all-trans;<br /> \n\
alias:= rmat;<br /> \n\
code:= 26;<br /> \n\
description:= Remove all the transactions in the past;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= <YES>: 1, <NO>: 0;</li> \n\
</ul> \n\
<h3 id=\"command-18\">command #18</h3> \n\
<ul> \n\
<li>command name:= rm-breakpoint;<br /> \n\
alias:= rmb;<br /> \n\
code:= 16;<br /> \n\
description:= Remove a breakpoint in task which id is the first parameter on the command provided as the second parameter;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= task ID;<br /> \n\
params[1]:= 0;<br /> \n\
paramNames[1]:= comamnd ID;</li> \n\
</ul> \n\
<h3 id=\"command-19\">command #19</h3> \n\
<ul> \n\
<li>command name:= reset;<br /> \n\
alias:= reset;<br /> \n\
code:= 2;<br /> \n\
description:= Resets the remote simulator;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-20\">command #20</h3> \n\
<ul> \n\
<li>command name:= raw-command;<br /> \n\
alias:= rc;<br /> \n\
code:= ;<br /> \n\
description:= Sends a raw command to the remote simulator;<br /> \n\
nb of param:= 5;<br /> \n\
params[0]:= 4;<br /> \n\
paramNames[0]:= param #0;<br /> \n\
params[1]:= 4;<br /> \n\
paramNames[1]:= param #1;<br /> \n\
params[2]:= 4;<br /> \n\
paramNames[2]:= param #2;<br /> \n\
params[3]:= 4;<br /> \n\
paramNames[3]:= param #3;<br /> \n\
params[4]:= 4;<br /> \n\
paramNames[4]:= param #4;</li> \n\
</ul> \n\
<h3 id=\"command-21\">command #21</h3> \n\
<ul> \n\
<li>command name:= restore-simulation-state-from-file;<br /> \n\
alias:= rssff;<br /> \n\
code:= 9;<br /> \n\
description:= Restores the simulation state from a file;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= File name;</li> \n\
</ul> \n\
<h3 id=\"command-22\">command #22</h3> \n\
<ul> \n\
<li>command name:= run-exploration;<br /> \n\
alias:= re;<br /> \n\
code:= 1 7;<br /> \n\
description:= Runs the simulation in exploration mode;<br /> \n\
nb of param:= 3;<br /> \n\
params[0]:= 6;<br /> \n\
paramNames[0]:= Minimum number of explored commands;<br /> \n\
params[1]:= 6;<br /> \n\
paramNames[1]:= Minimum number of explored branches;<br /> \n\
params[2]:= 2;<br /> \n\
paramNames[2]:= File name of the resulting graph, with NO extension;</li> \n\
</ul> \n\
<h3 id=\"command-23\">command #23</h3> \n\
<ul> \n\
<li>command name:= run-to-next-breakpoint;<br /> \n\
alias:= rtnb;<br /> \n\
code:= 1 0;<br /> \n\
description:= Runs the simulation until a breakpoint is met;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-24\">command #24</h3> \n\
<ul> \n\
<li>command name:= run-to-next-breakpoint-max-trans;<br /> \n\
alias:= rtnbmt;<br /> \n\
code:= 1 19;<br /> \n\
description:= Runs the simulation until a breakpoint is met or max number of transactions are executed;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= max nb of transactions;</li> \n\
</ul> \n\
<h3 id=\"command-25\">command #25</h3> \n\
<ul> \n\
<li>command name:= run-to-next-transfer-on-bus;<br /> \n\
alias:= rtntob;<br /> \n\
code:= 1 8;<br /> \n\
description:= Runs to the next transfer on bus which id is provided as argument;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= bus id;</li> \n\
</ul> \n\
<h3 id=\"command-26\">command #26</h3> \n\
<ul> \n\
<li>command name:= run-to-time;<br /> \n\
alias:= rtt;<br /> \n\
code:= 1 5;<br /> \n\
description:= Runs the simulation until time x is reached;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= x: time value;</li> \n\
</ul> \n\
<h3 id=\"command-27\">command #27</h3> \n\
<ul> \n\
<li>command name:= run-until-channel-access;<br /> \n\
alias:= ruca;<br /> \n\
code:= 1 12;<br /> \n\
description:= Run simulation until a operation is performed on the channel which ID is provided as parameter;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= Channel id;</li> \n\
</ul> \n\
<h3 id=\"command-28\">command #28</h3> \n\
<ul> \n\
<li>command name:= run-until-write-on-channel-access;<br /> \n\
alias:= ruwca;<br /> \n\
code:= 1 17;<br /> \n\
description:= Run simulation until a write operation is performed on the channel which channel name is provided as parameter;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Channel name;</li> \n\
</ul> \n\
<h3 id=\"command-29\">command #29</h3> \n\
<ul> \n\
<li>command name:= run-until-read-on-channel-access;<br /> \n\
alias:= rurca;<br /> \n\
code:= 1 18;<br /> \n\
description:= Run simulation until a read operation is performed on the channel which chanel name is provided as parameter;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Channel name;</li> \n\
</ul> \n\
<h3 id=\"command-30\">command #30</h3> \n\
<ul> \n\
<li>command name:= run-until-cpu-executes;<br /> \n\
alias:= ruce;<br /> \n\
code:= 1 9;<br /> \n\
description:= Run simulation until CPU which ID is provided as parameter executes;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= CPU id;</li> \n\
</ul> \n\
<h3 id=\"command-31\">command #31</h3> \n\
<ul> \n\
<li>command name:= run-until-memory-access;<br /> \n\
alias:= ruma;<br /> \n\
code:= 1 11;<br /> \n\
description:= Run simulation until the memory which ID is provided as parameter is accessed;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= Memory id;</li> \n\
</ul> \n\
<h3 id=\"command-32\">command #32</h3> \n\
<ul> \n\
<li>command name:= run-until-task-executes;<br /> \n\
alias:= rute;<br /> \n\
code:= 1 10;<br /> \n\
description:= Run simulation until the task which ID is provided as parameter executes;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= Task id;</li> \n\
</ul> \n\
<h3 id=\"command-33\">command #33</h3> \n\
<ul> \n\
<li>command name:= run-x-commands;<br /> \n\
alias:= rxcomm;<br /> \n\
code:= 1 4;<br /> \n\
description:= Runs the simulation for x commands;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= nb of commands;</li> \n\
</ul> \n\
<h3 id=\"command-34\">command #34</h3> \n\
<ul> \n\
<li>command name:= run-x-time-units;<br /> \n\
alias:= rxtu;<br /> \n\
code:= 1 6;<br /> \n\
description:= Runs the simulation for x units of time;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= nb of time units;</li> \n\
</ul> \n\
<h3 id=\"command-35\">command #35</h3> \n\
<ul> \n\
<li>command name:= run-x-transactions;<br /> \n\
alias:= rxtr;<br /> \n\
code:= 1 2;<br /> \n\
description:= Runs the simulation for x transactions;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= nb of transactions;</li> \n\
</ul> \n\
<h3 id=\"command-36\">command #36</h3> \n\
<ul> \n\
<li>command name:= save-simulation-state-in-file;<br /> \n\
alias:= sssif;<br /> \n\
code:= 8;<br /> \n\
description:= Saves the current simulation state into a file;<br /> \n\
nb of param:= 1;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= File name;</li> \n\
</ul> \n\
<h3 id=\"command-37\">command #37</h3> \n\
<ul> \n\
<li>command name:= save-trace-in-file;<br /> \n\
alias:= stif;<br /> \n\
code:= 7;<br /> \n\
description:= Saves the current trace of the simulation in a VCD, HTML, TXT or XML file;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= File format: 0-&gt; VCD, 1-&gt;HTML, 2-&gt;TXT, 3-&gt;XML;<br /> \n\
params[1]:= 2;<br /> \n\
paramNames[1]:= File name;</li> \n\
</ul> \n\
<h3 id=\"command-38\">command #38</h3> \n\
<ul> \n\
<li>command name:= show-timeline-trace;<br /> \n\
alias:= stlt;<br /> \n\
code:= 7 4;<br /> \n\
description:= Show the current timeline diagram tracein HTML format;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Task List;<br /> \n\
params[1]:= 1;<br /> \n\
paramNames[1]:= Scale idle time: 0 -&gt; no, 1 -&gt; yes;</li> \n\
</ul> \n\
<h3 id=\"command-39\">command #39</h3> \n\
<ul> \n\
<li>command name:= set-variable;<br /> \n\
alias:= sv;<br /> \n\
code:= 5;<br /> \n\
description:= Set the value of a variable;<br /> \n\
nb of param:= 3;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= task ID;<br /> \n\
params[1]:= 1;<br /> \n\
paramNames[1]:= variable ID;<br /> \n\
params[2]:= 1;<br /> \n\
paramNames[2]:= variable value;</li> \n\
</ul> \n\
<h3 id=\"command-40\">command #40</h3> \n\
<ul> \n\
<li>command name:= stop;<br /> \n\
alias:= stop;<br /> \n\
code:= 15;<br /> \n\
description:= Stops the currently running simulation;<br /> \n\
nb of param:= 0;</li> \n\
</ul> \n\
<h3 id=\"command-41\">command #41</h3> \n\
<ul> \n\
<li>command name:= write-in-channel;<br /> \n\
alias:= wic;<br /> \n\
code:= 6;<br /> \n\
description:= Writes y samples / events to channel / event x;<br /> \n\
nb of param:= 2;<br /> \n\
params[0]:= 1;<br /> \n\
paramNames[0]:= Channel ID;<br /> \n\
params[1]:= 2;<br /> \n\
paramNames[1]:= Nb of samples;</li> \n\
</ul> \n\
<h3 id=\"command-42\">command #42</h3> \n\
<ul> \n\
<li>command name:= add-virtual-signals;<br /> \n\
alias:= avs;<br /> \n\
code:= 1 16;<br /> \n\
description:= Send virtual events to channel;<br /> \n\
nb of param:= 3;<br /> \n\
params[0]:= 2;<br /> \n\
paramNames[0]:= Channel name;<br /> \n\
params[1]:= 2;<br /> \n\
paramNames[1]:= Nb of samples;<br /> \n\
params[2]:= 2;<br /> \n\
paramNames[2]:= value of samples;</li> \n\
</ul> \n\
</body> \n\
</html> \n\
"
