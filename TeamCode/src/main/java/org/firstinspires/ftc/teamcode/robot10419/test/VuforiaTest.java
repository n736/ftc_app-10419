package org.firstinspires.ftc.teamcode.robot10419.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot10419.Robot10419;

@TeleOp(name = "Robot10419 Test: Vuforia", group = "Robot10419 Test")
public class VuforiaTest extends LinearOpMode {
    Robot10419 robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot10419(hardwareMap, telemetry);

        waitForStart();

        robot.vuforia.activateTfod();

        while(opModeIsActive()) {
            if(robot.vuforia.scanMinerals())
                telemetry.addData("BLOCK", robot.vuforia.getBestMineral());
        }

        robot.vuforia.deactivateTfod();

        stop();
    }
}
