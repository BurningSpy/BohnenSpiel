import java.util.Arrays;
import java.util.LinkedList;

public class State {
  int[] field;
  int redPoints, bluePoints, heuristic, turn, depth, start, end;
  boolean redsTurn;
  State prev;
  LinkedList<State> children = new LinkedList<>();

  public State() {
    this.field = new int[12];
    Arrays.fill(this.field, 6);
    this.bluePoints = this.redPoints = 0;
    this.redsTurn = true;
    this.depth = 0;
    calcHeuristic();

    // determine iteration starting points
    if (!this.redsTurn) {
      start = 0;
      end = 5;
    } else {
      start = 6;
      end = 11;
    }
  }

  // Kopierkonstruktor
  public State(State state) {
    this.field = Arrays.copyOf(state.field, 12);
    this.bluePoints = state.bluePoints;
    this.redPoints = state.redPoints;
    this.redsTurn = !state.redsTurn;
    this.heuristic = state.heuristic;
    this.depth = state.depth + 1;
    // determine iteration starting point
    this.start = (state.start == 0) ?  6 : 0;
    this.end = (state.end == 5) ? 11 : 5;
  }

  // Move durchspielen
  public void doMove(int field) {
    int index;
    int capturedBeans = this.field[field];
    this.field[field] = 0;
    for (int i = 0; i < capturedBeans; i++) {
      index = (field + i) % 12;
      this.field[index]++;
    }
    while (true) {
      index = (field + capturedBeans) % 12;
      if (this.field[index] == 2 || this.field[index] == 4 || this.field[index] == 6) {
        if (this.redsTurn) {
          this.redPoints = this.redPoints + this.field[index];
        } else {
          this.bluePoints = this.redPoints + this.field[index];
        }
        this.field[index] = 0;
        capturedBeans--;
      } else {
        break;
      }
    }
    if (this.depth >= AiLogic.maxDepth) {
      calcHeuristic();
    }
  }

  // Find Neighbor/expand (list all next states)
  public void expand() {
    LinkedList<State> possibleStates = new LinkedList<>();

    // iterate over all of one player's possible moves
    for (int i = this.start; i <= this.end; i++) {
      if (this.field[i] != 0) {
        State nextState = new State(this); //
        nextState = calcNextState(nextState, i);
        if (nextState != null) {
          nextState.prev = this;
          nextState.turn = i;
          possibleStates.add(nextState);
        }
      }
    }

    // If no possible turns left, give points to enemy recalc heuristic
    if (this.children.size() == 0) {
      for(int i = (this.start == 0) ? 6 : 0; i <= ((this.end == 5) ? 11 : 5); i++){
        if(redsTurn){
          this.bluePoints += this.field[i];
        } else {
          this.redPoints += this.field[i];
        }
      }
      this.calcHeuristic();
    }
    this.children = possibleStates;
    AiLogic.calculatedStates.put(this.hashCode(), this);
  }

  private State calcNextState(State state, Integer move) {
    if (state.field[move] != 0) {
      state.doMove(move);
      return state;
    } else {
      return null;
    }
  }

  /** Heuristik berechnen */
  public void calcHeuristic() {
    if(this.bluePoints > 36){
      this.heuristic = 1000;
      return;
    }else if(this.redPoints > 36){
      this.heuristic = -1000;
      return;
    }
    this.heuristic = this.bluePoints - this.redPoints;
    int odd = 0;
    int max = 0;
    int index;
    int capturedbeans = 0;
    int help;
    for(int i=this.start; i<this.end;i++){
      help = i;
      while (true) {
        index = (this.field[i] + help)%12;
        if (this.field[index] == 1 || this.field[index] == 3 || this.field[index] == 5) {
          capturedbeans = capturedbeans + this.field[index] +1;

        } else {
          break;
        }
      }
      if(capturedbeans > max) {
        max = capturedbeans;
      }

    }
    this.heuristic = this.heuristic - max; /* geeigneten Faktor*/

    /* - quadratische Varianz der Bohnen in eigenen Feldern nach Zug */
    int sum = 0;
    double average = 0;
    int varianz;

    for (int i = this.start; i <= this.end; i++){
      sum = sum + this.field[i];
    }
    average = sum/6;

    sum = 0;
    for (int i = this.start; i<=this.end; i++) {
      sum = sum + (int) Math.pow((double) (average - this.field[i]), 2);
    }
    varianz = sum/6;
    this.heuristic = this.heuristic - (int)(varianz * 0.5); /* müssen hier einen geeigneten Faktor wählen */
  }



  @Override
  public boolean equals(Object o) {
    if (this.getClass() != o.getClass()) {
      return false;
    }
    State otherState = (State) o;
    if (this.redsTurn == otherState.redsTurn
        && this.bluePoints == otherState.bluePoints
        && this.redPoints == otherState.redPoints) {
      return Arrays.equals(this.field, otherState.field);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = (this.redsTurn ? 1 : 0) + this.bluePoints << 1 + this.redPoints << 3;
    for (int i = 0; i < this.field.length; i++) {
      result += this.field[i] << 2 * i + 5;
    }
    return result;
  }

  public static void main(String[] args) {
    State s = new State();
    s.redsTurn = true;
    s.field[0] = 1;
    s.field[1] = 1;
    s.field[2] = 1;
    s.field[3] = 1;
    s.field[4] = 1;
    s.field[5] = 3;
    s.field[6] = 1;
    s.field[7] = 1;
    s.field[8] = 3;
    s.field[9] = 14;
    s.field[10] = 5;
    s.field[11] = 14;
    System.out.println(AiLogic.chooseTurn(s));
  }
}
