package org.usfirst.frc.team5149.robot;



import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import edu.wpi.first.wpilibj.CameraServer;

import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibj.RobotDrive;

import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.SPI;

import edu.wpi.first.wpilibj.Spark;

import edu.wpi.first.wpilibj.Talon;

import edu.wpi.first.wpilibj.interfaces.Accelerometer;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

/**

 * The VM is configured to automatically run this class, and to call the

 * functions corresponding to each mode, as described in the IterativeRobot

 * documentation. If you change the name of this class or the package after

 * creating this project, you must also update the manifest file in the resource

 * directory.

 */

public class Robot extends IterativeRobot {

	private static final double FORWARD_SPEED = -.50;

	private static final double APPROACH_SPEED = -.45;





	@Override

	public void disabledPeriodic() {

		// TODO Auto-generated method stub

		double angle = driver.getDirectionDegrees();

		System.out.println(angle);

		System.out.println("Gyro is " + gyro.getAngle());

		Timer.delay(0.5);

		super.disabledPeriodic();

	}



	final String defaultAuto = "Default";

	final String customAuto = "My Auto";

	String autoSelected;

	

	

	final static int LEFT_MOTOR_PORT = 9 ;

	final static int RIGHT_MOTOR_PORT = 8;

	final static int DRIVER_JOYSTICK_PORT = 0;

	final static int DRIVER_LEFT_AXIS = 1;

	final static int DRIVER_RIGHT_AXIS = 5;

	final static int DRIVER_RIGHT_X_AXIS = 4;

	final static int DRIVER_LEFT_X_AXIS = 0;

	

	final static int DRIVER_RIGHT_TRIGGER = 6;

	final static int DRIVER_LEFT_TRIGGER = 5;

	

	final static int DRIVER_SWITCH_MODE_BUTTON = 1;

	final static int DRIVER_SPIN_RIGHT_POV = 90;

	final static int DRIVER_SPIN_LEFT_POV = 270;

	final static int ELEVATOR_PORT = 7;

	final static int CLIMBER_PORT =6;

	final static int RIGHT_GRABBER_PORT = 4;

	final static int LEFT_GRABBER_PORT = 5;

	final static int MANIPULATOR_JOYSTICK_PORT = 1;

	private static final int ELEVATOR_AXIS = 1;

	private static final int GRABBER_AXIS = 5;

	private static final int SWITCH_TOP_PORT = 0;

	private static final int SWITCH_BOTTOM_PORT = 1;

	private static final int DRIVER_RIGHT_BUMPER = 3;

	private static final int DRIVER_LEFT_BUMPER = 2;


	private static final char POSITION = 'R';

	private static final int DRIVER_SWITCH_DIRECTION_BUTTON = 3;

	private static final int MANIPULATOR_SWITCHMODE_BUTTON = 1;

	static boolean down = false;

	static boolean switchMode = false;
	
	static boolean switchDirectionDown = false;
	
	static boolean switchDirection = false;

	static boolean switchModeManipulator = false;
	
	static boolean switchModeManipulatorDown = false;

	Talon leftMotor = new Talon(LEFT_MOTOR_PORT);

	Talon rightMotor = new Talon(RIGHT_MOTOR_PORT);

	DigitalInput topSwitch= new DigitalInput(SWITCH_TOP_PORT);

	DigitalInput bottomSwitch = new DigitalInput(SWITCH_BOTTOM_PORT);

	SendableChooser<String> chooser = new SendableChooser<>();

	DifferentialDrive robot = new DifferentialDrive(leftMotor, rightMotor);

	

	

	Talon elevator = new Talon(ELEVATOR_PORT);

	Talon climber = new Talon(CLIMBER_PORT);

	Spark rightGrabber = new Spark(RIGHT_GRABBER_PORT);

	Spark leftGrabber = new Spark(LEFT_GRABBER_PORT);

	Joystick driver = new Joystick(DRIVER_JOYSTICK_PORT);

	Joystick manipulator = new Joystick(MANIPULATOR_JOYSTICK_PORT);

	ADXRS450_Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

	



	/**

	 * This function is run when the robot is first started up and should be used

	 * for any initialization code.

	 */

	@Override

	public void robotInit() {

		chooser.addDefault("Default Auto", defaultAuto);

		chooser.addObject("My Auto", customAuto);

		CameraServer.getInstance().startAutomaticCapture();

		SmartDashboard.putData("Auto choices", chooser);

		

	}



	/**

	 * This autonomous (along with the chooser code above) shows how to select

	 * between different autonomous modes using the dashboard. The sendable chooser

	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,

	 * remove all of the chooser code and uncomment the getString line to get the

	 * auto name from the text box below the Gyro

	 *

	 * You can add additional auto modes by adding additional comparisons to the

	 * switch structure below with additional strings. If using the SendableChooser

	 * make sure to add them to the chooser code above as well.

	 */

	

	@Override

	public void autonomousInit() {
		gyro.calibrate();
		gyro.reset();
		phaseCounter = 0;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		auton = new RobotAuton(POSITION,robot,rightGrabber,leftGrabber,elevator,gyro,topSwitch,bottomSwitch,gameData.charAt(0),gameData.charAt(1));
		phaseStartTime = System.currentTimeMillis();
		
	}
	RobotAuton auton;
	int phaseCounter;
	double phaseStartTime;
	String gameData;

