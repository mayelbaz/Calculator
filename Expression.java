import java.util.*;

public class Expression implements FormulaType{
    FormulaType left;
    Operator operator;
    FormulaType right;

    public Expression(FormulaType lefty, Operator op,FormulaType righty) {
        left=lefty;
        operator=op;
        right=righty;
    }

    @Override
    public int evaluate(Hashtable<String, Integer> var_ht ) {
        int leftVal = left.evaluate(var_ht);
        int rightVal = right.evaluate(var_ht);
        switch (operator){
            case ADD:
                return leftVal+rightVal;
            case SUB:
                return leftVal-rightVal;
            case MULT:
                return leftVal*rightVal;
            case DIV:
                if (rightVal==0)
                    throw new ArithmeticException();
                return leftVal/rightVal;
        }
        // No real reason to ever be here...
        throw new UnsupportedOperationException();
    }
}