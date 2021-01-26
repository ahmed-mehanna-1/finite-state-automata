import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * This class defines a Deterministic Finite Automata
 */
public class DFA {
    private State initState;
    private State[] finalStates;
    private String[] inputs;
    private String[][] trans;

    /**
     * This class defines a State of deterministic finite automata
     */
    final static class State {
        private String name;
        private final Map<String, State> nextState;

        /**
         * Constructing the state
         *
         * @param name  Name of the state
         */
        State(String name) {
            this.name = name;
            nextState = new HashMap<>();
        }

        /**
         * @param input         Input of finite state
         * @param state         Next state for specific input
         * @throws Exception    Throwing exception when something wrong happen
         */
        public void setNextState(String input, State state) throws Exception {
            if (state == null)
                throw new Exception("Invalid Transition Table");
            nextState.put(input, state);
        }

        public State getState(String input) {
            return nextState.get(input);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * This class defines a status of an input for the finite state
     */
    public final static class Status {
        private final String input;
        private final boolean status;
        private final String travel;

        /**
         *
         * @param input     Input string for the finite state
         * @param status    Status of the input (Accepted = true, Unaccepted = false)
         * @param travel    Travel string that describe the traversal in the finite state while testing the input
         */
        Status(String input, boolean status, String travel) {
            this.input = input;
            this.status = status;
            this.travel = travel;
        }

        public String getInput() {
            return input;
        }

        public boolean getStatus() {
            return status;
        }

        public String getTravel() {
            return travel;
        }
    }

    /**
     * Constructs an nondeterministic finite state automata by inputs from user.
     */
    public DFA() throws Exception {
        System.err.println("#1 Finite state inputs separated by 'space'");
        System.err.println("#2 Number of states");
        System.err.println("#3 Each row in transition table in one line separated by 'space' and in same order of inputs line");
        System.err.println("#4 Start state");
        System.err.println("#5 Final states separated by 'space'");
        System.out.println("Start Define The Finite State");
        Scanner scan = new Scanner(System.in);
        String[] inputs = scan.nextLine().split(" ");
        int numberOfStates = 0;
        try {
            numberOfStates = Integer.parseInt(scan.nextLine());
        }
        catch (Exception ignored) {
            System.err.println("Wrong Input");
            System.exit(1);
        }
        String[][] trans = new String[numberOfStates][inputs.length + 1];
        for (int i = 0; i < numberOfStates; i++)
            trans[i] = scan.nextLine().split(" ");
        String initState = scan.nextLine();
        String[] finalStates = scan.nextLine().split(" ");
        init(inputs, initState, finalStates, trans);
    }

    /**
     * Constructs a finite automata from information stored in a file.
     *
     * @param path of file to be read from
     */
    public DFA(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String[] inputs = br.readLine().split("\\s");
        int numberOfStates = 0;
        try {
            numberOfStates = Integer.parseInt(br.readLine());
        }
        catch (Exception ignored) {
            System.out.println("Something Wrong With Number of States");
            System.exit(1);
        }
        String[][] trans = new String[numberOfStates][inputs.length + 1];
        for (int i = 0; i < numberOfStates; i++)
            trans[i] = br.readLine().split("\\s");
        String initState = br.readLine();
        String[] finalStates = br.readLine().split("\\s");
        init(inputs, initState, finalStates, trans);
    }

    /**
     * Constructs a finite state automata with given arguments.
     *
     * @param inputs        inputs for finite state automata
     * @param initState     initial or start state
     * @param finalStates   list of final states
     * @param trans         2D array describe the transition table
     */
    public DFA(String[] inputs, String initState, String[] finalStates, String[][] trans) throws Exception {
        init(inputs, initState, finalStates, trans);
    }

    /**
     * Initialize the finite state
     *
     * @param inputs        inputs for finite state automata
     * @param initState     initial or start state
     * @param finalStates   list of final states
     * @param trans         2D array describe the transition table
     * @throws Exception    Handling exceptions while constructing the NFA
     */
    private void init(String[] inputs, String initState, String[] finalStates, String[][] trans) throws Exception {
        System.out.println("Inputs: " + Arrays.toString(inputs));
        System.out.println("Initial State: " + initState);
        System.out.println("Final States: " + Arrays.toString(finalStates));
        System.out.println("DFA Transition Table: ");
        for (String[] row: trans) {
            for (String state : row)
                System.out.print(state + "\t\t");
            System.out.println("\n_______________________");
        }
        System.out.println();
        this.inputs = inputs;
        this.trans = trans;
        Map<String, State> states = new HashMap<>();
        for (String[] tran : trans)
            states.put(tran[0], new State(tran[0]));
        this.finalStates = new State[finalStates.length];
        for (String[] tran : trans)
            for (int j = 1; j < trans[0].length; j++)
                states.get(tran[0]).setNextState(inputs[j - 1], states.get(tran[j]));
        this.initState = states.get(initState);
        for (int i = 0; i < finalStates.length; i++)
            this.finalStates[i] = states.get(finalStates[i]);
    }

    /**
     *  Start testing the input string on the finite state
     *
     * @param input         Input string for the finite state
     * @return              Status object that held data about the input string
     * @throws Exception    Throwing exception when something wrong happen
     */
    public Status start(String input) throws Exception {
        State currentState = initState;
        StringBuilder travel = new StringBuilder(currentState.getName());
        for (Character in: input.toCharArray()) {
            currentState = currentState.getState(String.valueOf(in));
            if (currentState == null)
                throw new Exception("You Have Entered Invalid Input");
            travel.append(" (").append(in).append(") ").append(currentState.getName());
        }
        for (State finalState: finalStates)
            if (finalState == currentState)
                return new Status(input, true, travel.toString());
        return new Status(input, false, travel.toString());
    }

    /**
     * Start testing more than one input string on the finite state
     *
     * @param inputs        List of input strings for the finite state
     * @return              List of Status objects, one for every input string in the input list
     * @throws Exception    Throwing exception when something wrong happen
     */
    public Status[] start(String[] inputs) throws Exception {
        Status[] inputsStatus = new Status[inputs.length];
        for (int i = 0; i < inputs.length; i++)
            inputsStatus[i] = start(inputs[i]);
        return inputsStatus;
    }

    /**
     *
     * @return  List of final states
     */
    public String[] getFinalStates() {
        List<String> finalStates = new ArrayList<>();
        for (State state: this.finalStates)
            finalStates.add(state.name);
        return finalStates.toArray(new String[0]);
    }

    public String[] getInputs() {
        return inputs;
    }

    public String[][] getTrans() {
        return trans;
    }

    public String getInitState() {
        return initState.name;
    }

}