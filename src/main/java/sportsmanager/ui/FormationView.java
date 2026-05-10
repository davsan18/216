package sportsmanager.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import sportsmanager.core.I18n;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Renders both teams' starting line-ups on a small pitch / court. */
final class FormationView {

    private FormationView() {}

    static Pane footballPitch(double w, double h, ITeam home, ITeam away,
                              List<IPlayer> homeOnField, List<IPlayer> awayOnField,
                              List<IPlayer> homeBench, List<IPlayer> awayBench) {
        Pane root = new Pane();
        root.setPrefSize(w, h);
        root.setMinSize(w, h);
        root.setMaxSize(w, h);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0d7a3a, #1ea84a); -fx-background-radius: 10;");

        Color line = Color.web("#ffffff", 0.8);
        Rectangle outer = new Rectangle(8, 8, w - 16, h - 16);
        outer.setFill(Color.TRANSPARENT); outer.setStroke(line); outer.setStrokeWidth(2);
        Line mid = new Line(w / 2, 8, w / 2, h - 8);
        mid.setStroke(line); mid.setStrokeWidth(2);
        Circle midC = new Circle(w / 2, h / 2, Math.min(w, h) * 0.10);
        midC.setFill(Color.TRANSPARENT); midC.setStroke(line); midC.setStrokeWidth(2);
        Rectangle pl = new Rectangle(8, h * 0.25, w * 0.14, h * 0.50);
        pl.setFill(Color.TRANSPARENT); pl.setStroke(line); pl.setStrokeWidth(2);
        Rectangle pr = new Rectangle(w - 8 - w * 0.14, h * 0.25, w * 0.14, h * 0.50);
        pr.setFill(Color.TRANSPARENT); pr.setStroke(line); pr.setStrokeWidth(2);
        root.getChildren().addAll(outer, mid, midC, pl, pr);

        placeFootballSide(root, w, h, homeOnField, true,  Color.web("#e74c3c"), home.getTactic());
        placeFootballSide(root, w, h, awayOnField, false, Color.web("#3498db"), away.getTactic());
        root.getChildren().add(benchBox(homeBench, 12, 28, true));
        root.getChildren().add(benchBox(awayBench, w - 132, 28, false));

        Label hLabel = new Label(home.getName());
        hLabel.setLayoutX(12); hLabel.setLayoutY(h - 22);
        hLabel.setPrefWidth(180);
        hLabel.setStyle(teamLabelStyle());
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 180); aLabel.setLayoutY(h - 22);
        aLabel.setPrefWidth(180);
        aLabel.setAlignment(Pos.CENTER_RIGHT);
        aLabel.setStyle(teamLabelStyle());
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    static Pane footballPitch(double w, double h, ITeam home, ITeam away,
                              List<IPlayer> homeOnField, List<IPlayer> awayOnField) {
        return footballPitch(w, h, home, away, homeOnField, awayOnField, new ArrayList<>(), new ArrayList<>());
    }

    private static String teamLabelStyle() {
        return "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 4, 0, 1, 1);";
    }

    private static void placeFootballSide(Pane root, double w, double h, List<IPlayer> players,
                                          boolean leftHalf, Color color, String tactic) {
        if (players == null || players.isEmpty()) return;
        List<IPlayer> gk = new ArrayList<>(), outfield = new ArrayList<>();
        for (IPlayer p : players) {
            String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
            if (pos.contains("kale") || pos.contains("goalkeeper") || pos.equals("gk")) gk.add(p);
            else outfield.add(p);
        }
        placeRowF(root, w, h, gk, leftHalf ? 0.08 : 0.92, color);

        int[] rows = parseFormation(tactic);
        int numRows = rows != null ? rows.length : 3;
        double offset = disciplineOffset(tactic, leftHalf);
        double[] xs = computeOutfieldXs(leftHalf, numRows, offset);

        if (rows != null) {
            outfield.sort(Comparator.comparingInt(FormationView::outfieldPositionOrder));
            int cursor = 0;
            for (int i = 0; i < rows.length && cursor < outfield.size(); i++) {
                int count = Math.min(rows[i], outfield.size() - cursor);
                placeRowF(root, w, h, outfield.subList(cursor, cursor + count), xs[i], color);
                cursor += count;
            }
            if (cursor < outfield.size()) {
                placeRowF(root, w, h, outfield.subList(cursor, outfield.size()), xs[xs.length - 1], color);
            }
        } else {
            List<IPlayer> def = new ArrayList<>(), mid = new ArrayList<>(), fwd = new ArrayList<>();
            for (IPlayer p : outfield) {
                String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
                if (pos.contains("defans") || pos.contains("bek") || pos.contains("stoper")) def.add(p);
                else if (pos.contains("forvet") || pos.contains("kanat") || pos.contains("santrafor")) fwd.add(p);
                else mid.add(p);
            }
            placeRowF(root, w, h, def, xs[0], color);
            placeRowF(root, w, h, mid, xs[numRows / 2], color);
            placeRowF(root, w, h, fwd, xs[numRows - 1], color);
        }
    }

