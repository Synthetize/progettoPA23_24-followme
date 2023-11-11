package it.unicam.cs.followme.list.model.commands.basic;

import it.unicam.cs.followme.list.model.Environment;
import it.unicam.cs.followme.list.model.robots.Robot;
import it.unicam.cs.followme.list.model.utils.CartesianCoordinate;
import it.unicam.cs.followme.list.model.utils.Coordinate;
import it.unicam.cs.followme.list.model.commands.Command;
import it.unicam.cs.followme.utilities.RobotCommand;

public class Move<R extends Robot> implements Command<R> {
    private final Coordinate targetCoordinates;
    private final int speed;

    private final Environment<R> environment;

    public Move(Coordinate targetCoordinates, int speed, Environment<R> environment) {
        this.targetCoordinates = targetCoordinates;
        this.speed = speed;
        this.environment = environment;
    }

    @Override
    public RobotCommand getCommandType() {
        return RobotCommand.MOVE;
    }

    @Override
    public void Run(R robot) {
        double distance = environment.getDistanceBetweenTwoCoordinates(targetCoordinates, environment.getRobotPosition(robot));
        double time = distance / speed;
        int numberOfSteps = (int) (time * 1);
        double stepX = (targetCoordinates.getX() - environment.getRobotPosition(robot).getX()) / numberOfSteps;
        double stepY = (targetCoordinates.getY() - environment.getRobotPosition(robot).getY()) / numberOfSteps;
        for (int i = 1; i <= numberOfSteps; i++) {
            Coordinate currentPosition = environment.getRobotPosition(robot);
            environment.setRobotPosition(robot, new CartesianCoordinate(currentPosition.getX() + stepX, currentPosition.getY() + stepY));
        }
    }

    public Coordinate getTargetCoordinate(){
        return targetCoordinates;
    }

    public int getSpeed(){
        return speed;
    }
}