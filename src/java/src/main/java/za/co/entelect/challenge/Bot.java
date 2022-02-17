package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

//import java.security.SecureRandom;

public class Bot {
    // private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        // this.random = new SecureRandom();
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        List<Object> blocksInFront = getBlocksInFront(myCar.position.lane, myCar.position.block, myCar.speed,
                gameState);

        if (isMaxSpeed(myCar.damage, myCar.speed) && myCar.damage > 2)
            return FIX;

        /* Greedy untuk Avoid Obstacles */
        if (blocksInFront.contains(Terrain.WALL) || blocksInFront.contains(Terrain.TRUCK)) {
            int left = 9999;
            int right = 9999;
            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            if (left > 1 && right > 1 && hasPowerUp(PowerUps.LIZARD, myCar.powerups))
                return LIZARD;

            if (left <= right)
                return TURN_LEFT;
            else
                return TURN_RIGHT;
        }

        /* Greedy untuk Fix Command */
        if ((myCar.damage == 1 || myCar.damage == 2) && hasPowerUp(PowerUps.BOOST, myCar.powerups))
            return FIX;

        if (blocksInFront.contains(Terrain.MUD) || blocksInFront.contains(Terrain.OIL_SPILL)) {
            int left = 9999;
            int right = 9999;
            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            if (left > 1 && right > 1 && hasPowerUp(PowerUps.LIZARD, myCar.powerups))
                return LIZARD;
            if (left == 0)
                return TURN_LEFT;
            else if (right == 0)
                return TURN_RIGHT;
        }

