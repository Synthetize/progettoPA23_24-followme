package it.unicam.cs.followme.list.model.commands.basic;

import it.unicam.cs.followme.list.model.commands.Command;
import it.unicam.cs.followme.list.model.commands.loops.LoopCommand;
import it.unicam.cs.followme.list.model.robots.Robot;
import it.unicam.cs.followme.utilities.RobotCommand;

public record Done(LoopCommand startingLoopCommand) implements Command {

    @Override
    public RobotCommand getCommandType() {
        return RobotCommand.DONE;
    }

}
