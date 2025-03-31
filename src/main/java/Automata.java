import java.util.*;

/**
 * Automata class represent a DFA.
 * This version is implemented with a HashMap to store the transition table.
 *
 * @author javiergs
 * @version 1.0
 */
public class Automata {
	
	private final HashMap<String, String> table = new HashMap<>();
	private final HashMap<String, String> acceptStates = new HashMap<>();
	/**
	 * Adds a transition to the DFA.
	 * Each transition is represented as a key-value pair in the table.
	 */
	public void addTransition(String currentState, String inputSymbol, String nextState) {
		table.put(currentState + "/" + inputSymbol, nextState);
	}
	/**
	 * Retrieves the next state based on the current state and input symbol.
	 * If no transition is found, returns "INVALID" (indicating a rejection state).
	 */
	public String getNextState(String currentState, char inputSymbol) {
		String ans = table.get(currentState + "/" + inputSymbol);
		if(ans == null){
			ans = "INVALID";
		}
		return ans;
	}
	/**
	 * Marks a state as an accepting (final) state and associates it with a token type.
	 */
	public void addAcceptState(String state, String name) {
		acceptStates.put(state, name);
	}
	/**
	 * Checks if a given state is an accepting state.
	 */
	public boolean isAcceptState(String name) {
		return acceptStates.containsKey(name);
	}
	/**
	 * Retrieves the token type associated with an accepting state.
	 */
	public String getAcceptStateName(String state) {
		return acceptStates.get(state);
	}
	
	public void printTable() {
		System.out.println("DFA Transition Table:");
		for (String state : table.keySet()) {
			String[] parts = state.split("/");
			System.out.println(parts[0] + " -> " + table.get(state) + " [label=\"" + parts[1] + "\"];");
		}
	}
	
}