	/**

	 * This function is called periodically during autonomous

	 */

	@Override

	public void autonomousPeriodic() {
		double elaspedPhaseTime = System.currentTimeMillis() - phaseStartTime;
		if (phaseCounter < auton.times.length) {
			if (auton.times[phaseCounter] < elaspedPhaseTime) {
				auton.zeroAllMotors();
				phaseCounter++;
				phaseStartTime = System.currentTimeMillis();
			} else {
				auton.runCurrentPhase(phaseCounter,elaspedPhaseTime);
			}
		}

	}

	

	
	/**

	 * This function is called periodically during operator control

	 */

	

	

	// Git comment test

	@Override

	public void teleopPeriodic() {

		

		driveOptions();

		elevator();

		grabber();



	}



	/**

	 * This function is called periodically during test mode

	 */

	@Override

	public void testPeriodic() {

	}



	public void driveOptions() {

					double leftPower;		// Amount of power that should go to the left motor (between 0 and 1)

					double rightPower;		// Amount of power that should go the the right motor (between 0 and 1)

					double speedFactor;		// Factor by which the left and right powers are multiplied by, will reduce powers if less than 1

					boolean switchModeButton; // Keeps track of button, that when pressed, will toggle the state of switchMode 
					
					boolean switchDirectionButton; // Keeps track of button, that when pressed, will toggle the state of switchDirection
					
					boolean rightTrigger;	 // See's if right trigger is pressed
					boolean leftTrigger; 	// See's if left trigger is pressed
					int dpad;				// Keeps track of which dPad direction is pressed

					
					
					

					leftPower  = driver.getRawAxis(DRIVER_LEFT_AXIS);		// Get the position of the left axis 

					rightPower = driver.getRawAxis(DRIVER_RIGHT_AXIS);		// Get the position of the right axis

					rightTrigger = driver.getRawButton(DRIVER_RIGHT_TRIGGER);
					leftTrigger = driver.getRawButton(DRIVER_LEFT_TRIGGER);

					switchModeButton = driver.getRawButton(DRIVER_SWITCH_MODE_BUTTON);	// Get the current state of the switchModeButton
			
					switchDirectionButton = driver.getRawButton(DRIVER_SWITCH_DIRECTION_BUTTON);
					dpad = driver.getPOV(0);				// Get the dpad direction pressed in degrees

					
					

					if (switchModeButton && !down) {		// down checks if the button was already pressed

						switchMode = !switchMode;

						down = true;

					} else if (!switchModeButton) {

						down = false;

					}

					if (switchDirectionButton && !switchDirectionDown) {
						switchDirection = !switchDirection;
						switchDirectionDown = true;
					}else if (!switchDirectionButton) {
						switchDirectionDown = false;
					}

					speedFactor = (switchMode) ? .5 : 1;

					if (switchDirection) {
						double tmp;
						tmp = leftPower;			//Swap
						leftPower = rightPower;
						rightPower = tmp;
						rightPower *= -1;			// negate
						leftPower *= -1;
					}

					leftPower = leftPower * speedFactor;

					rightPower = rightPower * speedFactor;
					
					if (rightTrigger) {
						robot.tankDrive(1 * speedFactor, -1 * speedFactor);
					}
					else if (leftTrigger) {
						robot.tankDrive(-1 * speedFactor, 1 * speedFactor);
					}
					else {
						switch (dpad) {
	
						case 180:
							if (switchDirection)
								rightPower = leftPower;
							
							robot.tankDrive(rightPower, rightPower);
	
							break;
	
						default:
							robot.tankDrive(rightPower, leftPower);
	
						}
					}
					

	}

	

	public void elevator() {
		
		double elevatorPower = manipulator.getRawAxis(ELEVATOR_AXIS);
		
		if (manipulator.getRawButton(MANIPULATOR_SWITCHMODE_BUTTON) && !switchModeManipulatorDown) {
			switchModeManipulator = !switchModeManipulator;
			switchModeManipulatorDown = true;
		}else if (!manipulator.getRawButton(MANIPULATOR_SWITCHMODE_BUTTON)) {
			switchModeManipulatorDown = false;
		}
		setElevatorPowerSafe(elevatorPower);
		

	}



	private void setElevatorPowerSafe(double elevatorPower) {

		boolean top = !topSwitch.get();

		boolean bottom = !bottomSwitch.get();

		boolean stopTop = elevatorPower < 0 && top;

		boolean stopBottom = elevatorPower > 0 && bottom;

		double speedFactor;
		
		speedFactor = (switchModeManipulator) ? .5 : 1;
		
		if (stopTop || stopBottom) {

			elevator.set(0);

		}

		else {

			elevator.set(elevatorPower * speedFactor);

		}

	}

	

	public void grabber() {

		double grabberPower = manipulator.getRawAxis(GRABBER_AXIS);

		double speedFactor;
		
		
		speedFactor = (switchModeManipulator) ? .5 : 1;
		
		rightGrabber.set(grabberPower * speedFactor);

		leftGrabber.set(-1 *grabberPower * speedFactor);

	}

	

}