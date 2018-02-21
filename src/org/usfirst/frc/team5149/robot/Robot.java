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
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 * 
 */
public class Robot extends IterativeRobot {
	private static final double FORWARD_SPEED = -.5;
	private static final double APPROACH_SPEED = -.5;


	
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
	private static final int MANIPULATOR_RELEASE_BUTTON = 1;
	private static final int MANIPULATOR_GRAB_BUTTON = 3;
	private static final int MANIPULATOR_ELEVATOR_UP = 2;
	private static final int MANIPULATOR_ELEVATOR_DOWN = 4;
	private static final int DRIVER_FORWARD_POV = 0;
	private static final int DRIVER_BACKWARD_POV = 180;
	private static final int DRIVER_REVERSE_DIRECTION_BUTTON = 3;
	private static final int CLIMBER_BUMPER = 3;
	private static final int CLIMBER_BUMPER_LEFT = 2;
	static boolean down = false;
	static boolean switchMode = false;
	static boolean switchDirectionDown = false;
	static boolean switchDirection= false;
	
	Talon leftMotor = new Talon(LEFT_MOTOR_PORT);
	Talon rightMotor = new Talon(RIGHT_MOTOR_PORT);
	DigitalInput topSwitch= new DigitalInput(SWITCH_TOP_PORT);
	DigitalInput bottomSwitch = new DigitalInput(SWITCH_BOTTOM_PORT);
	SendableChooser<String> chooser = new SendableChooser<>();
	DifferentialDrive robot = new DifferentialDrive(leftMotor, rightMotor);
	
	
	Talon elevator = new Talon(ELEVATOR_PORT);
	Spark climber = new Spark(CLIMBER_PORT);
	Spark rightGrabber = new Spark(RIGHT_GRABBER_PORT);
	Spark leftGrabber = new Spark(LEFT_GRABBER_PORT);
	Joystick driver = new Joystick(DRIVER_JOYSTICK_PORT);
	Joystick manipulator = new Joystick(MANIPULATOR_JOYSTICK_PORT);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	
	String gameData;
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
		//autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		gyro.calibrate();
		gyro.reset();
		phase = forwardPhase;
		phaseStartTime = System.currentTimeMillis();
		//gameData = DriverStation.getInstance().getGameSpecificMessage();
		System.out.println("test test");
	
		
	}
	int phase;
	final int forwardPhase = 0;
	final double forwardTime = 3000;
	
	final int turnPhase = 1;
	final double turnTime = 3000;
	
	final int approachPhase = 2;
	final double approachTime = 2000;
	
	final int elevatorPhase = 30;
	final double elevatorTime = 4000;
	
	final int dropPhase = 3;
	final double dropTime = 1000;
	

	final int done = 4;
	boolean test = true;
	double phaseStartTime;
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
		
		double elaspedPhaseTime = System.currentTimeMillis() - phaseStartTime;
	
		System.out.println("working");
		if (phase == forwardPhase) {
			if (forwardTime < elaspedPhaseTime ) {
				phase = turnPhase;
				phaseStartTime = System.currentTimeMillis();
			}
			else {
				autonDriveForward(FORWARD_SPEED);
				System.out.println("testing");
			}
		}
		else if (phase == turnPhase) {
			if (turnTime < elaspedPhaseTime ) {
				phase = approachPhase;
				phaseStartTime = System.currentTimeMillis();
			}
			else {
				turnAngle();
			}
		}
		else if (phase == approachPhase) {
			if (approachTime < elaspedPhaseTime ) {
				phase = dropPhase;
				phaseStartTime = System.currentTimeMillis();
				zeroAllMotors();
			}
			else {
				autonDriveForward(APPROACH_SPEED);
			}
		}
		else if (phase == elevatorPhase) {
			if (approachTime < elaspedPhaseTime ) {
				phase = dropPhase;
				phaseStartTime = System.currentTimeMillis();
			}
			else {
				liftElevator();
			}
		}
		else if (phase == dropPhase) {
			if (approachTime < elaspedPhaseTime ) {
				phase = done;
				phaseStartTime = System.currentTimeMillis();
			}
			else {
				spinGrabber();
			}
		}
		else if (phase == done) {
			zeroAllMotors();
			System.out.println("done done done");
		}
		
	}
	
	public void autonDriveForward(double power) {
		
		robot.tankDrive(power, power);
		/**
		double angle = gyro.getAngle();
		if (angle > 180) {
			angle -= 360;
		}
		if (angle > 10 || angle < -10 && 0!= 0 ) {
			double kp = 0.011;
			double output = kp * angle;
			double leftPower = power - output;
			double rightPower = power + output;
			robot.tankDrive(rightPower, leftPower);
		}
		else {
			robot.tankDrive(power, power);
		}
		**/
	}
	
	public void liftElevator() {
		setElevatorPowerSafe(1.0);
	}
	
	public void spinGrabber() {
		double speed = .5;
		rightGrabber.set(speed);
		leftGrabber.set(-1 * speed);
	}
	
	public void turnAngle() {
		double desiredAngle = 110;
		double angle = gyro.getAngle();
		double kp = 0.011;
		double error = desiredAngle - angle;
		double output = error * kp;
		robot.tankDrive(output, -1 * output);
		
	}
	public void zeroAllMotors() {
		robot.tankDrive(0,0);
		rightGrabber.set(0);
		leftGrabber.set(0);
		elevator.set(0);
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
		climber();

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	public void driveOptions() {
		// When true, the power values will be multiplied by the slow power, reducing the total power
				
			
					double leftPower;		// Amount of power that should go to the left motor (between 0 and 1)
					double rightPower;		// Amount of power that should go the the right motor (between 0 and 1)
					double speedFactor;		// Factor by which the left and right powers are multiplied by, will reduce powers if less than 1
					boolean switchModeButton; // Keeps track of button, that when pressed, will toggle the state of switchMode 
					double rightOffset; // The offset of the rightPower, which when greater than 0, will curve the robot right
					double leftOffset;  // The offset of the leftPower, which when greater than 0, will curve the robot left
					double rightMultipler;  // Amount by which the rightOffset is multiplied by, used to increase the sharpness of the curve 
					double leftMultipler;	// Amount by which the leftOffset is multiplied by, used to increase the sharpness of the curve
					int dpad;			// Keeps track of which dPad direction is pressed
					boolean switchDirectionButton;
					
					
					leftPower  = driver.getRawAxis(DRIVER_LEFT_AXIS);		// Get the position of the left axis 
					rightPower = driver.getRawAxis(DRIVER_RIGHT_AXIS);		// Get the position of the right axis
					
					switchModeButton = driver.getRawButton(DRIVER_SWITCH_MODE_BUTTON);	// Get the current state of the switchModeButton
					switchDirectionButton = driver.getRawButton(DRIVER_REVERSE_DIRECTION_BUTTON);
					
					rightMultipler = driver.getRawAxis(DRIVER_RIGHT_BUMPER);		// Get the intensity of the right bumper
					leftMultipler = driver.getRawAxis(DRIVER_LEFT_BUMPER);		// Get the intensity of the left bumper
					
					rightOffset = .4 * (1+rightMultipler);		
					leftOffset = .4 * (1 + leftMultipler);
			
					dpad = driver.getPOV(0);				// Get the dpad direction pressed in degrees
					
					if (rightPower < 0)
						rightOffset *= -1;
					if (leftPower < 0)
						leftOffset *= -1;
					
					if (switchModeButton && !down) {		// down checks if the button was already pressed
						switchMode = !switchMode;
						down = true;
					} else if (!switchModeButton) {
						down = false;
					}

					
					if (switchDirectionButton  && !switchDirectionDown) {
						
						switchDirection = !switchDirection;
						switchDirectionDown = true;
					}else if (!switchDirectionButton){
						switchDirectionDown = false;
					}
					speedFactor = (switchMode) ? .5 : 1;
					
					if (switchDirection) {
						speedFactor *= -1;
						double tmp = leftPower;
						leftPower = rightPower;
						rightPower = tmp;
					}
					leftPower = leftPower * speedFactor;
					rightPower = rightPower * speedFactor;
					
					if (!driver.getRawButton(DRIVER_RIGHT_TRIGGER))
						rightOffset = 0;
					if (!driver.getRawButton(DRIVER_LEFT_TRIGGER))
						leftOffset = 0;

					switch (dpad) {
					case DRIVER_SPIN_RIGHT_POV:
						robot.tankDrive(1 * speedFactor, -1 * speedFactor);
						break;
					case DRIVER_SPIN_LEFT_POV:
						robot.tankDrive(-1 * speedFactor, 1 * speedFactor);
						break;
					case DRIVER_FORWARD_POV:
						robot.tankDrive(-1 * speedFactor, -1 * speedFactor);
						break;
					case DRIVER_BACKWARD_POV:
						robot.tankDrive(1 * speedFactor, 1 * speedFactor);
						break;
						
					default:
						
						robot.tankDrive(rightPower - rightOffset, leftPower-leftOffset);
					}
						
					//boolean state = limitSwitch.get();
			
					//SmartDashboard.putBoolean("The switch is ", state);
					//System.out.println("the switch is currently " + state);
					 
		
	}
	
	public void elevator() {
		double elevatorPower = manipulator.getRawAxis(ELEVATOR_AXIS); // multiplied by negative one, on robot they inverted
		if (manipulator.getRawButton(MANIPULATOR_ELEVATOR_UP)) {
			elevatorPower = .5;
		}
		else if (manipulator.getRawButton(MANIPULATOR_ELEVATOR_DOWN)) {
			elevatorPower = -.5;
		}
		setElevatorPowerSafe(elevatorPower);
		
	}

	private void setElevatorPowerSafe(double elevatorPower) {
		boolean top = !topSwitch.get();
		boolean bottom = !bottomSwitch.get();
		boolean stopTop = elevatorPower < 0 && top;
		boolean stopBottom = elevatorPower > 0 && bottom;
		
		if (stopTop || stopBottom) {
			elevator.set(0);
			rumble();
		}
		else {
			elevator.set(elevatorPower);
			stopRumble();
		}
	}
	
	public void grabber() {
		double grabberPower = -1 * manipulator.getRawAxis(GRABBER_AXIS);
		if (manipulator.getRawButton(MANIPULATOR_RELEASE_BUTTON)) {
			grabberPower = -.5;
		}
		else if (manipulator.getRawButton(MANIPULATOR_GRAB_BUTTON)) {
			grabberPower = .5;
		}
		rightGrabber.set(grabberPower);
		leftGrabber.set(-1 *grabberPower);
	}
	public void climber() {
		double climberPower = -1 * manipulator.getRawAxis(CLIMBER_BUMPER);
		boolean safe1= manipulator.getRawAxis(CLIMBER_BUMPER_LEFT) > .3;
		
		if (safe1) {
			rumble();
			climber.set(climberPower);
		}else {
			stopRumble();
			climber.set(0);
		}
	}
	
	public void rumble() {
		manipulator.setRumble(RumbleType.kLeftRumble,.7);
		manipulator.setRumble(RumbleType.kRightRumble, .7);
		
		
	}
	public void stopRumble() {
		manipulator.setRumble(RumbleType.kLeftRumble,0);
		manipulator.setRumble(RumbleType.kRightRumble, 0);
	
	}
}
