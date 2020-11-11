package mcts;

import java.util.Date;
import java.util.HashMap;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 100;
  static double varianceFactor = 0.1;
  static double oddBeansFactor = 1;

  // Don't change anything here
  static int bestTurn;
  static HashMap<Integer, State> calculatedStates = new HashMap<>();
  static boolean isRed;
  static Date start;

  /**
   * calls the minimax function to determine the best move.
   *
   * @param state - given state for which we calc the minimax function
   * @return bestTurn
   */
  public static int chooseTurn(State state) {
    bestTurn = 0;
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
    return pickWinner();
  }

  public static State selection(State state) {
    return null;
  }

  public static State expansion(State state) {
    return null;
  }

  public static int simulation(State state) {
    return 0;
  }

  public static void backPropagation(int result, State state) {}

  public static int pickWinner() {
    return 0;
  }
}
