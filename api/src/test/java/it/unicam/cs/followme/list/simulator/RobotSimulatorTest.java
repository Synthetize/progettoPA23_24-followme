package it.unicam.cs.followme.list.simulator;

import it.unicam.cs.followme.list.model.Environment;
import it.unicam.cs.followme.list.model.SimulationEnvironment;
import it.unicam.cs.followme.list.model.commands.Command;
import it.unicam.cs.followme.list.model.robots.BasicRobot;
import it.unicam.cs.followme.list.model.shapes.CircleShape;
import it.unicam.cs.followme.list.model.shapes.Shape;
import it.unicam.cs.followme.list.model.CartesianCoordinate;
import it.unicam.cs.followme.list.model.Coordinate;
import it.unicam.cs.followme.list.parser_handler.ProgramParserHandler;
import it.unicam.cs.followme.utilities.FollowMeParserHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotSimulatorTest {
    Environment<BasicRobot> environment;
    RobotSimulator<BasicRobot> programExecutor;
    List<Command<BasicRobot>> program;
    FollowMeParserHandler programParserHandler;
    Map<BasicRobot, Coordinate> robotsList;
    @BeforeEach
    void setUp() {
        HashMap<BasicRobot, Coordinate> robots = new HashMap<>();
        HashMap<Shape, Coordinate> shapes = new HashMap<>();
        environment = new SimulationEnvironment<>(shapes, robots);
        program = new ArrayList<>();
        robotsList = new HashMap<>();
        programExecutor = new RobotSimulator<>(program, robotsList);
        programParserHandler = new ProgramParserHandler<>(environment, programExecutor);
        programParserHandler.parsingStarted();
    }

    @Test
    void shouldRunTheEntireProgram() {
        BasicRobot robot = new BasicRobot();
        BasicRobot robot2 = new BasicRobot();
        CartesianCoordinate robotCoordinate = new CartesianCoordinate(0, 0);
        environment.addRobots(List.of(robot, robot2), List.of(robotCoordinate, robotCoordinate));
        CircleShape circleShape = new CircleShape(5, "label_");
        CartesianCoordinate circleCoordinate = new CartesianCoordinate(0, 0);
        environment.addShapes(List.of(circleShape), List.of(circleCoordinate));
        robotsList.put(robot, robotCoordinate);
        robotsList.put(robot2, robotCoordinate);
        programParserHandler.signalCommand("label_");
        programParserHandler.untilCommandStart("label_");
        programParserHandler.moveCommand(new double[]{1, 1, 1});
        programParserHandler.doneCommand();
        programParserHandler.moveCommand(new double[]{1, 1, 1});
        programParserHandler.repeatCommandStart(2);
        programParserHandler.moveCommand(new double[]{-1, -1, 1});
        programParserHandler.moveCommand(new double[]{-1, -1, 1});
        programParserHandler.unsignalCommand("label_");
        programParserHandler.doneCommand();
        programParserHandler.parsingDone();
        programExecutor.simulate(1, 1000);
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("2,12", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("2,12", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot2).getX());
        assertEquals("2,12", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot2).getY());
        assertEquals("2,12", formatted);
    }

    @Test
    void shouldStopExecutionIfTimeIsOverWhileExecutingARepeatCommand() {
        BasicRobot robot = new BasicRobot();
        CartesianCoordinate robotCoordinate = new CartesianCoordinate(0, 0);
        environment.addRobots(List.of(robot), List.of(robotCoordinate));
        robotsList.put(robot, robotCoordinate);
        programParserHandler.repeatCommandStart(-1);
        programParserHandler.moveCommand(new double[]{1, 1, 1});
        programParserHandler.doneCommand();
        programParserHandler.parsingDone();
        programExecutor.simulate(0.5, 7);
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("4,6", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("4,6", formatted);
    }

    @Test
    void shouldStopExecutionIfTimeIsOverWhileExecutingAnUntilCommandAndLabelConditionIsStillSatisfied() {
        BasicRobot robot = new BasicRobot();
        robot.addLabel("label_");
        CartesianCoordinate robotCoordinate = new CartesianCoordinate(0, 0);
        environment.addRobots(List.of(robot), List.of(robotCoordinate));
        CircleShape circleShape = new CircleShape(10, "label_");
        environment.addShapes(List.of(circleShape), List.of(new CartesianCoordinate(0, 0)));
        robotsList.put(robot, robotCoordinate);
        programParserHandler.untilCommandStart("label_");
        programParserHandler.moveCommand(new double[]{0, 1, 1});
        programParserHandler.moveCommand(new double[]{1, 0, 1});
        programParserHandler.moveCommand(new double[]{0, -1, 1});
        programParserHandler.moveCommand(new double[]{-1, 0, 1});
        programParserHandler.doneCommand();
        programParserHandler.parsingDone();
        programExecutor.simulate(1, 5);
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("0", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("0", formatted);

        environment.setRobotPosition(robot, new CartesianCoordinate(0, 0));
        programExecutor.currentCommandIndex.set(0);
        programExecutor.simulate(1, 6);
        formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("0", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("1", formatted);

        environment.setRobotPosition(robot, new CartesianCoordinate(0, 0));
        programExecutor.currentCommandIndex.set(0);
        programExecutor.simulate(1, 7);
        formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("1", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("1", formatted);

        environment.setRobotPosition(robot, new CartesianCoordinate(0, 0));
        programExecutor.currentCommandIndex.set(0);
        programExecutor.simulate(1, 9);
        formatted = df.format(environment.getRobotCoordinate(robot).getX());
        assertEquals("0", formatted);
        formatted = df.format(environment.getRobotCoordinate(robot).getY());
        assertEquals("0", formatted);
    }
}
