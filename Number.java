import java.util.*;

public class Number implements FormulaType{
    int value;

    public Number(int val) {
         value = val;
    }

    @Override
    public int evaluate(Hashtable<String, Integer> var_ht) {
        return value;
    }
}