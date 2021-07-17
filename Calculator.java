import java.util.*;

public class Calculator {

    // A "DEFINE" sort of thing I can use later
    public static final int DEC = -1;
    public static final int REG = 0;
    public static final int INC = 1;

    // a hash to keep all variables' values
    Hashtable<String, Integer> variables_ht = new Hashtable<>();

    void process(String input) {

        // when this is called we already know the input is valid (except 0 division and unfamiliar variable)
        Scanner scanner = new Scanner(input);
        // we are going to change the hash table soon, according to the new input line we are about to process.
        // BUT, the process might fail and we'll want to undo these changes. we create a pre-changes copy of the HT for reconstruction.
        Hashtable<String, Integer> temp = new Hashtable<>(variables_ht);
        // Since we know its a valid formula, the first argument is the variable to be changed, the left in the assignment.
        String var = scanner.next();
        String assignment = scanner.next();
        // initializing the formula
        FormulaType formula;
        // if the assignment is +\-\/\* and = , we need to change our formula's logic. for example i+=1 --> i = i + 1
        // the right part of the assignment is recursively built into expression , variables and numbers.
        FormulaType left = new Variable(var,REG);
        formula = switch (assignment) {
            case "+=" -> new Expression(left, Operator.ADD,  RecProcess(scanner,false));
            case "-=" -> new Expression(left, Operator.SUB,  RecProcess(scanner,false));
            case "*=" -> new Expression(left, Operator.MULT, RecProcess(scanner,false));
            case "/=" -> new Expression(left, Operator.DIV,  RecProcess(scanner,false));
            default -> RecProcess(scanner,false);
        };
        // after the input is recursively built into it's formula structure (formula operator (formula operator (.... )))
        // evaluate the final result using the class's evaluation func, from left to right - the right order
        int result = formula.evaluate(temp);
        // the result is inserted into the relevant place in the variable hash table
        temp.put(var,result);
        // If we're here, no exception was thrown. we can switch between our safety temp hash to the regular saved one
        variables_ht=temp;
    }

    // processes a complete sub formula. reads the left operand and calls the other recursive processing with the rest
    private FormulaType RecProcess(Scanner scanner, boolean negative){
        // if boolean is true, our last operator was - (treated like +) so our current var is -1 * var
        //instead of getting the next var in the scanner, we make it be -1
        if (negative == true) {
            FormulaType negOne = new Number(-1);
            return RecProcess(negOne,scanner,true);
        }
        String left_as_string = scanner.next();
        FormulaType left = stringToFormula(left_as_string);
        return RecProcess(left,scanner,false);
    }
    // processes the current operator. creates an expression by the given left, operator and future to be processed right
    // when we see - sign, we translate it to  + and -1 * number , because minus have non chronological order
    // example : y = x - i  --->  y = x + -1 * i
    private FormulaType RecProcess(FormulaType left, Scanner scanner, boolean negative){
        // if boolean is true, our last operator was and our current left is -1 . the next (real) variable in the scanner
        // will be multiplied by the -1.
        if (negative){
             String right = scanner.next();
             FormulaType innerMulExpression = new Expression(left, Operator.MULT, stringToFormula(right));
             if (scanner.hasNext())
                     return RecProcess(innerMulExpression, scanner,false);
            return innerMulExpression;
        }
        if (scanner.hasNext()) {
            String s= scanner.next();
            switch (s) {
                case "+":
                    return new Expression(left, Operator.ADD, RecProcess(scanner,false));
                case "-":
                    // - is treated like a +, the boolean will flag the rec func to insert -1 * var next calculation
                    return new Expression(left, Operator.ADD, RecProcess(scanner,true));
                case "*":
                    // if it's multiplication then we need to get the next argument to compute result
                    String right = scanner.next();
                    FormulaType innerMulExpression = new Expression(left, Operator.MULT, stringToFormula(right));
                    // we need to check that the formula doesnt end here. if not - we continue from sub formula
                    if (scanner.hasNext()) {
                        return RecProcess(innerMulExpression, scanner,false);
                    }
                    return innerMulExpression;
                case "/":
                    // if it's division then we need to get the next argument to compute result
                    String divider = scanner.next();
                    FormulaType innerDivExpression = new Expression(left, Operator.DIV, stringToFormula(divider));
                    // we need to check that the formula doesnt end here. if not - we continue from sub formula
                    if (scanner.hasNext()) {
                        return RecProcess(innerDivExpression, scanner,false);
                    }
                    return innerDivExpression;
            }
        }
        return left;
    }

