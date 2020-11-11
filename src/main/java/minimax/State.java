package minimax;

import java.util.Arrays;
import java.util.LinkedList;

public class State {
  int[] field;
  int redPoints;
  int bluePoints;
  int turn;
  int start;
  int end;
  double heuristic;
  boolean redsTurn;
  boolean gameOver;
  State prev;
  LinkedList<State> children = new LinkedList<>();

  /** initializes the very first game State. */
  public State() {
    this.field = new int[12];
    Arrays.fill(this.field, 6);
    this.bluePoints = this.redPoints = 0;
    this.redsTurn = AiLogic.isRed;
    this.gameOver = false;

    // determine iteration starting points
    if (this.redsTurn) {
      start = 0;
      end = 5;
    } else {
      start = 6;
      end = 11;
    }
  }

  /**
   * creates a new state and copies the attributes via the given parameter.
   *
   * @param state the state from which to copies the attributes from
   */
  public State(State state) {
    this.field = Arrays.copyOf(state.field, 12);
    this.bluePoints = state.bluePoints;
    this.redPoints = state.redPoints;
    this.redsTurn = !state.redsTurn;
    this.heuristic = state.heuristic;
    this.prev = state;
    this.gameOver = state.gameOver;
    // determine iteration starting point
    this.start = (state.start == 0) ? 6 : 0;
    this.end = (state.end == 5) ? 11 : 5;
    this.turn = 0;
    this.children = new LinkedList<>();
  }

  /**
   * plays out a single move on the state instance.
   *
   * @param field which move to play
   */
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
          this.redPoints += this.field[index];
        } else {
          this.bluePoints += this.field[index];
        }
        this.field[index] = 0;
        capturedBeans--;
      } else {
        break;
      }
    }
    if (this.redPoints + this.bluePoints > 72) {
      System.out.println("WTF");
    }
    calcHeuristic();
  }

  /** creates all children nodes of the state instance. */
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
    // If no possible turns left, give points to other player
    if (this.children.size() == 0) {
      for (int i = (this.start == 0) ? 6 : 0; i <= ((this.end == 5) ? 11 : 5); i++) {
        if (redsTurn) {
          this.bluePoints += this.field[i];
        } else {
          this.redPoints += this.field[i];
        }
      }
      this.gameOver = true;
      calcHeuristic();
    }
    AiLogic.calculatedStates.put(this.hashCode(), this);
  }

  /** calculate Heuristic. */
  public void calcHeuristic() {
    // checks if the game is over and we can have definite values of loss/win
    if (gameOver) {
      if (this.bluePoints > this.redPoints) {
        this.heuristic = AiLogic.isRed ? -1000 : 1000;
        return;
      } else if (this.redPoints > this.bluePoints) {
        this.heuristic = AiLogic.isRed ? 1000 : -1000;
        return;
      } else {
        this.heuristic = 0;
        return;
      }
    }

    // adds values of the already won points to the heuristic
    if (AiLogic.isRed) {
      this.heuristic = (this.bluePoints - this.redPoints);
    } else {
      this.heuristic = (this.redPoints - this.bluePoints);
    }

    // gives points depending on the amount of 1,3 or 5 fields after a turn
    int max = 0;
    int index;
    int capturedBeans = 0;
    int help;
    for (int i = this.start; i < this.end; i++) {
      help = i;
      while (true) {
        if (help < 0) {
          break;
        }
        index = (this.field[i] + help) % 12;
        if (this.field[index] == 1 || this.field[index] == 3 || this.field[index] == 5) {
          capturedBeans += this.field[index] + 1;
          help--;
        } else {
          break;
        }
      }
      if (capturedBeans > max) {
        max = capturedBeans;
      }
      capturedBeans = 0;
    }
    if (AiLogic.isRed && redsTurn || !AiLogic.isRed && !redsTurn) {
      this.heuristic -= AiLogic.oddBeansFactor * max;
    } else {
      this.heuristic += AiLogic.oddBeansFactor * max;
    }

    /* - squared variance of own fields after turn */
    double sum = 0;
    double average;
    for (int i = this.start; i <= this.end; i++) {
      sum += this.field[i];
    }
    average = sum / 6;
    sum = 0;
    for (int i = this.start; i <= this.end; i++) {
      sum += (int) Math.pow(average - this.field[i], 2);
    }
    double variance = sum / 6;
    this.heuristic -= (int) (variance * AiLogic.varianceFactor);
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
}
