# Invariants

## What are invariants?

P-invariants (or place invariants) of Petri Nets can be used to prove mutual exclusion properties.

For more information, see:
Apvrille, Ludovic and Saqui-Sannes, Pierre de Static
analysis techniques to verify mutual exclusion situations within SysML models. (
In Press: 2013) In: SDL 2013 - 16th International System Design Languages
Forum, 26-28 Jun 2013, Montreal, Canada

You can also access to the paper as follows:
https://oatao.univ-toulouse.fr/8846/1/Saqui-Sannes_8846.pdf


## Invariants in Avatar models
 In the scope of Avatar, P-invariants, or invariants for short, can be used to prove mutual exclusion between states  of state machine diagrams. To compute invariants, TTool first translates an Avatar model into a Petri net. Then, it computes the incidence matrix of that Petri net. Finally, it relies on the Farkas algorithm to compute mutual exclusion between states.


## how to proceed

1. Select states of states machines for which you would like to study mutual exclusion. for this, do a right click on a state and select "Check for mutual exclusion". This stage is optional since in stage 4. you can select to study the mutual exclusion between all states of your state machine diagrams.
2. Check the syntax of the model. We now assume the syntax of the model produces no error.
3. Click on the "Mutual exclusion analysis" icon. A window shall open.
4. Select optins and click on start. Mutual exclusion (as well as other intermediate models) are given in the tabs of the dialog window.


## Backtracing to models
Once computed, mutual exclusions are displayed next to each state of the state machine.


## Limitations
- Combinatory explosion
- Timing operators (after clauses, timers) are ignored



