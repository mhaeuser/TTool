# Reachability graphs

## What is a reachability graph?
A reachability graph (RG) represents all possible execution paths and states of a system. Here, a reachability graph is produced from an Avatar design.
To be generated, the graph generator considers all possible transitions from states of states machine, independently from the probabilities attached to transitions between the states of state machines. Said differently, the graph generator considers all possible transitions between states of states machines. So, even transitions with low probability are in the RG.

## How to generate a reachability graph?
 Reachability Graphs can be created by using the internal model-checker of TTool.
This video on formal verification details the steps to generate a RG: https://www.youtube.com/watch?v=8IYJ1UDUbvQ

## Model-checking: dialog window
Once the dialog window for model-checking has been opened, the following options are available:

### General options
- **Empty transitions** can be ignored, i.e., there are not displayed in the R.G. as internal actions
- The **search type** can be selected between "BFS" and "DFS". BFS means "Breadth-First Search". It consists in exploring concurrent branches. In Depth-First search, each branch is first explored to the end and then concurrent branches are explored. Depending on the system, it can better to use BFS or DFS. It one technique takes for a given pragma, do try the other one.
- The **word size** states the length of an integer. this impacts the memory used to store the states of the R.G.
- The **maximum number of threads** makes it possible to limit the load on a machine making it more responsive during the generation of a R.G. By default, TTool uses the maximum number of concurrent threads allowed by your system architecture.
- **Ignore concurrency between internal actions** refers to the fact that, when two actions a1 and a2 are possible between two blocks, the model-checker tries to execute a1 and then a2, but also a2 then a1. In case this option is selected, only one out of the two possibilities is analyzed by the model-checker.
- **Limit number of states** makes it possible to specify a number of states after which the R.G. generation stops. Beware, in that case, the graph could be incomplete.
- **Time constraint for RG generation** puts a timeout on the graph generation after which the generation is stopped. Beware, in that case, the graph could be incomplete.

### Basic properties
Basic properties refer to deadlocks, reinitialization, loops of internal actions, reachability of states in state machines, and liveness of states in state machines.
- **Deadlocks** refers to the fact that from at least on state of the RG, there is no output transition to another state.
- **Reinitialization** means that from any state of the RG, it is possible to go back to the initial state of the R.
- **Internal action loops** means that there exists at least one loop in the graph with only internal actions.
- **Reachability** studies, if selected, either for selected states of states machines or for all states of states machines, whether there exists at least one path for each of these states from the initial state of the RG.
- **Liveness** means that, for each considered states of state machines, all paths from the initial state must reach each of them.

### Advanced properties
Advanced properties refer to [Safety pragmas](file://avatarsafetypragmas.md). Safety pragmas can be selected for being verified individually.


It is also possible to generate traces proving either that there are satisfied or non satisfied. These traces can be obtained as a graph ("Generate trace (graph format)") or as a text file ("Generate trace (TXT format)") which contains more information than the graph.
Traces are generated in the default directory, and the text area at the bottom of the dialog window informs users about which trace files have been generated.

Traces are generated in the following cases, independently from T/F being at the top of the pragma:
- A[]: if the property is not satisfied
- A<>: if the property is not satisfied
- E[]: if the property is satisfied
- E<>: if the property is satisfied
- "-->": if the property is not satisfied

Once generated, traces in graph format are listed in the left tree, "R. graphs" section.

### Generating the RG

It is possible to select if the RG is generated, or not, where it is generated, and if it is also saved in dotty format. Once generated, RG are available in the left tree, "R. graphs" section.


### Starting the model-checker
Just click on the "start" button. This button is activated only if at least one basic property as been selected, or one pragma has been selected, or the RG generation has been selected.


## Working with RGs

Once an RG has been generated, it is listed in the left tree, section "R. graphs". A right click on a RG (or on a trace) offers several options:
- Getting statistics on the graph
- Displaying the graph
- Minimizing the graph

## Displaying the graph

An RG is displayed with an external library called "GraphStream" provided with TTool. A RG can displayed only if its number of states and transitions is of reasonable size. Usually, more than 500 states or transitions will make the displaying slow and useless.

The displaying of graphs can be customized using a CSS specification added to the configuration file of TTool. Below is provided an example of such a specification. All the CSS directives given in https://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/ can be used in this definition.

    <RGStyleSheet data="
    node {fill-color: #B1CAF1; text-color: black; size: 20px, 20px; text-size:14;}     
    node.deadlock {fill-color: red; text-color: white; size: 20px, 20px; text-size:16;}    
    node.init { fill-color: green; text-color: black; size: 20px, 20px; text-size:16;}
    edge {text-color: black; shape: cubic-curve; text-size:10;}    
    edge.defaultedge {text-size:10; text-color:black;}  
    edge.external {text-color:blue; text-size:14; text-offset: -20, -20; text-alignment: along;}" />

- *node.init* corresponds to the first node of the graph. Here, it is colored in green
- *node.deadlock* corresponds to nodes with no output transitions. They are colored in red.
- *node* corresponds to other nodes. They are colors with RGB color "B1CAF1"
- *edge* defines the characteristics of the normal edges.
- *edge.defaultedge defines the specification of edges with internal actions, i.e. with no communication action
- *edge.external* refers to edges used for communications between blocks.








