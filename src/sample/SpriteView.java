package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.value.ObservableValue;

public class SpriteView extends StackPane {

    private final ImageView imageView;
    private Color color;
    EventHandler<ActionEvent> arrivalHandler;
    double colorOffset;

    public void setDirection(Main.Direction direction) {
        this.direction.setValue(direction);
    }

    public static class Mary extends Shepherd {

        // Image by Terra-chan: http://www.rpgmakervx.net/index.php?showtopic=29404
        static final Image MARY = loadImage("images/mary.png");

        public Mary(Main.Location loc) {
            super(MARY, loc);
        }
    }

    public static class Shepherd extends SpriteView {

        private ObservableList<SpriteView> animals;

        public ObservableList<SpriteView> getAnimals() {
            return animals;
        }

        public Shepherd(Image spriteSheet, Main.Location loc) {
            super(spriteSheet, loc);
            animals = FXCollections.observableArrayList();
            animals.addListener(new ListChangeListener<SpriteView>() {

                @Override
                public void onChanged(ListChangeListener.Change<? extends SpriteView> c) {
                    ObservableList<Node> children = ((Group) getParent()).getChildren();
                    while (c.next()) {
                        if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                            children.removeAll(c.getRemoved());
                            children.addAll(c.getAddedSubList());
                            SpriteView prev = Shepherd.this;
                            int number = 0;
                            for (SpriteView a : animals) {
                                a.following = prev;
                                a.number.set(++number);
                                prev.followers.clear();
                                prev.followers.add(a);
                                prev = a;
                            }
                        }
                    }

                }
            }
            );
            arrivalHandler = new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    MapObject object = Main.map[location.get().getX()][location.get().getY()];
                    if (object != null) {
                        object.visit(Shepherd.this);
                    }
                }
            };

        }
    }

    public static class Lamb extends NumberedSpriteView {

        // Image by Mack: http://www.rpgmakervx.net/index.php?showtopic=15704
        static final Image LAMB = loadImage("images/lamb.png");
        private ChangeListener<Main.Direction> directionListener
                = new ChangeListener<Main.Direction>() {

                    @Override
                    public void changed(ObservableValue<? extends Main.Direction> ov, Main.Direction o, Main.Direction o2) {
                        switch (o2) {
                            case RIGHT:
                                label.setTranslateX(-4 * Main.SCALE);
                                label.setTranslateY(2 * Main.SCALE);
                                break;
                            case LEFT:
                                label.setTranslateX(4 * Main.SCALE);
                                label.setTranslateY(2 * Main.SCALE);
                                break;
                            case UP:
                                label.setTranslateX(0);
                                label.setTranslateY(-2 * Main.SCALE);
                                break;
                            case DOWN:
                                label.setTranslateX(0);
                                label.setTranslateY(-9 * Main.SCALE);
                                break;
                        }
                    }
                };
        
        public Lamb(SpriteView following) {
            super(LAMB, following);
            direction.addListener(directionListener);
            directionListener.changed(direction, direction.getValue(), direction.getValue());
        }
    }

    public static class Chicken extends NumberedSpriteView {

        // Image by LovelyBlue: http://l0velyblue.deviantart.com/art/Chicken-203764427
        static final Image CHICKEN = loadImage("images/chicken.png");
        private ChangeListener<Main.Direction> directionListener
                = new ChangeListener<Main.Direction>() {

                    @Override
                    public void changed(ObservableValue<? extends Main.Direction> ov, Main.Direction o, Main.Direction o2) {
                        switch (o2) {
                            case RIGHT:
                                label.setTranslateX(0);
                                label.setTranslateY(4 * Main.SCALE);
                                break;
                            case LEFT:
                                label.setTranslateX(0);
                                label.setTranslateY(4 * Main.SCALE);
                                break;
                            case UP:
                                label.setTranslateX(0);
                                label.setTranslateY(2 * Main.SCALE);
                                break;
                            case DOWN:
                                label.setTranslateX(0);
                                label.setTranslateY(9 * Main.SCALE);
                                break;
                        }
                    }
                };

        public Chicken(SpriteView following) {
            super(CHICKEN, following);
            colorOffset = 1;
            direction.addListener(directionListener);
            directionListener.changed(direction, direction.getValue(), direction.getValue());
        }
    }

    public static class Eggs extends NumberedSpriteView {

        static final Image EGGS = loadImage("images/eggs.png");

        public Eggs(SpriteView following) {
            super(EGGS, following);
        }

        public static List<SpriteView> hatch(SpriteView sv) {
            LinkedList<SpriteView> answer = new LinkedList<>();
            if (!(sv instanceof Eggs)) {
                answer.add(sv);
                
            }
            else {
                for (int i = 0; i < 3; i++) {
                    answer.add(new Chicken(sv));
                }
            }
            return answer;
        }
//        public static Stream<SpriteView> hatch(SpriteView sv) {
//            if (!(sv instanceof Eggs)) {
//                return Stream.of(sv);
//            }
//            return Stream.iterate(sv, Chicken::new).skip(1).limit(3);
//        }
    }

    public static class NumberedSpriteView extends SpriteView {

        protected final Label label;

        public NumberedSpriteView(Image spriteSheet, SpriteView following) {
            super(spriteSheet, following);
            label = new Label();
            label.textProperty().bind(number.asString());
            label.setFont(Font.font("Impact", 12 * Main.SCALE));
            getChildren().add(label);
        }
    }

    private SpriteView following;
    IntegerProperty number = new SimpleIntegerProperty();

    public int getNumber() {
        return number.get();
    }

    public SpriteView(Image spriteSheet, SpriteView following) {
        this(spriteSheet, following.getLocation().offset(-following.getDirection().getXOffset(), -following.getDirection().getYOffset()));
        number.set(following.number.get() + 1);
        this.following = following;
        setDirection(following.getDirection());
        following.followers.add(this);
        setMouseTransparent(true);
    }

    public SpriteView getFollowing() {
        return following;
    }

    ObjectProperty<Main.Direction> direction = new SimpleObjectProperty<>();
    ObjectProperty<Main.Location> location = new SimpleObjectProperty<>();
    IntegerProperty frame = new SimpleIntegerProperty(1);
    int spriteWidth;
    int spriteHeight;
    Timeline walking;
    List<SpriteView> followers = new ArrayList<>();

    static Image loadImage(String url) {
        return new Image(SpriteView.class.getResource(url).toString(), Main.SPRITE_SIZE * 3 * Main.SCALE, Main.SPRITE_SIZE * 4 * Main.SCALE, true, false);
    }

    public SpriteView(Image spriteSheet, Main.Location loc) {
        imageView = new ImageView(spriteSheet);
        this.location.set(loc);
        setTranslateX(loc.getX() * Main.CELL_SIZE);
        setTranslateY(loc.getY() * Main.CELL_SIZE);
        ChangeListener<Object> updateImage = new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                imageView.setViewport(new Rectangle2D(frame.get() * spriteWidth, direction.get().getOffset() * spriteHeight, spriteWidth, spriteHeight));
            }
        };

        direction.addListener(updateImage);
        frame.addListener(updateImage);
        spriteWidth = (int) (spriteSheet.getWidth() / 3);
        spriteHeight = (int) (spriteSheet.getHeight() / 4);
        direction.set(Main.Direction.RIGHT);
        getChildren().add(imageView);
    }

    public void startAnimation() {
        final Timeline timeline = new Timeline(Animation.INDEFINITE,
                new KeyFrame(Duration.seconds(.25), new KeyValue(frame, 0)),
                new KeyFrame(Duration.seconds(.5), new KeyValue(frame, 1)),
                new KeyFrame(Duration.seconds(.75), new KeyValue(frame, 2)),
                new KeyFrame(Duration.seconds(1), new KeyValue(frame, 1))
        );
        timeline.onFinishedProperty().setValue(
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent t) {
                        timeline.play();
                    }
                }
        );

        //        e -> timeline.play());
        timeline.play();
    }

    public void move(Main.Direction direction) {
        if (walking != null && walking.getStatus().equals(Animation.Status.RUNNING)) {
            return;
        }
        this.direction.set(direction);
        moveTo(location.getValue().offset(direction.getXOffset(), direction.getYOffset()));
    }

    public void moveTo(Main.Location loc) {
        for (SpriteView f : followers) {
            f.moveTo(location.getValue());
        }
        direction.set(location.getValue().directionTo(loc));
        location.setValue(loc);
        walking = new Timeline(Animation.INDEFINITE,
                new KeyFrame(Duration.seconds(1), new KeyValue(translateXProperty(), loc.getX() * Main.CELL_SIZE)),
                new KeyFrame(Duration.seconds(1), new KeyValue(translateYProperty(), loc.getY() * Main.CELL_SIZE)),
                new KeyFrame(Duration.seconds(.25), new KeyValue(frame, 0)),
                new KeyFrame(Duration.seconds(.5), new KeyValue(frame, 1)),
                new KeyFrame(Duration.seconds(.75), new KeyValue(frame, 2)),
                new KeyFrame(Duration.seconds(1), new KeyValue(frame, 1))
        );
        walking.setOnFinished(arrivalHandler);
        walking.play();
    }

    public Main.Location getLocation() {
        return location.get();
    }

    public Main.Direction getDirection() {
        return direction.get();
    }

    public void setColor(Color color) {
        this.color = color;
        if (color == null) {
            imageView.setEffect(null);
        } else {
            imageView.setEffect(new ColorAdjust(color.getHue() / 180 - colorOffset, 0.3, 0, 0));
        }
    }

    public Color getColor() {
        return color;
    }
}
