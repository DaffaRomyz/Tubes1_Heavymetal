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

        /*** Greedy untuk menggunakan Fix ***/

        /**
         * Greedy dilakukan dengan cara memprioritaskan FIX apabila damage yang dimiliki
         * lebih dari 2
         */

        if (isMaxSpeed(myCar.damage, myCar.speed) && myCar.damage > 2)
            return FIX;

        /*** Greedy untuk menghindari Obstacles ***/
        if (blocksInFront.contains(Terrain.WALL) || blocksInFront.contains(Terrain.TRUCK)) {

            /**
             * Greedy dilakukan dengan cara menghitung nilai prioritas pada masing-masing
             * lane, depan, kiri, maupun kanan. Kemudian memilih lane yang memiliki
             * obstacles dengan nilai terkecil
             */

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

        /*** Greedy untuk menggunakan Fix apabila memiliki Boost ***/
        if ((myCar.damage == 1 || myCar.damage == 2) && hasPowerUp(PowerUps.BOOST, myCar.powerups))
            return FIX;

        /*** Greedy untuk menghindari Obstacles ***/
        if (blocksInFront.contains(Terrain.MUD) || blocksInFront.contains(Terrain.OIL_SPILL)) {

            /**
             * Greedy dilakukan dengan cara menghitung nilai prioritas pada masing-masing
             * lane, depan, kiri, maupun kanan. Kemudian memilih lane yang memiliki
             * obstacles dengan nilai terkecil
             */

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

        /*** Greedy untuk menggunakan Boost ***/
        if (myCar.damage == 0 && hasPowerUp(PowerUps.BOOST, myCar.powerups)) {

            /*
             * Greedy dilakukan dengan cara memastikan apabila menggunakan Boost tidak
             * akan menabrak obstacles
             */

            int front = obstaclesCheckFront(myCar.position.lane, myCar.position.block, BOOST_SPEED, gameState);
            if (front == 0)
                return BOOST;
        }

        /*** Greedy untuk menggunakan EMP ***/

        /**
         * Greedy dilakukan dengan cara memastikan apakah lawan berada di posisi lane
         * yang akan terkena EMP apabila digunakan
         */

        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && myCar.position.block < opponent.position.block
                && myCar.position.lane + 2 > opponent.position.lane && myCar.position.lane - 2 < opponent.position.lane)
            return EMP;

        /*** Greedy untuk menggunakan Tweet ***/
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups) && myCar.position.block > opponent.position.block
                && opponent.speed < BOOST_SPEED) {

            /**
             * Greedy dilakukan dengan cara memprediksi gerak lawan kemana dia akan berbelok
             * dalam dua ronde, kemudian meletakkan apabila kemungkinan lawan berbelok ke
             * lane tersebut tinggi
             * 
             * Prediksi yang digunakan dengan asumsi lawan akan bergerak menuju lane yang
             * tidak memiliki obstacles atau obstaclesnya paling sedikit
             */

            /* Prediksi pergerakan lawan pertama */
            int left = 9999;
            int right = 9999;
            int front;

            if (opponent.position.lane > 1)
                left = obstaclesCheckLeft(opponent.position.lane, opponent.position.block, opponent.speed, gameState);
            if (opponent.position.lane < 4)
                right = obstaclesCheckRight(opponent.position.lane, opponent.position.block, opponent.speed, gameState);
            front = obstaclesCheckFront(opponent.position.lane, opponent.position.block, opponent.speed, gameState);

            int opponentLane;

            if (left > 1 && right > 1 && front > 1)
                opponentLane = opponent.position.lane;
            else if (left <= right && left <= front)
                opponentLane = opponent.position.lane - 1;
            else if (right <= left && right <= front)
                opponentLane = opponent.position.lane + 1;
            else
                opponentLane = opponent.position.lane;

            /* Prediksi pergerakan lawan kedua */
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

            /* Fungsi seleksi */
            if (left == 0 && right > 0 && front > 0)
                return new TweetCommand(opponentLane - 1, opponent.position.block + opponent.speed + 1);
            if (left > 0 && right == 0 && front > 0)
                return new TweetCommand(opponentLane + 1, opponent.position.block + opponent.speed + 1);
            if (front == 0)
                return new TweetCommand(opponentLane, opponent.position.block + opponent.speed + 1);
        }

        /*** Greedy untuk menggunakan Oil ***/
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && myCar.position.block > opponent.position.block) {

            /**
             * Greedy dilakukan dengan cara menggunakan oil jika lane kiri dan lane kanan
             * pemain terdapat obstacles dan jalan di depan pemain tidak terdapat obstacles
             */

            int left = 9999;
            int right = 9999;

            if (myCar.position.lane > 1)
                left = obstaclesCheckLeft(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
            if (myCar.position.lane < 4)
                right = obstaclesCheckRight(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

            if (left != 0 && right != 0)
                return OIL;
        }

        /*** Greedy untuk menggunakan Accelerate ***/

        /**
         * Greedy dilakukan dengan cara memastikan apabila menggunakan Accelerate tidak
         * akan menabrak obstacles
         */

        int nextMyCarSpeed = nextSpeed(myCar.damage, myCar.speed);
        int front = obstaclesCheckFront(myCar.position.lane, myCar.position.block, nextMyCarSpeed, gameState);
        if (front == 0)
            return ACCELERATE;
        return DO_NOTHING;
    }

    /*** Fungsi isMaxSpeed ***/

    /*
     * Fungsi akan menghasilkan true apabila car dalam posisi maxSpeed sesuai dengan
     * damage yang dimilikinya
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

    /*** Fungsi nextSpeed ***/

    /**
     * Fungsi akan menghasilkan integer berupa speed kendaraan apabila menggunakan
     * Accelerate sesuai dengan aturan yang ada, kemudian menyesuaikan dengan damage
     * yang dimilki
     */

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

    /*** Fungsi hasPowerUp ***/

    /**
     * Fungsi ini mengmbalikan true apabila powerUpCheck dimiliki oleh powerups
     * dari suatu kendaraan
     */

    private Boolean hasPowerUp(PowerUps powerUpCheck, PowerUps[] powerups) {
        for (PowerUps powerUp : powerups) {
            if (powerUp.equals(powerUpCheck))
                return true;
        }
        return false;
    }

    /*** Fungsi obstaclesCheck ***/

    /**
     * Fungsi ini menghitung nilai prioritas pada masing-masing lane yaitu kiri,
     * depan, dan kanan, berdasarkan Obstacles yang terdapat pada masing-masing lane
     */

    private int obstaclesCheckLeft(int lane, int block, int speed, GameState gameState) {
        if (lane <= 1)
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
        if (lane >= 4)
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

    /*** Fungsi getBlocks ***/

    /**
     * Fungsi ini mengmbalikan List of Terrain yang ada pada lane kiri, depan, dan
     * kanan sesuai dengan panjang speed dari input
     */

    public List<Object> getBlocksInFront(int lane, int block, int speed, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        int laneCheck = lane - 1;
        if (laneCheck < 0)
            laneCheck = 0;
        Lane[] laneList = map.get(laneCheck);
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

        int laneCheck = lane - 2;
        if (laneCheck < 0)
            laneCheck = 0;
        Lane[] laneList = map.get(laneCheck);
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