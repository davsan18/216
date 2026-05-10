package sportsmanager.ui;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public final class FieldBackground {

    private FieldBackground() {}

    public static Pane footballField() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #0d7a3a, #1ea84a);");

        Color line = Color.web("#ffffff", 0.85);
        double stroke = 3.0;

        Rectangle outer = new Rectangle();
        outer.setFill(Color.TRANSPARENT);
        outer.setStroke(line);
        outer.setStrokeWidth(stroke);
        outer.xProperty().bind(pane.widthProperty().multiply(0.04));
        outer.yProperty().bind(pane.heightProperty().multiply(0.06));
        outer.widthProperty().bind(pane.widthProperty().multiply(0.92));
        outer.heightProperty().bind(pane.heightProperty().multiply(0.88));

        Line midLine = new Line();
        midLine.setStroke(line);
        midLine.setStrokeWidth(stroke);
        midLine.startXProperty().bind(pane.widthProperty().multiply(0.5));
        midLine.endXProperty().bind(pane.widthProperty().multiply(0.5));
        midLine.startYProperty().bind(pane.heightProperty().multiply(0.06));
        midLine.endYProperty().bind(pane.heightProperty().multiply(0.94));

        Circle midCircle = new Circle();
        midCircle.setFill(Color.TRANSPARENT);
        midCircle.setStroke(line);
        midCircle.setStrokeWidth(stroke);
        midCircle.centerXProperty().bind(pane.widthProperty().multiply(0.5));
        midCircle.centerYProperty().bind(pane.heightProperty().multiply(0.5));
        midCircle.radiusProperty().bind(Bindings.min(pane.widthProperty(), pane.heightProperty()).multiply(0.10));

        Circle midDot = new Circle();
        midDot.setFill(line);
        midDot.centerXProperty().bind(pane.widthProperty().multiply(0.5));
        midDot.centerYProperty().bind(pane.heightProperty().multiply(0.5));
        midDot.setRadius(4);

        Rectangle penaltyLeft = new Rectangle();
        penaltyLeft.setFill(Color.TRANSPARENT);
        penaltyLeft.setStroke(line);
        penaltyLeft.setStrokeWidth(stroke);
        penaltyLeft.xProperty().bind(pane.widthProperty().multiply(0.04));
        penaltyLeft.yProperty().bind(pane.heightProperty().multiply(0.27));
        penaltyLeft.widthProperty().bind(pane.widthProperty().multiply(0.14));
        penaltyLeft.heightProperty().bind(pane.heightProperty().multiply(0.46));

        Rectangle penaltyRight = new Rectangle();
        penaltyRight.setFill(Color.TRANSPARENT);
        penaltyRight.setStroke(line);
        penaltyRight.setStrokeWidth(stroke);
        penaltyRight.xProperty().bind(pane.widthProperty().multiply(0.82));
        penaltyRight.yProperty().bind(pane.heightProperty().multiply(0.27));
        penaltyRight.widthProperty().bind(pane.widthProperty().multiply(0.14));
        penaltyRight.heightProperty().bind(pane.heightProperty().multiply(0.46));

        Rectangle goalLeft = new Rectangle();
        goalLeft.setFill(Color.TRANSPARENT);
        goalLeft.setStroke(line);
        goalLeft.setStrokeWidth(stroke);
        goalLeft.xProperty().bind(pane.widthProperty().multiply(0.04));
        goalLeft.yProperty().bind(pane.heightProperty().multiply(0.40));
        goalLeft.widthProperty().bind(pane.widthProperty().multiply(0.05));
        goalLeft.heightProperty().bind(pane.heightProperty().multiply(0.20));

        Rectangle goalRight = new Rectangle();
        goalRight.setFill(Color.TRANSPARENT);
        goalRight.setStroke(line);
        goalRight.setStrokeWidth(stroke);
        goalRight.xProperty().bind(pane.widthProperty().multiply(0.91));
        goalRight.yProperty().bind(pane.heightProperty().multiply(0.40));
        goalRight.widthProperty().bind(pane.widthProperty().multiply(0.05));
        goalRight.heightProperty().bind(pane.heightProperty().multiply(0.20));

        Circle penDotL = new Circle(4, line);
        penDotL.centerXProperty().bind(pane.widthProperty().multiply(0.13));
        penDotL.centerYProperty().bind(pane.heightProperty().multiply(0.5));

        Circle penDotR = new Circle(4, line);
        penDotR.centerXProperty().bind(pane.widthProperty().multiply(0.87));
        penDotR.centerYProperty().bind(pane.heightProperty().multiply(0.5));

        pane.getChildren().addAll(outer, midLine, midCircle, midDot,
                penaltyLeft, penaltyRight, goalLeft, goalRight, penDotL, penDotR);
        return pane;
    }

    public static Pane volleyballCourt() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #d97a3c, #a85c2a);");

        Color line = Color.web("#ffffff", 0.9);
        Color netColor = Color.web("#1f1f1f", 0.85);
        double stroke = 3.0;

        Rectangle court = new Rectangle();
        court.setFill(Color.web("#e89a5a", 0.45));
        court.setStroke(line);
        court.setStrokeWidth(stroke);
        court.xProperty().bind(pane.widthProperty().multiply(0.10));
        court.yProperty().bind(pane.heightProperty().multiply(0.15));
        court.widthProperty().bind(pane.widthProperty().multiply(0.80));
        court.heightProperty().bind(pane.heightProperty().multiply(0.70));

        Line attackLeft = new Line();
        attackLeft.setStroke(line);
        attackLeft.setStrokeWidth(stroke);
        attackLeft.startXProperty().bind(pane.widthProperty().multiply(0.10 + 0.80 / 3.0));
        attackLeft.endXProperty().bind(pane.widthProperty().multiply(0.10 + 0.80 / 3.0));
        attackLeft.startYProperty().bind(pane.heightProperty().multiply(0.15));
        attackLeft.endYProperty().bind(pane.heightProperty().multiply(0.85));

        Line attackRight = new Line();
        attackRight.setStroke(line);
        attackRight.setStrokeWidth(stroke);
        attackRight.startXProperty().bind(pane.widthProperty().multiply(0.10 + 0.80 * 2.0 / 3.0));
        attackRight.endXProperty().bind(pane.widthProperty().multiply(0.10 + 0.80 * 2.0 / 3.0));
        attackRight.startYProperty().bind(pane.heightProperty().multiply(0.15));
        attackRight.endYProperty().bind(pane.heightProperty().multiply(0.85));

        Line net = new Line();
        net.setStroke(netColor);
        net.setStrokeWidth(8);
        net.setStrokeLineCap(StrokeLineCap.ROUND);
        net.startXProperty().bind(pane.widthProperty().multiply(0.5));
        net.endXProperty().bind(pane.widthProperty().multiply(0.5));
        net.startYProperty().bind(pane.heightProperty().multiply(0.08));
        net.endYProperty().bind(pane.heightProperty().multiply(0.92));

        Line netTop = new Line();
        netTop.setStroke(Color.WHITE);
        netTop.setStrokeWidth(4);
        netTop.startXProperty().bind(pane.widthProperty().multiply(0.5).subtract(2));
        netTop.endXProperty().bind(pane.widthProperty().multiply(0.5).add(2));
        netTop.startYProperty().bind(pane.heightProperty().multiply(0.08));
        netTop.endYProperty().bind(pane.heightProperty().multiply(0.08));

        pane.getChildren().addAll(court, attackLeft, attackRight, net, netTop);
        return pane;
    }
}
