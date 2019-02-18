package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Robot10419 {

    public static HardwareMap hardwareMap;
    public static Telemetry telemetry;

    Drivetrain drivetrain;
    public Vuforia vuforia;
    Arm arm;
    Hanger hanger;
    IMU imu;


    public Robot10419(HardwareMap hardwareMap, Telemetry telemetry, boolean useVuforia) {
        Robot10419.hardwareMap = hardwareMap;
        Robot10419.telemetry = telemetry;

        drivetrain = new Drivetrain();
        arm = new Arm();
        hanger = new Hanger();
        imu = new IMU();
        if(useVuforia) {
            vuforia = new Vuforia();
        }
    }

}
