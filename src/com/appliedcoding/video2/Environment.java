package com.appliedcoding.video2;

import com.appliedcoding.video1.Console;

import java.util.ArrayList;
import java.util.List;

public class Environment {

    private Food food;
    private Position topLeft;
    private Position bottomRight;
    private Snake snake;
    private List<Obstacle> obstacles = new ArrayList<>();

    public Environment(Position topLeft, Position bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public boolean isOutOfBounds(Position head) {
        int x = head.getX();
        int y = head.getY();

        return x > bottomRight.getX() || x < topLeft.getX() || y > bottomRight.getY() || y < topLeft.getY();
    }

    public boolean isCollision(Position head) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isCollision(head)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSnakeFoundFood() {
        if (food == null || snake == null) {
            return false;
        }

        return snake.getHead().equals(food.getPosition());
    }

    public void addFood(char foodLabel) {
        boolean collision;
        int foodX;
        int foodY;
        do {
            collision = false;
            foodX = (int) (Math.random() * (bottomRight.getX() + 1));
            foodY = (int) (Math.random() * (bottomRight.getY() + 1));
            Position foodPosition = new Position(foodX, foodY);
            for (Position body : snake.getBody()) {
                if (foodPosition.equals(body)) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.isCollision(foodPosition)) {
                        collision = true;
                        break;
                    }
                }
            }
        } while (collision);

        food = new Food(foodX, foodY, foodLabel);
    }

    public void paint(Console console) {
        for (Obstacle obstacle : obstacles) {
            obstacle.paint(console);
        }

        Position foodPosition = food.getPosition();
        console.putCharAt(food.getLabel(), foodPosition.getY(), foodPosition.getX());
    }

    public Food getFood() {
        return food;
    }

    public Position getTopLeft() {
        return topLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
}
