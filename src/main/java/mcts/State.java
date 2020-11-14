package mcts;

import java.util.Arrays;
import java.util.LinkedList;

public class State {
  int[] field;
  int redPoints;
  int bluePoints;
  int turn;
  int start;
  int end;
  int depth;
  double heuristic;
  boolean redsTurn;
  boolean gameOver;
  int winsRed;
  int winsBlue;
  State prev;
  LinkedList<State> children = new LinkedList<>();
  int possibleChildren;

  /** initializes the very first game State. */
  public State() {
    this.field = new int[12];
    Arrays.fill(this.field, 6);
    this.bluePoints = this.redPoints = 0;
    this.redsTurn = AiLogic.isRed;
    this.gameOver = false;
    this.possibleChildren = 6;
    this.depth = 0;
    this.winsRed = 0;
    this.winsBlue = 0;
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
    this.depth = state.depth + 1;
    this.children = new LinkedList<>();
    this.winsRed = 0;
    this.winsBlue = 0;
    this.possibleChildren = 0;
  }

  /**
   * plays out a single move on the state instance.
   *
   * @param move which move to play
   */
  public void doMove(int move) {
    // if no moves available give points to enemy
    this.turn = move;
    boolean fieldEmtpy = true;
    for (int i = start; i <= end; i++) {
      if (this.field[i] != 0) {
        fieldEmtpy = false;
      }
    }
    if (fieldEmtpy) {
      for (int i = start; i <= end; i++) {
        if (redsTurn) {
          bluePoints += this.field[i];
        } else {
          redPoints += this.field[i];
        }
      }
      return;
    }
    int index;
    int capturedBeans = this.field[move];
    this.field[move] = 0;
    for (int i = 1; i <= capturedBeans; i++) {
      index = (move + i) % 12;
      this.field[index]++;
    }
    while (true) {
      if (capturedBeans < 0) {
        break;
      }
      index = (move + capturedBeans) % 12;
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
    setMaxChildren();
  }

  /** calculates how many children a state can possibly have. */
  public void setMaxChildren() {
    this.possibleChildren = 0;
    for (int i = this.start; i <= this.end; i++) {
      if (this.field[i] != 0) {
        this.possibleChildren++;
      }
    }
  }

  /** calculate Heuristic. */
  public void calcHeuristic() {
    // if (gameOver) {
    if (this.bluePoints > this.redPoints) {
      this.heuristic = AiLogic.isRed ? -1000 : 1000;
      return;
    } else if (this.redPoints > this.bluePoints) {
      this.heuristic = AiLogic.isRed ? 1000 : -1000;
      return;
    }
    // else {
    //  this.heuristic = 0;
    //  return;
    // }
    // }
    if (AiLogic.isRed) {
      this.heuristic = (this.bluePoints - this.redPoints);
    } else {
      this.heuristic = (this.redPoints - this.bluePoints);
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
          capturedbeans += this.field[index] + 1;
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
      this.heuristic -= AiLogic.oddBeansFactor * max;
    } else {
      this.heuristic += AiLogic.oddBeansFactor * max;
    }

    /* - quadratische Varianz der Bohnen in eigenen Feldern nach Zug */
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
