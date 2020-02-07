package edu.wpi.first.wpilibj.defaultCode;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.Victor;

//Last change: code cleaned up

/**
 * - Robot:
 *   - Digital Sidecar 1:
 *     - PWM 3/4 - Connected to "left" drive motor(s)
 *     - PWM 1/2 - Connected to "right" drive motor(s)
 */
public class DefaultRobot extends IterativeRobot {
    // Declare variable for the robot drive system
    RobotDrive m_robotDrive;		// robot will use PWM 1-4 for drive motors

    int m_dsPacketsReceivedInCurrentSecond;	// keep track of the ds packets received in the current second

    // Declare variables for the two joysticks being used
    Joystick m_rightStick;			// joystick 1 (arcade stick or right tank stick)
    Joystick m_leftStick;			// joystick 2 (tank left stick)
    Joystick m_xboxController;

    // drive mode selection
    static final int UNINITIALIZED_DRIVE = 0;
    static final int ARCADE_DRIVE = 1;
    static final int TANK_DRIVE = 2;
    int m_driveMode;

    //wiring:
    static final int portRightFront = 1;
    static final int portRightBack = 2;
    static final int portLeftFront = 3;
    static final int portLeftBack = 4;

    static final boolean debug = true; // Why always true???

    // Local variables to count the number of periodic loops performed
    int m_autoPeriodicLoops;
    int m_disabledPeriodicLoops;
    int m_telePeriodicLoops;

    Victor leftFront;
    Victor leftBack;
    Victor rightFront;
    Victor rightBack;

    /**
     * Constructor for this "BuiltinDefaultCode" Class.
     *
     * The constructor creates all of the objects used for the different inputs and outputs of
     * the robot.  Essentially, the constructor defines the input/output mapping for the robot,
     * providing named objects for each of the robot interfaces.
     */
    public DefaultRobot() {
        System.out.println("BuiltinDefaultCode Constructor Started\n");

        // Robot DRIVE init Method
        // RobotDrive​(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
        m_robotDrive = new RobotDrive(portLeftFront, portLeftBack, portRightFront, portRightBack);
        
        // Create a robot using standard right/left robot drive on PWMS 1, 2, 3, and #4
        rightFront = new Victor(portRightFront);
        rightBack = new Victor(portRightBack);
        leftFront = new Victor(portLeftFront);
        leftBack = new Victor(portLeftBack);

        m_dsPacketsReceivedInCurrentSecond = 0;

        // Define joysticks being used at USB port #1 and USB port #2 on the Drivers Station
        m_xboxController = new Joystick(1);
        m_rightStick = new Joystick(2);
        // Iterate over all the buttons on each joystick, setting state to false for each

        // Iterate over all the solenoids on the robot, constructing each in turn

        // Set drive mode to uninitialized
        m_driveMode = UNINITIALIZED_DRIVE;

        // Initialize counters to record the number of loops completed in autonomous and teleop modes
        m_autoPeriodicLoops = 0;
        m_disabledPeriodicLoops = 0;
        m_telePeriodicLoops = 0;

        System.out.println("BuiltinDefaultCode Constructor Completed\n");
        if (debug){
            //print drive motor configuration
            System.out.println("Right Front:"+portRightFront+" Right Back:"+portRightBack+" Left Front:"+portLeftFront+" Left Back:"+portLeftBack);
        }

    }


    /********************************** Init Routines *************************************/

    public void robotInit() {
        // Actions which would be performed once (and only once) upon initialization of the
        // robot would be put here.
        // Set Inverted Motors, so the thing doesn't explode
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        m_robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        System.out.println("RobotInit() completed.\n");
    }

    public void disabledInit() {
        m_disabledPeriodicLoops = 0;			// Reset the loop counter for disabled mode
        startSec = (int)(Timer.getUsClock() / 1000000.0);
        printSec = startSec + 1;
    }

    public void autonomousInit() {
        m_autoPeriodicLoops = 0;				// Reset the loop counter for autonomous mode
        if (debug)
            System.out.println("Auto Just Started");
    }

    public void teleopInit() {
        m_telePeriodicLoops = 0;				// Reset the loop counter for teleop mode
        m_dsPacketsReceivedInCurrentSecond = 0;	// Reset the number of dsPackets in current second
        m_driveMode = UNINITIALIZED_DRIVE;		// Set drive mode to uninitialized
        if (debug)
            System.out.println("Teleop Just Started");

    }

    /********************************** Periodic Routines *************************************/
    static int printSec;
    static int startSec;

    public void disabledPeriodic()  {
        // feed the user watchdog at every period when disabled
        Watchdog.getInstance().feed();

        // increment the number of disabled periodic loops completed
        m_disabledPeriodicLoops++;

        // while disabled, printout the duration of current disabled mode in seconds
        if ((Timer.getUsClock() / 1000000.0) > printSec) {
            System.out.println("Disabled seconds: " + (printSec - startSec));
            printSec++;
        }
    }

    public void autonomousPeriodic() {
        // feed the user watchdog at every period when in autonomous
        Watchdog.getInstance().feed();

        m_autoPeriodicLoops++;
        // When on the first periodic loop in autonomous mode, start driving forwards at half spe
        if (debug)
            System.out.println("Auto is running (50% power fwd)");
        leftFront.set(0.5);
        leftBack.set(-0.5);
        rightFront.set(0.5);
        rightBack.set(-0.5);
    }

    public void teleopPeriodic() {
        // feed the user watchdog at every period when in autonomous
        Watchdog.getInstance().feed();

        // increment the number of teleop periodic loops completed
        m_telePeriodicLoops++;

        /*
         * Code placed in here will be called only when a new packet of information
         * has been received by the Driver Station.  Any code which needs new information
         * from the DS should go in here
         */

        m_dsPacketsReceivedInCurrentSecond++;					// increment DS packets received

        // put Driver Station-dependent code here
        if (debug){
            m_robotDrive.setSafetyEnabled(false);
            System.out.print("Joy stick 5:");
            System.out.println(m_xboxController.getRawAxis(5));
            System.out.print("Joy stick 2:");
            System.out.println(m_xboxController.getRawAxis(2));
            //-------------------------------------------------
            leftFront.set(m_xboxController.getRawAxis(2));
            leftBack.set(-m_xboxController.getRawAxis(2));
            rightFront.set(m_xboxController.getRawAxis(5));
            rightBack.set(-m_xboxController.getRawAxis(5));
        } else {
            m_robotDrive.setSafetyEnabled(false);
            System.out.print("Joy stick 5:");
            System.out.println(m_xboxController.getRawAxis(5));
            System.out.print("Joy stick 2:");
            System.out.println(m_xboxController.getRawAxis(2));
            m_robotDrive.tankDrive(m_xboxController.getRawAxis(2), m_xboxController.getRawAxis(5));
        }
        if (m_driveMode != TANK_DRIVE) {
            // if newly entered tank drive, print out a message
            System.out.println("Tank Drive\n");
            m_driveMode = TANK_DRIVE;
        }
    }
    int GetLoopsPerSec() {
        return 20;
    }
}
