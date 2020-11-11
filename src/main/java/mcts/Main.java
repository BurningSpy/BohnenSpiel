package mcts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;

public class Main {
  // static String server = "http://127.0.0.1:5000";
  static String server = "http://bohnenspiel.informatik.uni-mannheim.de";

  /*
   Bonjwa:
    Seine Ursprünge liegen im asiatischen Raum. Insbesondere der Buddhismus prägte diesen Begriff.
    So kann sich eine Person laut der Religion einen Bonjwa nennen,
    wenn diese eine bestimmte Stufe der Selbsterkenntnis erreicht hat.
    Im modernen Zeitalter wird dieser Ausdruck in Korea für bemerkenswerte Leistungen verwendet[...]
  */
  static String name = "Mr. Anderson";

  static int p1 = 0; // Rot
  static int p2 = 0; // Blau

  /** entry point for the program. */
  public static void main(String[] args) throws Exception {
    // System.out.println(load(server));
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Enter 'join', 'create' or 'open'");
    String s = br.readLine();
    switch (s) {
      default:
        System.out.println("Wrong input. Closing program.");
        return;
      case "join":
        System.out.print("Enter Game-ID: ");
        s = br.readLine();
        joinGame(s);
        break;
      case "create":
        createGame();
        break;
      case "open":
        openGames();
    }
  }

  @SuppressWarnings("all")
  static void createGame() throws Exception {
    String url = server + "/api/creategame/" + name;
    String gameId = load(url);
    System.out.println("Spiel erstellt. ID: " + gameId);

    url = server + "/api/check/" + gameId + "/" + name;
    while (true) {
      Thread.sleep(3000);
      String state = load(url);
      System.out.print("." + " (" + state + ")");
      if (state.equals("0") || state.equals("-1")) {
        break;
      } else if (state.equals("-2")) {
        System.out.println("time out");
        return;
      }
    }
    play(gameId, 0);
  }

  static void openGames() throws Exception {
    String url = server + "/api/opengames";
    String[] openGames = load(url).split(";");
    for (String openGame : openGames) {
      System.out.println(openGame);
    }
  }

  static void joinGame(String gameId) throws Exception {
    String url = server + "/api/joingame/" + gameId + "/" + name;
    String state = load(url);
    System.out.println("Join-Game-State: " + state);
    if (state.equals("1")) {
      play(gameId, 6);
    } else if (state.equals("0")) {
      System.out.println("error (join game)");
    }
  }

  @SuppressWarnings("all")
  static void play(String gameId, int offset) throws Exception {
    String checkUrl = server + "/api/check/" + gameId + "/" + name;
    String statesMsgUrl = server + "/api/statemsg/" + gameId;
    String stateIdUrl = server + "/api/state/" + gameId;
    int[] board = {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6}; // position 1-12
    int start;
    int end;
    if (offset == 0) {
      start = 7;
      end = 12;
    } else {
      start = 1;
      end = 6;
    }
    AiLogic.isRed = (offset == 0) ? true : false;
    State currentState;

    while (true) {
      Thread.sleep(50);
      int moveState = Integer.parseInt(load(checkUrl));
      int stateId = Integer.parseInt(load(stateIdUrl));
      if (stateId != 2 && ((start <= moveState && moveState <= end) || moveState == -1)) {
        if (moveState != -1) {
          int selectedField = moveState - 1;
          updateBoard(board, selectedField);
          System.out.println("Gegner waehlte: " + moveState + " /\t" + p1 + " - " + p2);
          System.out.println(printBoard(board) + "\n");
        }
        // calculate fieldID
        // System.out.println("Finde Zahl: ");
        currentState = new State();
        currentState.field = Arrays.copyOf(board, 12);
        currentState.redPoints = p1;
        currentState.bluePoints = p2;

        int selectField;
        do {
          selectField = AiLogic.chooseTurn(currentState);
          // System.out.println("\t-> " + selectField );
        } while (board[selectField] == 0);

        updateBoard(board, selectField);
        System.out.println("Waehle Feld: " + (selectField + 1) + " /\t" + p1 + " - " + p2);
        System.out.println(printBoard(board) + "\n\n");

        move(gameId, selectField + 1);
      } else if (moveState == -2 || stateId == 2) {
        System.out.println("GAME Finished");
        checkUrl = server + "/api/statemsg/" + gameId;
        System.out.println(load(checkUrl));
        return;
      } else {
        System.out.println("- " + moveState + "\t\t" + load(statesMsgUrl));
      }
    }
  }

  static void updateBoard(int[] board, int field) {
    int startField = field;

    int value = board[field];
    board[field] = 0;
    while (value > 0) {
      field = (++field) % 12;
      board[field]++;
      value--;
    }

    if (board[field] == 2 || board[field] == 4 || board[field] == 6) {
      do {
        if (startField < 6) {
          p1 += board[field];
        } else {
          p2 += board[field];
        }
        board[field] = 0;
        field = (field == 0) ? 11 : --field;
      } while (board[field] == 2 || board[field] == 4 || board[field] == 6);
    }
  }

  static String printBoard(int[] board) {
    StringBuilder s = new StringBuilder();
    for (int i = 11; i >= 6; i--) {
      if (i != 6) {
        s.append(board[i]).append("; ");
      } else {
        s.append(board[i]);
      }
    }

    s.append("\n");
    for (int i = 0; i <= 5; i++) {
      if (i != 5) {
        s.append(board[i]).append("; ");
      } else {
        s.append(board[i]);
      }
    }

    return s.toString();
  }

  static void move(String gameId, int fieldId) throws Exception {
    String url = server + "/api/move/" + gameId + "/" + name + "/" + fieldId;
    System.out.println(load(url));
  }

  static String load(String url) throws Exception {
    URI uri = new URI(url.replace(" ", ""));
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      sb.append(line);
    }
    bufferedReader.close();
    return (sb.toString());
  }
}
