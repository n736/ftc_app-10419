package org.firstinspires.ftc.teamcode.old_autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "CraterAutonomous10419", group = "10419")
public class CraterAutonomous10419 extends LinearOpMode {

    static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;
    DcMotor motors[];

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor actuator, intake, winch, rotator;

    static final double ROBOT_RADIUS = 6;
    static final int WHEEL_DIAMETER_INCHES = 5;
    static final double WHEEL_CIRCUMFERENCE_INCHES = WHEEL_DIAMETER_INCHES * Math.PI;

    static double TICKS_PER_REV;
    static double TICKS_PER_INCH;

    @Override
    public void runOpMode() throws InterruptedException {

        configure();

        waitForStart();

        setPower(0.5);

        unfold();

        unhook();

        flipIn();

        strafe(Strafe.BACKWARD, 200);

        flipOut();

        stop();
    }


    void configure() {
        frontLeft = hardwareMap.get(DcMotor.class, "front left");
        frontRight = hardwareMap.get(DcMotor.class, "front right");
        backLeft = hardwareMap.get(DcMotor.class, "back left");
        backRight = hardwareMap.get(DcMotor.class, "back right");

        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        TICKS_PER_REV = frontLeft.getMotorType().getTicksPerRev();
        TICKS_PER_INCH = (TICKS_PER_REV) / (WHEEL_CIRCUMFERENCE_INCHES);

        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rotator = hardwareMap.get(DcMotor.class, "rotator");
        actuator = hardwareMap.get(DcMotor.class, "actuator");
        intake = hardwareMap.get(DcMotor.class, "intake");
        winch = hardwareMap.get(DcMotor.class, "winch");
    }

    void unhook() {
        strafe(Strafe.RIGHT, 15);
    }

    void unfold() {
        actuator.setPower(1);
        sleep(500);
        actuator.setPower(0);

        winch.setPower(0.5);
        rotator.setPower(0.5);
        sleep(2000);
        winch.setPower(0);
        rotator.setPower(0);
    }

    void flipIn() {
        rotator.setPower(0.5);
        sleep(500);
        rotator.setPower(-0.1);
    }

    void flipOut() {
        rotator.setPower(-0.5);
        sleep(1500);
        rotator.setPower(0);
        actuator.setPower(1.0);
        sleep(2500);
        actuator.setPower(0);
        rotator.setPower(-0.5);
        sleep(3000);
        rotator.setPower(0);
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

    void strafe(Strafe direction, double inches) {
        int mods[] = direction.modifiers;
        int counts = inchesToTicks(inches);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT],
                counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
    }

    void turn(Turn direction, double degrees) {
        int mods[] = direction.modifiers;
        int counts = degreesToTicks(degrees);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT],
                counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
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

    void setPower(double power) {
        for (DcMotor motor : motors) motor.setPower(power);
    }

    void setPower(double... power) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setPower(power[i]);
        }
    }


    void addTargetPosition(int... count) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setTargetPosition(motor.getCurrentPosition() + count[i]);
        }
    }

    static int degreesToTicks(double degrees) {
        double radians = Math.toRadians(degrees);
        double inches = ROBOT_RADIUS * radians;
        return inchesToTicks(inches);
    }

    static int inchesToTicks(double inches) {
        return (int) Math.round(inches * TICKS_PER_INCH);
    }
}
