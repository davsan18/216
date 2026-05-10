package sportsmanager.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.List;

/** Renders both teams' starting line-ups on a small pitch / court. Fixed size, no binding. */
final class FormationView {

    private FormationView() {}

    static Pane footballPitch(double w, double h, ITeam home, ITeam away,
                              List<IPlayer> homeOnField, List<IPlayer> awayOnField) {
        Pane root = new Pane();
        root.setPrefSize(w, h);
        root.setMinSize(w, h);
        root.setMaxSize(w, h);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0d7a3a, #1ea84a); -fx-background-radius: 10;");

        // Pitch lines
        Color line = Color.web("#ffffff", 0.8);
        Rectangle outer = new Rectangle(8, 8, w - 16, h - 16);
        outer.setFill(Color.TRANSPARENT); outer.setStroke(line); outer.setStrokeWidth(2);
        Line mid = new Line(w / 2, 8, w / 2, h - 8);
        mid.setStroke(line); mid.setStrokeWidth(2);
        Circle midC = new Circle(w / 2, h / 2, Math.min(w, h) * 0.10);
        midC.setFill(Color.TRANSPARENT); midC.setStroke(line); midC.setStrokeWidth(2);
        // Penalty boxes
        Rectangle pl = new Rectangle(8, h * 0.25, w * 0.14, h * 0.50);
        pl.setFill(Color.TRANSPARENT); pl.setStroke(line); pl.setStrokeWidth(2);
        Rectangle pr = new Rectangle(w - 8 - w * 0.14, h * 0.25, w * 0.14, h * 0.50);
        pr.setFill(Color.TRANSPARENT); pr.setStroke(line); pr.setStrokeWidth(2);
        root.getChildren().addAll(outer, mid, midC, pl, pr);

        placeFootballSide(root, w, h, homeOnField, true,  Color.web("#e74c3c"), home.getName());
        placeFootballSide(root, w, h, awayOnField, false, Color.web("#3498db"), away.getName());

        // Team labels
        Label hLabel = new Label(home.getName());
        hLabel.setLayoutX(12); hLabel.setLayoutY(h - 22);
        hLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0, 1, 1);");
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 100); aLabel.setLayoutY(h - 22);
        aLabel.setAlignment(Pos.CENTER_RIGHT);
        aLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0, 1, 1);");
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    private static void placeFootballSide(Pane root, double w, double h, List<IPlayer> players,
                                          boolean leftHalf, Color color, String teamName) {
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
        // X positions in half-width units (0.0 = own goal line, 1.0 = midline)
        double[] xs = leftHalf
                ? new double[] { 0.06, 0.22, 0.38, 0.46 }   // GK, DEF, MID, FWD
                : new double[] { 0.94, 0.78, 0.62, 0.54 };
        placeRowF(root, w, h, gk,  xs[0], color);
        placeRowF(root, w, h, def, xs[1], color);
        placeRowF(root, w, h, mid, xs[2], color);
        placeRowF(root, w, h, fwd, xs[3], color);
    }

    private static void placeRowF(Pane root, double w, double h, List<IPlayer> players, double xRatio, Color color) {
        int n = players.size();
        if (n == 0) return;
        for (int i = 0; i < n; i++) {
            double x = w * xRatio;
            double y = h * (0.10 + (0.80 * (i + 0.5) / n));
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
        // Net
        Line net = new Line(w / 2, h * 0.06, w / 2, h * 0.94);
        net.setStroke(Color.web("#1f1f1f", 0.85)); net.setStrokeWidth(5);
        // Attack lines
        Line al = new Line(w * (0.08 + 0.84 / 3.0), h * 0.12, w * (0.08 + 0.84 / 3.0), h * 0.88);
        Line ar = new Line(w * (0.08 + 0.84 * 2.0 / 3.0), h * 0.12, w * (0.08 + 0.84 * 2.0 / 3.0), h * 0.88);
        al.setStroke(line); ar.setStroke(line);
        al.setStrokeWidth(2); ar.setStrokeWidth(2);
        root.getChildren().addAll(court, net, al, ar);

        placeVolley(root, w, h, homeOnField, true,  Color.web("#e74c3c"));
        placeVolley(root, w, h, awayOnField, false, Color.web("#3498db"));

        Label hLabel = new Label(home.getName());
        hLabel.setLayoutX(12); hLabel.setLayoutY(h - 22);
        hLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0, 1, 1);");
        Label aLabel = new Label(away.getName());
        aLabel.setLayoutX(w - 12 - 110); aLabel.setLayoutY(h - 22);
        aLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0, 1, 1);");
        root.getChildren().addAll(hLabel, aLabel);

        return root;
    }

    private static void placeVolley(Pane root, double w, double h, List<IPlayer> players, boolean leftHalf, Color color) {
        if (players == null || players.isEmpty()) return;
        // Two columns × three rows on each side
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

    private static StackPane playerToken(IPlayer p, double x, double y, Color color) {
        Circle dot = new Circle(13);
        dot.setFill(color);
        dot.setStroke(Color.WHITE);
        dot.setStrokeWidth(2);
        Label num = new Label(String.valueOf(p.getSkillLevel()));
        num.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
        StackPane node = new StackPane(dot, num);
        Label name = new Label(badge(p) + p.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 4, 0, 0, 1);");
        name.setLayoutY(16);
        StackPane container = new StackPane();
        container.getChildren().addAll(node, name);
        StackPane.setAlignment(name, Pos.BOTTOM_CENTER);
        container.setLayoutX(x - 30);
        container.setLayoutY(y - 14);
        container.setPrefSize(60, 38);
        return container;
    }

    private static String badge(IPlayer p) {
        if (p.hasRedCard()) return "🟥 ";
        if (p.isInjured()) return "⚕ ";
        if (p.getYellowCards() > 0) return "🟨 ";
        return "";
    }
}
