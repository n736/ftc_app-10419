package org.firstinspires.ftc.teamcode.robot10419;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;


public class Vuforia {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final float mmPerInch = 25.4f;
    private static final float mmFTCFieldWidth = (12 * 6) * mmPerInch;       // the width of the FTC field (from the center point to the outer panels)
    private static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor
    private static final String VUFORIA_KEY = "AZtC8eP/////AAABmSwVc1k7gEjPo6jHzaDLZcIPUHJbhaOoBhA1R0wnf+ygjY7PEsOKla3Qf74nM/pRTMjP/ppTXfBzwNpwLSVVxbojTNB8NDdVCp3ytC0sVeB1nzIH4flR9CglPR1nOvqnryPF+caxZQVxQodAKop2eWkFM9W+HIHqprwH4lPessFH9LsOgSKvtw2VX2KX0uEOUxbiytLPpCJdJiJ5uPAXeo1mBkCHE4BI/b1hQRmqNmuxYKJu4/MYC+vbBki4k46L780KqIsHacR3lpEzRihej896NtwTS+mQqklj92lJDizvZyWJT6AV6wMYARdD5QnRR1Cut3oaC/PNGO6VQNK59lUywGVs0gY5sO39HIin7hm+";
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    VuforiaLocalizer.Parameters parameters;
    List<VuforiaTrackable> allTrackables;
    VuforiaTrackables targetsRoverRuckus;
    VuforiaTrackable blueRover;
    VuforiaTrackable redFootprint;
    VuforiaTrackable frontCraters;
    VuforiaTrackable backSpace;
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private OpenGLMatrix lastLocation = null;
    private boolean targetVisible = false;

    public Vuforia() {
        this.init();
    }

    public void init() {
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            Robot10419.telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        Robot10419.telemetry.addData(">", "Press Play to start tracking");
        Robot10419.telemetry.update();

        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsRoverRuckus);

        OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                .translation(0, mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
        blueRover.setLocation(blueRoverLocationOnField);

        OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                .translation(0, -mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
        redFootprint.setLocation(redFootprintLocationOnField);

        OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                .translation(-mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90));
        frontCraters.setLocation(frontCratersLocationOnField);

        OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                .translation(mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        backSpace.setLocation(backSpaceLocationOnField);
//TODO: fill in
        final int CAMERA_FORWARD_DISPLACEMENT = 110;   // eg: Camera is 110 mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = 200;   // eg: Camera is 200 mm above ground
        final int CAMERA_LEFT_DISPLACEMENT = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        CAMERA_CHOICE == FRONT ? 90 : -90, 0, 0));

        /**  Let all the trackable listeners know where the phone is.  */
        /*for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }*/
    }

    private void initVuforia() {
        int cameraMonitorViewId = Robot10419.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", Robot10419.hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        loadTrackables();
    }

    private void loadTrackables() {
        targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");

        blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");

        redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");

        frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");

        backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");
    }

    private void initTfod() {
        int tfodMonitorViewId = Robot10419.hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", Robot10419.hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void activateTfod() {
        if (tfod != null) {
            tfod.activate();
        }
    }

    public void deactivateTfod() {
        if (tfod != null) {
            tfod.deactivate();
        }
    }

    /*public boolean isGoldMineral() {
        scanMinerals();

        Recognition rec;

        if(rec.getLabel().equals("Gold Mineral")) return true;
        return false;
    }*/

    List<Recognition> updatedRecognitions;

    public Recognition getBestMineral() {
        Recognition bestRecognition = updatedRecognitions.get(0);
        double bestAngle = Double.MAX_VALUE;

        for(int i = 0; i < updatedRecognitions.size(); i++) {
            Recognition rec = updatedRecognitions.get(i);
            double angle = Math.abs(rec.estimateAngleToObject(DEGREES));
            float confidence = rec.getConfidence();
            if(confidence > 0.85 && angle < bestAngle) {
                bestRecognition = rec;
                bestAngle = angle;
            }
        }

        updatedRecognitions = null;

        return bestRecognition;
    };

    public boolean scanMinerals() {
        if (tfod != null) {
            updatedRecognitions = tfod.getUpdatedRecognitions();
        }

        return updatedRecognitions != null && updatedRecognitions.size() > 0 && updatedRecognitions.get(0) != null;
    }

    public void activateTrackables() {
        targetsRoverRuckus.activate();
    }

    public void deactivateTrackable() {
        targetsRoverRuckus.deactivate();
    }

    /*public String getFieldSide() {
        VuforiaTrackable trackable;
        return trackable.getName().split("-")[0];
    }*/

    VuforiaTrackable visibleTrackable;

    public boolean scanNavTarget() {
        // check all the trackable target to see which one (if any) is visible.
        targetVisible = false;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                Robot10419.telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                visibleTrackable = trackable;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }/*

        // Provide feedback as to where the robot is located (if we know).
        if (targetVisible) {
            // express position (translation) of robot in inches.
            VectorF translation = lastLocation.getTranslation();
            Robot10419.telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            Robot10419.telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        } else {
            Robot10419.telemetry.addData("Visible Target", "none");
        }
        Robot10419.telemetry.update();*/

        return targetVisible;
    }

    boolean isCraterForward() {
        String side = visibleTrackable.getName();
        Robot10419.telemetry.addData("trackable", side);
        Robot10419.telemetry.update();
        return side.equals("Blue-Rover") || side.equals("Red-Footprint");
    }
}
