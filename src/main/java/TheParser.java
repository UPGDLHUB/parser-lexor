import java.util.Vector;

public class TheParser {
	
	private Vector<TheToken> tokens;
	private int currentToken;
	
	public TheParser(Vector<TheToken> tokens) {
		this.tokens = tokens;
		currentToken = 0;
	}
	
	public void run() {
		RULE_CLASS();
	}

	//our methods
	private void RULE_CLASS() {
		System.out.println("- RULE_CLASS");
		if (tokens.get(currentToken).getValue().equals("class")) {
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
		} else {
			error(1);
		}

		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
		} else {
			error(2);
		}

		if (tokens.get(currentToken).getValue().equals("{")) {
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
		} else {
			error(3);
		}

		RULE_METHOD();

		if (tokens.get(currentToken).getValue().equals("}")) {
			currentToken++;
			System.out.println("- }");
		} else {
			error(4);
		}

	}

	private void RULE_METHOD(){
		System.out.println("-- RULE_METHOD");

		boolean entro=false;
		while(tokens.get(currentToken).getType().equals("KEYWORD")){
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
			entro=true;
		}
		if(!entro)
			error(5);
		
		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
		} else {
			error(6);
		}

		if (tokens.get(currentToken).getValue().equals("(")) {
			currentToken++;
			System.out.println(tokens.get(currentToken-1).getValue());
		} else {
			error(7);
		}
		if(!tokens.get(currentToken).getValue().equals(")"))
			RULE_PARAMS();

		if (tokens.get(currentToken).getValue().equals(")") && tokens.get(currentToken+1).getValue().equals("{")) {
			currentToken+=2;
			System.out.println(tokens.get(currentToken-2).getValue() + tokens.get(currentToken-1).getValue());
		} else {
			error(10);
		}
	}

	private void RULE_PARAMS(){
		while(true){
			if (tokens.get(currentToken).getType().equals("KEYWORD")) {
				currentToken++;
				System.out.println(tokens.get(currentToken-1).getValue());
			} else {
				error(8);
			}

			if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
				currentToken++;
				System.out.println(tokens.get(currentToken-1).getValue());
			} else {
				error(9);
			}

			if (tokens.get(currentToken).getValue().equals(",")) {
				currentToken++;
				System.out.println(tokens.get(currentToken-1).getValue());
			} else {
				break;
			}

		}
	}
	








	private void RULE_PROGRAM() {
		System.out.println("- RULE_PROGRAM");
		if (tokens.get(currentToken).getValue().equals("{")) {
			currentToken++;
			System.out.println("- {");
		} else {
			error(1);
		}
		RULE_BODY();
		if (tokens.get(currentToken).getValue().equals("}")) {
			currentToken++;
			System.out.println("- }");
		} else {
			error(2);
		}
	}
	
	public void RULE_BODY() {
		System.out.println("-- RULE_BODY");
		while (!tokens.get(currentToken).getValue().equals("}")) {
			RULE_EXPRESSION();
			if (tokens.get(currentToken).getValue().equals(";")) {
				System.out.println("-- ;");
				currentToken++;
			} else {
				error(3);
			}
		}
	}
	
	public void RULE_EXPRESSION() {
		System.out.println("--- RULE_EXPRESSION");
		RULE_X();
		while (tokens.get(currentToken).getValue().equals("|")) {
			currentToken++;
			System.out.println("--- |");
			RULE_X();
		}
	}
	
	public void RULE_X() {
		System.out.println("---- RULE_X");
		RULE_Y();
		while (tokens.get(currentToken).getValue().equals("&")) {
			currentToken++;
			System.out.println("---- |");
			RULE_Y();
		}
	}
	
	public void RULE_Y() {
		System.out.println("----- RULE_Y");
		if (tokens.get(currentToken).getValue().equals("!")) {
			currentToken++;
			System.out.println("----- !");
		}
		RULE_R();
	}
	
	public void RULE_R() {
		System.out.println("------ RULE_R");
		RULE_E();
		while (tokens.get(currentToken).getValue().equals("<")
			| tokens.get(currentToken).getValue().equals(">")
			| tokens.get(currentToken).getValue().equals("==")
			| tokens.get(currentToken).getValue().equals("!=")
		) {
			currentToken++;
			System.out.println("------ relational operator");
			RULE_E();
		}
	}
	
	public void RULE_E() {
		System.out.println("------- RULE_E");
		RULE_A();
		while (tokens.get(currentToken).getValue().equals("-")
			| tokens.get(currentToken).getValue().equals("+")
		) {
			currentToken++;
			System.out.println("------- + or -");
			RULE_A();
		}
		
	}
	
	public void RULE_A() {
		System.out.println("-------- RULE_A");
		RULE_B();
		while (tokens.get(currentToken).getValue().equals("/")
			| tokens.get(currentToken).getValue().equals("*")
		) {
			currentToken++;
			System.out.println("-------- * or /");
			RULE_B();
		}
		
	}
	
	public void RULE_B() {
		System.out.println("--------- RULE_B");
		if (tokens.get(currentToken).getValue().equals("-")) {
			currentToken++;
			System.out.println("--------- -");
		}
		RULE_C();
	}
	
	public void RULE_C() {
		System.out.println("---------- RULE_C");
		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			currentToken++;
			System.out.println("---------- IDENTIFIER");
		} else if (tokens.get(currentToken).getType().equals("INTEGER")) {
			currentToken++;
			System.out.println("---------- INTEGER");
		} else if (tokens.get(currentToken).getValue().equals("(")) {
			currentToken++;
			System.out.println("---------- (");
			RULE_EXPRESSION();
			if (tokens.get(currentToken).getValue().equals(")")) {
				currentToken++;
				System.out.println("---------- )");
			} else {
				error(4);
			}
		} else {
			error(5);
		}
	}
	
	private void error(int error) {
		System.out.println("Error " + error +
			" at line " + tokens.get(currentToken));
		System.exit(1);
	}
	
}

