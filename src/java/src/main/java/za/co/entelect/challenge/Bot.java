package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private final List<Command> directionList = new ArrayList<>();

    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        List<Object> blocksInFront = getBlocksInFront(myCar.position.lane, myCar.position.block, gameState);

        /* Greedy untuk Avoid Obstacles */
        if (blocksInFront.contains(Terrain.WALL) || blocksInFront.contains(Terrain.CYBER_TRUCK)) {
            if (myCar.position.lane > 0) {
                List<Object> blocksInRightLane = getBlocksInFront(myCar.position.lane + 1, myCar.position.block,
                        gameState);
            }
            if (myCar.position.lane < 4) {
                List<Object> blocksInLeftLane = getBlocksInFront(myCar.position.lane - 1, myCar.position.block,
                        gameState);

            }
            // Command TURN = changeLaneFunction(myCar.position, );
            return TURN;
        }

        /* Greedy untuk Fix Command */
        if (myCar.damage >= 5)
            return FIX;
        if (myCar.damage == 4 && myCar.speed == 3)
            return FIX;
        if (myCar.damage == 3 && myCar.speed == 6)
            return FIX;
        if ((myCar.damage == 1 || myCar.damage == 2) && myCar.speed >= 8
                && hasPowerUp(PowerUps.BOOST, myCar.powerups))
            return FIX;

        if (blocks.contains(Terrain.MUD) || blocks.contains(Terrain.OIL_SPILL)) {
            Command TURN = changeLaneFunction(myCar.position, blocks);
            return TURN;
        }

        return ACCELERATE;
    }

    /* Fungsi untuk mengecek lane ketika ingin belok */
    private Command changeLaneFunction(Position pos, List<Object> blocks) {

        return TURN_LEFT;
    }

    /* Fungsi untuk mengecek apakah memiliki power up tertentu */
    private Boolean hasPowerUp(PowerUps powerUpCheck, PowerUps[] store) {
        for (PowerUps powerUp : store) {
            if (powerUp.equals(powerUpCheck))
                return true;
        }
        return false;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

}