package org.firstinspires.ftc.teamcode.random;

import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.random.Robot10419.hardwareMap;

public class Winch {

    DcMotor winch;

    public Winch() {
        winch = hardwareMap.get(DcMotor.class, "winch");
    }

    public void fold() {

    }

    public void unfold() {

    }


}
