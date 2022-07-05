package condition.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StringPiece {
   private String piece;
   private int index;

    @Override
    public String toString() {
        return this.getPiece();
    }
}
