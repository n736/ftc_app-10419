package org.firstinspires.ftc.teamcode.random;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

class Robot10419 {

    static final double ROBOT_RADIUS = -1;

    static HardwareMap hardwareMap;

    IMU imu;
    Drivetrain drivetrain;
    Winch winch;
    Arm arm;
    ArmIntake intake;
    // Launcher launcher;

    double angle = 0;

    Robot10419(HardwareMap hardwareMap, DcMotor.RunMode drivetrainRunMode) {
        Robot10419.hardwareMap = hardwareMap;

        drivetrain = new Drivetrain(drivetrainRunMode);

        imu = new IMU();
    }
}