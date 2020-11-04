import java.util.Date;

public class AiLogic {

  public static int chooseTurn() {
    Date start = new Date();
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
