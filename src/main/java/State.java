import java.util.Arrays;
import java.util.LinkedList;

public class State {
  int[] spielfeld;
  int punkteRot, punkteBlau, heuristik, turn;
  boolean redsTurn;
  State prev;
  LinkedList<State> children = new LinkedList<>();

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
    this.heuristik = state.heuristik;
  }

  // Move durchspielen
  public void doMove(int field) {
    int index;
    int hilfe = this.spielfeld[field];
    boolean test = true;
    this.spielfeld[field] = 0;
    for (int i = 0; i < hilfe; i++) {
      index = (field + i) % 12;
      this.spielfeld[index] = this.spielfeld[index] + 1;
    }
    while (test) {
      index = (field + hilfe) % 12;
      if (this.spielfeld[index] == 2 || this.spielfeld[index] == 4 || this.spielfeld[index] == 6) {
        if (this.redsTurn) {
          this.punkteRot = this.punkteRot + this.spielfeld[index];
        } else {
          this.punkteBlau = this.punkteRot + this.spielfeld[index];
        }
        hilfe = hilfe - 1;
      } else {
        test = false;
      }
    }
    calcHeuristic();
  }

  // Find Neighbor/expand (list all next states)
  public void expand() {
    int start, end;
    LinkedList<State> possibleStates = new LinkedList<>();

    // determine iteration starting point
    if (!this.redsTurn) {
      start = 0;
      end = 5;
    } else {
      start = 6;
      end = 11;
    }
    // iterate over all of one player's possible moves
    for (int i = start; i <= end; i++) {
      if (this.spielfeld[i] != 0) {
        State nextState = new State(this); //
        nextState = calcNextState(nextState, i);
        if (nextState != null) {
          nextState.prev = this;
          nextState.turn = i;
          possibleStates.add(nextState);
        }
      }
    }
    this.children = possibleStates;
  }

  private State calcNextState(State state, Integer move) {
    if (state.spielfeld[move] != 0) {
      state.doMove(move);
      return state;
    } else {
      return null;
    }
  }

  /** Heuristik berechnen */
  public void calcHeuristic() {
    this.heuristik = this.punkteBlau + this.punkteRot;
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
