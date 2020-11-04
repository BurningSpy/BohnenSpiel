import java.util.Date;
import java.util.LinkedList;

public class AiLogic {

  public static int chooseTurn(State state) {
    Date start = new Date();
    LinkedList<State> newStates = state.expand();
    int turnToPlay = minimax();

    System.out.println(new Date().getTime() - start.getTime());
    return turnToPlay;
  }

  // minmax with alpha-beta-pruning
  public static int minimax() {






    return 0;
  }

  // logic for starvation

  // logic for early game

}
