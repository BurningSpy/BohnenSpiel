import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

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
  public State(State state) {
    this.spielfeld = Arrays.copyOf(state.spielfeld, 12);
    this.punkteBlau = state.punkteBlau;
    this.punkteRot = state.punkteRot;
    this.redsTurn = state.redsTurn;
  }

  // Move durchspielen
  public void doMove(int field) {
    int index;
    int hilfe = this.spielfeld[field];
    this.spielfeld[field] = 0; 
    for (int i=0; i<hilfe; i++){
      index = (field+i) % 12;
      this.spielfeld[index] = this.spielfeld[index] + 1;
    }
    while(test){
      if(
      
      
    // calcHeuristic()
  }

  // Find Neighbor/expand
  public LinkedList<State> expand() {
    // alle möglichen Züge hier
    State possibleState = new State(this);
    possibleState.doMove(1);
    // someLinkedList.add(possibleStates)
    return new LinkedList<>();
  }

  /** Heuristik berechnen */
  public void calcHeuristic() {
    // berechnen, blablabla
    // this.heuristik = value
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
