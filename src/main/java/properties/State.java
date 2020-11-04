package properties;

import java.util.Arrays;

public class State {
  int[] spielfeld;
  int punkteRot, punkteBlau;
  boolean redsTurn;

  public State() {
    this.spielfeld = new int[12];
    Arrays.fill(this.spielfeld, 6);
    this.punkteBlau = this.punkteRot = 0;
    this.redsTurn = true;
  }

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
