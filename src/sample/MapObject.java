package sample;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MapObject extends SpriteView {

    public static class Barn extends MapObject {

        // Image by LovelyBlue: http://l0velyblue.deviantart.com/art/barncharset-350737104
        static final Image BARN = loadImage("images/barn.png");

        public Barn(Main.Location loc) {
            super(BARN, loc);
        }

        @Override
        public void visit(Shepherd s) {
            SpriteView tail = s.getAnimals().isEmpty()
                    ? s : s.getAnimals().get(s.getAnimals().size() - 1);
            SpriteView seed = tail;
            for (int i = 0; i < 7; i++) {
                SpriteView sv = new SpriteView.Lamb(seed);
                s.getAnimals().add(sv);
                seed = sv;
            }

        }
    }

    public static class Rainbow extends MapObject {

        static final Image RAINBOW = loadImage("images/rainbow.png");

        public Rainbow(Main.Location loc) {
            super(RAINBOW, loc);
            startAnimation();
        }

        @Override
        public void visit(Shepherd s) {
            for (SpriteView a : s.getAnimals()) {
                switch (a.getNumber() % 4) {
                    case 0:
                        a.setColor(Color.GREEN);
                        break;
                    case 1:
                        a.setColor(null);
                        break;
                    case 2:
                        a.setColor(Color.YELLOW);
                        break;
                    case 3:
                        a.setColor(Color.CYAN);

                }
            }
        }
    }

    public static class Church extends MapObject {

        // Image by LovelyBlue: http://l0velyblue.deviantart.com/art/Church-350736943
        static final Image CHURCH = loadImage("images/church.png");
        LongProperty mealsServed = new SimpleLongProperty();

        public Church(Main.Location loc) {
            super(CHURCH, loc);
            Label label = new Label();
            label.textProperty().bind(mealsServed.asString());
            label.setFont(Font.font("Impact", 12 * Main.SCALE));
            label.setTranslateX(-8 * Main.SCALE);
            label.setTranslateY(3 * Main.SCALE);
            getChildren().add(label);
        }

        @Override
        public void visit(Shepherd s) {
            List<SpriteView> toRemove = new LinkedList<>();
            for (SpriteView a : s.getAnimals()) {
                if (a.getColor() == null) {
                    toRemove.add(a);
                    mealsServed.set(mealsServed.get()+1);
                }
            }
            s.getAnimals().removeAll(toRemove);

        }
    }

    public static class ChickenCoop extends MapObject {

        // Image by LovelyBlue: http://l0velyblue.deviantart.com/art/chickencoop-350736803
        static final Image CHICKEN_COOP = loadImage("images/chicken-coop.png");

        public ChickenCoop(Main.Location loc) {
            super(CHICKEN_COOP, loc);
        }

        @Override
        public void visit(Shepherd s) {

            List<SpriteView> newList = new LinkedList<>();
            for (SpriteView a : s.getAnimals()) {
                Eggs eggs = new Eggs(a.getFollowing());
                newList.add(eggs);
            }
            s.getAnimals().setAll(newList);

        }
    }

    public static class Nest extends MapObject {

        // Image derived from Lokilech's Amselnest: http://commons.wikimedia.org/wiki/File:Amselnest_lokilech.jpg
        static final Image NEST = loadImage("images/nest.png");

        public Nest(Main.Location loc) {
            super(NEST, loc);
        }

        @Override
        public void visit(Shepherd s) {
            List<SpriteView> newList = new LinkedList<>();
            for (SpriteView a: s.getAnimals()) {
                newList.addAll(SpriteView.Eggs.hatch(a));
            }
            s.getAnimals().setAll(newList);
//            s.getAnimals().setAll(s.getAnimals()
//                    .stream().parallel()
//                    .flatMap(SpriteView.Eggs::hatch)
//                    .collect(Collectors.toList())
//            );
        }
    }

    public static class Fox extends MapObject {

        // Image by PinedaVX: http://www.rpgmakervx.net/index.php?showtopic=9422
        static final Image FOX = loadImage("images/fox.png");

        public Fox(Main.Location loc) {
            super(FOX, loc);
            startAnimation();
        }

        @Override
        public void visit(Shepherd shepherd) {
            double mealSize = 0;
            for (SpriteView a: shepherd.getAnimals()) {
                mealSize = mealSize + a.getScaleX();
            }
            setScaleX(getScaleX() + mealSize * .2);
            setScaleY(getScaleY() + mealSize * .2);
            shepherd.getAnimals().clear();
        }
    }

    public MapObject(Image spriteSheet, Main.Location loc) {
        super(spriteSheet, loc);
        Main.map[loc.getX()][loc.getY()] = this;
    }

    public abstract void visit(Shepherd shepherd);
}
