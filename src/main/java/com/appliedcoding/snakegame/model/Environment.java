package com.appliedcoding.snakegame.model;

import com.appliedcoding.io.Position;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.view.Canvas;

public class Environment {

    private final Position topLeft;
    private final Position bottomRight;
    private final int width;
    private final int height;
    private final Snake snake;
    private Food food;
    private Obstacle obstacle;
    private GameState gameState;

    public Environment(Canvas canvas, GameState state) {
        this(canvas.getWidth(), 2 * canvas.getHeight(), state);
    }

    public Environment(int width, int height, GameState state) {
        this.width = width;
        this.height = height;
        this.gameState = state;
        topLeft = new Position(1, 1);
        bottomRight = new Position(width, height);

        snake = new Snake(new Position((width + 1) / 2, height / 2));

        createObstacles();
        addFood();
    }

    private void createObstacles() {
        obstacle = new Obstacle();

        float minX = topLeft.getX();
        float maxX = bottomRight.getX();
        float minY = topLeft.getY();
        float maxY = bottomRight.getY();
        float distX = maxX - minX;
        float distY = maxY - minY;
        float halfX = distX / 2f;
        float halfY = distY / 2f;
        float quarterY = distY / 4f;
        float eighthX = distX / 8f;
        float tenthX = distX / 10f;
        float tenthY = distY / 10f;
        float sixteenthX = distX / 16f;
        float sixteenthY = distY / 16f;

        float midX = minX + halfX;

        obstacle.addLineMirrorH(new Position(Math.round(minX), Math.round(minY)),
                new Position(Math.round(minX), Math.round(maxY)), (int) maxX);

        obstacle.addLine(new Position(Math.round(minX + 1), Math.round(minY)),
                new Position(Math.round(maxX - 1), Math.round(minY)));
        obstacle.addLine(new Position(Math.round(minX + 1), Math.round(maxY)),
                new Position(Math.round(maxX - 1), Math.round(maxY)));

        if (!Configuration.OBSTACLES_ENABLED) {
            return;
        }

        // V
        obstacle.addLineMirrorH(
                new Position(Math.round(Math.max(minX + 3, minX + sixteenthX)),
                        Math.round(Math.max(minY + 2, minY + tenthY))),
                new Position(Math.round(midX - 0.1f), Math.round(Math.min(minY + 4 * tenthY, halfY - 1))),
                (int) maxX);

        // horizontal middle
        obstacle.addLine(
                new Position((int) Math.max(minX + 2, Math.round(minX + eighthX)), Math.round(minY + halfY)),
                new Position((int) Math.min(maxX - 2, Math.round(maxX - eighthX)), Math.round(minY + halfY)));

        // horizontal low left
        obstacle.addLine(new Position((int) Math.max(minX + 2, Math.round(minX + tenthX)), Math.round(maxY - quarterY)),
                new Position(Math.round(minX + 4 * tenthX), Math.round(maxY - quarterY)));

        // horizontal low right
        obstacle.addLine(new Position(Math.round(maxX - 4 * tenthX), Math.round(maxY - quarterY)),
                new Position((int) Math.min(maxX - 2, Math.round(maxX - tenthX)), Math.round(maxY - quarterY)));

        // vertical low
        obstacle.addLine(new Position(Math.round(midX - 0.1f), Math.round(maxY - 4 * tenthY)),
                new Position(Math.round(midX - 0.1f), (int) Math.min(maxY - 3, Math.round(maxY - sixteenthY))));
        if (distX % 2 == 1) {
            obstacle.addLine(new Position(Math.round(midX + 0.1f), Math.round(maxY - 4 * tenthY)),
                    new Position(Math.round(midX + 0.1f), (int) Math.min(maxY - 3, Math.round(maxY - sixteenthY))));
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isOutOfBounds(Position position) {
        int x = position.getX();
        int y = position.getY();

        return x < topLeft.getX() || x > bottomRight.getX() || y < topLeft.getY() || y > bottomRight.getY();
    }

    public void addFood() {
        Position positionUpper;
        Position positionLower;
        boolean isLoop;
        do {
            int foodX = (int) (topLeft.getX() + Math.random() * bottomRight.getX());
            int foodY = (int) (topLeft.getY() + Math.random() * (bottomRight.getY() - 1));
            if (foodY % 2 == 0) {
                positionUpper = new Position(foodX, foodY - 1);
                positionLower = new Position(foodX, foodY);
            } else {
                positionUpper = new Position(foodX, foodY);
                positionLower = new Position(foodX, foodY + 1);
            }

            isLoop = snake.contains(positionUpper) || snake.contains(positionLower);
            if (!isLoop) {
                if (obstacle.contains(positionUpper) || obstacle.contains(positionLower)) {
                    isLoop = true;
                }
            }
        } while (isLoop);

        char label;
        if (food == null) {
            label = '1';
        } else {
            label = food.getNextLabel();
        }

        food = new Food(positionUpper, positionLower, label);
    }

    public void moveSnake() {
        snake.move();
        gameState.incSteps();

        if (checkCollision() || gameState.getFoodCredits() == 0) {
            gameState.setGameOver();
        } else if (hasFoundFood()) {
            gameState.setFoundFood();
            gameState.incFoodCount();
            gameState.setFoodCredits(Configuration.FOOD_CREDITS);

            if (gameState.getFoodCount() == Configuration.FOOD_COUNT_TARGET) {
                gameState.setWin();
            } else {
                addFood();
                int amount = (width + height) / 2 * 20 / 100; // 5% of avg
                snake.grow(amount);
            }
        } else {
            gameState.updateFoodCredits(-1);
        }
    }

    public boolean checkCollision() {
        return isOutOfBounds(snake.getHead()) || snake.isEatingItself() || isObstacleAt(snake.getHead());
    }

    public boolean hasFoundFood() {
        return food.getPosition().contains(snake.getHead());
    }

    public boolean isObstacleAt(Position position) {
        return obstacle.contains(position);
    }

    private boolean isFoodAt(Position position) {
        return food.getPosition().contains(position);
    }

    public double getDistanceTo(int[][] directions, int direction, EnvironmentObjectType objectType) {
        double result = 0;
        Position head = snake.getHead();
        int x = head.getX();
        int y = head.getY();
        boolean isLoop = true;

        do {
            x += directions[0][direction];
            y += directions[1][direction];
            result++;

            Position rayPosition = new Position(x, y);
            if (isOutOfBounds(rayPosition)) {
                isLoop = false;
                result = 0;
            } else {
                switch (objectType) {
                    case Food:
                        if (isFoodAt(rayPosition)) {
                            isLoop = false;
                        }
                        break;

                    case Obstacle:
                        if (isObstacleAt(rayPosition)) {
                            isLoop = false;
                        }
                        break;

                    case Snake:
                        if (snake.isSnakeAt(rayPosition)) {
                            isLoop = false;
                        }
                        break;
                }
            }
        } while (isLoop);

        return result;
    }

    public double getDistanceToFood() {
        Position head = snake.getHead();
        int snakeX = head.getX();
        int snakeY = head.getY();

        double result = -2;
        for (Position food : getFood().getPosition()) {
            int foodX = food.getX();
            int foodY = food.getY();
            Direction direction = snake.getDirection();
            double dist = Math.sqrt(Math.pow(snakeX - foodX, 2) + Math.pow(snakeY - foodY, 2));

            if (direction == Direction.Up && foodY > snakeY ||
                    direction == Direction.Right && foodX < snakeX ||
                    direction == Direction.Down && foodY < snakeY ||
                    direction == Direction.Left && foodX > snakeX) {
                dist = -1;
            }

            if (result == -2) {
                result = dist;
            } else if (result == -1 && dist == -1) {
                result = 0;
            } else if (result == -1) {
                result = dist;
            } else if (dist != -1) {
                result = Math.min(result, dist);
            }
        }

        return result;
    }

    public double getHeatMap(int[][] directions, int direction) {
        Position head = snake.getHead();
        int x = head.getX() + directions[0][direction];
        int y = head.getY() + directions[1][direction];

        int distance = Integer.MAX_VALUE;
        for (Position food : getFood().getPosition()) {
            int foodX = food.getX();
            int foodY = food.getY();

            distance = Math.min(distance, Math.abs(x - foodX) + Math.abs(y - foodY));
        }

        int maxHeat = width + height - 2;
        return maxHeat - distance;
    }

    public double getSineAngleToFood() {
        Position head = snake.getHead();
        int snakeX = head.getX();
        int snakeY = head.getY();

        double result = 0;
        for (Position food : getFood().getPosition()) {
            int foodX = food.getX();
            int foodY = food.getY();
            double distance = getDistanceToFood();
            double sine = 0;

            switch (snake.getDirection()) {
                case Up:
                    if (foodY > snakeY) {
                        sine = 0;
                    } else {
                        sine = (foodX - snakeX) / distance;
                    }
                    break;

                case Right:
                    if (foodX < snakeX) {
                        sine = 0;
                    } else {
                        sine = (foodY - snakeY) / distance;
                    }
                    break;

                case Down:
                    if (foodY < snakeY) {
                        sine = 0;
                    } else {
                        sine = (snakeX - foodX) / distance;
                    }
                    break;

                case Left:
                    if (foodX > snakeX) {
                        sine = 0;
                    } else {
                        sine = (snakeY - foodY) / distance;
                    }
                    break;
            }

            if (result == 0) {
                result = sine;
            } else if (result >= 0 || sine >= 0) {
                result = Math.min(result, sine);
            } else {
                result = Math.max(result, sine);
            }
        }

        return result;
    }

    public double getCosineAngleToFood() {
        Position head = snake.getHead();
        int snakeX = head.getX();
        int snakeY = head.getY();

        double result = -1;
        for (Position food : getFood().getPosition()) {
            int foodX = food.getX();
            int foodY = food.getY();
            double distance = getDistanceToFood();
            double cosine = 0;

            switch (snake.getDirection()) {
                case Up:
                    cosine = Math.max(0, (snakeY - foodY) / distance);
                    break;

                case Right:
                    cosine = Math.max(0, (foodX - snakeX) / distance);
                    break;

                case Down:
                    cosine = Math.max(0, (foodY - snakeY) / distance);
                    break;

                case Left:
                    cosine = Math.max(0, (snakeX - foodX) / distance);
                    break;
            }

            if (result == -1) {
                result = cosine;
            } else if (result < 1 && cosine < 1) {
                result = Math.min(result, cosine);
            } else {
                result = 1;
            }
        }

        return result;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public Food getFood() {
        return food;
    }

    public Snake getSnake() {
        return snake;
    }
}
