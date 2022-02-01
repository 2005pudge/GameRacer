package games.racer;

import games.racer.road.RoadManager;

public class PlayerCar extends GameObject{

    private static int playerCarHeight = ShapeMatrix.PLAYER.length;
    public int speed = 1;
    private Direction direction;

    public PlayerCar() {
        // 3-я полоса движения и на 1 клетку выше нижнего края поля
        super(RacerGame.WIDTH/2 + 2, RacerGame.HEIGHT - playerCarHeight - 1, ShapeMatrix.PLAYER);
    }

    public void move() {
        // за края дороги выезжаем максимум колесом, на 1 клетку
        if (x < RoadManager.LEFT_BORDER) {
            x = RoadManager.LEFT_BORDER;
        } else if (x > RoadManager.RIGHT_BORDER - width) {
            x = RoadManager.RIGHT_BORDER - width;
        }
        
        if(direction==Direction.LEFT) {
            x--;
        } else if(direction==Direction.RIGHT) {
            x++;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void stop(){
        // в случае столкновения меняем модель машинки на разбитую
        matrix = ShapeMatrix.PLAYER_DEAD;
    }
}