        /* Greedy untuk powerups */
        if (myCar.damage == 0 && hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }

        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && myCar.position.block < opponent.position.block
                && (myCar.position.lane + 2 == opponent.position.lane
                        || myCar.position.lane - 2 == opponent.position.lane))
            return EMP;

        if (hasPowerUp(PowerUps.TWEET, myCar.powerups) && myCar.position.block > opponent.position.block) {
            int left = 9999;
            int right = 9999;
            int front;

            if (opponent.position.lane > 1)
                left = obstaclesCheckLeft(opponent.position.lane, opponent.position.block, opponent.speed, gameState);
            if (opponent.position.lane < 4)
                right = obstaclesCheckRight(opponent.position.lane, opponent.position.block, opponent.speed, gameState);
            front = obstaclesCheckFront(opponent.position.lane, opponent.position.block, opponent.speed, gameState);

            int opponentLane;

            if (left > 1 && right > 1 && front > 1 && hasPowerUp(PowerUps.LIZARD, opponent.powerups))
                opponentLane = opponent.position.lane;
            else if (left <= right && left <= front)
                opponentLane = opponent.position.lane - 1;
            else if (right <= left && right <= front)
                opponentLane = opponent.position.lane + 1;
            else
                opponentLane = opponent.position.lane;

            if (opponentLane > 1)
                left = obstaclesCheckLeft(opponentLane, opponent.position.block + opponent.speed,
                        opponent.speed, gameState);
            if (opponentLane < 4)
                right = obstaclesCheckRight(opponentLane, opponent.position.block + opponent.speed,
                        opponent.speed, gameState);
            front = obstaclesCheckFront(opponentLane, opponent.position.block + opponent.speed,
                    opponent.speed, gameState);

            if (left > 0 && right > 0 && front == 0)
                return new TweetCommand(opponent.position.lane, opponent.position.block + opponent.speed + 1);
            if (left == 0 && right > 0 && front > 0)
                return new TweetCommand(opponent.position.lane - 1, opponent.position.block + opponent.speed + 1);
            if (left > 0 && right == 0 && front > 0)
                return new TweetCommand(opponent.position.lane + 1, opponent.position.block + opponent.speed + 1);

            /*
             * front = obstaclesCheckFront(myCar.position.lane, myCar.position.block,
             * myCar.speed, gameState);
             * if (myCar.position.lane > 1)
             * left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block,
             * myCar.speed, gameState);
             * if (myCar.position.lane < 4)
             * right = obstaclesCheckRight(myCar.position.lane, myCar.position.block,
             * myCar.speed, gameState);
             * 
             * if (left != 0 && right != 0 && front == 0)
             * return new TweetCommand(myCar.position.lane, myCar.position.block +
             * myCar.speed - 3);
             */
        }

        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && myCar.position.block > opponent.position.block) {
            int left = 9999;
            int right = 9999;

            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            if (left != 0 && right != 0)
                return OIL;
        }

        return ACCELERATE;
    }

    /*
     * Menghasilkan true apabila car dalam posisi maxSpeed sesuai dengan damage yang
     * dimilikinya
     */
    private Boolean isMaxSpeed(int damage, int speed) {
        if (damage == 5 && speed == 0)
            return true;
        else if (damage == 4 && speed == 3)
            return true;
        else if (damage == 3 && speed == 6)
            return true;
        else if (damage == 2 && speed == 8)
            return true;
        else
            return damage <= 1 && speed == 9;
    }

    /* Fungsi untuk mengecek lane ketika ingin belok */
    private int obstaclesCheckLeft(int lane, int block, int speed, GameState gameState) {
        List<Object> blocksInLeftLane = getBlocksInLeft(lane, block, speed, gameState);
        int sum = 0;
        for (Object contain : blocksInLeftLane) {
            if (contain == Terrain.WALL || contain == Terrain.TRUCK)
                sum += 10;
            if (contain == Terrain.PLAYER)
                sum += 2;
            if (contain == Terrain.MUD || contain == Terrain.OIL_SPILL || contain == Terrain.PLAYER)
                sum += 1;
        }

        return sum;
    }

    private int obstaclesCheckRight(int lane, int block, int speed, GameState gameState) {
        List<Object> blocksInRightLane = getBlocksInRight(lane, block, speed, gameState);
        int sum = 0;
        for (Object contain : blocksInRightLane) {
            if (contain == Terrain.WALL || contain == Terrain.TRUCK)
                sum += 10;
            if (contain == Terrain.PLAYER)
                sum += 2;
            if (contain == Terrain.MUD || contain == Terrain.OIL_SPILL || contain == Terrain.PLAYER)
                sum += 1;
        }

        return sum;
    }

    private int obstaclesCheckFront(int lane, int block, int speed, GameState gameState) {
        List<Object> blocksInFrontLane = getBlocksInFront(lane, block, speed, gameState);
        int sum = 0;
        for (Object contain : blocksInFrontLane) {
            if (contain == Terrain.WALL || contain == Terrain.TRUCK)
                sum += 10;
            if (contain == Terrain.PLAYER)
                sum += 2;
            if (contain == Terrain.MUD || contain == Terrain.OIL_SPILL)
                sum += 1;
        }

        return sum;
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
    public List<Object> getBlocksInFront(int lane, int block, int speed, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock + 1, 0); i <= block - startBlock + speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (laneList[i].occupiedByPlayerId > 0)
                laneList[i].terrain = Terrain.PLAYER;

            if (laneList[i].isOccupiedByCyberTruck)
                laneList[i].terrain = Terrain.TRUCK;

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    public List<Object> getBlocksInLeft(int lane, int block, int speed, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 2);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + speed - 1; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (laneList[i].occupiedByPlayerId > 0)
                laneList[i].terrain = Terrain.PLAYER;

            if (laneList[i].isOccupiedByCyberTruck)
                laneList[i].terrain = Terrain.TRUCK;

            blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

    public List<Object> getBlocksInRight(int lane, int block, int speed, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + speed - 1; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (laneList[i].occupiedByPlayerId > 0)
                laneList[i].terrain = Terrain.PLAYER;

            if (laneList[i].isOccupiedByCyberTruck)
                laneList[i].terrain = Terrain.TRUCK;

            blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

}