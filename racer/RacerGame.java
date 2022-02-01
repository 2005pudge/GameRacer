package games.racer;

import com.javarush.engine.cell.*;
import games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;     // количество встречных машин
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private boolean isGameStopped;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private int score;

    @Override
    public void initialize() {
        // аналог метода main. Определяем игровое поле и запускаем игру
        setScreenSize(WIDTH, HEIGHT);
        showGrid(false);
        createGame();
    }

    private void createGame() {
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        score = 3500;
        // количество очков уменьшается с каждым шагом
        drawScene();
        setTurnTimer(40);
        // выше задали, что разметка и др. объекты движутся каждые 40мс
        isGameStopped = false;
    }

    private void drawScene() {
        // отрисовываем игровое поле и объекты
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (j < ROADSIDE_WIDTH || j >= WIDTH - ROADSIDE_WIDTH) setCellColor(j, i, Color.FORESTGREEN);
                    // выше цвет обочины установили зелёным
                else if (j < CENTER_X || j > CENTER_X) setCellColor(j, i, Color.BLACK);
                    // ну а цвет дороги — чёрный
                else setCellColor(j, i, Color.WHITE);
                // всё остальное (разметка) закрасили белым
            }
        }
    }

    private void moveAll() {
        // запускаем игровые объекты по полю
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onTurn(int step) {
        // задаём действия для каждого момента игрового времени
        if (roadManager.checkCrush(player)) {
            gameOver();
            drawScene();
            return;
        }
        roadManager.generateNewRoadObjects(this);
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {
            finishLine.show();
        }
        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }
        score -= 5;
        // с каждым шагом уменьшаем кол-во очков, так что ускоряться выгодно
        setScore(score);
        moveAll();
        drawScene();
    }


    @Override
    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && y >= 0 && x < 64 && y < 64) super.setCellColor(x, y, color);
        else ;
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE && isGameStopped) {
            // в случае проигрыша перезапуск игры по пробелу
            createGame();
        } else if (key == Key.UP) {
            // ускорение
            player.speed = 2;
        } else if (key == Key.RIGHT) {
            player.setDirection(Direction.RIGHT);
        } else if (key == Key.LEFT) {
            player.setDirection(Direction.LEFT);
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.UP) {
            // снимаем ускорение при отпускании клавиши
            player.speed = 1;
        } else {
            switch (key) {
                case LEFT: {
                    if (player.getDirection() == Direction.LEFT) {
                        // отпустили клавишу — остаёмся на месте
                        player.setDirection(Direction.NONE);
                        break;
                    }
                }
                case RIGHT: {
                    if (player.getDirection() == Direction.RIGHT) {
                        player.setDirection(Direction.NONE);
                        break;
                    }
                }
            }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        stopTurnTimer();
        player.stop();
        showMessageDialog(Color.BLACK, "Папробуй ещё раз \n" +
                "Жми пробел", Color.WHITE, 25);
    }

    private void win() {
        isGameStopped = true;
        stopTurnTimer();
        showMessageDialog(Color.BLACK, "Первый нах", Color.WHITE, 25);
    }
}
