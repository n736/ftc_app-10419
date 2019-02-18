package org.firstinspires.ftc.teamcode.random;

import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.random.Robot10419.hardwareMap;

public class Arm {

    DcMotor rotator;
    DcMotor actuator;

    public Arm() {
        rotator = hardwareMap.get(DcMotor.class, "rotator");
        actuator = hardwareMap.get(DcMotor.class, "actuator");
    }

    public void extend() {

    }

    public void collapse() {

    }

    public void flip() {

    }

}
