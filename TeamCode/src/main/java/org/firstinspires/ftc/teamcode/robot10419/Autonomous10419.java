package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.claim;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.double_sample;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.hang;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.park;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.sample;
import static org.firstinspires.ftc.teamcode.robot10419.Drivetrain.TILE;
import static org.firstinspires.ftc.teamcode.robot10419.Drivetrain.TILE_DIAGONAL;

@Autonomous(name = "10419: Autonomous", group = "10419")
public class Autonomous10419 extends LinearOpMode {

    Robot10419 robot;

    double TAPE_TO_CENTER = 18;
    double ROBOT_CENTER_TO_PHONE = 0;
    double NAV_TO_DEPOT = (5.0/2.0) * TILE - 13;
    double DEPOT_TO_CRATER = (7.0/2.0) * TILE - 6;
    double UNLATCH_DISTANCE = 0;
    double SCAN_TURN = 30;
    double TO_CENTER = TILE_DIAGONAL / 2 + 12;
    double TO_DIAGONAL = TILE + 12;
    double NAV_TO_CRATER = TILE / 2 - 10;


    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot10419(hardwareMap, telemetry, true);

        robot.drivetrain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.arm.setActuatorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.arm.setRotatorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.hanger.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        robot.drivetrain.setTargetPosition(0);
        robot.drivetrain.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.hanger.actuator.setTargetPosition(0);
        robot.hanger.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.drivetrain.setPower(1.00);

        if(hang) {
            unlatch();
        }

        if(sample) sample();

        if(claim || double_sample || park) {

            robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TAPE_TO_CENTER);
            waitForStop();

            robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, Drivetrain.TILE_DIAGONAL - 1.5);
            waitForStop();

            robot.drivetrain.turn(Drivetrain.Turn.LEFT, 49);
            waitForStop();

            robot.vuforia.activateTrackables();

            while(opModeIsActive() && !robot.vuforia.scanNavTarget());

            if (robot.vuforia.isCraterForward()) {
                telemetry.addData("TURNING", "TrUE");
                telemetry.update();
                robot.drivetrain.turn(Drivetrain.Turn.LEFT, 180);
                waitForStop();

                robot.drivetrain.strafe(Drivetrain.Strafe.RIGHT, TILE / 2 + 1);
                waitForStop();
            } else {
                telemetry.addData("TURNING", "NopeE");
                telemetry.update();

                robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TILE / 2 + 1);
                waitForStop();
            }

            robot.vuforia.deactivateTrackable();

            if (claim || double_sample) {
                robot.drivetrain.strafe(Drivetrain.Strafe.FORWARD, NAV_TO_DEPOT);
                waitForStop();

                if (double_sample) double_sample();


                if (claim) claim();

                if (park) {
                    robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, DEPOT_TO_CRATER);
                    waitForStop();

                    flipArm();
                    // flip out and extend arm
                    // why not intake, might get a mineral or two
                }
            } else {
                if (park) {
                    robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, NAV_TO_CRATER);
                    waitForStop();

                    flipArm();
                    // flip out and extend arm
                    // why not intake, might get a mineral or two
                }
            }
        }

        stop();
    }

    void claim() {

        robot.arm.outtake();
        sleep(1500);
        robot.arm.offtake();
    }

    void unlatch() {
        robot.hanger.setPower(1.0);

        robot.hanger.setTargetPosition(-23826 / 2);
        while(opModeIsActive() && robot.hanger.isBusy());

        robot.hanger.setPower(0);

        robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, 6);
        waitForStop();

        robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, 10);
        waitForStop();

        robot.drivetrain.strafe(Drivetrain.Strafe.FORWARD, 3);
        waitForStop();

        robot.drivetrain.turn(Drivetrain.Turn.LEFT, 17.8);
        waitForStop();
    }

    void lineUp() {
        //something with color sensors??
    }

    void double_sample() {
        if(center) {
            knockCenterMineral();
        } else if(right) {
            robot.drivetrain.turn(Drivetrain.Turn.RIGHT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());

            knockDiagonalMineral();
            robot.drivetrain.turn(Drivetrain.Turn.LEFT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());
        } else {
            robot.drivetrain.turn(Drivetrain.Turn.LEFT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());

            knockDiagonalMineral();

            robot.drivetrain.turn(Drivetrain.Turn.RIGHT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());
        }
    }

    boolean center = false, left = false, right = false;

    void sample() {

        robot.vuforia.activateTfod();

        while(opModeIsActive() && !robot.vuforia.scanMinerals());

        if(robot.vuforia.getBestMineral().getLabel().equals("Gold Mineral")) {
            center = true;
            knockCenterMineral();
        } else {

            robot.drivetrain.turn(Drivetrain.Turn.RIGHT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());

            sleep(750);

            while(opModeIsActive() && !robot.vuforia.scanMinerals());

            if(robot.vuforia.getBestMineral().getLabel().equals("Gold Mineral")) {
                right = true;
                knockDiagonalMineral();
                robot.drivetrain.turn(Drivetrain.Turn.LEFT, SCAN_TURN);
                while(opModeIsActive()  && robot.drivetrain.isBusy());
            } else {
                left = true;
                robot.drivetrain.turn(Drivetrain.Turn.LEFT, 2*SCAN_TURN);
                while(opModeIsActive()  && robot.drivetrain.isBusy());

                knockDiagonalMineral();

                robot.drivetrain.turn(Drivetrain.Turn.RIGHT, SCAN_TURN);
                while(opModeIsActive()  && robot.drivetrain.isBusy());
            }
        }

        robot.vuforia.deactivateTfod();
    }

    void flipArm() {
        robot.arm.setRotatorPower(-1.0);
        robot.arm.setActuatorPower(1.0);
        sleep(5000);
        robot.arm.setActuatorPower(0);
        robot.arm.setRotatorPower(0);
    }

    void knockCenterMineral() {
        robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TO_CENTER);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.RIGHT, TO_CENTER);
        while(opModeIsActive()  && robot.drivetrain.isBusy());
    }

    void knockDiagonalMineral() {
        robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TO_DIAGONAL);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.RIGHT, TO_DIAGONAL);
        while(opModeIsActive()  && robot.drivetrain.isBusy());
    }

    void scanNavTarget() {

    }

    void waitForStop() {
        while (opModeIsActive() && robot.drivetrain.isBusy()) ;
    }
}
