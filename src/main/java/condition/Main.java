package condition;

import condition.util.ConditionParser;
import condition.wrapper.Condition;

import java.util.Map;

import static condition.wrapper.Operator.OR;

public class Main {
    //      private static final String RULE = "C1ANDC2OR(C3ORC4AND((C5ORC6)ANDC7)ANDC8)";
    private static final String RULE = "C1ANDC2ORC3";
    private static final Map<String, Boolean> rules =
            Map.of(
                    "C1", false, "C2", false, "C3", false, "C4", true, "C5", false, "C6", true, "C7", true,
                    "C8", true);

//  private static final String RULE =
//      "(C1AND(C2OR(C8ANDC9)))OR(((C3ANDC10)ORC11)ANDC12)OR(C4AND((C5ORC6)ANDC7))ORC13ANDC14";

    //  private static final String RULE =
    //          "()AND(((C3ANDC10)ORC11)ANDC12)OR()";

    public static void main(String[] args) {
        Condition condition = new ConditionParser().parse(RULE);
        boolean answer = calculateCondition(condition);
        System.out.println(answer);
    }


    private static boolean calculateCondition(Condition condition) {
        System.out.println("calculate");
        if (condition.getOperator().equals(OR)) {
            return calculateOrCondition(condition);
        }
        return calculateAndCondition(condition);
    }

    private static boolean calculateAndCondition(Condition condition) {
        boolean condition1 = calculateObject(condition.getCondition1());
        if (!condition1) {
            return false;
        }
        return calculateObject(condition.getCondition2());
    }

    private static boolean calculateOrCondition(Condition condition) {
        boolean condition1 = calculateObject(condition.getCondition1());
        if (condition1) {
            return true;
        }
        return calculateObject(condition.getCondition2());
    }

    private static boolean calculateObject(Object condition) {
        if (condition instanceof String) {
            Boolean result = rules.get((String) condition);
            return result != null && result;
        } else if (condition instanceof Condition) {
            return calculateCondition((Condition) condition);
        }
        throw new RuntimeException();
    }

}
