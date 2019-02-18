package org.firstinspires.ftc.teamcode.random;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Test: Manual Perspective Drive", group = "Test")
public class ManualPerspectiveDriveTest extends OpMode {

    private DcMotor frontLeft,
            frontRight,
            backLeft,
            backRight;


    @Override
    public void init() {

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    double angle = 0;

    @Override
    public void loop() {

        double lx =  gamepad1.left_stick_x;
        double ly = -gamepad1.left_stick_y;
        double rx =  gamepad1.right_stick_x;
        double ry = -gamepad1.right_stick_y;

        telemetry.addData("LX", lx);
        telemetry.addData("LY", ly);
        telemetry.addData("RX", rx);
        telemetry.addData("RY", ry);


        double z = ly * Math.cos(angle) + lx * -Math.sin(angle);
        double x = ly * Math.sin(angle) + lx * Math.cos(angle);

        double yaw = rx;

        if(gamepad1.left_bumper) {
            angle = (Math.acos(ry) * (rx > 0 ? -1 : 1));
        } else {
            frontLeft.setPower(x + z + yaw);
            frontRight.setPower(-x + z - yaw);
            backLeft.setPower(-x + z + yaw);
            backRight.setPower(x + z - yaw);
        }

        telemetry.addData("Angle", Math.toDegrees(angle));
        telemetry.addData("Z", z);
        telemetry.addData("X", x);
        telemetry.addData("YAW", yaw);
        telemetry.addData("FL", frontLeft.getPower());
        telemetry.addData("FR", frontRight.getPower());
        telemetry.addData("BL", backLeft.getPower());
        telemetry.addData("BR", backRight.getPower());
        telemetry.update();

    }
}