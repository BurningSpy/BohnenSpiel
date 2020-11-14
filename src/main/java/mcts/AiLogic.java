package mcts;

import java.util.Date;
import java.util.LinkedList;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 100;
  static double varianceFactor = 0.4;
  static double oddBeansFactor = 1;
  static double cValue = Math.sqrt(2);

  // Don't change anything here
  static int bestTurn;
  static boolean isRed;
  static Date start;

  /**
   * calls the minimax function to determine the best move.
   *
   * @param state - given state for which we calc the minimax function
   * @return bestTurn
   */
  public static int chooseTurn(State state) {
    start = new Date();
    bestTurn = monteCarlo(state);
    System.out.println("Time for turn: " + (new Date().getTime() - start.getTime()) + "ms");

    return bestTurn;
  }

  /**
   * calculates the monte carlo tree search algorithm.
   *
   * @param state given state to determine best turn from
   * @return best turn to play
   */
  public static int monteCarlo(State state) {
    while (new Date().getTime() - start.getTime() < 2500) {
      State selectedState = selection(state);
      State newState = expansion(selectedState);
      int result = simulation(newState);
      backPropagation(result, newState);
    }
    return pickWinnerByPercentage(state);
  }

  /**
   * selects which state to to expand next.
   *
   * @param state the root state from which we start our calculations
   * @return the state is going to be expanded next
   */
  public static State selection(State state) {
    if (state.children.size() < state.possibleChildren) {
      return state;
    }
    State selectedState = state;
    do {
      double maxUtc = 0;
      int bestKid = 0;
      int i = 0;
      for (State kiddo : selectedState.children) {
        double np = kiddo.prev.winsBlue + kiddo.prev.winsRed;
        double nk = kiddo.winsRed + kiddo.winsBlue;
        double vk = kiddo.redsTurn ? kiddo.winsRed / nk : kiddo.winsBlue / nk;
        double utcValue = vk + ((np != 0) ? cValue * Math.sqrt(Math.log(np) / nk) : 0);
        if (utcValue > maxUtc) {
          bestKid = i;
          maxUtc = utcValue;
        }
        i++;
      }
      if (selectedState.children.size() > 0) {
        selectedState = selectedState.children.get(bestKid);
      } else {
        break;
      }
    } while (selectedState.children.size() == selectedState.possibleChildren);
    return selectedState;
  }

  /**
   * expands the given state.
   *
   * @param state the state to expand
   * @return the expanded state
   */
  public static State expansion(State state) {
    if (state.children.size() == state.possibleChildren) {
      return state;
    }
    LinkedList<Integer> possibleMoves = new LinkedList<>();
    for (int i = state.start; i <= state.end; i++) {
      if (state.field[i] != 0) {
        possibleMoves.add(i);
      }
    }
    for (State existingKiddo : state.children) {
      possibleMoves.removeFirstOccurrence(existingKiddo.turn);
    }
    State newChild = new State(state);
    int randomTurn = (int) (Math.random() * possibleMoves.size());
    newChild.doMove(possibleMoves.get(randomTurn));
    state.children.add(newChild);
    return newChild;
  }

  /**
   * simulates a game from the given state.
   *
   * @param state the state from which to simulate from
   * @return +1 if won, -1 if loss and 0 if draw
   */
  public static int simulation(State state) {
    if (state.possibleChildren == 0) {
      state.calcHeuristic();
      return (state.heuristic > 0 ? 1 : state.heuristic == 0 ? 0 : -1);
    }
    State help = new State(state);
    help.depth--;
    help.redsTurn = !help.redsTurn;
    help.prev = state.prev;
    help.start = (help.start == 0) ? 6 : 0;
    help.end = (help.end == 5) ? 11 : 5;

    while (true) {
      if ((help.redPoints > 36 && isRed) || (help.bluePoints > 36 && !isRed)) {
        return 1;
      } else if ((help.redPoints > 36 && !isRed) || (help.bluePoints > 36 && isRed)) {
        return -1;
      } else if (help.redPoints == 36 && help.bluePoints == 36) {
        return 0;
      } else if (help.depth >= maxDepth) {
        help.calcHeuristic();
        if(help.redPoints > help.bluePoints && isRed || help.redPoints < help.bluePoints && !isRed){
          return 1;
        } else if (help.bluePoints > help.redPoints && isRed || help.redPoints > help.bluePoints && !isRed){
          return -1;
        }else {
          return 0;
        }
      }
      LinkedList<Integer> possibleMoves = new LinkedList<>();
      for (int i = help.start; i <= help.end; i++) {
        if (help.field[i] != 0) {
          possibleMoves.add(i);
        }
      }
      if (possibleMoves.size() == 0) {
        help.calcHeuristic();
        return (help.heuristic > 0 ? 1 : help.heuristic == 0 ? 0 : -1);
      }
      int randomTurn = (int) (Math.random() * possibleMoves.size());
      help.doMove(possibleMoves.get(randomTurn));
      help = new State(help);
    }
  }

  /**
   * propagates the result of the simulation back to the root.
   *
   * @param result +1 if won, -1 if loss and 0 if draw
   * @param state the state from where the back propagation starts
   */
  public static void backPropagation(int result, State state) {
    boolean redWin = isRed && result == 1 || !isRed && result == -1 || result == 0;
    boolean blueWin = isRed && result == -1 || !isRed && result == 1 || result == 0;
    do {
      state.winsRed += redWin ? 1 : 0;
      state.winsBlue += blueWin ? 1 : 0;
      state = state.prev;
    } while (state != null);
  }

  /**
   * chooses the best turn to play depending on the amount of games at each state.
   *
   * @param state the root node from where we pick the best turn
   * @return number of field to play
   */
  public static int pickWinner(State state) {
    double max = 0;
    int winner = 0;
    for (int i = 0; i < state.children.size(); i++) {
      int playedGamesAtNode = state.children.get(i).winsBlue + state.children.get(i).winsRed;
      if (playedGamesAtNode > max) {
        max = playedGamesAtNode;
        winner = state.children.get(i).turn;
      }
    }
    return winner;
  }
  public static int pickWinnerByPercentage(State state) {
    double winPercent = 0;
    int winner = 0;
    for (int i = 0; i < state.children.size(); i++) {
      State child = state.children.get(i);
      double redPercentage = child.winsRed / (child.winsRed + child.winsBlue);
      double currentPercent = (isRed) ? redPercentage : (1-redPercentage);
      if (currentPercent > winPercent) {
        winPercent = currentPercent;
        winner = state.children.get(i).turn;
      }
    }
    return winner;
  }
}
