package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import static org.firstinspires.ftc.teamcode.robot10419.Robot10419.telemetry;

public class Drivetrain {
    static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;
    static final double ROBOT_RADIUS = 7.62;
    static final double TILE = 23.5;
    static final double TILE_DIAGONAL = Math.sqrt(Math.pow(TILE, 2) + Math.pow(TILE, 2));// c = a^2 + b^2
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

    Drivetrain() {
        frontLeft = Robot10419.hardwareMap.get(DcMotor.class, "FL");
        frontRight = Robot10419.hardwareMap.get(DcMotor.class, "FR");
        backLeft = Robot10419.hardwareMap.get(DcMotor.class, "BL");
        backRight = Robot10419.hardwareMap.get(DcMotor.class, "BR");

        TICKS_PER_REV = frontLeft.getMotorType().getTicksPerRev();
        TICKS_PER_INCH = TICKS_PER_REV / WHEEL_CIRCUMFERENCE_INCHES;

        motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void mecanumDrive(double ly, double lx, double rx, double angle) {
        angle = Math.toRadians(angle);
        double z = ly * Math.cos(angle) + lx * -Math.sin(angle);
        double x = ly * Math.sin(angle) + lx * Math.cos(angle);
        double yaw = rx;

        frontLeft.setPower(z + x + yaw);
        frontRight.setPower(z - x - yaw);
        backLeft.setPower(z - x + yaw);
        backRight.setPower(z + x - yaw);

        telemetry.addData("Angle", Math.toDegrees(angle));
        telemetry.addData("Z", z);
        telemetry.addData("X", x);
        telemetry.addData("YAW", yaw);
        telemetry.addData("FL", frontLeft.getPower());
        telemetry.addData("FR", frontRight.getPower());
        telemetry.addData("BL", backLeft.getPower());
        telemetry.addData("BR", backRight.getPower());
    }

    static int inchesToTicks(double inches) {
        return (int) Math.round(inches * TICKS_PER_INCH);
    }

    static int degreesToTicks(double degrees) {
        double radians = Math.toRadians(degrees);
        double inches = ROBOT_RADIUS * radians;
        return inchesToTicks(inches);
    }

    boolean isBusy() {
        return frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy();
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

    void setTargetPosition(int position) {
        for (DcMotor motor : motors) motor.setTargetPosition(position);
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
        counts *= (2.0/3.0);
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
}
