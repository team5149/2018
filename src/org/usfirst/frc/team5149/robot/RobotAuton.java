package org.usfirst.frc.team5149.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotAuton {
	private final int STABILIZE = 0;
	private final int FORWARD = 1;
	private final int TURNRIGHT = 2;
	private final int TURNLEFT = 3;
	private final int APPROACH = 4;
	private final int LIFT = 5;
	private final int RELEASE = 6;

	
	public int[] phases;
	public int[] times;
	private DifferentialDrive robot;
	private Spark rightArm;
	private Spark leftArm;
	private Talon elevator;
	private ADXRS450_Gyro gyro;
	private DigitalInput topSwitch;
	private DigitalInput bottomSwitch;
	public RobotAuton(int position,DifferentialDrive robotPassed, Spark rightPassed, Spark leftPassed, Talon elevatorPassed, ADXRS450_Gyro gyroPassed,DigitalInput topSwitchPassed, DigitalInput bottomSwitchPassed,char ourSwitch, char ourScale) {
		robot = robotPassed;
		rightArm = rightPassed;
		leftArm = leftPassed;
		elevator = elevatorPassed;
		gyro = gyroPassed;
		topSwitch = topSwitchPassed;
		bottomSwitch = bottomSwitchPassed;
		
		if (position == 0 || position == 5) {
			if (ourSwitch == 'R') {
				phases = new int[6];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				phases[2] = TURNLEFT;
				phases[3] = APPROACH;
				phases[4] = LIFT;
				phases[5] = RELEASE;
				times = new int[6];
				times[0] = 3000;
				times[1] = 1000;
				times[2] = 1000;
				times[3] = 1000;
				times[4] = 1000;
				times[5] = 1000;
			} 
			else if (ourScale == 'R') {
				phases = new int[6];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				phases[2] = TURNLEFT;
				phases[3] = APPROACH;
				phases[4] = LIFT;
				phases[5] = RELEASE;
				times = new int[6];
				times[0] = 3000;
				times[1] = 1000;
				times[2] = 1000;
				times[3] = 1000;
				times[4] = 1000;
				times[5] = 1000;
			}else {
				phases = new int[2];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				times = new int[2];
				times[0] = 3000;
				times[1] = 1000;
			}
			
		}
		else if (position == 1 || position == 4) {
			phases = new int[2];
			phases[0] = STABILIZE;
			phases[1] = FORWARD;
			times = new int[2];
			times[0] = 3000;
			times[1] = 1000;
		}
		else if (position == 2 || position == 3) {
			if (ourSwitch == 'L') {
				phases = new int[6];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				phases[2] = TURNRIGHT;
				phases[3] = APPROACH;
				phases[4] = LIFT;
				phases[5] = RELEASE;
				times = new int[6];
				times[0] = 1000;
				times[1] = 1000;
				times[2] = 1000;
				times[3] = 1000;
				times[4] = 1000;
				times[5] = 1000;
			}
			else if (ourScale == 'L') {
				phases = new int[6];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				phases[2] = TURNRIGHT;
				phases[3] = APPROACH;
				phases[4] = LIFT;
				phases[5] = RELEASE;
				times = new int[6];
				times[0] = 1000;
				times[1] = 1000;
				times[2] = 1000;
				times[3] = 1000;
				times[4] = 1000;
				times[5] = 1000;
			}else {
				phases = new int[2];
				phases[0] = STABILIZE;
				phases[1] = FORWARD;
				times = new int[2];
				times[0] = 1000;
				times[1] = 1000;
			}
			
		}
	}
	public void stabilizeBox(double passedTime) {
		if (passedTime < 1000) {
			liftElevator(1);
		}
		else if (passedTime < 2000) {
			liftElevator(-1);
		}
		else if (passedTime < 3500) {
			runGrabber(-.3);
		}
	}
	public void liftElevator(double elevatorPower) {
		boolean top = !topSwitch.get();
		boolean bottom = !bottomSwitch.get();
		boolean stopTop = elevatorPower < 0 && top;

		boolean stopBottom = elevatorPower > 0 && bottom;

		

		if (stopTop || stopBottom) {

			elevator.set(0);

		}

		else {

			elevator.set(elevatorPower);

		}
	}
	public void runGrabber(double power) {
		double speed = power;

		rightArm.set(speed);

		leftArm.set(-1 * speed);

	}
	public void turnRightAngle() {
		double desiredAngle = 90;

		double angle = gyro.getAngle();

		double kp = 0.011;

		double error = desiredAngle - angle;

		double output = error * kp;

		robot.tankDrive(output, -1 * output);
	}
	public void turnLeftAngle() {
		double desiredAngle = -90;

		double angle = gyro.getAngle();
		
		if (angle > 180)
			angle -= 360;
		
		double kp = 0.011;

		double error = desiredAngle - angle;

		double output = error * kp;

		robot.tankDrive(output, -1 * output);
	}
	public void autonDriveForward() {
		double angle = gyro.getAngle();
		double power = .55;
		if (angle > 180) {
			angle -= 360;

		}

		if (angle > 10 || angle < -10) {

			double kp = 0.011;

			double output = kp * angle;

			double leftPower = power - output;

			double rightPower = power + output;
			
			leftPower *= -1;
			rightPower *= -1;

			robot.tankDrive(rightPower, leftPower);

		}

		else {

			robot.tankDrive(power, power);

		}
	}
	public void autonApproachForward() {
		robot.tankDrive(-.45,-.45);
	}
	public void zeroAllMotors() {
		robot.tankDrive(0,0);

		rightArm.set(0);

		leftArm.set(0);

		elevator.set(0);
	}
	public void runCurrentPhase(int i,double passedTime) {
		if (phases[i] == STABILIZE) {
			stabilizeBox(passedTime);
		}
		else if (phases[i] == FORWARD) {
			autonDriveForward();
		}
		else if (phases[i] == TURNRIGHT) {
			turnRightAngle();
		}
		else if (phases[i] == TURNLEFT) {
			turnLeftAngle();
		}
		else if (phases[i] == APPROACH) {
			autonApproachForward();
		}
		else if (phases[i] == LIFT) {
			liftElevator(1);
		}
		else if (phases[i] == RELEASE) {
			runGrabber(.3);
		}
	}
	
}
