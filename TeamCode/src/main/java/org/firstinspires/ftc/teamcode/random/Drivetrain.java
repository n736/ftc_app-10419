package org.firstinspires.ftc.teamcode.random;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import static org.firstinspires.ftc.teamcode.random.Robot10419.hardwareMap;

public class Drivetrain {

    static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;
    static final String configurationNames[] = {"front left", "front right", "back left", "back right"};


    static double TICKS_PER_REV;
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
    static final double COUNTS_PER_INCH = (TICKS_PER_REV) / (WHEEL_CIRCUMFERENCE_INCHES);

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor motors[];

    Drivetrain(DcMotor.RunMode mode) {
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

    }

    static int inchesToCounts(double inches) {
        return (int) Math.round(inches * Drivetrain.COUNTS_PER_INCH);
    }

    static int degreesToCounts(double degrees) {
        double radians = Math.toRadians(degrees);
        double inches = Robot10419.ROBOT_RADIUS * radians;
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

    void setPower(double... power) {
        for (int i = 0; i < motors.length; i++) {
            DcMotor motor = motors[i];
            motor.setPower(power[i]);
        }
    }

    void move(Strafe direction, double inches) {
        int mods[] = direction.modifiers;
        int counts = inchesToCounts(inches);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT], counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
    }

    void move(Turn direction, double degrees) {
        int mods[] = direction.modifiers;
        int counts = degreesToCounts(degrees);
        addTargetPosition(counts * mods[FRONT_LEFT], counts * mods[FRONT_RIGHT], counts * mods[BACK_LEFT], counts * mods[BACK_RIGHT]);
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