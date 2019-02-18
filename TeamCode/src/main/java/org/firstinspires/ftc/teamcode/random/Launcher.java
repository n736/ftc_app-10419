package org.firstinspires.ftc.teamcode.random;

public class Launcher {


    DcMotor motor;

    Launcher() {
        motor = hardwareMap.get(DcMotor.class, "launcher motor");
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    void turn180() {
        motor.setTargetPosition(motor.getCurrentPosition() + 180);
    }
}
