import java.util.Date;
import java.util.HashMap;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 11;

  // Don't change anything here
  static int bestTurn;
  static HashMap<Integer, State> calculatedStates = new HashMap<>();
  static boolean isRed;

  /**
   * calls the minimax function to determine the best move.
   *
   * @param state - given state for which we calc the minimax function
   * @return bestTurn
   */
  public static int chooseTurn(State state) {
    bestTurn = 0;
    Date start = new Date();
    double heuristic = minimax(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    System.out.println("Enscheidung fuer Zug: " + bestTurn);
    System.out.println("Zeit fuer Zug: " + (new Date().getTime() - start.getTime()) + "ms");
    System.out.println("Anzahl Kinder: " + state.children.size());
    System.out.println("Wert des \"besten\"Zuges: " + heuristic);
    return bestTurn;
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

    /* If state is already known then don't calculate children again
      This should save time if there are a lot of same states where we calculate
      the same children
      Doesn't work correctly so we leave this out for now

       State help = calculatedStates.get(state.hashCode());
       if (help == null) {
         state.expand();
       } else {
         help.turn = state.turn;
         help.depth = state.depth;
         for (int i = 0; i < help.children.size(); i++) {
           help.children.get(i).depth = help.depth + 1;
         }
         state = help;
       }
    */
    state.expand();
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

        /*if we are on depth 0 we determine which move to do this way
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
