package org.firstinspires.ftc.teamcode.robot10419.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.claim;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.delay;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.double_sample;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.hang;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.park;
import static org.firstinspires.ftc.teamcode.robot10419.AutonomousSettings.sample;

@Autonomous(name = "settings test", group = "10419")
public class AutonomousSettingsTest extends LinearOpMode {


    /**
     * Override this method and place your code here.
     * <p>
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException {

        output("Hang", hang);
        output("Sample", sample);
        output("Delay", delay);
        output("Claim", claim);
        output("Double Sample", double_sample);
        output("Park", park);
        update();

        waitForStart();

        // add only the MANDATORY pathways between each task

        if(hang) hang();

        if(sample) sample();

        if(delay) delay();

        if(claim) claim();

        if(double_sample) double_sample();

        if(park) park();

        stop();

    }

    public void hang() {
        outputTask("hang");
    }

    public void sample() {
        outputTask("sample");
    }

    public void delay() {
        outputTask("delay");
    }

    public void claim() {
        outputTask("claim");
    }

    public void double_sample() {
        outputTask("double sample");
    }

    public void park() {
        outputTask("park");
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

    void outputTask(String task) {
        output("Current Task", task);
    }

    String booleanToTag(boolean b) {
        return b ? "Yes" : "No";
    }
}