    private static double[] computeOutfieldXs(boolean leftHalf, int numRows, double offset) {
        double start = leftHalf ? 0.20 : 0.80;
        double end   = leftHalf ? 0.48 : 0.52;
        double maxX  = leftHalf ? 0.48 : Double.MAX_VALUE;
        double minX  = leftHalf ? Double.MIN_VALUE : 0.52;
        double[] xs = new double[numRows];
        if (numRows == 1) {
            xs[0] = clampX((start + end) / 2.0 + offset, minX, maxX);
            return xs;
        }
        for (int i = 0; i < numRows; i++) {
            double t = (double) i / (numRows - 1);
            xs[i] = clampX(start + t * (end - start) + offset, minX, maxX);
        }
        return xs;
    }

    private static double clampX(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double disciplineOffset(String tactic, boolean leftHalf) {
        if (tactic == null || !tactic.contains("/")) return 0;
        String d = tactic.split("/")[1].trim().toLowerCase();
        if (d.contains("attack") || d.contains("hücum")) return leftHalf ?  0.04 : -0.04;
        if (d.contains("defensive") || d.contains("savun")) return leftHalf ? -0.04 :  0.04;
        return 0;
    }

    private static int[] parseFormation(String tactic) {
        if (tactic == null || tactic.isBlank()) return null;
        String formation = tactic.contains("/") ? tactic.split("/")[0].trim() : tactic.trim();
        String[] parts = formation.split("-");
        if (parts.length < 2) return null;
        try {
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) result[i] = Integer.parseInt(parts[i].trim());
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int outfieldPositionOrder(IPlayer p) {
        String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
        if (pos.contains("defans") || pos.contains("bek") || pos.contains("stoper")) return 0;
        if (pos.contains("forvet") || pos.contains("kanat") || pos.contains("santrafor")) return 2;
        return 1;
    }

    private static void placeRowF(Pane root, double w, double h, List<IPlayer> players, double xRatio, Color color) {
        int n = players.size();
        if (n == 0) return;
        double topMargin = 0.10, bottomMargin = 0.90;
        double range = bottomMargin - topMargin;
        for (int i = 0; i < n; i++) {
            double x = w * xRatio;
            double y = h * (topMargin + range * (i + 0.5) / n);
            root.getChildren().add(playerToken(players.get(i), x, y, color, w, h));
        }
    }

    static Pane volleyballCourtView(double w, double h, ITeam home, ITeam away,
                                    List<IPlayer> homeOnField, List<IPlayer> awayOnField) {
        Pane root = new Pane();
        root.setPrefSize(w, h);
        root.setMinSize(w, h);
        root.setMaxSize(w, h);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #d97a3c, #a85c2a); -fx-background-radius: 10;");

        Color line = Color.web("#ffffff", 0.85);
        Rectangle court = new Rectangle(w * 0.08, h * 0.12, w * 0.84, h * 0.76);
        court.setFill(Color.web("#e89a5a", 0.45));
        court.setStroke(line); court.setStrokeWidth(2);
        Line net = new Line(w / 2, h * 0.06, w / 2, h * 0.94);
        net.setStroke(Color.web("#1f1f1f", 0.85)); net.setStrokeWidth(5);
        Line al = new Line(w * (0.08 + 0.84 / 3.0), h * 0.12, w * (0.08 + 0.84 / 3.0), h * 0.88);
        Line ar = new Line(w * (0.08 + 0.84 * 2.0 / 3.0), h * 0.12, w * (0.08 + 0.84 * 2.0 / 3.0), h * 0.88);
        al.setStroke(line); ar.setStroke(line);
        al.setStrokeWidth(2); ar.setStrokeWidth(2);
        root.getChildren().addAll(court, net, al, ar);

        placeVolley(root, w, h, homeOnField, true,  Color.web("#e74c3c"));
        placeVolley(root, w, h, awayOnField, false, Color.web("#3498db"));

        Label hLabel = new Label(home.getName());
        hLabel.setLayoutX(12); hLabel.setLayoutY(h - 22);
        hLabel.setPrefWidth(180);
        hLabel.setStyle(teamLabelStyle());
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 180); aLabel.setLayoutY(h - 22);
        aLabel.setPrefWidth(180);
        aLabel.setAlignment(Pos.CENTER_RIGHT);
        aLabel.setStyle(teamLabelStyle());
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    private static void placeVolley(Pane root, double w, double h, List<IPlayer> players, boolean leftHalf, Color color) {
        if (players == null || players.isEmpty()) return;

        // Sort players into canonical volleyball roles
        List<IPlayer> setters = new ArrayList<>();
        List<IPlayer> opposites = new ArrayList<>();
        List<IPlayer> outsides = new ArrayList<>();
        List<IPlayer> middles = new ArrayList<>();
        List<IPlayer> liberos = new ArrayList<>();
        List<IPlayer> others = new ArrayList<>();
        for (IPlayer p : players) {
            String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
            if (pos.contains("libero")) liberos.add(p);
            else if (pos.contains("çapraz") || pos.contains("opposite") || pos.contains("karşı")) opposites.add(p);
            else if (pos.contains("pasör") || pos.contains("setter")) setters.add(p);
            else if (pos.contains("orta") || pos.contains("middle")) middles.add(p);
            else if (pos.contains("smaç") || pos.contains("spiker")) outsides.add(p);
            else others.add(p);
        }

        // Slots correspond to volleyball positions 1..6 (5-1 system layout):
        //   1 = setter (back-right)        4 = opposite (front-left)
        //   2 = outside hitter (front-rt)  5 = outside hitter (back-left)
        //   3 = middle blocker (front-ctr) 6 = libero / 2nd middle (back-center)
        IPlayer[] slots = new IPlayer[6];
        if (!setters.isEmpty())   slots[0] = setters.remove(0);
        if (!outsides.isEmpty())  slots[1] = outsides.remove(0);
        if (!middles.isEmpty())   slots[2] = middles.remove(0);
        if (!opposites.isEmpty()) slots[3] = opposites.remove(0);
        if (!outsides.isEmpty())  slots[4] = outsides.remove(0);
        if (!liberos.isEmpty())   slots[5] = liberos.remove(0);
        else if (!middles.isEmpty()) slots[5] = middles.remove(0);

        List<IPlayer> remaining = new ArrayList<>();
        remaining.addAll(setters);
        remaining.addAll(outsides);
        remaining.addAll(middles);
        remaining.addAll(opposites);
        remaining.addAll(liberos);
        remaining.addAll(others);
        for (int i = 0; i < 6 && !remaining.isEmpty(); i++) {
            if (slots[i] == null) slots[i] = remaining.remove(0);
        }

        double frontX = leftHalf ? w * 0.40 : w * 0.60;
        double backX  = leftHalf ? w * 0.18 : w * 0.82;
        double topY = h * 0.25, midY = h * 0.50, botY = h * 0.75;

        // Map canonical positions to (x,y). Right side mirrors top<->bottom so each team's
        // "back-right (pos 1)" sits closest to the visual bottom-back corner of its own half.
        double[][] coords = leftHalf
            ? new double[][] {
                { backX,  botY },  // 1 back-right
                { frontX, botY },  // 2 front-right
                { frontX, midY },  // 3 front-center
                { frontX, topY },  // 4 front-left
                { backX,  topY },  // 5 back-left
                { backX,  midY }   // 6 back-center
              }
            : new double[][] {
                { backX,  topY },  // 1 back-right (mirrored)
                { frontX, topY },  // 2 front-right
                { frontX, midY },  // 3 front-center
                { frontX, botY },  // 4 front-left
                { backX,  botY },  // 5 back-left
                { backX,  midY }   // 6 back-center
              };

        for (int i = 0; i < 6; i++) {
            if (slots[i] == null) continue;
            root.getChildren().add(playerToken(slots[i], coords[i][0], coords[i][1], color, w, h, true));
        }
    }

    private static javafx.scene.Node playerToken(IPlayer p, double x, double y, Color color,
                                                double boardWidth, double boardHeight) {
        return playerToken(p, x, y, color, boardWidth, boardHeight, false);
    }

    private static javafx.scene.Node playerToken(IPlayer p, double x, double y, Color color,
                                                double boardWidth, double boardHeight, boolean compact) {
        HBox badges = new HBox(2);
        badges.setAlignment(Pos.CENTER);
        if (p.getGoalsThisMatch() > 0) {
            String icon = compact ? "🏐" : "⚽";
            String goalText = compact
                    ? icon + p.getGoalsThisMatch()
                    : icon + String.join(",", p.getGoalMinutes());
            Label gl = new Label(goalText);
            gl.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;"
                    + " -fx-background-color: rgba(0,0,0,0.65); -fx-padding: 1 5; -fx-background-radius: 6;");
            badges.getChildren().add(gl);
        }
        if (p.getSubInClock() != null) {
            Label l = new Label("🔁" + p.getSubInClock());
            l.setStyle("-fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;"
                    + " -fx-background-color: #16a085; -fx-padding: 0 4; -fx-background-radius: 6;");
            badges.getChildren().add(l);
        }

        Circle dot = new Circle(12);
        dot.setFill(color);
        boolean showCardOutline = !compact && (p.hasRedCard() || p.getYellowCards() > 0);
        Color stroke = !compact && p.hasRedCard() ? Color.web("#c0392b")
                : (!compact && p.getYellowCards() > 0 ? Color.web("#f1c40f") : Color.WHITE);
        dot.setStroke(stroke);
        dot.setStrokeWidth(showCardOutline ? 4 : 2);
        Label num = new Label(String.valueOf(p.getJerseyNumber() > 0 ? p.getJerseyNumber() : p.getSkillLevel()));
        num.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
        StackPane dotPane = new StackPane(dot, num);

        Label name = new Label(p.getName());
        name.setMaxWidth(86);
        name.setAlignment(Pos.CENTER);
        name.setWrapText(true);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 4, 0, 0, 1);");

        VBox col = new VBox(1, badges, dotPane, name);
        col.setAlignment(Pos.CENTER);
        // IMPORTANT: layoutX/Y must be set on the Node returned (which is the child of the parent Pane).
        double tokenW = 90;
        double tokenH = 58;
        col.setLayoutX(clamp(x - tokenW / 2.0, 4, boardWidth - tokenW - 4));
        col.setLayoutY(clamp(y - tokenH / 2.0, 4, boardHeight - tokenH - 28));
        col.setPrefSize(tokenW, tokenH);
        col.setMouseTransparent(true);
        return col;
    }

