import java.util.Date;

public class AiLogic {
  // change values here to make AI better-slower/worse-faster
  static int maxDepth = 10;

  public static int chooseTurn(State state) {
    Date start = new Date();
    int turnToPlay = minimax(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    System.out.println(new Date().getTime() - start.getTime());
    return turnToPlay;
  }

  // minimax with alpha-beta-pruning
  public static int minimax(State state, int depth, double alpha, double beta) {
    if (depth >= maxDepth) {
      return state.heuristik;
    }
    // even depth means MaxPlayer
    if (depth % 2 == 0) {
      double bestValue = Double.NEGATIVE_INFINITY;
      state.expand();
      for (State s : state.children) {
        int value = minimax(s, depth + 1, alpha, beta);
        bestValue = Math.max(bestValue, value);
        alpha = Math.max(alpha, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
      return (int) bestValue;
    }
    // uneven depth means MinPlayer
    else {
      double bestValue = Double.POSITIVE_INFINITY;
      state.expand();
      for (State s : state.children) {
        int value = minimax(s, depth + 1, alpha, beta);
        bestValue = Math.min(bestValue, value);
        beta = Math.min(beta, bestValue);
        if (beta <= alpha) {
          break;
        }
      }
      return (int) bestValue;
    }
  }

  // logic for starvation

  // logic for early game

  public static void main(String[] args) {
    State test = new State();
    int turn = chooseTurn(test);
    System.out.println(turn);
  }
}
