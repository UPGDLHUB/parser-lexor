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

		while (currentToken < tokens.size() && !tokens.get(currentToken).getValue().equals("}")) {
			RULE_METHOD();
		}

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
		RULE_BODY();
		if (tokens.get(currentToken).getValue().equals("}")) {
			System.out.println(tokens.get(currentToken).getValue());
			currentToken++;
		} else {
			error(11);
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


	private void RULE_WHILE(){
		if(tokens.get(currentToken).getValue().equals("while")){
			currentToken++;
		}else{
			error(8);
		}
		if(tokens.get(currentToken).getValue().equals("(")){
			currentToken++;
		}else{
			error(7);
		}
		//Mandamos a llamar a la regla expresión
		RULE_EXPRESSION();
		if(tokens.get(currentToken).getValue().equals(")")){
			currentToken++;
		}else{
			error(7);
		}
		RULE_PROGRAM();
	}

	private void RULE_IF() {
		System.out.println("-- RULE_IF");

		if (tokens.get(currentToken).getValue().equals("if")) {
			currentToken++;
			System.out.println("if");
		} else {
			error(8);
		}

		if (tokens.get(currentToken).getValue().equals("(")) {
			currentToken++;
			System.out.println("(");
		} else {
			error(8);
		}

		RULE_EXPRESSION();

		if (tokens.get(currentToken).getValue().equals(")")) {
			currentToken++;
			System.out.println(")");
		} else {
			error(8);
		}
		if(tokens.get(currentToken).getValue().equals("{")){
			RULE_PROGRAM();
		}else{
			RULE_BODY();
		}



		if (tokens.get(currentToken).getValue().equals("else")) {
			currentToken++;
			System.out.println("else");
			RULE_PROGRAM();
		}
	}

	private void RULE_FOR() {
		System.out.println("-- RULE_FOR");

		if (tokens.get(currentToken).getValue().equals("for")) {
			currentToken++;
			System.out.println("for");
		} else {
			error(8);
		}

		if (tokens.get(currentToken).getValue().equals("(")) {
			currentToken++;
			System.out.println("(");
		} else {
			error(7);
		}

		if (tokens.get(currentToken).getType().equals("KEYWORD") &&
				currentToken + 2 < tokens.size() &&
				tokens.get(currentToken + 2).getValue().equals("=")) {
			currentToken++;
			RULE_ASSIGNMENT();
		} else {
			error(7);
		}

		if (tokens.get(currentToken).getValue().equals(";")) {
			currentToken++;
			System.out.println(";");
		} else {
			error(7);
		}

		RULE_EXPRESSION();

		if (tokens.get(currentToken).getValue().equals(";")) {
			currentToken++;
			System.out.println(";");
		} else {
			error(7);
		}

		RULE_ASSIGNMENT();

		if (tokens.get(currentToken).getValue().equals(")")) {
			currentToken++;
			System.out.println(")");
		} else {
			error(7);
		}

		// Llamar a RULE_PROGRAM para procesar el cuerpo del for
		RULE_PROGRAM();
	}
	private void RULE_DO_WHILE() {
		System.out.println("-- RULE_DO_WHILE");

		if (tokens.get(currentToken).getValue().equals("do")) {
			System.out.println("do");
			currentToken++;
		} else {
			error(14);
		}

		RULE_PROGRAM();

		if (tokens.get(currentToken).getValue().equals("while")) {
			System.out.println("while");
			currentToken++;
		} else {
			error(14);
		}

		if (tokens.get(currentToken).getValue().equals("(")) {
			System.out.println("(");
			currentToken++;
		} else {
			error(14);
		}

		RULE_EXPRESSION();

		if (tokens.get(currentToken).getValue().equals(")")) {
			System.out.println(")");
			currentToken++;
		} else {
			error(14);
		}

		if (tokens.get(currentToken).getValue().equals(";")) {
			System.out.println(";");
			currentToken++;
		} else {
			error(14);
		}
	}

	private void RULE_RETURN() {
		System.out.println("-- RULE_RETURN");

		// Verify that the token is "return"
		if (tokens.get(currentToken).getValue().equals("return")) {
			System.out.println("return");
			currentToken++;
		} else {
			error(15);
		}

		if (!tokens.get(currentToken).getValue().equals(";")) {
			RULE_EXPRESSION();
		}
	}
	private void RULE_CALL_METHOD() {
		System.out.println("-- CALL_METHOD_RULE");

		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			System.out.println("Método: " + tokens.get(currentToken).getValue());
			currentToken++;
		} else {
			error(16);
		}

		if (tokens.get(currentToken).getValue().equals("(")) {
			System.out.println("(");
			currentToken++;
		} else {
			error(17);
		}

		// Procesar los parámetros de la llamada (puede ser lista vacía)
		RULE_CALL_PARAMS();

		// Se espera el paréntesis de cierre ")"
		if (tokens.get(currentToken).getValue().equals(")")) {
			System.out.println(")");
			currentToken++;
		} else {
			error(18); // Error: se esperaba ")"
		}
	}

	private void RULE_CALL_PARAMS() {
		System.out.println("-- CALL_PARAMS_RULE");

		// Si el siguiente token es ")" no hay parámetros
		if (tokens.get(currentToken).getValue().equals(")")) {
			return;
		}

		// Se asume que hay al menos una expresión como parámetro
		RULE_EXPRESSION();

		// Mientras se encuentren comas, se procesan más expresiones como parámetros
		while (tokens.get(currentToken).getValue().equals(",")) {
			System.out.println(",");
			currentToken++;
			RULE_EXPRESSION();
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
		while (currentToken < tokens.size() && !tokens.get(currentToken).getValue().equals("}")) {
			if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
				RULE_ASSIGNMENT();
				if(tokens.get(currentToken).getValue().equals(";")) {
					currentToken++;
				} else {
					error(3);
					currentToken++;
				}
			}
			else if ((
					tokens.get(currentToken).getValue().equals("int") ||
							tokens.get(currentToken).getValue().equals("float") ||
							tokens.get(currentToken).getValue().equals("boolean") ||
							tokens.get(currentToken).getValue().equals("char") ||
							tokens.get(currentToken).getValue().equals("string") ||
							tokens.get(currentToken).getValue().equals("void")
			)) {
				RULE_VARIABLE();
				if (currentToken < tokens.size() && tokens.get(currentToken).getValue().equals(";")) {
					currentToken++;
				} else {
					error(3);
				}
			}
			else if (tokens.get(currentToken).getValue().equals("while")) {
				RULE_WHILE();
			}
			else if (tokens.get(currentToken).getValue().equals("for")) {
				RULE_FOR();
			}
			else if (tokens.get(currentToken).getValue().equals("if")) {
				RULE_IF(); // Método que procesa el if (incluyendo else, si lo hay)
			}else if (tokens.get(currentToken).getValue().equals("do")) {
				RULE_DO_WHILE();
			}
			else if (tokens.get(currentToken).getValue().equals("return")) {
				RULE_RETURN();
				if (tokens.get(currentToken).getValue().equals(";")) {
					currentToken++;
				} else {
					error(3);
				}
			}
			else {
				error(4);
			}
		}
	}
	private void RULE_VARIABLE() {
		System.out.println("-- RULE_VARIABLE");

		if (tokens.get(currentToken).getValue().equals("int") ||
				tokens.get(currentToken).getValue().equals("float") ||
				tokens.get(currentToken).getValue().equals("boolean") ||
				tokens.get(currentToken).getValue().equals("char") ||
				tokens.get(currentToken).getValue().equals("string") ||
				tokens.get(currentToken).getValue().equals("void")) { // Agregar aquí más tipos si es necesario
			System.out.println(tokens.get(currentToken).getValue());
			currentToken++;
		} else {
			error(7);
		}

		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			System.out.println(tokens.get(currentToken).getValue());
			currentToken++;
		} else {
			error(5);
		}
	}
	private void RULE_ASSIGNMENT() {
		System.out.println("-- RULE_ASSIGNMENT");

		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			currentToken++;
			System.out.println(tokens.get(currentToken - 1).getValue());
		} else {
			error(5);
		}

		if (tokens.get(currentToken).getValue().equals("=")) {
			currentToken++;
			System.out.println("=");
		} else {
			error(6);
		}
		RULE_EXPRESSION();
	}
	
	public void RULE_EXPRESSION() {
		System.out.println("--- RULE_EXPRESSION");
		RULE_X();
		while (tokens.get(currentToken).getValue().equals("||")) {
			currentToken++;
			System.out.println("--- ||");
			RULE_X();
		}
	}
	
	public void RULE_X() {
		System.out.println("---- RULE_X");
		RULE_Y();
		while (tokens.get(currentToken).getValue().equals("&&")) {
			currentToken++;
			System.out.println("---- &&");
			RULE_Y();
		}
	}
	
	public void RULE_Y() {
		System.out.println("----- RULE_Y");
		while (tokens.get(currentToken).getValue().equals("!")) {
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
			if (currentToken + 1 < tokens.size() && tokens.get(currentToken + 1).getValue().equals("(")) {
				RULE_CALL_METHOD();
			} else {
				System.out.println("---------- IDENTIFIER: " + tokens.get(currentToken).getValue());
				currentToken++;
			}
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

