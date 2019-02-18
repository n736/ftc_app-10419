package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Hanger {
    DcMotor actuator;

    int EXTEND_POSITION = 1000, HANG_POSITION = 800, UNLATCH_POSITION = 0;

    public Hanger() {
        actuator = Robot10419.hardwareMap.get(DcMotor.class, "Hanger");
        actuator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    void setPower(double power) {
        actuator.setPower(power);
    }
    void setTargetPosition(int position) {

        actuator.setTargetPosition(position);
    }

    boolean isBusy() {
        return actuator.isBusy();
    }

    void setMode(DcMotor.RunMode mode) {
        actuator.setMode(mode);
    }

    void latch() {
        actuator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    void unlatch() {
        actuator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    boolean isOutOfBounds(double power) {
        return (power < 0 && actuator.getCurrentPosition() <=0) /*|| (power > 0 && actuator.getCurrentPosition() > MAX*/;
    }

    void resetEncoders() {
        actuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        actuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
