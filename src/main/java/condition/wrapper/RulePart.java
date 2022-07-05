package condition.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RulePart {
       private String part;
       private Condition condition;

}
