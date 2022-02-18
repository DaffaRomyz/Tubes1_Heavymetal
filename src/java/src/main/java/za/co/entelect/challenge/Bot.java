package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {
    private final static int MINIMUM_SPEED = 0;
    private final static int SPEED_STATE_1 = 3;
    private final static int INITIAL_SPEED = 5;
    private final static int SPEED_STATE_2 = 6;
    private final static int SPEED_STATE_3 = 8;
    private final static int MAXIMUM_SPEED = 9;
    private final static int BOOST_SPEED = 15;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command DO_NOTHING = new DoNothingCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        List<Object> blocksInFront = getBlocksInFront(myCar.position.lane, myCar.position.block, myCar.speed,
                gameState);

        /* Greedy untuk Fix */
        if (isMaxSpeed(myCar.damage, myCar.speed) && myCar.damage > 2)
            return FIX;

        /* Greedy untuk Avoid Obstacles */
        if (blocksInFront.contains(Terrain.WALL) || blocksInFront.contains(Terrain.TRUCK)) {

            /* Semakin kecil semakin prioritas untuk dilewati */
            int left = 9999;
            int right = 9999;

            /* Cek obstacles yang ada pada map, dan mencari nilai prioritasnya */
            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            /* Fungsi seleksi */
            if (left > 1 && right > 1 && hasPowerUp(PowerUps.LIZARD, myCar.powerups))
                return LIZARD;
            if (left <= right)
                return TURN_LEFT;
            else
                return TURN_RIGHT;
        }

        /* Greedy untuk Fix Command untuk menggunakan Boost */
        if ((myCar.damage == 1 || myCar.damage == 2) && hasPowerUp(PowerUps.BOOST, myCar.powerups))
            return FIX;

        if (blocksInFront.contains(Terrain.MUD) || blocksInFront.contains(Terrain.OIL_SPILL)) {

            /* Semakin kecil semakin prioritas untuk dilewati */
            int left = 9999;
            int right = 9999;

            /* Cek obstacles yang ada pada map, dan mencari nilai prioritasnya */
            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            int front = obstaclesCheckFront(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            /* Fungsi seleksi */
            if (left > 1 && right > 1 && front > 1 && hasPowerUp(PowerUps.LIZARD, myCar.powerups))
                return LIZARD;
            if (left <= right && left <= front)
                return TURN_LEFT;
            if (right <= left && right <= front)
                return TURN_RIGHT;
        }

        /* Greedy untuk menggunakan Boost */
        if (myCar.damage == 0 && hasPowerUp(PowerUps.BOOST, myCar.powerups)) {

            /*
             * Hanya akan menggunakan Boost apabila tidak akan terkena obstacles saat
             * menggunakan Boost
             */
            int front = obstaclesCheckFront(myCar.position.lane, myCar.position.block, BOOST_SPEED, gameState);
            if (front == 0)
                return BOOST;
        }

        /* Greedy untuk menggunakan EMP */
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && myCar.position.block < opponent.position.block
                && myCar.position.lane + 2 > opponent.position.lane && myCar.position.lane - 2 < opponent.position.lane)
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

            left = 9999;
            right = 9999;
            if (opponentLane > 1)
                left = obstaclesCheckLeft(opponentLane, opponent.position.block + opponent.speed,
                        opponent.speed, gameState);
            if (opponentLane < 4)
                right = obstaclesCheckRight(opponentLane, opponent.position.block + opponent.speed,
                        opponent.speed, gameState);
            front = obstaclesCheckFront(opponentLane, opponent.position.block + opponent.speed,
                    opponent.speed, gameState);

            if (left == 0 && right > 0 && front > 0)
                return new TweetCommand(opponentLane - 1, opponent.position.block + opponent.speed + 1);
            if (left > 0 && right == 0 && front > 0)
                return new TweetCommand(opponentLane + 1, opponent.position.block + opponent.speed + 1);
            if (front == 0)
                return new TweetCommand(opponentLane, opponent.position.block + opponent.speed + 1);
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

        int nextMyCarSpeed = nextSpeed(myCar.damage, myCar.speed);
        int front = obstaclesCheckFront(myCar.position.lane, myCar.position.block, nextMyCarSpeed, gameState);
        if (front == 0)
            return ACCELERATE;
        return DO_NOTHING;
    }

    /*
     * Menghasilkan true apabila car dalam posisi maxSpeed sesuai dengan damage yang
     * dimilikinya
     */
    private Boolean isMaxSpeed(int damage, int speed) {
        if (damage >= 5 && speed == MINIMUM_SPEED)
            return true;
        else if (damage == 4 && speed == SPEED_STATE_1)
            return true;
        else if (damage == 3 && speed == SPEED_STATE_2)
            return true;
        else if (damage == 2 && speed == SPEED_STATE_3)
            return true;
        else
            return damage <= 1 && speed == MAXIMUM_SPEED;
    }

    private int nextSpeed(int damage, int speed) {
        int next;
        switch (speed) {
            case MINIMUM_SPEED:
                next = SPEED_STATE_1;
                break;
            case SPEED_STATE_1:
            case INITIAL_SPEED:
                next = SPEED_STATE_2;
                break;
            case SPEED_STATE_2:
                next = SPEED_STATE_3;
                break;
            default:
                next = MAXIMUM_SPEED;
        }

        if (damage >= 5)
            next = 0;
        else if (damage == 4 && next > 3)
            next = 3;
        else if (damage == 3 && next > 6)
            next = 6;
        else if (damage == 2 && next > 8)
            next = 8;
        else if (damage == 1)
            next = 9;

        return next;
    }

    /* Fungsi untuk mengecek lane ketika ingin belok */
    private int obstaclesCheckLeft(int lane, int block, int speed, GameState gameState) {
        if (lane == 1)
            return 9999;
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
        if (lane == 4)
            return 9999;
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
    private Boolean hasPowerUp(PowerUps powerUpCheck, PowerUps[] powerups) {
        for (PowerUps powerUp : powerups) {
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