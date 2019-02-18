package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "10419: TeleOp", group = "10419")
public class TeleOp10419 extends OpMode {
    Robot10419 robot;

    @Override
    public void init() {
        robot = new Robot10419(hardwareMap, telemetry, false);

        robot.drivetrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.arm.setActuatorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.arm.setRotatorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.hanger.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    boolean endGame = false;
    double angle;

    @Override
    public void start() {
        robot.imu.start();

        robot.imu.setOffset(robot.imu.getAngle());
    }

    @Override
    public void loop() {
        double ly = -gamepad1.left_stick_y;
        double lx = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if(gamepad1.left_stick_button) {
            robot.imu.setOffset();
        }
        if(gamepad2.left_bumper) {
            if(gamepad2.right_stick_button) {
                // use the right stick to determine angle
                double angle = Math.toDegrees(Math.acos(-gamepad2.right_stick_y)) * (gamepad2.right_stick_x > 0 ? -1 : 1);
                robot.imu.setOffset(angle);
            }
        }
        if(!gamepad1.right_stick_button) {
            angle = robot.imu.getAngle();
        }

        robot.drivetrain.mecanumDrive(ly, lx, rx, angle);

        double triggers1 = gamepad1.right_trigger - gamepad1.left_trigger;
        double triggers2 = gamepad2.right_trigger - gamepad2.left_trigger;
        double rotatorPower = triggers1 > 0.1 ? triggers1 : triggers1 + triggers2 ;
        robot.arm.setRotatorPower(rotatorPower);

        if(!isSwitchingControllers()) {
            if (gamepad1.a) robot.arm.intake();
            else if (gamepad1.y) robot.arm.outtake();
            else if (gamepad1.b) robot.arm.offtake();
            else if (gamepad2.a) robot.arm.intake();
            else if (gamepad2.y) robot.arm.outtake();
            else if (gamepad2.b) robot.arm.offtake();
        }

        double hangerPower = 0;
        double actuatorPower = 0;

        if(!gamepad2.left_bumper) {
            hangerPower = -gamepad2.right_stick_y;
            actuatorPower = -gamepad2.left_stick_y;
        }

        if(endGame) {
            hangerPower = gamepad1.right_bumper ? 1.0 : (gamepad1.left_bumper ? -1.0 : hangerPower);
        } else {
            actuatorPower = gamepad1.right_bumper ? 1.0 : (gamepad1.left_bumper ? -1.0 : actuatorPower);
        }

        //if(gamepad1.x || gamepad2.x || !robot.hanger.isOutOfBounds(hangerPower))
            robot.hanger.setPower(-hangerPower);
        //else robot.hanger.setPower(0);
       // if(gamepad1.x || gamepad2.x || !robot.arm.isActuatorOutOfBounds(actuatorPower))
            robot.arm.setActuatorPower(actuatorPower);
        //else robot.arm.setActuatorPower(0);

        if(gamepad1.dpad_right) endGame = true;
        else if(gamepad1.dpad_left) endGame = false;

        if(gamepad2.dpad_up) robot.hanger.latch();
        else if(gamepad2.dpad_down) robot.hanger.unlatch();
        else if(gamepad2.dpad_right) robot.hanger.resetEncoders();
        else if(gamepad2.dpad_left) robot.arm.resetActuatorEncoders();

        telemetry.addData("actuator", robot.arm.actuator.getCurrentPosition());
        telemetry.addData("hanger", robot.hanger.actuator.getCurrentPosition());

        telemetry.addData("angle", Math.atan(gamepad1.right_stick_y/gamepad1.right_stick_x));

        telemetry.update();

    }

    boolean isSwitchingControllers() {
        return (gamepad1.start && (gamepad1.a || gamepad1.b) )
            || (gamepad2.start && (gamepad2.a || gamepad2.b) );
    }
}
