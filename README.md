# Finite State Automata

Creation of DFA | Testing input strings on DFA | Creation of NFA | Conversion from NFA to DFA

## Detrministic Finite Automata

Creation of DFA | Testing input strings on DFA

### Explanation

Creating DFA with any number of states and inputs and both can be numbers, alphabet or symbols. Constructing of the DFA have done with HashMap.

### Algorithm for constructing the DFA

Inputs for the algorithm are:

* List of inputs or symbols
* Number of states (if the user will construct this DFA in the runtime)
* Initial state
* List of final states
* Transition table (2D array)

Transition Table:

State | Input[0] | Input[1]
------------ | ------------- | -------------
q0 | q2 | q1
q1 | q3 | q0
q2 | q0 | q3
q3 | q1 | q2

The transition table does not has the headr of the transition table which include the input symbols because we have a List of inputs as input for the algorithm. </br>
So how the code work?

1. Create a Map of States which has String as a key and State object as a value.
2. Traverse in the transition table with first column only to creat all of the State objects that we need to construct the DFA such as q0, q1, q2, and q3 in the above example.
3. We need an another Map for each state to maping the inputs for each state which has a String as a key and a State object as a value.
4. Traverse in the transition table then get every state in the first column and modify its Map to include a pointr (reference) for the next state.
5. Create a intial state object and make it pointing for the initial state in the DFA.
6. Create a List of State these states are the final state.
7. To test an input string in this DFA we have to traverse in the map of the current state by changing the current state with the pointer of the next state from the map included in the state.
8. After complete the traversing in the map check if the current state is one of the final state or not then you can detect if this input string is accepted or not.

## Nondeterministic Finite Automata

Creation of NFA | Converting NFA to DFA

### Explanation

Creating NFA with same technique in DFA and convert it to equivilint DFA object to be able to test input strings with that DFA.
Why I had create the NFA using same algorithm of DFA? to improve and add new things to the code in the feture.

### Algorithm for converting NFA to DFA

The inputs for code are nothing because the algorithm working on the NFA map.

how the converting done?

1. Expand DFA State to include an ArrayList of NFA States this array will store the closer state.
2. Change the value of DFA State Map to ArrayList of NFA States because in the NFA the one input able to have more than one next state so we have ArrayList of NFA State which held next states.
3. Create DFA Map and initialize it with closer of the initial state of NFA.
4. Create a pointer to the current state in DFA Map which is the state that we want to determine its next state for every input of the Finite State.
5. Check if the name of the current state included in the NFA Map or not
6. If NFA Map does not has a state with current state name then we have to make a combination between more than one state of the NFA to get the next state for the input, this combination is s1 closer for that input concatenation with s2 closer and so on for each state, then go to step 8.
7. If NFA Map has a state with current state name then take its next state closer to complete next step.
8. Map the closer of the next state to its input in the current state Map iff the DFA Map has a state name simillar to the closer of the next state, then reapet from step 5 for each input.
9. If the DFA Map does not has the state name create a new DFA State and map it in the DFA Map then change the current state to be this new state then repeat from step 5.
10. Afret completing all of the above steps correctly we will get a complete DFA Map like in the constructing the DFA algorithm.
11. Convert this DFA Map to an 2D array to be a transition table of DFA.
12. Set DFA initial state to be the closer of NFA initial state.
13. Create a List that held all of the final states which they are any state containing a one of the NFA final state.
14. Create a List of inputs that is simillar to NFA inputs except the Epsilon.
15. Create a DFA object using the DFA List of inputs, initial state, List of final states and transition table.

Now we have a NFA object and a DFA object came from converting the NFA to DFA.
