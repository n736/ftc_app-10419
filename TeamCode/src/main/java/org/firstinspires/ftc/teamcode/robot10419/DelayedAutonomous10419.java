package org.firstinspires.ftc.teamcode.robot10419;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "10419: Delayed Autonomous", group = "10419")
public class DelayedAutonomous10419 extends Autonomous10419 {
    @Override
    void scanNavTarget() {
        sleep(5000);
        super.scanNavTarget();
    }
}
