package org.firstinspires.ftc.teamcode.random;

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import java.util.Locale;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.Func;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@TeleOp(name = "MecanumDriveGryo")
public class MecanumDriveButStupider extends OpMode {

    ElapsedTime time;

    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;

    double ftpower = 0.5;

    private DcMotor frontLeft,
            frontRight,
            backLeft,
            backRight;



    @Override
    public void init() {


        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        composeTelemetry();
    }

    double angle = 0;
    double angleOffset = 0;

    @Override
    public void start() {
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        time = new ElapsedTime();
    }

    double prevTime = 0.0;

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

        angle = (angles.firstAngle - angleOffset + 180) % 360 - 180;

        if(gamepad1.left_bumper) {
            angle = 0; // -angleOffset ?
        }

        if(gamepad1.right_bumper) {
            angleOffset = angles.firstAngle;
        }

        double z = ly * Math.cos(angle) + lx * -Math.sin(angle);
        double x = ly * Math.sin(angle) + lx * Math.cos(angle);

        double yaw = rx;

        frontLeft.setPower(x + z + yaw);
        frontRight.setPower(-x + z - yaw);
        backLeft.setPower(-x + z + yaw);
        backRight.setPower(x + z - yaw);
        //}

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

    void composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
            gravity  = imu.getGravity();
        }
        });

        telemetry.addLine()
                .addData("status", new Func<String>() {
                    @Override public String value() {
                        return imu.getSystemStatus().toShortString();
                    }
                })
                .addData("calib", new Func<String>() {
                    @Override public String value() {
                        return imu.getCalibrationStatus().toString();
                    }
                });

        telemetry.addLine()
                .addData("heading", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });

        telemetry.addLine()
                .addData("grvty", new Func<String>() {
                    @Override public String value() {
                        return gravity.toString();
                    }
                })
                .addData("mag", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f",
                                Math.sqrt(gravity.xAccel*gravity.xAccel
                                        + gravity.yAccel*gravity.yAccel
                                        + gravity.zAccel*gravity.zAccel));
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}
