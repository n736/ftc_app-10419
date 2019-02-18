package org.firstinspires.ftc.teamcode.random;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "DepotAutonomous10419", group = "10419")
public class Autonomous10419 extends LinearOpMode {

    private static final double ROBOT_RADIUS = 5;
    private static final double TILE = 24;
    private static final double TILE_DIAG = Math.sqrt(Math.pow(TILE, 2) + Math.pow(TILE, 2));

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor actuator, intake, winch, rotator;
    IMU imu;


    /**
     * Override this method and place your code here.
     * <p>
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException {

        configMotors();

        rotator = hardwareMap.get(DcMotor.class, "rotator");
        actuator = hardwareMap.get(DcMotor.class, "actuator");
        intake = hardwareMap.get(DcMotor.class, "intake");
        winch = hardwareMap.get(DcMotor.class, "winch");

        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);


        waitForStart();

        setPower(0.5);

        //unfold();

        // move and unhook
        unhook();

        move(Strafe.BACKWARD, 100);
        turn(Turn.RIGHT, 45);
        move(Strafe.FORWARD, 100);

        // sample

        turn(Turn.LEFT, 90);
        move(Strafe.FORWARD, 1.5*TILE_DIAG);
        turn(Turn.LEFT, 45);
        move(Strafe.FORWARD, 2);

        flip();

        stop();
    }

    void configMotors() {

        frontLeft = hardwareMap.get(DcMotor.class, "front left");
        frontRight = hardwareMap.get(DcMotor.class, "front right");
        backLeft = hardwareMap.get(DcMotor.class, "back left");
        backRight = hardwareMap.get(DcMotor.class, "back right");

        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    void unhook() {
        move(Strafe.RIGHT, 20);
    }

    void unfold() {
        winch.setPower(0.5);
        rotator.setPower(0.5);
        sleep(2000);
        winch.setPower(0);
        rotator.setPower(0);
    }

    void flip() {
        rotator.setPower(0.5);
        sleep(500);
    }

    static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;

    /**
     * Encoder cycles gone through per rotation of REV HD Hex motors
     */
    static final int CYCLES_PER_ROTATION = 28;
    /**
     * Gear ratio of REV HD Hex motors used for drivetrain
     */
    static final int GEAR_RATIO = 40;
    /**
     * Encoder counts output per rotation of REV HD Hex motor given gear ratio
     */
    static final int COUNTS_PER_ROTATION = CYCLES_PER_ROTATION * GEAR_RATIO;
    /**
     * Radius of the mecanum wheels in inches
     */
    static final int WHEEL_RADIUS_INCHES = 4;
    /**
     * Circumference of the mecanum wheels in inches
     */
    static final double WHEEL_CIRCUMFERENCE_INCHES = 2 * WHEEL_RADIUS_INCHES * Math.PI;
    /**
     * Encoder counts output per inch rotated by the mecanum wheels
     */
    static final double COUNTS_PER_INCH = (COUNTS_PER_ROTATION) / (WHEEL_CIRCUMFERENCE_INCHES);

    DcMotor motors[];

    static int inchesToCounts(double inches) {
        return (int) Math.round(inches * COUNTS_PER_INCH);
    }

    static int degreesToCounts(double degrees) {
        double radians = Math.toRadians(degrees);
        double inches = ROBOT_RADIUS * radians;
        return inchesToCounts(inches);
    }

    void addTargetPosition(int... count) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setTargetPosition(motor.getCurrentPosition() + count[i]);
        }
    }

    void setMode(DcMotor.RunMode mode) {
        for (DcMotor motor : motors) motor.setMode(mode);
    }

    void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for (DcMotor motor : motors) motor.setZeroPowerBehavior(behavior);
    }

    void setDirection(DcMotorSimple.Direction leftDirection) {
        frontLeft.setDirection(leftDirection);
        backLeft.setDirection(leftDirection);

        frontRight.setDirection(leftDirection.inverted());
        backRight.setDirection(leftDirection.inverted());
    }

    void setPower(double power) {
        for (DcMotor motor : motors) motor.setPower(power);
    }

    void setPower(double ... power) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setPower(power[i]);
        }
    }

    void waitForStop() {
        while(frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy());
    }

    void move(Strafe direction, double inches) {
        int mods[] = direction.modifiers;
        int counts = inchesToCounts(inches);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT], counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);

        waitForStop();
    }

    void turn(Turn direction, double degrees) {
        int mods[] = direction.modifiers;
        int counts = degreesToCounts(degrees);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT], counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);

        waitForStop();
    }

    enum Strafe {
        FORWARD(1, 1, 1, 1), BACKWARD(-1, -1, -1, -1), LEFT(-1, 1, 1, -1), RIGHT(1, -1, -1, 1);

        int modifiers[];

        Strafe(int... mod) {
            modifiers = mod;
        }
    }

    enum Turn {
        LEFT(-1, 1, -1, 1), RIGHT(1, -1, 1, -1), INWARD(-1, -1, 1, 1), OUTWARD(1, 1, -1, -1);

        int modifiers[];

        Turn(int... mod) {
            modifiers = mod;
        }
    }
}
