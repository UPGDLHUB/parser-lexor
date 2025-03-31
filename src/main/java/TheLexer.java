import java.io.*;
import java.util.Vector;
import java.util.Set;

/**
 * Lexer class to analyze the input file
 * This is a basic functional lexer
 *
 * @author javiergs
 * @author Sinuhe Tijash
 * @author Hector Eguiarte
 * @version 1.0
 */
public class TheLexer {

	private File file;
	private Automata dfa;
	private Vector<TheToken> tokens;

	/**
	 * Constructor for the lexer initialized with a File.
	 * Sets up the DFA with transitions and accepting states for the different tokens.
	 */
	public TheLexer(File file) {
		this.file = file;
		tokens = new Vector<>();
		dfa = new Automata();
		dfa.addTransition("s0", "0", "s1");
		dfa.addTransition("s1", "b", "s2");
		dfa.addTransition("s1", "B", "s2");
		dfa.addTransition("s2", "0", "s3");
		dfa.addTransition("s2", "1", "s3");
		dfa.addTransition("s3", "0", "s3");
		dfa.addTransition("s3", "1", "s3");

		//cases for createStatesFor function:
		//		a-z, A-Z, 0-9, 1-9, all
		//createStatesFor(   case,	state 1, 	state2,		Automata )
		dfa= createStatesFor("a-z", "s0", "sIden", dfa);
		dfa= createStatesFor("A-Z", "s0", "sIden", dfa);
		dfa= createStatesFor("a-z", "sIden", "sIden", dfa);
		dfa= createStatesFor("A-Z", "sIden", "sIden", dfa);
		dfa= createStatesFor("0-9", "sIden", "sIden", dfa);

		//for the operators : 		=, <, >, !, ==, <=, >=, ==
		dfa.addTransition("s0", "<", "operator");
		dfa.addTransition("s0", ">", "operator");
		dfa.addTransition("s0", "!", "operator");
		dfa.addTransition("s0", "=", "operator");
		dfa.addTransition("s0", "*", "operator");
		dfa.addTransition("s0", "+", "operator");
		dfa.addTransition("s0", "-", "operator");
		dfa.addTransition("operator", "=", "operatorDue");

		//for numbers 1-9
		dfa= createStatesFor("1-9", "s0", "integer", dfa);
		dfa= createStatesFor("0-9", "integer", "integer", dfa);

		//for strings 		"everything"
		dfa.addTransition("s0", "\"", "strs");
		dfa= createStatesFor("all", "strs", "strs", dfa);
		dfa.addTransition("strs", "\"", "strsEnd");

		//for string in ''
		dfa.addTransition("s0", "'", "chars");
		dfa= createStatesFor("all", "chars", "charsM", dfa);
		dfa.addTransition("charsM", "'", "charsEnd");



		//hexadecimal
		dfa.addTransition("s1", "x", "hex");
		dfa.addTransition("s1", "X", "hex");
		dfa= createStatesFor("0-9", "hex", "hex", dfa);
		for(char c='a'; c<='f'; c++)
			dfa.addTransition("hex", String.valueOf(c), "hex");
		for(char c='A'; c<='F'; c++)
			dfa.addTransition("hex", String.valueOf(c), "hex");


		//octal
		for(char c='0'; c<='7'; c++){
			dfa.addTransition("s1", String.valueOf(c), "oct");
			dfa.addTransition("oct", String.valueOf(c), "oct");
		}

		//incomplete Numbers
		dfa.addTransition("s1", "8", "incNum");
		dfa.addTransition("oct", "8", "incNum");
		dfa.addTransition("s1", "9", "incNum");
		dfa.addTransition("oct", "9", "incNum");
		dfa= createStatesFor("0-9", "incNum", "incNum", dfa);

		//for float numbers
		dfa.addTransition( "integer",".", "float");
		dfa.addTransition( "s1",".", "float");
		dfa.addTransition( "s0",".", "float");
		dfa.addTransition( "oct",".", "float");
		dfa.addTransition( "incNum",".", "float");
		dfa= createStatesFor("0-9", "float", "float", dfa);
		dfa.addTransition( "float","f", "finalFloat");
		dfa.addTransition( "float","F", "finalFloat");


		//for exponential numbers
		dfa.addTransition( "integer","e", "incExp");
		dfa.addTransition( "float","e", "incExp");
		dfa.addTransition( "integer","E", "incExp");
		dfa.addTransition( "float","E", "incExp");
		dfa.addTransition( "incExp","-", "incExpS");
		dfa.addTransition( "incExp","+", "incExpS");
		dfa= createStatesFor("0-9", "incExpS", "exp", dfa);
		dfa= createStatesFor("0-9", "incExp", "exp", dfa);

		//for comments
		dfa.addTransition( "s0","/", "div");
		dfa.addTransition( "div","/", "SLComment");
		dfa.addTransition( "div","*", "MLComment");
		dfa= createStatesFor("all", "SLComment", "SLComment", dfa);
		dfa= createStatesFor("all", "MLComment", "MLComment", dfa);
		dfa= createStatesFor("all", "pEndMLComment", "MLComment", dfa);
		dfa.addTransition("SLComment", "\n", "EndSLComment");
		dfa.addTransition("MLComment", "*", "pEndMLComment");
		dfa.addTransition("pEndComment", "*", "pEndMLComment");
		dfa.addTransition("pEndMLComment", "/", "EndMLComment");

		dfa.addAcceptState("EndMLComment", "COMMENT");
		dfa.addAcceptState("div", "OPERATOR");
		dfa.addAcceptState("EndSLComment", "COMMENT");
		dfa.addAcceptState("float", "DOUBLE");
		dfa.addAcceptState("finalFloat", "FLOAT");
		dfa.addAcceptState("oct", "OCTAL");
		dfa.addAcceptState("hex", "HEXADECIMAL");
		dfa.addAcceptState("charsEnd", "CHAR");
		dfa.addAcceptState("strsEnd", "STRING");
		dfa.addAcceptState("integer", "INTEGER");
		dfa.addAcceptState("sIden", "IDENTIFIER");
		dfa.addAcceptState("s3", "BINARY");
		dfa.addAcceptState("s1", "INTEGER");
		dfa.addAcceptState("operator", "OPERATOR");
		dfa.addAcceptState("operatorDue", "OPERATOR");
		dfa.addAcceptState("exp", "EXPONENTIAL");

	}
	/**
	 * Reads the file and begins lexer process
	 */
	public void run() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		String complete="";
		while ((line = reader.readLine()) != null) {
			complete += line+'\n';
		}
		algorithm(complete);
	}
	/**
	 * Processes a line and tokenizes it based on the DFA transitions.
	 */
	private void algorithm(String line) {
		String currentState = "s0";
		String nextState;
		String string = "";
		int index = 0;
		boolean insideString = false;
		boolean insideMLComment = false;
		boolean insideSLComment = false;

		while (index < line.length()) {
			char currentChar = line.charAt(index);

			if (insideSLComment) {
				string += currentChar;
				if (currentChar == '\n') { // End SL comment
					tokens.add(new TheToken(string, "COMMENT"));
					insideSLComment = false;
					currentState = "s0";
					string = "";
				}
				index++;
				continue;
			}

			if (insideMLComment) {
				string += currentChar;
				if (string.endsWith("*/")) { // End ML comment
					tokens.add(new TheToken(string, "COMMENT"));
					insideMLComment = false;
					currentState = "s0";
					string = "";
				}
				index++;
				continue;
			}

			if (currentChar == '"') {
				insideString = !insideString;
			}

			if (insideString || // Inside string
					(!isOperator(currentChar) && !isDelimiter(currentChar) && !isSpace(currentChar)) ||
					((currentChar == '-' || currentChar == '+') && currentState.equals("incExp")) ||
					(currentChar == '/' && currentState.equals("s0")) ||
					(currentChar == '/' && currentState.equals("div")) ||
					(currentChar == '*' && currentState.equals("div"))) {

				if (currentState.equals("div") && currentChar == '/') {
					insideSLComment = true;
					string = "//";
					index++;
					continue;
				}

				if (currentState.equals("div") && currentChar == '*') {
					insideMLComment = true;
					string = "/*";
					index++;
					continue;
				}

				if (currentState.equals("strs") && currentChar == '\n') {
					insideString = false;
					tokens.add(new TheToken(string, "ERROR"));
					string = "";
					currentState = "s0";
					index++;
					continue;
				}

				nextState = dfa.getNextState(currentState, currentChar);
				if ((nextState.equals("INVALID") && (((isNumber(currentState) && isOperator(currentChar)))||
						(isOperator(string))))){
					tokens.add(new TheToken(string, dfa.getAcceptStateName(currentState)));
					string = "";
					currentState = "s0";
					continue;
				}else{
					string += currentChar;
					currentState = nextState;
					index++;
					continue;
				}

			} else {
				if (dfa.isAcceptState(currentState)) {
					if (isKeyWord(string))
						tokens.add(new TheToken(string, "KEYWORD"));
					else
						tokens.add(new TheToken(string, dfa.getAcceptStateName(currentState)));
				} else if (!currentState.equals("s0")) {
					tokens.add(new TheToken(string, "ERROR"));
				}

				if (isOperator(currentChar)&&currentChar!='/') {
					tokens.add(new TheToken(String.valueOf(currentChar), "OPERATOR"));
				} else if (isDelimiter(currentChar)) {
					tokens.add(new TheToken(String.valueOf(currentChar), "DELIMITER"));
				}

				if (currentChar != '/') {
					currentState = "s0";
					string = "";
				} else {
					currentState = "div";
					string = "/";
				}
			}
			index++;
		}

		if (dfa.isAcceptState(currentState)) {
			if (isKeyWord(string))
				tokens.add(new TheToken(string, "KEYWORD"));
			else
				tokens.add(new TheToken(string, dfa.getAcceptStateName(currentState)));
		} else if (!currentState.equals("s0")) {
			tokens.add(new TheToken(string, "ERROR"));
		}
	}

	private boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n';
	}

	private boolean isDelimiter(char c) {
		return c == ',' || c == ';' || c == ':' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']';
	}

	private boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
	}

	private boolean isOperator(String c) {
		return c.equals("+")||c.equals("-")||c.equals("<")||c.equals(">")||c.equals("<=")||c.equals(">=")||
				c.equals("=")||c.equals("==")||c.equals("!=")||c.equals("*")||c.equals("/")||c.equals("%");
	}

	private boolean isNumber(String c) {
		return c.equals("float")|| c.equals("finalFloat") || c.equals("oct") || c.equals("hex") || c.equals("integer")||
				c.equals("s1")|| c.equals("exp");
	}

	//Next 2 functions are for keyword identification
	private static final Set<String> KEYWORDS = Set.of(
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
		"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
		"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
		"native", "new", "package", "private", "protected", "public", "return", "short", "static",
		"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
		"void", "volatile", "while", "String","true","false"
	);

	private boolean isKeyWord(String s) {
		return KEYWORDS.contains(s);
	}
	/**
	 * Creates transitions in the DFA for a specified range of characters.
	 * This function adds state transitions for different character groups,
	 * such as lowercase letters, uppercase letters, numbers, and all printable characters.
	 */
	private Automata createStatesFor(String opt, String state1, String state2, Automata dfa1){
		Automata dfa= dfa1;
		String letter;
		switch(opt){
			case "a-z":
				for(char c = 'a'; c <= 'z'; c++) {
					dfa.addTransition(state1, String.valueOf(c), state2);
				}
			break;
			case "A-Z":
				for(char c = 'A'; c <= 'Z'; c++) {
					dfa.addTransition(state1, String.valueOf(c), state2);
				}
			break;
			case "0-9":
				for(char c = '0'; c <= '9'; c++) {
					dfa.addTransition(state1, String.valueOf(c), state2);
				}
			break;
			case "1-9":
				for(char c = '1'; c <= '9'; c++) {
					dfa.addTransition(state1, String.valueOf(c), state2);
				}
			break;
			case "all":
				for (char c = 32; c <= 126; c++) {
					if ((c != '"')&&(c != '\\')) {
						dfa.addTransition(state1, String.valueOf(c), state2);
						dfa.addTransition("espChar"+state1, String.valueOf(c), state2);
					}else{
						if(c== '\\'){
							dfa.addTransition(state1, String.valueOf(c),"espChar"+state1);
							dfa.addTransition("espChar"+state1, String.valueOf(c),state2);
						}
					}
				}
				dfa.addTransition(state1, " ", state2);
				dfa.addTransition(state1, "\t", state2);
				dfa.addTransition(state1, "\n", state2);
				break;
		}
		return dfa;
	}

	public void printTokens() {
		for (TheToken token : tokens) {
			String type = token.getType();
			String redColor = "\u001B[31m";
			String resetColor = "\u001B[0m";

			if (type.equals("ERROR")) {
				System.out.printf("%10s\t|\t%s%s%s\n", token.getValue(), redColor, type, resetColor);
			} else {
				System.out.printf("%10s\t|\t%s\n", token.getValue(), type);
			}
		}

	}

	public Vector<TheToken> getTokens() {
		return tokens;
	}

}
