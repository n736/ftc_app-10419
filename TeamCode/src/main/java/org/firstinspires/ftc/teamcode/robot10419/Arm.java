package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.hardware.DcMotor;


public class Arm {
    DcMotor rotator, actuator, intake;

    static double INTAKE_MAX_POWER = 1.0;
    static int ACTUATOR_EXTENSION_LENGTH = 1000;

    public Arm() {
        rotator = Robot10419.hardwareMap.get (DcMotor.class, "Rotator");
        actuator = Robot10419.hardwareMap.get(DcMotor.class, "Actuator");
        intake = Robot10419.hardwareMap.get(DcMotor.class, "Intake");

        rotator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        actuator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    void rotate(Direction direction, double angle) {

    }

    // do math to show extension of the channels length in inches given the gear ratio and actuator stuff
    void extend() {
        actuator.setTargetPosition(actuator.getCurrentPosition() + ACTUATOR_EXTENSION_LENGTH);
    }

    void contract() {
        actuator.setTargetPosition(0);
    }

    void intake() {
        intake.setPower(INTAKE_MAX_POWER);
    }

    void outtake() {
        intake.setPower(-INTAKE_MAX_POWER);
    }

    void offtake() {
        intake.setPower(0);
    }

    void setRotatorMode(DcMotor.RunMode mode) {
        rotator.setMode(mode);
    }

    void setRotatorPower(double power) {
        rotator.setPower(power);
    }

    void setActuatorMode(DcMotor.RunMode mode) {
        actuator.setMode(mode);
    }

    void setActuatorPower(double power) {
        actuator.setPower(power);
    }

    boolean isActuatorOutOfBounds(double power) {
        return (power < 0 && actuator.getCurrentPosition() <=0) /*|| (power > 0 && actuator.getCurrentPosition() > MAX*/;
    }

    void resetActuatorEncoders() {
        actuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        actuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // do something with gyro so up is up either way? or just make it relative to picking up minerals
    enum Direction {
        DOWN, UP;
    }
}