    // This helper func receives a formula type in a string form and returns it as it's type
    // if its pre dec/inc we'll separate the argument into an expression of new variable, assignment and 1
    // if its pre / post dec/inc we'll "flag" it as such in the variable's construction to be changed in the calculation later
    private FormulaType stringToFormula(String s){
        if (isNumber(s))
            return new Number(Integer.parseInt(s));
        else if (isVariable(s))
            return new Variable(s,REG);
        else if (isPreDecrement(s))
            return new Expression(new Variable(s.substring(2), DEC), Operator.SUB, new Number(INC));
        else if (isPostDecrement(s))
            return new Variable(s.substring(0,s.length()-2), DEC);
        else if (isPreIncrement(s))
            return new Expression(new Number(INC), Operator.ADD, new Variable(s.substring(2), INC));
        else if (isPostIncrement(s))
            return new Variable(s.substring(0,s.length()-2), INC);
        throw new UnsupportedOperationException();
    }

    void printValues(){
        System.out.println(Collections.singletonList(variables_ht));
    }

    boolean validityCheck(String input) {
            Scanner scanner = new Scanner(input);
            // the first argument of the line must be a variable and the second an assignment
            if (!scanner.hasNext() || !isVariable(scanner.next())) return false;
            if (!scanner.hasNext() || !isAssignment(scanner.next())) return false;
            // after validating the left side of the assignment, the right side must be:
            // value - operator  - value - operator - value ... a value can be a variable, a number and can come with inc/dec sign attached!
            int pair= 0;
            while (scanner.hasNext()) {
                String s = scanner.next();
                // if we are pair, its a type of variable
                if (pair%2==0) {
                    if (!isValue(s))
                        return false;
                }
                else {
                    if (!isOperator(s))
                        return false;
                }
                // change parity
                pair++;
            }
            // we need to make sure we ended with a value and not an operator!
        return pair % 2 == 1;
    }

    // The following functions are helper funcs to help decide the type of the string

    private boolean isValue(String s) {
        return isNumber(s) || isVariable(s) ||
                isPreIncrement(s) || isPostIncrement(s) || isPreDecrement(s) || isPostDecrement(s) ;
    }

    private boolean isAssignment(String s) {
        return s.equals("=") ||  s.equals("+=")  ||  s.equals("-=")  ||  s.equals("/=")  ||  s.equals("*=");
    }

    private boolean isOperator(String s) {
        return s.equals("/") ||  s.equals("*")  ||  s.equals("-") ||  s.equals("+");
    }

    // we need to check the whole string is made of digits
    private boolean isNumber(String s) {
        if (s.charAt(0)!= '-' && !Character.isDigit(s.charAt(0)))
            return false;
        if (s.charAt(0)== '-' && s.length()==1)
            return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

    // according to our conventions, the first characters is a letter and the rest are letters, numbers or _
    private boolean isVariable(String s) {
        if (!Character.isLetter(s.charAt(0)))
            return false;
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isLetterOrDigit(s.charAt(i)) && s.charAt(i)!='_')
                return false;
        }
        return true;
    }

    // the if it's pre inc/dec we need to cut the first 2 chars of the string to recieve a clean variable (without ++/--)
    // if it's post inc/dec we need to cut the last 2 from the exact same reason
    private boolean isPreIncrement(String s) {
        if (s.length()<3 || s.charAt(0)!= '+' || s.charAt(1)!= '+' )
           return false;
       return isVariable(s.substring(2));
    }

    private boolean isPostIncrement(String s) {
        if (s.length()<3 || s.charAt(s.length()-1)!= '+' || s.charAt(s.length()-2)!= '+' )
            return false;
        return isVariable(s.substring(0,s.length()-2));
    }
    private boolean isPreDecrement(String s) {
        if (s.length()<3 || s.charAt(0)!= '-' || s.charAt(1)!= '-' )
            return false;
        return isVariable(s.substring(2));
    }

    private boolean isPostDecrement(String s) {
        if (s.length()<3 || s.charAt(s.length()-1)!= '-' || s.charAt(s.length()-2)!= '-' )
            return false;
        return isVariable(s.substring(0,s.length()-2));
    }
}
