
Ονοματεπώνυμο: Παντελεήμων Μαλέκας
Α.Μ: 1115201600268

Part 1: For the first part, I implemented the recursive descent parser that evaluates our mathematical expressions. The grammar that I used supports priority between the operators and has removed the left recursion. It is summarised in: 

#Production rule
Goal       →  Expr
Expr       → Term ExprTail 
ExprTail   →   +  Term ExprTail
	    |  -  Term ExprTail
	    |    ε
Term       →  Factor TermTail
TermTail   →  *  Factor TermTail
           |  /  Factor TermTail
           |  ε
Factor     → Number
           | (Expr)

The Calculator.java contains the definition of the Calculator class and the main function. The ParseError.java is used to print the parse error messages. I should note that "Factor" is absent from the Calculator.java program. I didn't think it was necessary to use a function, so a simple check is performed in order to proceed to either a number or an (Expr). Also, a Create_Num function is used in order to get each number from stdin. For more code related specifics, check the comments in the Calculator.java.

A user may run the program by typing the expression and \n. My program doesn't identify whitespaces. If the input is valid, the result will be printed in stdout. If a parse error occurs, it will be printed in stderr.

Compilation command: make
Run command:         make execute

Part 2: For the second part I defined the lexical rules in the scanner.flex file. The Macro Declarations section contains definitions for LineTermination, Whitespace, Identifiers, as well as the "Lbrace" term which is the combination of the ){ characters. In the Lexical Rules section the operators of our input language are defined as well as the <STRING> state, which is used for the String parsing.

Moving on, the grammar of our language is defined in the parser.cup file. After connecting the Scanner with the .cup program, what follows is the declarations of our Terminals and Non terminals. 

Finally, we reach the grammar rules of our input language. The first non terminal is named  "program". It may consist of a declaration list, an expression list (our statements in the main function) or both. Func_decl is the list of our function declarations. Inside the body of our function declaration a single bexpr is used. bexpr is the same thing as the default expr, with the only difference being that a bexpr may support identifiers as part of the expression (unlike the main function which can't support variables). I know that it wasn't necessary but I could take care of it, so I did. After the function body non terminals, we move on to a list of expressions, which are our statements in the main function. Regarding the if statements, they are evaluated to an expression that uses the ternary operator. Also the prefix operator of our input language is evaluated to the startsWith() function (Note: since startsWith() works the opposite way of the prefix operator, the operands are switched. Example: For the "if ("aa" prefix "aabb")" condition, the output program evaluates this to "aabb".startsWith("aa") ). Regarding the reverse operator, the reverse() function is used and the result is converted to String format with toString(). For more details regarding the grammar rules check the comments in the parser.cup.

The Main.java runs the parser and after the program's execution, the output .java program is printed in stdout.

Compilation command: make
Run command:         make execute < [the input file]

I tried to check as many test cases as I could. I tested the three examples that were in the lecture's website and the following cases also work for sure: 

foo(){
"John"
}

func(a,b,c){
a+b+c
}

foo()
"0" 
"20" + "15"
"1" + "3" + "4" + "5" + "6"
reverse "USSR"
reverse reverse "USSR"
"aa" + reverse "abc" 
"aa" + reverse "abc" + "bb" + reverse "1234"
reverse "678" + "aa" + reverse reverse "1234"
reverse "aa" + if ("aa" prefix "aabb") "yes" else "no"
"aa" + reverse if ("aa" prefix "aabb") "yes" else "no"
if ("aagg" prefix "aabbcc") "yes1" else "no1"
if ( "ab11" + if("aa22" prefix "aabb33") "yes44" else "no55" prefix "aabb" ) 
	if ("aa" prefix "aabb") "yes" else "no"
else 
	if ("aa" prefix "aabb") "yes" else "no"
reverse "aa" + if ("aa" prefix "aabb") "yes" else "no"
func("aa","bb","cc") + "bbc"
func(if ("aa" prefix "aabb") "yes" else "no", "cc", "f") + if ("aa" prefix "aabb") "yes" else "no" 
if ( reverse "aa" + "ab11" + if("aa22" prefix "aabb33") "yes44" else "no55" prefix "aabb" ) 
	if ("aa" prefix "aabb") "yes" else "no"
else 
	if ("aa" prefix "aabb") "yes" else "no"
if (reverse func("a"+"g","b",foo()) + "g" +"h" prefix "aabbcc") "yes1" else "no1"
reverse func("a"+"g","b",foo()) + "g" +"h" 
