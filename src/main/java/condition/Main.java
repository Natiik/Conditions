package condition;

import condition.util.ConditionCalculator;
import condition.util.ConditionParser;
import condition.wrapper.Condition;

public class Main {
//          private static final String RULE = "C1ANDC2OR(C3ORC4AND((C5ORC6)ANDC7)ANDC8)";
//    private static final String RULE = "C1ANDC2ORC3";
  private static final String RULE =
//      "(C1AND(C2OR(C8ANDC9)))OR(((C3ANDC10)ORC11)ANDC12)OR(C4AND((C5ORC6)ANDC7))ORC13ANDC14";
  "C1ANDC2OR(C3ORC4ANDC5)";

    public static void main(String[] args) {
        Condition condition = new ConditionParser().parse(RULE);
        boolean answer = new ConditionCalculator().calculate(condition);
        System.out.println(answer);
    }

}
