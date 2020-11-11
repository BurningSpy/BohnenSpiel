package mcts;

import java.util.Date;
import java.util.LinkedList;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 100;
  static double varianceFactor = 0.1;
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
    // System.out.println("Enscheidung fuer Zug: " + bestTurn);
    System.out.println("Zeit fuer Zug: " + (new Date().getTime() - start.getTime()) + "ms");
    // System.out.println("Anzahl Kinder: " + state.children.size());
    // System.out.println("Wert des \"besten\"Zuges: " + heuristic);
    return bestTurn;
  }

  /**
   * calculates the monte carlo tree search algorithm
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
    return pickWinner(state);
  }

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
        double utcValue = vk + cValue * Math.sqrt(Math.log(np) / nk);
        if (utcValue > maxUtc) {
          bestKid = i;
          maxUtc = utcValue;
        }
        i++;
      }
      selectedState = state.children.get(bestKid);
    } while (selectedState.children.size() == selectedState.possibleChildren);
    return selectedState;
  }

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

  public static int simulation(State state) {
    if (state.possibleChildren == 0) {
      state.calcHeuristic();
      return (state.heuristic > 0 ? 1 : state.heuristic == 0 ? 0 : -1);
    }
    State help = new State(state);
    while (true) {
      if ((help.redPoints > 36 && isRed) || (help.bluePoints > 36 && !isRed)) {
        return 1;
      } else if ((help.redPoints > 36 && !isRed) || (help.bluePoints > 36 && isRed)) {
        return -1;
      } else if (help.redPoints == 36 && help.bluePoints == 36) {
        return 0;
      } else if (help.depth >= maxDepth) {
        help.calcHeuristic();
        return (state.heuristic > 0 ? 1 : state.heuristic == 0 ? 0 : -1);
      }
      LinkedList<Integer> possibleMoves = new LinkedList<>();
      for (int i = state.start; i <= state.end; i++) {
        if (state.field[i] != 0) {
          possibleMoves.add(i);
        }
      }
      if (possibleMoves.size() == 0) {
        help.calcHeuristic();
        return (state.heuristic > 0 ? 1 : state.heuristic == 0 ? 0 : -1);
      }
      State newChild = new State(state);
      int randomTurn = (int) (Math.random() * possibleMoves.size());
      newChild.doMove(possibleMoves.get(randomTurn));
    }
  }

  public static void backPropagation(int result, State state) {
    boolean redWin = isRed && result == 1 || !isRed && result == -1 || result == 0;
    boolean blueWin = isRed && result == -1 || !isRed && result == 1 || result == 0;
    do {
      state.winsRed += redWin ? 1 : 0;
      state.winsBlue += blueWin ? 1 : 0;
      state = state.prev;
    } while (state.prev != null);
  }

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
}
