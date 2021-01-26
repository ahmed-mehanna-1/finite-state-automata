public class Main {
    /**
     * Testing DFA class
     */
    public static void testDFA() {
        String[] inputs = {"a", "b", "c"};
        String initState = "1";
        String[] finalStates = {"2", "4"};
        String[][] trans = {
                {"1", "2", "3", "1"},
                {"2", "1", "3", "4"},
                {"3", "2", "4", "1"},
                {"4", "3", "3", "2"}
        };

        DFA fs = null;
        try {
            fs = new DFA(inputs, initState, finalStates, trans);
//            fs = new DFA();
//            fs = new DFA("write file path");
        } catch (Exception ignored) {
            System.err.println("You Have An Error In Transition Table");
            System.exit(0);
        }

        try {
//            DFA.Status status = fs.start("baabbbaa");
//            System.out.println(status.getInput() + "  " + status.getTravel() + "  " + status.getStatus() + "\n");
//
            String[] finiteStateInputs = {"abc", "bcacba", "ccca", "cbabc", "b", "bbbb"};
            DFA.Status[] finiteStateInputsStatus = fs.start(finiteStateInputs);
            for (DFA.Status inputStatus : finiteStateInputsStatus)
                System.out.println(inputStatus.getInput() + "  " + inputStatus.getTravel() + "  " + inputStatus.getStatus());
        } catch (Exception ignored) {
            System.err.println("You Have Entered Invalid Input To Finite State");
        }
    }

    /**
     * Testing convert NFA to DFA
     */
    public static void testNFA() throws Exception {
        /*
         Epsilon  = Ɛ
         Phi  = Φ
         Separator between multiple next state = ,
         */

        // Test case1
//        String[] inputs = {"a", "b", "Ɛ"};
//        String initState = "1";
//        String[] finalStates = {"2"};
//        String[][] trans = {
//                {"1", "3", "Φ", "2"},
//                {"2", "1", "Φ", "Φ"},
//                {"3", "2", "2,3", "Φ"}
//        };

        // Test case2
//        String[] inputs = {"a", "b", "Ɛ"};
//        String initState = "1";
//        String[] finalStates = {"2"};
//        String[][] trans = {
//                {"1", "Φ", "2", "2"},
//                {"2", "3", "2", "3,1"},
//                {"3", "3", "1", "4"},
//                {"4", "1,2", "3", "Φ"}
//        };

        // Test case3
//        String[] inputs = {"0", "Ɛ"};
//        String initState = "A";
//        String[] finalStates = {"B", "D"};
//        String[][] trans = {
//                {"A", "Φ", "B,D"},
//                {"B", "C", "Φ"},
//                {"C", "B", "Φ"},
//                {"D", "E", "Φ"},
//                {"E", "F", "Φ"},
//                {"F", "D", "Φ"},
//        };

        // Test case4
//        String[] inputs = {"a", "b"};
//        String initState = "q0";
//        String[] finalStates = {"q2", "q1"};
//        String[][] trans = {
//                {"q0", "q0", "q0,q1"},
//                {"q1", "Φ", "q2"},
//                {"q2", "Φ", "Φ"}
//        };

        // Test case5
//        String[] inputs = {"a", "b"};
//        String initState = "A";
//        String[] finalStates = {"C"};
//        String[][] trans = {
//                {"A", "A,B", "C"},
//                {"B", "A", "B"},
//                {"C", "Φ", "A,B"}
//        };

        // Test case6
//        String[] inputs = {"0", "1", "Ɛ"};
//        String initState = "A";
//        String[] finalStates = {"C"};
//        String[][] trans = {
//                {"A", "B,C", "A", "B"},
//                {"B", "Φ", "B", "C"},
//                {"C", "C", "C", "Φ"}
//        };

        // Test case7
        String[] inputs = {"0", "1", "Ɛ"};
        String initState = "0";
        String[] finalStates = {"1", "3"};
        String[][] trans = {
                {"0", "Φ", "Φ", "1,3"},
                {"1", "2", "1", "Φ"},
                {"2", "1", "2", "Φ"},
                {"3", "3", "4", "Φ"},
                {"4", "4", "3", "Φ"}
        };


        NFA nfa = new NFA(inputs, initState, finalStates, trans);
//        NFA nfa = new NFA();
//        NFA nfa = new NFA("write file path");
        DFA dfa = nfa.toDFA();
    }

    public static void main(String[] args) throws Exception {

        testDFA();

        System.out.println("\n\n");

        testNFA();

    }
}
