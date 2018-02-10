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
	@Override
	public void teleopPeriodic() {
		boolean down = false;
		boolean switchMode = false;
		//limitSwitch = new DigitalInput(1);
		gyro.reset();
	
		while (isOperatorControl() && isEnabled()) {
			double leftPower  = driver.getRawAxis(DRIVER_LEFT_AXIS);
			double rightPower = driver.getRawAxis(1);
			
			boolean zero = false;
			leftPower = rightPower;
			if ( rightPower < .05 && rightPower > -0.05) {
				rightPower = turnX;
				leftPower = -1 * rightPower;
				zero = true;
			}
			
			if (!zero) {
				if (turnX > 0 ) {
					leftPower -= turnX;
				}else if (turnX < 0) {
					rightPower += turnX;
				}
			}
			System.out.println(rightPower);
			robot.tankDrive(rightPower, leftPower);
		
			double slowFactor = 1;

			boolean buttonPressed = driver.getRawButton(DRIVER_SWITCH_MODE_BUTTON);
			if (buttonPressed && !down) {
				switchMode = !switchMode;
				down = true;
			} else if (!buttonPressed) {
				down = false;

			}

			slowFactor = (switchMode) ? .5 : 1;

			System.out.println("leftpower before is " + leftPower + " right Power before is " + rightPower);
			leftPower = leftPower * slowFactor;
			rightPower = rightPower * slowFactor;
			
			
			double roffset = 0;
			double loffset = 0;
			double rightMultipler = driver.getRawAxis(3);
			double leftMultipler = driver.getRawAxis(2);
			
			if (driver.getRawButton(DRIVER_RIGHT_TRIGGER)) {
				//leftPower = rightPower;
				if (rightPower > 0) {
					//rightPower -= .4 * (1 + rightMultipler);
					roffset = .4 * (1 + rightMultipler);
					
				}
				else {
					//rightPower += .4 * (1 + rightMultipler);
					roffset = -1 * .4 * (1+rightMultipler);
				}
			}else if (driver.getRawButton(DRIVER_LEFT_TRIGGER)) {
				if (leftPower > 0) {
					//leftPower -= .4 * (1 + leftMultipler);
					loffset = .4 * (1 + leftMultipler);
			
				}
				else {
					//leftPower += .4 * (1 + leftMultipler);
					loffset = -1 * .4 * (1+leftMultipler);
			
				}
			}
			
			System.out.println("right multiplier is " + rightMultipler) ;
			int dpad = driver.getPOV(0);
			if (driver.getRawButton(3)) {
				speed += 0.25;
			}else if (driver.getRawButton(4)){
				speed -= 0.25;
			}
			switch (dpad) {
			case DRIVER_SPIN_RIGHT_POV:
				robot.tankDrive(1 * slowFactor, -1 * slowFactor);
				break;
			case DRIVER_SPIN_LEFT_POV:
				robot.tankDrive(-1 * slowFactor, 1 * slowFactor);
				break;
			case 180:
				robot.tankDrive(rightPower - roffset, rightPower - loffset);
				break;

				
			default:
				robot.tankDrive(rightPower - roffset, leftPower-loffset);
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
	
	public void turnAngle(double desiredAngle) {
		
		double kp = 0.013;
		double angle = gyro.getAngle();
		if (desiredAngle > 0)
			desiredAngle += 270;
		while (desiredAngle > Math.abs(angle)) {
			angle = gyro.getAngle();
			double error = desiredAngle - angle;
			double output = kp*error ;
			robot.tankDrive(output, -1.0*output);
		}
	
	}
	public double getAngle() {
		double degrees = Math.toDegrees(Math.atan2(driver.getRawAxis(1), driver.getRawAxis(0))) ;
		if (degrees < 0)
			degrees += 360;
		if (driver.getRawAxis(0) == 0)
			degrees = 0;
		System.out.println("x axis " + driver.getRawAxis(0) + " y axis " + driver.getRawAxis(1));
		return degrees;
	}
}
