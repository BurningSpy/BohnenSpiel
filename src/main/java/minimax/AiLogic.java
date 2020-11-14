package minimax;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int laterRoundsDepth = 12;
  static int firstRoundDepth = 10;
  static int dfsDepth = 13;

  static double varianceFactor = 0.4;
  static double oddBeansFactor = 1;
  static int msToKeepCalculating = 2600;

  // Don't change anything here
  static boolean firstRound = true;
  static int bestTurn;
  static HashMap<Integer, State> calculatedStates = new HashMap<>();
  static boolean isRed;
  static int maxDepth;
  static LinkedList<State> dfs = new LinkedList<>();
  static long start;

  /**
   * calls the minimax function to determine the best move.
   *
   * @param state - given state for which we calc the minimax function
   * @return bestTurn
   */
  public static int chooseTurn(State state) {
    bestTurn = 0;
    state.prev = null;
    start = new Date().getTime();
    if (firstRound) {
      maxDepth = firstRoundDepth;
      firstRound = false;
    } else {
      maxDepth = laterRoundsDepth;
    }
    minimax(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    System.out.println("Time for picking turn: " + (new Date().getTime() - start) + "ms");

    keepExpanding(state);

    System.out.println("Time after more expanding: " + (new Date().getTime() - start) + "ms");
    return bestTurn;
  }

  /**
   * method used to keep expanding after we figured out what turn to play. this will make us be able
   * to utilize our time better and calculate overall more states, because the first turn usually is
   * the one with the most cost as we have not seen any states at that point, but in later states we
   * would otherwise waste precious calculation time on our turn.
   *
   * @param state the state to expand
   */
  public static void keepExpanding(State state) {
    if (!state.gameOver && state.children.size() == 0) {
      state.expand();
    }
    if (state.depth <= dfsDepth) {
      for (State child : state.children) {
        dfs.push(child);
        // check to see if we still have time left before we need to send our turn to the server
        if (new Date().getTime() - start < msToKeepCalculating) {
          keepExpanding(dfs.removeFirst());
        }
      }
    }
  }

  /**
   * calculates the minimax algorithm with alpha-beta-pruning according to pseudo-code at
   * https://en.wikipedia.org/wiki/Alpha-beta_pruning
   *
   * @param state given state to determine min/max value of
   * @param depth depth of the calculation. determines if we should do min or max
   * @param alpha value for pruning
   * @param beta value for pruning
   * @return best Heuristic value for the Min/Max Node at given depth
   */
  public static double minimax(State state, int depth, double alpha, double beta) {
    if (depth >= maxDepth) {
      return state.heuristic;
    }
    // only expand if this state hasn't already been expanded before
    if (state.children.size() == 0) {
      state.expand();
    }
    // if this is true then we have no possible move on this state and the game would end
    if (state.children.size() == 0) {
      return state.heuristic;
    }

    // even depth means MaxPlayer
    double bestValue;
    if (depth % 2 == 0) {
      bestValue = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < state.children.size(); i++) {
        double value = minimax(state.children.get(i), depth + 1, alpha, beta);

        /*if we are on depth 0 we determine which move to do this way:
         whatever is the highest value getting back up from the recursion calls
         determines which of the up to 6 moves is the best
         Don't have to do this in the Min-Part because Depth 0 always is for Max-Player
        */
        if (depth == 0) {

          if (value > bestValue) {
            bestTurn = state.children.get(i).turn;
          }
        }

        bestValue = Math.max(bestValue, value);
        alpha = Math.max(alpha, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
    } else { // uneven depth means MinPlayer
      bestValue = Double.POSITIVE_INFINITY;
      for (State s : state.children) {
        double value = minimax(s, depth + 1, alpha, beta);
        bestValue = Math.min(bestValue, value);
        beta = Math.min(beta, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
    }
    return (int) bestValue;
  }
}
