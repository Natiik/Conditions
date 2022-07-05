package condition.util;

import condition.wrapper.Condition;

import java.util.Map;

import static condition.wrapper.Operator.AND;
import static condition.wrapper.Operator.OR;

public class ConditionCalculator {
    private static final Map<String, Boolean> rules =
            Map.of(
                    "C1", false, "C2", false, "C3", false, "C4", true, "C5", false, "C6", true, "C7", true,
                    "C8", true);

    public boolean calculate(Condition condition) {
        System.out.println("calculate");
        if (condition.getOperator().equals(OR)) {
            return calculateOrCondition(condition);
        } else if (condition.getOperator().equals(AND)) {
            return calculateAndCondition(condition);
        }
        return calculateSimpleCondition(condition);
    }

    private boolean calculateSimpleCondition(Condition condition) {
        return calculateObject(condition.getCondition1());
    }

    private boolean calculateAndCondition(Condition condition) {
        boolean condition1 = calculateObject(condition.getCondition1());
        if (!condition1) {
            return false;
        }
        return calculateObject(condition.getCondition2());
    }

    private boolean calculateOrCondition(Condition condition) {
        boolean condition1 = calculateObject(condition.getCondition1());
        if (condition1) {
            return true;
        }
        return calculateObject(condition.getCondition2());
    }

    private boolean calculateObject(Object condition) {
        if (condition instanceof String) {
            Boolean result = rules.get((String) condition);// todo add logic of condition calculating
            if (result == null) {
                throw new RuntimeException("No condition found");
            }
            return result;
        } else if (condition instanceof Condition) {
            return calculate((Condition) condition);
        }
        throw new RuntimeException();
    }
}
