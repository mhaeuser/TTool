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
    .display.math{display: block; text-align: center; margin: 0.5rem auto;} \n\
  </style> \n\
  <link rel=\"stylesheet\" href=\"/Users/ludovicapvrille/TTool/src/main/resources/help/help.css\" /> \n\
  <!--[if lt IE 9]> \n\
    <script src=\"//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv-printshiv.min.js\"></script> \n\
  <![endif]--> \n\
</head> \n\
<body> \n\
<h1 id=\"diplodocus-simulator\">Diplodocus Simulator</h1> \n\
<h2 id=\"objective\">Objective</h2> \n\
<p>The simulator of DIPLODOCUS intends to simulate a DIPLODOCUS mapping, taking into account functional tasks, the system architectures (CPU, buses, etc.) and the allocations of tasks and their communucations to the system architecture</p> \n\
<h2 id=\"generating-simulation-code-and-commiling-it\">Generating simulation code and commiling it?</h2> \n\
<p>The first step is to create a DIPLODOCUS mapping. Once the mapping model has been checked against syntax errors <img src=\"file:../ui/util/checkmodel.gif\" alt=\"syntax checking icon\" />, it is possible to generate a C++ code <img src=\"file:../ui/util/gensystc.gif\" alt=\"simulation code generation icon\" /> that represents the mapping model. If you are using a model in TTool, then the code is generated by default in TTool/simulators/c++2 for models. If your model has been made in a project, then the code is generated into the “c++_code” subdirectory of your project.</p> \n\
<p>The second step is to compile the code. You can directly do it from TTool with the code generation window, second tab. Another option is to open a terminal, and to enter the following command:</p> \n\
<pre><code>$ make</code></pre> \n\
<h2 id=\"using-the-simulator-from-ttool\">Using the simulator from TTool</h2> \n\
<p>The third tab of the simulation code generation window provides several options to start the simulator, e.g. either running simulation until completion or running the server in interactive mode. For the latter, the simulator is started in server mode, and TTool connects via sockets to the server in order to remotely drive the simulation.</p> \n\
<h2 id=\"command-line-use-of-the-simulation\">Command-line use of the simulation</h2> \n\
<p>Once the simulator has been compiled, do as follows to list possible options:</p> \n\
<pre><code>$ ./run.x -help</code></pre> \n\
<p>Basically, options are used to express the working mode (<code>-server</code>for interactive mode, <code>-explo</code>to generate a reachability graphs, …), to set the output (e.g., <code>-gpath</code>or finally to set commands to be executed, either from the command line (<code>-cmd</code>) or from a file (<code>-file</code>). For instance:</p> \n\
<pre><code>$ ./run.x -cmd &#39;1 6 100 ; 3 Task1 ; 1 0 ; 3 Task1&#39; \n\
means &#39;run-to-time 100 ; get-variable-of-task Task1 ; run-to-next-breakpoint ; get-variable-of-task Task1&#39; \n\
(Using the corresponding code instead of the command name when running in command line mode)</code></pre> \n\
<h3 id=\"simulator-commands\">Simulator commands</h3> \n\
<p>The parameters types are defined as follow:</p> \n\
<pre><code>Type = 0: &quot;&quot;; \n\
Type = 1: &lt;Int&gt;; \n\
Type = 2: &lt;String&gt;; \n\
Type = 3: [Int]; \n\
Type = 4: [String]; \n\
Type = 6: [int between 0 and 100 (percentage); \n\
Not defined: &lt;unknow param&gt;</code></pre> \n\
<table style=\"width:100%;\"> \n\
<colgroup> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
<col style=\"width: 11%\" /> \n\
</colgroup> \n\
<thead> \n\
<tr class=\"header\"> \n\
<th style=\"text-align: center;\">Command name</th> \n\
<th style=\"text-align: center;\">Alias</th> \n\
<th style=\"text-align: center;\">Code</th> \n\
<th style=\"text-align: center;\">Description</th> \n\
<th style=\"text-align: center;\">Parameter 1</th> \n\
<th style=\"text-align: center;\">Parameter 2</th> \n\
<th style=\"text-align: center;\">Parameter 3</th> \n\
<th style=\"text-align: center;\">Parameter 4</th> \n\
<th style=\"text-align: center;\">Parameter 5</th> \n\
</tr> \n\
</thead> \n\
<tbody> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">active-breakpoints</td> \n\
<td style=\"text-align: center;\">ab</td> \n\
<td style=\"text-align: center;\">20</td> \n\
<td style=\"text-align: center;\">Active / unactive breakpoints</td> \n\
<td style=\"text-align: center;\">[Type: 1] 0/1 (unactive / active)</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">add-breakpoint</td> \n\
<td style=\"text-align: center;\">abp</td> \n\
<td style=\"text-align: center;\">11</td> \n\
<td style=\"text-align: center;\">Set a breakpoint in task which id is the first parameter on the command provided as the second parameter</td> \n\
<td style=\"text-align: center;\">[Type: 1] task ID</td> \n\
<td style=\"text-align: center;\">[Type: 0] comamnd ID</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">calculate-latencies</td> \n\
<td style=\"text-align: center;\">cl</td> \n\
<td style=\"text-align: center;\">23</td> \n\
<td style=\"text-align: center;\">Calculate latencies between checkpoints</td> \n\
<td style=\"text-align: center;\">[Type: 1] Checkpoint 1 id</td> \n\
<td style=\"text-align: center;\">[Type: 1] Checkpoint2 id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">choose-branch</td> \n\
<td style=\"text-align: center;\">cb</td> \n\
<td style=\"text-align: center;\">12</td> \n\
<td style=\"text-align: center;\">Chooses the branch of the given command of a task</td> \n\
<td style=\"text-align: center;\">[Type: 1] task ID</td> \n\
<td style=\"text-align: center;\">[Type: 0] command ID</td> \n\
<td style=\"text-align: center;\">[Type: 0] branch ID</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">get-breakpoint-list</td> \n\
<td style=\"text-align: center;\">gbl</td> \n\
<td style=\"text-align: center;\">18</td> \n\
<td style=\"text-align: center;\">Returns the list of breakpoints currently set</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">get-command-of-task</td> \n\
<td style=\"text-align: center;\">gcot</td> \n\
<td style=\"text-align: center;\">14</td> \n\
<td style=\"text-align: center;\">Returns the current command of the task provided as argument</td> \n\
<td style=\"text-align: center;\">[Type: 0] Task id (or “all”)</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">get-benchmark</td> \n\
<td style=\"text-align: center;\">gb</td> \n\
<td style=\"text-align: center;\">10</td> \n\
<td style=\"text-align: center;\">Returns information on hardware nodes of the architecture</td> \n\
<td style=\"text-align: center;\">[Type: 1] 0: show benchmark, 1:save in file</td> \n\
<td style=\"text-align: center;\">[Type: 0] Name of file</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">get-executed-operators</td> \n\
<td style=\"text-align: center;\">geo</td> \n\
<td style=\"text-align: center;\">21</td> \n\
<td style=\"text-align: center;\">Returns the list of executed operators</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">get-hashcode</td> \n\
<td style=\"text-align: center;\">gh</td> \n\
<td style=\"text-align: center;\">19</td> \n\
<td style=\"text-align: center;\">Returns the hashcode of the tmap under simulation</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">get-info-on-hw</td> \n\
<td style=\"text-align: center;\">gioh</td> \n\
<td style=\"text-align: center;\">4</td> \n\
<td style=\"text-align: center;\">Returns information on hardware nodes of the architecture</td> \n\
<td style=\"text-align: center;\">[Type: 1] 0: CPU, 1:Bus, 2: Mem, 3: Bridge, 4: Channel, 5: Task</td> \n\
<td style=\"text-align: center;\">[Type: 1] id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">get-numer-of-branches</td> \n\
<td style=\"text-align: center;\">gnob</td> \n\
<td style=\"text-align: center;\">17</td> \n\
<td style=\"text-align: center;\">Returns the number of branches the current command has</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">get-simulation-time</td> \n\
<td style=\"text-align: center;\">time</td> \n\
<td style=\"text-align: center;\">13</td> \n\
<td style=\"text-align: center;\">Returns the current absolute time unit of the simulation</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">get-variable-of-task</td> \n\
<td style=\"text-align: center;\">gvof</td> \n\
<td style=\"text-align: center;\">3</td> \n\
<td style=\"text-align: center;\">Returns the value of a variable in a task</td> \n\
<td style=\"text-align: center;\">[Type: 0] Task id</td> \n\
<td style=\"text-align: center;\">[Type: 0] Variable id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">kill</td> \n\
<td style=\"text-align: center;\">kill</td> \n\
<td style=\"text-align: center;\">0</td> \n\
<td style=\"text-align: center;\">Terminates the remote simulator</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">list-transactions</td> \n\
<td style=\"text-align: center;\">lt</td> \n\
<td style=\"text-align: center;\">22</td> \n\
<td style=\"text-align: center;\">Get the most recent transactions</td> \n\
<td style=\"text-align: center;\">[Type: 2] Max. nb of transactions</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">list-all-transactions-of-a-task</td> \n\
<td style=\"text-align: center;\">lat</td> \n\
<td style=\"text-align: center;\">25</td> \n\
<td style=\"text-align: center;\">Get all transactions of Task</td> \n\
<td style=\"text-align: center;\">[Type: 2] Task Name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">remove-all-trans</td> \n\
<td style=\"text-align: center;\">rmat</td> \n\
<td style=\"text-align: center;\">26</td> \n\
<td style=\"text-align: center;\">Remove all the transactions in the past</td> \n\
<td style=\"text-align: center;\">[Type: 1] Yes : 1, No : 0</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">rm-breakpoint</td> \n\
<td style=\"text-align: center;\">rmb</td> \n\
<td style=\"text-align: center;\">16</td> \n\
<td style=\"text-align: center;\">Remove a breakpoint in task which id is the first parameter on the command provided as the second parameter</td> \n\
<td style=\"text-align: center;\">[Type: 1] task ID</td> \n\
<td style=\"text-align: center;\">[Type: 0] comamnd ID</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">reset</td> \n\
<td style=\"text-align: center;\">reset</td> \n\
<td style=\"text-align: center;\">2</td> \n\
<td style=\"text-align: center;\">Resets the remote simulator</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">raw-command</td> \n\
<td style=\"text-align: center;\">rc</td> \n\
<td style=\"text-align: center;\"></td> \n\
<td style=\"text-align: center;\">Sends a raw command to the remote simulator</td> \n\
<td style=\"text-align: center;\">[Type: 4] param #0</td> \n\
<td style=\"text-align: center;\">[Type: 4] param #1</td> \n\
<td style=\"text-align: center;\">[Type: 4] param #2</td> \n\
<td style=\"text-align: center;\">[Type: 4] param #3</td> \n\
<td style=\"text-align: center;\">[Type: 4] param #4</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">restore-simulation-state-from-file</td> \n\
<td style=\"text-align: center;\">rssff</td> \n\
<td style=\"text-align: center;\">9</td> \n\
<td style=\"text-align: center;\">Restores the simulation state from a file</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-exploration</td> \n\
<td style=\"text-align: center;\">re</td> \n\
<td style=\"text-align: center;\">1 7</td> \n\
<td style=\"text-align: center;\">Runs the simulation in exploration mode</td> \n\
<td style=\"text-align: center;\">[Type: 6] Minimum number of explored commands</td> \n\
<td style=\"text-align: center;\">[Type: 6] Minimum number of explored branches</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name of the resulting graph, with NO extension</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-to-next-breakpoint</td> \n\
<td style=\"text-align: center;\">rtnb</td> \n\
<td style=\"text-align: center;\">1 0</td> \n\
<td style=\"text-align: center;\">Runs the simulation until a breakpoint is met</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-to-next-breakpoint-max-trans</td> \n\
<td style=\"text-align: center;\">rtnbmt</td> \n\
<td style=\"text-align: center;\">1 19</td> \n\
<td style=\"text-align: center;\">Runs the simulation until a breakpoint is met or max number of transactions are executed</td> \n\
<td style=\"text-align: center;\">[Type: 1] max nb of transactions</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-to-next-transfer-on-bus</td> \n\
<td style=\"text-align: center;\">rtntob</td> \n\
<td style=\"text-align: center;\">1 8</td> \n\
<td style=\"text-align: center;\">Runs to the next transfer on bus which id is provided as argument</td> \n\
<td style=\"text-align: center;\">[Type: 1] bus id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-to-time</td> \n\
<td style=\"text-align: center;\">rtt</td> \n\
<td style=\"text-align: center;\">1 5</td> \n\
<td style=\"text-align: center;\">Runs the simulation until time x is reached</td> \n\
<td style=\"text-align: center;\">[Type: 1] x: time value</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-until-channel-access</td> \n\
<td style=\"text-align: center;\">ruca</td> \n\
<td style=\"text-align: center;\">1 12</td> \n\
<td style=\"text-align: center;\">Run simulation until a operation is performed on the channel which ID is provided as parameter</td> \n\
<td style=\"text-align: center;\">[Type: 1] Channel id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-until-write-on-channel-access</td> \n\
<td style=\"text-align: center;\">ruwca</td> \n\
<td style=\"text-align: center;\">1 17</td> \n\
<td style=\"text-align: center;\">Run simulation until a write operation is performed on the channel which channel name is provided as parameter</td> \n\
<td style=\"text-align: center;\">[Type: 2] Channel name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-until-read-on-channel-access</td> \n\
<td style=\"text-align: center;\">rurca</td> \n\
<td style=\"text-align: center;\">1 18</td> \n\
<td style=\"text-align: center;\">Run simulation until a read operation is performed on the channel which chanel name is provided as parameter</td> \n\
<td style=\"text-align: center;\">[Type: 2] Channel name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-until-cpu-executes</td> \n\
<td style=\"text-align: center;\">ruce</td> \n\
<td style=\"text-align: center;\">1 9</td> \n\
<td style=\"text-align: center;\">Run simulation until CPU which ID is provided as parameter executes</td> \n\
<td style=\"text-align: center;\">[Type: 1] CPU id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-until-memory-access</td> \n\
<td style=\"text-align: center;\">ruma</td> \n\
<td style=\"text-align: center;\">1 11</td> \n\
<td style=\"text-align: center;\">Run simulation until the memory which ID is provided as parameter is accessed</td> \n\
<td style=\"text-align: center;\">[Type: 1] Memory id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-until-task-executes</td> \n\
<td style=\"text-align: center;\">rute</td> \n\
<td style=\"text-align: center;\">1 10</td> \n\
<td style=\"text-align: center;\">Run simulation until the task which ID is provided as parameter executes</td> \n\
<td style=\"text-align: center;\">[Type: 1] Task id</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-x-commands</td> \n\
<td style=\"text-align: center;\">rxcomm</td> \n\
<td style=\"text-align: center;\">1 4</td> \n\
<td style=\"text-align: center;\">Runs the simulation for x commands</td> \n\
<td style=\"text-align: center;\">[Type: 1] nb of commands</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">run-x-time-units</td> \n\
<td style=\"text-align: center;\">rxtu</td> \n\
<td style=\"text-align: center;\">1 6</td> \n\
<td style=\"text-align: center;\">Runs the simulation for x units of time</td> \n\
<td style=\"text-align: center;\">[Type: 1] nb of time units</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">run-x-transactions</td> \n\
<td style=\"text-align: center;\">rxtr</td> \n\
<td style=\"text-align: center;\">1 2</td> \n\
<td style=\"text-align: center;\">Runs the simulation for x transactions</td> \n\
<td style=\"text-align: center;\">[Type: 1] nb of transactions</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">save-simulation-state-in-file</td> \n\
<td style=\"text-align: center;\">sssif</td> \n\
<td style=\"text-align: center;\">8</td> \n\
<td style=\"text-align: center;\">Saves the current simulation state into a file</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">save-trace-in-file</td> \n\
<td style=\"text-align: center;\">stif</td> \n\
<td style=\"text-align: center;\">7</td> \n\
<td style=\"text-align: center;\">Saves the current trace of the simulation in a VCD, HTML, TXT or XML file</td> \n\
<td style=\"text-align: center;\">[Type: 1] File format: 0-&gt; VCD, 1-&gt;HTML, 2-&gt;TXT, 3-&gt;XML</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">show-timeline-trace</td> \n\
<td style=\"text-align: center;\">stlt</td> \n\
<td style=\"text-align: center;\">7 4</td> \n\
<td style=\"text-align: center;\">Show the current timeline diagram trace in HTML format</td> \n\
<td style=\"text-align: center;\">[Type: 2] Task List</td> \n\
<td style=\"text-align: center;\">[Type: 1] Scale idle time: 0 -&gt; no, 1 -&gt; yes</td> \n\
<td style=\"text-align: center;\">[Type: 2] Start Time</td> \n\
<td style=\"text-align: center;\">[Type: 2] End Time</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">set-variable</td> \n\
<td style=\"text-align: center;\">sv</td> \n\
<td style=\"text-align: center;\">5</td> \n\
<td style=\"text-align: center;\">Set the value of a variable</td> \n\
<td style=\"text-align: center;\">[Type: 1] task ID</td> \n\
<td style=\"text-align: center;\">[Type: 1] variable ID</td> \n\
<td style=\"text-align: center;\">[Type: 1] variable value</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">stop</td> \n\
<td style=\"text-align: center;\">stop</td> \n\
<td style=\"text-align: center;\">15</td> \n\
<td style=\"text-align: center;\">Stops the currently running simulation</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">write-in-channel</td> \n\
<td style=\"text-align: center;\">wic</td> \n\
<td style=\"text-align: center;\">6</td> \n\
<td style=\"text-align: center;\">Writes y samples / events to channel / event x</td> \n\
<td style=\"text-align: center;\">[Type: 1] Channel ID</td> \n\
<td style=\"text-align: center;\">[Type: 2] Nb of samples</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">add-virtual-signals</td> \n\
<td style=\"text-align: center;\">avs</td> \n\
<td style=\"text-align: center;\">1 16</td> \n\
<td style=\"text-align: center;\">Send virtual events to channel</td> \n\
<td style=\"text-align: center;\">[Type: 2] Channel name</td> \n\
<td style=\"text-align: center;\">[Type: 2] Nb of samples</td> \n\
<td style=\"text-align: center;\">[Type: 2] value of samples</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"odd\"> \n\
<td style=\"text-align: center;\">save-status-in-file</td> \n\
<td style=\"text-align: center;\">ssif</td> \n\
<td style=\"text-align: center;\">27</td> \n\
<td style=\"text-align: center;\">Saves the current status into a file</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
<tr class=\"even\"> \n\
<td style=\"text-align: center;\">save-time-in-file</td> \n\
<td style=\"text-align: center;\">stf</td> \n\
<td style=\"text-align: center;\">28</td> \n\
<td style=\"text-align: center;\">Saves the current time into a file</td> \n\
<td style=\"text-align: center;\">[Type: 2] File name</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
<td style=\"text-align: center;\">-</td> \n\
</tr> \n\
</tbody> \n\
</table> \n\
</body> \n\
</html> \n\
"
