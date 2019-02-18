package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.robot10419.Drivetrain.TILE;
import static org.firstinspires.ftc.teamcode.robot10419.Drivetrain.TILE_DIAGONAL;

@Autonomous(name = "10419: Autonomous Crater", group = "10419")
public class Autonomous10419Crater extends LinearOpMode {

    Robot10419 robot;

    double TAPE_TO_CENTER = 18;
    double ROBOT_CENTER_TO_PHONE = 0;
    double NAV_TO_DEPOT = (5.0/2.0) * TILE - 13;
    double DEPOT_TO_CRATER = (7.0/2.0) * TILE - 6;
    double UNLATCH_DISTANCE = 0;
    double SCAN_TURN = 37;
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

        unlatch();

        sample();

        flipArm();


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

                robot.drivetrain.strafe(Drivetrain.Strafe.FORWARD, 4.20);
                while(opModeIsActive()  && robot.drivetrain.isBusy());

                knockDiagonalMineral();
                robot.drivetrain.turn(Drivetrain.Turn.LEFT, SCAN_TURN);
                while(opModeIsActive()  && robot.drivetrain.isBusy());
            } else {
                left = true;
                robot.drivetrain.turn(Drivetrain.Turn.LEFT, 2*SCAN_TURN);
                while(opModeIsActive()  && robot.drivetrain.isBusy());

                knockDiagonalMineral();
            }

            robot.drivetrain.turn(Drivetrain.Turn.RIGHT, SCAN_TURN);
            while(opModeIsActive()  && robot.drivetrain.isBusy());
        }

        robot.vuforia.deactivateTfod();
    }

    void flipArm() {
        robot.arm.setRotatorPower(-1.0);
        sleep(4000);
        robot.arm.setRotatorPower(0);
    }

    void knockCenterMineral() {

        robot.drivetrain.turn(Drivetrain.Turn.RIGHT, 8);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TO_CENTER);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.RIGHT, TO_CENTER);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.turn(Drivetrain.Turn.RIGHT, 90);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, TO_CENTER);
        while(opModeIsActive()  && robot.drivetrain.isBusy());
    }

    void knockDiagonalMineral() {
        robot.drivetrain.strafe(Drivetrain.Strafe.LEFT, TO_DIAGONAL);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.RIGHT, TO_DIAGONAL);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.turn(Drivetrain.Turn.RIGHT, 90);
        while(opModeIsActive()  && robot.drivetrain.isBusy());

        robot.drivetrain.strafe(Drivetrain.Strafe.BACKWARD, TO_DIAGONAL);
        while(opModeIsActive()  && robot.drivetrain.isBusy());
    }

    void scanNavTarget() {

    }

    void waitForStop() {
        while (opModeIsActive() && robot.drivetrain.isBusy()) ;
    }
}