    private static double clamp(double value, double min, double max) {
        if (max < min) return min;
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static VBox benchBox(List<IPlayer> bench, double x, double y, boolean left) {
        List<IPlayer> orderedBench = orderedFootballBench(bench);
        VBox box = new VBox(2);
        box.setLayoutX(x);
        box.setLayoutY(y);
        box.setPrefWidth(120);
        box.setMaxWidth(120);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.48); -fx-background-radius: 8;"
                + " -fx-padding: 5; -fx-border-color: rgba(255,255,255,0.35); -fx-border-radius: 8;");
        Label title = new Label(I18n.t("mv.bench"));
        title.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        box.getChildren().add(title);
        int limit = Math.min(orderedBench.size(), 7);
        for (int i = 0; i < limit; i++) {
            IPlayer p = orderedBench.get(i);
            Label l = new Label("#" + p.getJerseyNumber() + " " + p.getName());
            l.setMaxWidth(110);
            l.setAlignment(left ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            l.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 9px; -fx-font-weight: bold;");
            box.getChildren().add(l);
        }
        return box;
    }

    private static List<IPlayer> orderedFootballBench(List<IPlayer> bench) {
        List<IPlayer> ordered = new ArrayList<>(bench == null ? List.of() : bench);
        ordered.sort(Comparator
                .comparingInt(FormationView::footballPositionOrder)
                .thenComparingInt(IPlayer::getJerseyNumber)
                .thenComparing(IPlayer::getName));
        return ordered;
    }

    private static int footballPositionOrder(IPlayer player) {
        String pos = player.getPosition() == null ? "" : player.getPosition().toLowerCase();
        if (pos.contains("kale") || pos.contains("goalkeeper") || pos.equals("gk")) return 0;
        if (pos.contains("defans") || pos.contains("bek") || pos.contains("stoper")
                || pos.contains("defender")) return 1;
        if (pos.contains("orta") || pos.contains("midfield")) return 2;
        if (pos.contains("forvet") || pos.contains("kanat") || pos.contains("santrafor")
                || pos.contains("forward") || pos.contains("striker")) return 3;
        return 4;
    }
}
