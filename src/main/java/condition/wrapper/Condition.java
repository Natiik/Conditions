package condition.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Condition {
  private Object condition1;
  private Object condition2;
  private Operator operator;

    @Override
    public String toString() {
        return "(" + condition1 + operator.name() + condition2 + ")";
    }
}
