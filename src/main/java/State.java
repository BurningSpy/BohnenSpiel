import java.util.Arrays;
import java.util.Date;

public class State {
  int[] spielfeld;
  int punkteRot, punkteBlau, heuristik;
  boolean redsTurn;

  public State() {
    this.spielfeld = new int[12];
    Arrays.fill(this.spielfeld, 6);
    this.punkteBlau = this.punkteRot = 0;
    this.redsTurn = true;
    calcHeuristic();
  }

  // Kopierkonstruktor
  public State(State state) {}

  // Move durchspielen
  public static void doMove(int field) {}

  // Find Neighbor/expand
  public static void expand() {

  }

  /** Heuristik berechnen */
  public static void calcHeuristic() {}

  @Override
  public boolean equals(Object o) {
    if (this.getClass() != o.getClass()) {
      return false;
    }
    State otherState = (State) o;
    if (this.redsTurn == otherState.redsTurn
        && this.punkteBlau == otherState.punkteBlau
        && this.punkteRot == otherState.punkteRot) {
      for (int i = 0; i < this.spielfeld.length; i++) {
        if (this.spielfeld[i] != otherState.spielfeld[i]) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  // multiplizieren die einzelnen Werte des Spielzustandes mit Primzahlen, um bei
  // unterschiedlichen States möglichst unterschiedliche hashcodes zu bekommen
  @Override
  public int hashCode() {
    int result = (this.redsTurn ? 1 : 0) + this.punkteBlau << 1 + this.punkteRot << 3;
    for (int i = 0; i < this.spielfeld.length; i++) {
      result += this.spielfeld[i] << 2 * i + 5;
    }
    return result;
  }
}
