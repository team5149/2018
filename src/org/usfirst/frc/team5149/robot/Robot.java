package org.usfirst.frc.team5149.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
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
	
	double speed = 0.25;
	final static int LEFT_MOTOR_PORT = 0 ;
	final static int RIGHT_MOTOR_PORT = 1;
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
	
	DigitalInput limitSwitch;
	SendableChooser<String> chooser = new SendableChooser<>();
	RobotDrive robot = new RobotDrive(LEFT_MOTOR_PORT, RIGHT_MOTOR_PORT);
	Joystick driver = new Joystick(DRIVER_JOYSTICK_PORT);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		gyro.reset();
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
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		gyro.calibrate();
		gyro.reset();

		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		double timeStamp = System.currentTimeMillis();
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			while (isAutonomous() && isEnabled()) {
				robot.tankDrive(speed,  speed);
			} 
			break;
		case defaultAuto:
		default:
			while (isAutonomous() && isEnabled()) {
				double kp = 0.013;
				double angle = gyro.getAngle();
				double error = 90 - angle;
				double output = kp*error ;
				if (90 > Math.abs(angle)) {
					robot.tankDrive(output, -1.0*output);
					System.out.println("angle is " + angle + "\tOutput is " + output );
				}
				
		
			}
		
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	
	
	// Git comment test
	@Override
	public void teleopPeriodic() {
		
		boolean down = false;
		boolean switchMode = false;  // When true, the power values will be multiplied by the slow power, reducing the total power
		//limitSwitch = new DigitalInput(1);
		gyro.reset();
	
		while (isOperatorControl() && isEnabled()) {
			double leftPower;		// Amount of power that should go to the left motor (between 0 and 1)
			double rightPower;		// Amount of power that should go the the right motor (between 0 and 1)
			double speedFactor;		// Factor by which the left and right powers are multiplied by, will reduce powers if less than 1
			boolean switchModeButton; // Keeps track of button, that when pressed, will toggle the state of switchMode 
			double rightOffset; // The offset of the rightPower, which when greater than 0, will curve the robot right
			double leftOffset;  // The offset of the leftPower, which when greater than 0, will curve the robot left
			double rightMultipler;  // Amount by which the rightOffset is multiplied by, used to increase the sharpness of the curve 
			double leftMultipler;	// Amount by which the leftOffset is multiplied by, used to increase the sharpness of the curve
			int dpad;			// Keeps track of which dPad direction is pressed
			double lSwitchState;	// keeps track of the limitSwitch state
			
			leftPower  = driver.getRawAxis(DRIVER_LEFT_AXIS);		// Get the position of the left axis 
			rightPower = driver.getRawAxis(DRIVER_RIGHT_AXIS);		// Get the position of the right axis
			
			switchModeButton = driver.getRawButton(DRIVER_SWITCH_MODE_BUTTON);	// Get the current state of the switchModeButton
			
			rightMultipler = driver.getRawAxis(3);		// Get the intensity of the right bumper
			leftMultipler = driver.getRawAxis(2);		// Get the intensity of the left bumper
			
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

			speedFactor = (switchMode) ? .5 : 1;

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
			case 180:
				robot.tankDrive(rightPower - rightOffset, rightPower - leftOffset);
				break;

				
			default:
				
				robot.tankDrive(rightPower - rightOffset, leftPower-leftOffset);
			}
			//boolean state = limitSwitch.get();
			SmartDashboard.putNumber("Current Auto Speed" , speed);
			//SmartDashboard.putBoolean("The switch is ", state);
			//System.out.println("the switch is currently " + state);
			 
			Timer.delay(0.005);

		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	
}
