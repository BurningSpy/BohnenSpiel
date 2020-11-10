import java.util.Arrays;
import java.util.LinkedList;

public class State {
  int[] field;
  int redPoints;
  int bluePoints;
  int turn;
  int depth;
  int start;
  int end;
  double heuristic;
  boolean redsTurn;
  State prev;
  LinkedList<State> children = new LinkedList<>();

  public State() {
    this.field = new int[12];
    Arrays.fill(this.field, 6);
    this.bluePoints = this.redPoints = 0;
    this.redsTurn = AiLogic.isRed;
    this.depth = 0;

    // determine iteration starting points
    if (this.redsTurn) {
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
    this.prev = state;
    // determine iteration starting point
    this.start = (state.start == 0) ? 6 : 0;
    this.end = (state.end == 5) ? 11 : 5;
    this.turn = 0;
    this.children = new LinkedList<>();
  }

  // Move durchspielen
  public void doMove(int field) {
    int index;
    int capturedBeans = this.field[field];
    this.field[field] = 0;
    for (int i = 1; i <= capturedBeans; i++) {
      index = (field + i) % 12;
      this.field[index]++;
    }
    while (true) {
      if (capturedBeans < 0) {
        break;
      }
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
        nextState.doMove(i);
        nextState.turn = i;
        boolean added = false;
        if (redsTurn) {
          for (int j = 0; j < possibleStates.size(); j++) {
            State s2 = possibleStates.get(j);
            if (s2.heuristic > nextState.heuristic) {
              possibleStates.add(j, nextState);
              added = true;
              break;
            }
          }
        } else {
          for (int j = 0; j < possibleStates.size(); j++) {
            State s2 = possibleStates.get(j);
            if (s2.heuristic < nextState.heuristic) {
              possibleStates.add(j, nextState);
              added = true;
              break;
            }
          }
        }
        if (!added) {
          possibleStates.add(nextState);
        }
      }
    }
    this.children = possibleStates;
    // If no possible turns left, give points to enemy recalc heuristic
    if (this.children.size() == 0) {
      for (int i = (this.start == 0) ? 6 : 0; i <= ((this.end == 5) ? 11 : 5); i++) {
        if (redsTurn) {
          this.bluePoints += this.field[i];
        } else {
          this.redPoints += this.field[i];
        }
      }
      calcHeuristic();
    }
    AiLogic.calculatedStates.put(this.hashCode(), this);
  }

  /** calculate Heuristic. */
  public void calcHeuristic() {

    if (this.bluePoints > 36) {
      this.heuristic = (!AiLogic.isRed) ? +1000 : -1000;
      return;
    } else if (this.redPoints > 36) {
      this.heuristic = (!AiLogic.isRed) ? -1000 : +1000;
      return;
    }

    if (AiLogic.isRed) {
      this.heuristic = (this.bluePoints - this.redPoints) * 3;
    } else {
      this.heuristic = (this.redPoints - this.bluePoints) * 3;
    }

    int max = 0;
    int index;
    int capturedbeans = 0;
    int help;
    for (int i = this.start; i < this.end; i++) {
      help = i;
      while (true) {
        if (help < 0) {
          break;
        }
        index = (this.field[i] + help) % 12;
        if (this.field[index] == 1 || this.field[index] == 3 || this.field[index] == 5) {
          capturedbeans = capturedbeans + this.field[index] + 1;
          help--;
        } else {
          break;
        }
      }
      if (capturedbeans > max) {
        max = capturedbeans;
      }
      capturedbeans = 0;
    }

    if (AiLogic.isRed && redsTurn || !AiLogic.isRed && !redsTurn) {
      this.heuristic = this.heuristic - max;
    } else {
      this.heuristic = this.heuristic + max;
    }

    /* - quadratische Varianz der Bohnen in eigenen Feldern nach Zug */
    double sum = 0;
    double average;

    for (int i = this.start; i <= this.end; i++) {
      sum = sum + this.field[i];
    }
    average = sum / 6;

    sum = 0;
    for (int i = this.start; i <= this.end; i++) {
      sum = sum + (int) Math.pow(average - this.field[i], 2);
    }
    double variance = sum / 6;
    this.heuristic =
        this.heuristic - (int) (variance * 0.1); /* müssen hier einen geeigneten Faktor wählen */
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
    s.field[0] = 0;
    s.field[1] = 2;
    s.field[2] = 5;
    s.field[3] = 1;
    s.field[4] = 4;
    s.field[5] = 16;
    s.field[6] = 0;
    s.field[7] = 0;
    s.field[8] = 12;
    s.field[9] = 3;
    s.field[10] = 13;
    s.field[11] = 0;
    s.bluePoints = 0;
    s.redPoints = 16;
    s.redsTurn = false;
    AiLogic.isRed = true;
    System.out.println(AiLogic.chooseTurn(s));
  }
}
