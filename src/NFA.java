import jdk.jshell.execution.Util;

import javax.print.attribute.standard.NumberUp;
import javax.print.attribute.standard.NumberUpSupported;
import javax.swing.text.Utilities;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.ToIntFunction;

/**
 * This class defines a Nondeterministic Finite Automata
 */
public class NFA {
    private String[] inputs;
    private String initState;
    private String[] finalStates;
    private String[][] trans;
    private Map<String, State> nfaMap;

    /**
     * This class defines a State of nondeterministic finite automata
     */
    private final static class State {
        private final String name;
        private final Map<String, ArrayList<State>> nextState;
        private final List<State> closer;

        /**
         * Constructing the state
         *
         * @param name  Name of the state
         */
        State(String name) {
            this.name = name;
            nextState = new HashMap<>();
            closer = new ArrayList<>();
            closer.add(this);
        }

        /**
         * @param input         Input of finite state
         * @param state         Next state for specific input
         * @throws Exception    Throwing exception when something wrong happen
         */
        public void setNextState(String input, State state) throws Exception {
            if (state == null)
                throw new Exception("Invalid Transition Table");
            if (!nextState.containsKey(input))
                nextState.put(input, new ArrayList<>());
            nextState.get(input).add(state);
            nextState.get(input).sort(new Comparator<State>() {
                @Override
                public int compare(State o1, State o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        /**
         * @param state State will be considered as one of the closer
         */
        public void addCloser(State state) {
            closer.add(state);
        }

        /**
         * @param input Specific input of the finite state
         * @return      String that held the next state for that input
         */
        public String getNextState(String input) {
            List<State> nextStates = nextState.get(input);
            if (nextStates == null)
                return "Φ";
            StringBuilder stateName = new StringBuilder();
            for (State state: nextStates)
                stateName.append(state.getCloser()).append(",");
            List<String> states = Arrays.asList(stateName.toString().split(","));
            stateName = new StringBuilder();
            for (String state: states)
                if (stateName.indexOf(state + ",") == -1 && stateName.indexOf("," + state + ",") == -1)
                    stateName.append(state).append(",");
            stateName.deleteCharAt(stateName.lastIndexOf(","));
            return stateName.toString();
        }

        /**
         * Complete creating the closer of the state
         */
        public void finishCloser() {
            for (int i = 0; i < closer.size(); i++)
                for (State state: closer.get(i).closer)
                    if (!closer.contains(state))
                        closer.add(state);
            closer.sort((o1, o2) -> {
                if (!Utils.isDigit(o1.getName()) || !Utils.isDigit(o2.getName())) {
                    if (o1.getName().length() < o2.getName().length())
                        return -1;
                    else if (o2.getName().length() < o1.getName().length())
                        return 1;
                    for (int i = 0; i < o1.getName().length(); i++)
                        if ((int)o1.getName().charAt(i) < (int)o2.getName().charAt(i))
                            return -1;
                        else if ((int)o1.getName().charAt(i) > (int)o2.getName().charAt(i))
                            return 1;
                    return 0;
                }
                int x = Integer.parseInt(o1.getName());
                int y = Integer.parseInt(o2.getName());
                return x - y;
            });
        }

        /**
         * @return String that held the closer of the state
         */
        public String getCloser() {
            if (closer.size() == 1)
                return name;
            StringBuilder closerName = new StringBuilder();
            for (State state : closer)
                closerName.append(state.getName()).append(",");
            closerName.deleteCharAt(closerName.lastIndexOf(","));
            return closerName.toString();
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Constructs an nondeterministic finite state automata by inputs from user.
     */
    public NFA() throws Exception {
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
    public NFA(String path) throws Exception {
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
    public NFA(String[] inputs, String initState, String[] finalStates, String[][] trans) throws Exception {
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
    private void init (String[] inputs, String initState, String[] finalStates, String[][] trans) throws Exception {
        System.out.println("Inputs: " + Arrays.toString(inputs));
        System.out.println("Initial State: " + initState);
        System.out.println("Final States: " + Arrays.toString(finalStates));
        System.out.println("NFA Transition Table: ");
        for (String[] row: trans) {
            for (String state : row)
                System.out.print(state + "\t\t");
            System.out.println("\n_______________________");
        }
        System.out.println();
        this.inputs = inputs;
        this.initState = initState;
        this.finalStates = finalStates;
        this.trans = trans;
        nfaMap = new HashMap<>();

        for (String[] row: trans)
            nfaMap.put(row[0], new State(row[0]));
        for (String[] row: trans) {
            for (int i = 1; i < inputs.length + 1; i++) {
                if (inputs[i - 1].equals("Ɛ") && !row[i].equals("Φ")) {
                    String[] states = row[i].split(",");
                    for (String state : states)
                        nfaMap.get(row[0]).addCloser(nfaMap.get(state));
                }
                if (!row[i].equals("Φ")) {
                    String[] states = row[i].split(",");
                    for (String state : states)
                        nfaMap.get(row[0]).setNextState(inputs[i - 1], nfaMap.get(state));
                }
            }
        }
        Set<String> keys = nfaMap.keySet();
        for (String key: keys)
            nfaMap.get(key).finishCloser();
    }

    /**
     * Building the Map that will describe the transition table of deterministic finite state automata
     *
     * @param nfaMap        Map that describe the NFA
     * @param dfaMap        Map that describe the DFA
     * @param currentState  The current state will determine its next states in DFA
     * @throws Exception    Handling exceptions while constructing the DFA
     */
    private void buildDFAMap(Map<String, State> nfaMap, Map<String, DFA.State> dfaMap, String currentState) throws Exception {
        if (currentState.equals("Φ"))
            for (String input: inputs)
                dfaMap.get(currentState).setNextState(input, dfaMap.get(currentState));
        else
            for (String input: inputs) {
                if (input.equals("Ɛ"))
                    continue;
                State state = nfaMap.get(currentState);
                StringBuilder stateName = new StringBuilder();
                if (state != null)
                    stateName.append(state.getNextState(input));
                else {
                    String[] states = currentState.split(",");
                    List<String> sName = new ArrayList<>();
                    for (String s: states) {
                        s = nfaMap.get(s).getNextState(input);
                        if (!s.equals("Φ"))
                            for (String x: s.split(","))
                                if (!sName.contains(x))
                                    sName.add(x);
                    }
                    if (sName.size() == 0)
                        stateName.append("Φ");
                    else {
                        sName.sort((o1, o2) -> {
                            if (!Utils.isDigit(o1) || !Utils.isDigit(o2)) {
                                if (o1.length() < o2.length())
                                    return -1;
                                else if (o2.length() < o1.length())
                                    return 1;
                                for (int i = 0; i < o1.length(); i++)
                                    if ((int)o1.charAt(i) < (int)o2.charAt(i))
                                        return -1;
                                    else if ((int)o1.charAt(i) > (int)o2.charAt(i))
                                        return 1;
                                return 0;
                            }
                            int x = Integer.parseInt(o1);
                            int y = Integer.parseInt(o2);
                            return x - y;
                        });
                        for (String s: sName)
                            stateName.append(s).append(",");
                        stateName.deleteCharAt(stateName.lastIndexOf(","));
                    }
                }
                if (!dfaMap.containsKey(stateName.toString())) {
                    dfaMap.put(stateName.toString(), new DFA.State(stateName.toString()));
                    buildDFAMap(nfaMap, dfaMap, stateName.toString());
                }
                dfaMap.get(currentState).setNextState(input, dfaMap.get(stateName.toString()));
            }
    }

    /**
     * Create the Map of deterministic finite state automata
     *
     * @return              Map that describe the DFA
     * @throws Exception    Handling exceptions while constructing DFA
     */
    private Map<String, DFA.State> createDFAMap() throws Exception {
        Map<String, DFA.State> dfaMap = new HashMap<>();
        System.out.println(nfaMap.get(initState).getCloser());
        dfaMap.put(nfaMap.get(initState).getCloser(), new DFA.State(nfaMap.get(initState).getCloser()));
        buildDFAMap(nfaMap, dfaMap, nfaMap.get(initState).getCloser());
        return dfaMap;
    }

    /**
     *  Convert Nondeterministic Finite State Automata to Deterministic Finite State Automata
     *
     * @return              Returning DFA object that allow us to traverse and testing strings with this DFA
     * @throws Exception    Handling exceptions while constructing DFA
     */
    public DFA toDFA() throws Exception {
        Map<String, DFA.State> dfaMap = createDFAMap();

        String[][] trans;
        String[] inputs;
        List<String> finalStates = new ArrayList<>();
        List<String> nfaFinalStates = Arrays.asList(this.finalStates);
        Set<String> keys = dfaMap.keySet();
        if (Arrays.asList(this.inputs).contains("Ɛ")) {
            trans = new String[keys.size()][this.inputs.length];
            inputs = new String[this.inputs.length - 1];
        }
        else {
            trans = new String[keys.size()][this.inputs.length + 1];
            inputs = new String[this.inputs.length];
        }

        int i = 0, j = 1;
        Iterator<String> currentKey = keys.stream().sorted(String::compareTo).iterator();
        while (currentKey.hasNext()) {
            String key = currentKey.next();
            trans[i][0] = dfaMap.get(key).getName();
            for (String input: this.inputs)
                if (!input.equals("Ɛ"))
                    trans[i][j++] = dfaMap.get(key).getState(input).getName();
            for (String state: trans[i][0].split(","))
                if (nfaFinalStates.contains(state)) {
                    finalStates.add(trans[i][0]);
                    break;
                }
            i++;
            j = 1;
        }
        j = 0;
        for (i = 0; i < this.inputs.length; i++)
            if (!this.inputs[i].equals("Ɛ"))
                inputs[j++] = this.inputs[i];
        return new DFA(inputs, nfaMap.get(this.initState).getCloser(), finalStates.toArray(new String[0]), trans);
    }
}
