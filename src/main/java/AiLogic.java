import java.util.Date;
import java.util.HashMap;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 18;

  // Don't change anything here
  static int bestTurn;
  static HashMap<Integer, State> calculatedStates = new HashMap<>();

  public static int chooseTurn(State state) {
    bestTurn = 0;
    Date start = new Date();
    minimax(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    System.out.println("Enscheidung fuer Zug: " + bestTurn);
    System.out.println("Zeit fuer Zug: " + (new Date().getTime() - start.getTime()) + "ms");
    System.out.println("Anzahl Kinder: " + state.children.size());
    return bestTurn;
  }

  // minimax with alpha-beta-pruning
  public static int minimax(State state, int depth, double alpha, double beta) {
    if (depth >= maxDepth) {
      return state.heuristic;
    }

    // if state is already known then don't calculate children again
    State help = calculatedStates.get(state.hashCode());
    if (help == null) {
      state.expand();
    } else {
      help.turn = state.turn;
      help.depth = state.depth;
      for (int i = 0; i < state.children.size(); i++) {
        help.children.get(i).depth = help.depth + 1;
      }
      state = help;
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
        int value = minimax(state.children.get(i), depth + 1, alpha, beta);
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
    }
    // uneven depth means MinPlayer
    else {
      bestValue = Double.POSITIVE_INFINITY;
      for (State s : state.children) {
        int value = minimax(s, depth + 1, alpha, beta);
        bestValue = Math.min(bestValue, value);
        beta = Math.min(beta, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
    }
    return (int) bestValue;
  }

  // logic for starvation

  // logic for early game

  public static void main(String[] args) {
    State test = new State();
    int turn = chooseTurn(test);
    System.out.println(turn);
  }
}
