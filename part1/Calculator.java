import java.io.InputStream;
import java.io.IOException;

class Calculator {

    private int lookaheadToken; //This variable is used to store every token that is being parsed

    private InputStream in;  //Used to store the input stream

    private int open_brackets; //Used to check how many brackets are still open. If there are still open brackets at the end of the evaluations, we have a parse error

    private boolean unchanged_term; //Used in cases where TermTail is null (ε) (This variable is unused because there are no such cases, but I decided to keep it for debugging purposes)

    private boolean unchanged_expr; //Used in cases where ExprTail is null (ε)

    public Calculator(InputStream in) throws IOException { //Constructor function reads the first token
        this.in = in;
        lookaheadToken = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError { //Consume reads the next token of the input stream
        if (lookaheadToken != symbol)
            throw new ParseError();
        lookaheadToken = in.read();
    }

    private int evalDigit(int digit){ //Used to convert an ASCII character to a decimal int
        return digit - '0';
    }

    private int Create_Num(String init_str) throws IOException, ParseError{ //Create_Num is used to return a full number. Whether one of single or multiple digits.

        if(lookaheadToken >= '0' && lookaheadToken <= '9') { //If we have more than one digits, we convert every number into String format and concatinate them. Then Create_Num is recursively called for the next possible digits. 

            
            int next_digit = evalDigit(lookaheadToken);
            init_str = init_str + next_digit;

            consume(lookaheadToken);
            return Create_Num(init_str);

        } else if(lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '*' || lookaheadToken == '/'
                || lookaheadToken == '\n' || lookaheadToken == ')') { //If the next token is not a digit, but one of these symbols, we have finished parsing the desired number so it is returned in an Integer format.

            return Integer.parseInt(init_str);

        } else{ //Otherwise we have a parse error.
            throw new ParseError();
        }


    }


    private int Term() throws IOException, ParseError{ //Our Term is either a number or an (expr)

        if (lookaheadToken >= '0' && lookaheadToken <= '9'){ //In case we have a number

            int first_digit = evalDigit(lookaheadToken);
            String str_num = String.valueOf(first_digit);
            consume(lookaheadToken);

            int num = Create_Num(str_num); //We use Create_Num to get our number


            if (lookaheadToken == '*' || lookaheadToken == '/'){ //We use TermTail if a multiplication or division follows. TermTail is evaluated deeper in our recursion tree, thus keeping the mathematical precedence 
                return TermTail(num); 
            } else if(lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '\n'
                    || lookaheadToken == ')'){ //In these cases there is no TermTail, so we return the number 

                return num;
            } else{
                throw new ParseError();
            }
        }else if (lookaheadToken == '('){ //If a bracket follows we evaluate a new Expr. Open brackets is encremented before calling the Expr function, and after the call we decremented it, thus making sure the brackets open and close accordingly.
            consume(lookaheadToken);
            this.open_brackets++;

            int rv = Expr();

            if(lookaheadToken == ')'){
                this.open_brackets--;
                consume(lookaheadToken);
            }

            return rv;
        }else {
            throw new ParseError();
        }



    }

    private int TermTail(int input) throws IOException, ParseError { //TermTail is used if we have a multiplication or a division

        int result = -1;
        this.unchanged_term = false;

        int num2 = -1;
        if (lookaheadToken == '*'){ //If a multiplication follows 
            consume(lookaheadToken);

            if (lookaheadToken >= '0' && lookaheadToken <= '9'){ //In case we have a number
                int first_digit = evalDigit(lookaheadToken);
                String str_num = String.valueOf(first_digit);
                consume(lookaheadToken);
                num2 = Create_Num(str_num);  //We use Create_Num to get our number
            }else if (lookaheadToken == '(') { //If a bracket follows we evaluate a new Expr.
                consume(lookaheadToken);
                this.open_brackets++;

                num2 = Expr();

                if (lookaheadToken == ')') {
                    this.open_brackets--;
                    consume(lookaheadToken);
                }

            }else {
                throw new ParseError();
            }


            result = input * num2; //Carrying out the operation


            if (lookaheadToken == '*' || lookaheadToken == '/'){ //Recursively calling TermTail if another multiplication or division follows
                return TermTail(result);
            } else if(lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '\n'
                        || lookaheadToken == ')'){ //Or if it's one of these symbols, we return our result
                return result;
            } else{
                throw new ParseError();
            }
        }

        if (lookaheadToken == '/'){ //Same thing for division
            consume(lookaheadToken);

            if (lookaheadToken >= '0' && lookaheadToken <= '9'){
                int first_digit = evalDigit(lookaheadToken);
                String str_num = String.valueOf(first_digit);
                consume(lookaheadToken);
                num2 = Create_Num(str_num);
            }else if (lookaheadToken == '(') {
                consume(lookaheadToken);
                this.open_brackets++;

                num2 = Expr();

                if (lookaheadToken == ')') {
                    this.open_brackets--;
                    consume(lookaheadToken);
                }

            }else {
                throw new ParseError();
            }

            result = input / num2;

            if (lookaheadToken == '*' || lookaheadToken == '/'){
                return TermTail(result);
            } else if(lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '\n'
                        || lookaheadToken == ')'){
                return result;
            } else{
                throw new ParseError();
            }
        }

        this.unchanged_term = true; //If we ended up with some other symbol this mean TermTail is of no value.

        return result;

    }

    private int ExprTail(int input) throws IOException, ParseError{ //ExprTail is used for addition and subtraction operations.


        int result = -1;
        this.unchanged_expr = false;

        if (lookaheadToken == '+'){ //If we have an addition
            consume(lookaheadToken);

            int num2 = Term(); //We get our second number by evaluating a Term() function

            result = input + num2;
 
            if (lookaheadToken == '+' || lookaheadToken == '-' ){ //Recursively calling ExprTail if another addition or subtraction follows
                return ExprTail(result);
            } else if(lookaheadToken == '\n' || lookaheadToken == ')' ){ //Or if it's one of these symbols, we return our result
                return result;
            } else{
                throw new ParseError();
            }
        }

        if (lookaheadToken == '-'){ //Same thing for subtraction
            consume(lookaheadToken);

            int num2 = Term();

            result = input - num2;

            if (lookaheadToken == '+' || lookaheadToken == '-' ){
                return ExprTail(result);
            } else if(lookaheadToken == '\n' || lookaheadToken == ')'){
                return result;
            } else{
                throw new ParseError();
            }
        }


        this.unchanged_expr = true; //If we ended up with some other symbol this mean ExprTail is of no value.

        return result;

    }


    private int Expr() throws IOException, ParseError { //Expr is our base function

        if((lookaheadToken >= '0' && lookaheadToken <= '9') || lookaheadToken == '('){ //Expr must begin with a number

            int term_res = Term(); //The first non-terminal we need is the result of a Term() function
 
            if (lookaheadToken == ')'){ //If this Expr was in brackets, we need to return the result of Term()
                this.open_brackets--;

                consume(lookaheadToken);
                return term_res;
            }

            if (lookaheadToken == '*' ){ //Special cases. Only used for Exprs that are in brackets.
                consume(lookaheadToken);
                term_res = term_res * Term();
            }else if (lookaheadToken == '/'){
                consume(lookaheadToken);
                term_res = term_res / Term();
            }else if (lookaheadToken == '\n'){
                return term_res;
            }

            int expr_res = ExprTail(term_res); //Getting the result of ExprTail


            if(this.unchanged_expr == true){ //If ExprTail is of no value, we return only the result of Term(), otherwise the result of both functions.
                return term_res;
            }else{
                return expr_res;
            }
        }else {
            throw new ParseError();
        }



    }


    public int eval() throws IOException, ParseError { //Eval is our starting function.
        int rv = Expr();
        if (lookaheadToken != '\n' && lookaheadToken != -1) { //If the last token isn't '\n' we have a parse error
            throw new ParseError();
        }
        if (lookaheadToken == '\n' && open_brackets > 0){ //If the user typed in an expression and didn't close some brackets, we also have a parse error.
            throw new ParseError();
        }
        return rv;
    }

    public static void main(String[] args) {
        try {
            Calculator evaluate = new Calculator(System.in);
            System.out.println(evaluate.eval()); //Printing the result in stdout.
        }
        catch (IOException e) { //Printing error messages in stderr.
            System.err.println(e.getMessage());
        }
        catch(ParseError err){
            System.err.println(err.getMessage());
        }
    }

}


