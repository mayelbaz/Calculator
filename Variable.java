
import java.util.*;

public class Variable implements FormulaType{
    String variable;
    int inc_or_dec = 0;

    public Variable(String var ,int x) {
        variable = var;
        inc_or_dec =x;
    }

    @Override
    public int evaluate(Hashtable<String, Integer> var_ht ) {
        // if the variable is not in the table at all, it means we're trying to use an undeclared variable
        if (!var_ht.containsKey(variable))
            throw new ArrayStoreException();
        // we need to update the increment / decrement in the overall table. the number we return is before this change
        // because if its post - its not part of the calculation, and if its pre, we updated it in the formula (++i = i+1)
        int num = var_ht.get(variable);
        var_ht.put(variable,num+ inc_or_dec);
        return num;
        }

    }
