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
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.List;

/** Renders both teams' starting line-ups on a small pitch / court. */
final class FormationView {

    private FormationView() {}

    static Pane footballPitch(double w, double h, ITeam home, ITeam away,
                              List<IPlayer> homeOnField, List<IPlayer> awayOnField) {
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

        placeFootballSide(root, w, h, homeOnField, true,  Color.web("#e74c3c"));
        placeFootballSide(root, w, h, awayOnField, false, Color.web("#3498db"));

        Label hLabel = new Label(home.getName());
        hLabel.setLayoutX(12); hLabel.setLayoutY(h - 22);
        hLabel.setStyle(teamLabelStyle());
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 140); aLabel.setLayoutY(h - 22);
        aLabel.setAlignment(Pos.CENTER_RIGHT);
        aLabel.setStyle(teamLabelStyle());
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    private static String teamLabelStyle() {
        return "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 4, 0, 1, 1);";
    }

    private static void placeFootballSide(Pane root, double w, double h, List<IPlayer> players,
                                          boolean leftHalf, Color color) {
        if (players == null || players.isEmpty()) return;
        List<IPlayer> gk = new ArrayList<>(), def = new ArrayList<>(), mid = new ArrayList<>(), fwd = new ArrayList<>();
        for (IPlayer p : players) {
            String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
            if (pos.contains("kale")) gk.add(p);
            else if (pos.contains("defans") || pos.contains("bek") || pos.contains("stoper")) def.add(p);
            else if (pos.contains("orta")) mid.add(p);
            else if (pos.contains("forvet") || pos.contains("kanat") || pos.contains("santrafor")) fwd.add(p);
            else mid.add(p);
        }
        // X positions in normalized width
        double[] xs = leftHalf
                ? new double[] { 0.05, 0.20, 0.35, 0.45 }
                : new double[] { 0.95, 0.80, 0.65, 0.55 };
        placeRowF(root, w, h, gk,  xs[0], color);
        placeRowF(root, w, h, def, xs[1], color);
        placeRowF(root, w, h, mid, xs[2], color);
        placeRowF(root, w, h, fwd, xs[3], color);
    }

    private static void placeRowF(Pane root, double w, double h, List<IPlayer> players, double xRatio, Color color) {
        int n = players.size();
        if (n == 0) return;
        double topMargin = 0.10, bottomMargin = 0.90;
        double range = bottomMargin - topMargin;
        for (int i = 0; i < n; i++) {
            double x = w * xRatio;
            double y = h * (topMargin + range * (i + 0.5) / n);
            root.getChildren().add(playerToken(players.get(i), x, y, color));
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
        hLabel.setStyle(teamLabelStyle());
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 140); aLabel.setLayoutY(h - 22);
        aLabel.setStyle(teamLabelStyle());
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    private static void placeVolley(Pane root, double w, double h, List<IPlayer> players, boolean leftHalf, Color color) {
        if (players == null || players.isEmpty()) return;
        double[] cols = leftHalf
                ? new double[] { w * 0.18, w * 0.36 }
                : new double[] { w * 0.82, w * 0.64 };
        double[] rows = { h * 0.25, h * 0.50, h * 0.75 };
        for (int i = 0; i < players.size() && i < 6; i++) {
            double x = cols[i / 3];
            double y = rows[i % 3];
            root.getChildren().add(playerToken(players.get(i), x, y, color));
        }
    }

    private static javafx.scene.Node playerToken(IPlayer p, double x, double y, Color color) {
        // Top badges row (goals, cards, sub-in) — placed above the player dot
        HBox badges = new HBox(2);
        badges.setAlignment(Pos.CENTER);
        if (p.getGoalsThisMatch() > 0) {
            String mins = String.join(",", p.getGoalMinutes());
            String goalText = "⚽" + mins;
            Label gl = new Label(goalText);
            gl.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;"
                    + " -fx-background-color: rgba(0,0,0,0.65); -fx-padding: 1 5; -fx-background-radius: 6;");
            badges.getChildren().add(gl);
        }
        if (p.hasRedCard()) {
            Label l = new Label("🟥");
            l.setStyle("-fx-font-size: 11px;");
            badges.getChildren().add(l);
        } else if (p.getYellowCards() > 0) {
            Label l = new Label(p.getYellowCards() == 2 ? "🟨🟨" : "🟨");
            l.setStyle("-fx-font-size: 11px;");
            badges.getChildren().add(l);
        }
        if (p.getSubInClock() != null) {
            Label l = new Label("🔁" + p.getSubInClock());
            l.setStyle("-fx-text-fill: white; -fx-font-size: 9px; -fx-font-weight: bold;"
                    + " -fx-background-color: #16a085; -fx-padding: 0 4; -fx-background-radius: 6;");
            badges.getChildren().add(l);
        }

        Circle dot = new Circle(14);
        dot.setFill(color);
        dot.setStroke(Color.WHITE);
        dot.setStrokeWidth(2);
        Label num = new Label(String.valueOf(p.getSkillLevel()));
        num.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        StackPane dotPane = new StackPane(dot, num);

        Label name = new Label(p.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 4, 0, 0, 1);");

        VBox col = new VBox(1, badges, dotPane, name);
        col.setAlignment(Pos.CENTER);
        // IMPORTANT: layoutX/Y must be set on the Node returned (which is the child of the parent Pane).
        col.setLayoutX(x - 45);
        col.setLayoutY(y - 30);
        col.setPrefSize(90, 64);
        col.setMouseTransparent(true);
        return col;
    }
}
