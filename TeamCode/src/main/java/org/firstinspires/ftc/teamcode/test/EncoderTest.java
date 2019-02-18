package org.firstinspires.ftc.teamcode.test;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Test: Encoders", group = "Test")
public class EncoderTest extends LinearOpMode {

    static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;
    static final double ROBOT_RADIUS = 7.62;
    static final double TILE = 23.5;
    /**
     * Radius of the mecanum wheels in inches
     */
    static final int WHEEL_DIAMETER_INCHES = 5;
    /**
     * Circumference of the mecanum wheels in inches
     */
    static final double WHEEL_CIRCUMFERENCE_INCHES = WHEEL_DIAMETER_INCHES * Math.PI;
    static double TICKS_PER_REV;
    /**
     * Encoder counts output per inch rotated by the mecanum wheels
     */
    static double TICKS_PER_INCH;

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor motors[];

    /*Drivetrain(DcMotor.RunMode mode) {
        frontLeft = hardwareMap.get(DcMotor.class, "front left");
        frontRight = hardwareMap.get(DcMotor.class, "front right");
        backLeft = hardwareMap.get(DcMotor.class, "back left");
        backRight = hardwareMap.get(DcMotor.class, "back right");

        TICKS_PER_REV = frontLeft.getMotorType().getTicksPerRev();

        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
        setMode(mode);

    }*/

    static int inchesToTicks(double inches) {
        return (int) Math.round(inches * TICKS_PER_INCH);
    }

    static int degreesToTicks(double degrees) {
        double radians = Math.toRadians(degrees);
        double inches = ROBOT_RADIUS * radians;
        return inchesToTicks(inches);
    }

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "front left");
        frontRight = hardwareMap.get(DcMotor.class, "front right");
        backLeft = hardwareMap.get(DcMotor.class, "back left");
        backRight = hardwareMap.get(DcMotor.class, "back right");

        TICKS_PER_REV = frontLeft.getMotorType().getTicksPerRev();
        TICKS_PER_INCH = (TICKS_PER_REV) / (WHEEL_CIRCUMFERENCE_INCHES);

        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();

        setPower(0.5);

        strafe(Strafe.FORWARD, TILE);
        strafe(Strafe.BACKWARD, TILE);

        turn(Turn.LEFT, 90);
        turn(Turn.RIGHT, 90);

        setPower(1.0);

        strafe(Strafe.FORWARD, TILE);

        stop();
    }

    void waitForStop() {
        while(opModeIsActive() || frontLeft.isBusy() ||frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy());
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

    void setPower(double... power) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setPower(power[i]);
        }
    }

    void strafe(Strafe direction, double inches) {
        int mods[] = direction.modifiers;
        int counts = inchesToTicks(inches);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT],
                counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
        waitForStop();
    }

    void turn(Turn direction, double degrees) {
        int mods[] = direction.modifiers;
        int counts = degreesToTicks(degrees);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT],
                counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
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
        LEFT(-1, 1, -1, 1), RIGHT(1, -1, 1, -1);

        int modifiers[];

        Turn(int... mod) {
            modifiers = mod;
        }
    }
}