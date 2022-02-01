package games.racer.road;

import com.javarush.engine.cell.Game;
import games.racer.*;
import games.racer.PlayerCar;
import games.racer.RacerGame;

import java.util.ArrayList;
import java.util.List;

public class RoadManager {
    public final static int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public final static int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private final static int FIRST_LANE_POSITION = 16;
    // выше определили крайнюю левую позицию объектов
    private final static int FOURTH_LANE_POSITION = 44;
    // крайняя правая позиция встречных объектов
    private final static int PLAYER_CAR_DISTANCE = 12;
    private int passedCarsCount = 0;

    private List<RoadObject> items = new ArrayList<>();

    public void draw(Game game) {
        for (RoadObject item : items) {
            item.draw(game);
        }
    }

    public void move(int boost) {
        for (RoadObject item : items) {
            item.move(boost + item.speed, items);
        }
        deletePassedItems();
    }

    private RoadObject createRoadObject(RoadObjectType roadObjectType, int x, int y) {
        if (roadObjectType == RoadObjectType.THORN) {
            return new Thorn(x, y);
        } else if (roadObjectType == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else return new Car(roadObjectType, x, y);
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        // позицию объекта определяем рандомно между метками
        int y = -1 * RoadObject.getHeight(type);
        // чтобы объект появился плавно, пусть появляется вне экрана
        RoadObject temp = createRoadObject(type, x, y);
        if ((temp != null) && isRoadSpaceFree(temp)) {
            // каждый созданный объект добавляем в список объектов на экране
            items.add(createRoadObject(type, x, y));
        }
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void generateThorn(Game game) {
        if (game.getRandomNumber(100) < 10 && !isThornExists()) {
            // с вероятностью ок. 10% создаём шип на дороге
            // если уже есть 1 на поле, новый не создаём
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    private boolean isThornExists() {
        for (RoadObject item : items) {
            if (item.type == RoadObjectType.THORN)
                return true;
        }
        return false;
    }

    private void generateRegularCar(Game game) {
        int carTypeNumber = game.getRandomNumber(4);
        if (game.getRandomNumber(100) < 30) {
            // с вероятностью ок. 30% создаём обычную машину нам навстречу
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private void generateMovingCar(Game game) {
        if (game.getRandomNumber(100) < 10 && !isMovingCarExists()) {
            // с вероятностью ок. 10% создаём движущуюся машину нам навстречу
            // если уже есть 1 на поле, новую не создаём
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    private boolean isMovingCarExists() {
        for (RoadObject item : items) {
            if (item.type == RoadObjectType.DRUNK_CAR) {
                return true;
            }
        }
        return false;
    }

    private void deletePassedItems() {
        // удаляем из списка объектов те, что прошли экран
        // инкрементируем число проехавших мимо машин, чтобы дойти
        // до нашего искомого количества (40)
        for (RoadObject item : new ArrayList<>(items)) {
            if (item.y >= RacerGame.HEIGHT) {
                items.remove(item);
                if (item.type != RoadObjectType.THORN) {
                    passedCarsCount++;
                }
            }
        }
    }

    public boolean checkCrush(PlayerCar playerCar) {
        for (RoadObject roadObject : items) {
            if (roadObject.isCollision(playerCar)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoadSpaceFree(RoadObject roadObject) {
        for (RoadObject roadObject1 : items) {
            if (roadObject1.isCollisionWithDistance(roadObject, PLAYER_CAR_DISTANCE)) {
                return false;
            }
        }
        return true;
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }
}
