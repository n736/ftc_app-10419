package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "10419: Autonomous Settings", group = "10419")
public class AutonomousSettings extends LinearOpMode {

    static boolean hang, sample, delay, claim, double_sample, park;

    @Override
    public void runOpMode() {

        outputAll();

        waitForStart();

        output("Select Settings", "A");
        output("Delayed Autonomous", "B");
        output("Full Autonomous", "X");
        output("Double Autonomous", "Y");
        update();

        while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b || gamepad1.x || gamepad1.y) );

        if(gamepad1.b) {
            delay = true;
            hang = true;
            sample = true;
            claim = true;
            double_sample = false;
            park = true;
        } else if(gamepad1.x) {
            hang = true;
            sample = true;
            claim = true;
            double_sample = false;
            park = true;
        } else if(gamepad1.y) {
            hang = true;
            sample = true;
            claim = true;
            double_sample = true;
            park = true;
        } else {

            output("Hang", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            hang = gamepad1.a;

            output("Hang", hang);
            output("Sample", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            sample = gamepad1.a;

            output("Hang", hang);
            output("Sample", sample);
            output("Delay", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            delay = gamepad1.a;

            output("Hang", hang);
            output("Sample", sample);
            output("Delay", delay);
            output("Claim", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            claim = gamepad1.a;

            output("Hang", hang);
            output("Sample", sample);
            output("Delay", delay);
            output("Claim", claim);
            output("Double Sample", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            double_sample = gamepad1.a;

            output("Hang", hang);
            output("Sample", sample);
            output("Delay", delay);
            output("Claim", claim);
            output("Double Sample", double_sample);
            output("Park", "?");
            update();
            sleep(500);
            while( opModeIsActive() &&  !(gamepad1.a || gamepad1.b) );
            park = gamepad1.a;
        }

        outputAll();

        while(opModeIsActive());

        stop();
    }

    void outputAll() {
        output("Hang", hang);
        output("Sample", sample);
        output("Delay", delay);
        output("Claim", claim);
        output("Double Sample", double_sample);
        output("Park", park);
        update();
    }

    void update() {
        telemetry.update();
    }

    void output(String caption, String value) {
        telemetry.addData(caption, value);
    }

    void output(String caption, boolean value) {
        output(caption, booleanToTag(value));
    }

    String booleanToTag(boolean b) {
        return b ? "Yes" : "No";
    }
}
