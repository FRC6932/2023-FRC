// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// THIS IS 2023 ROBOT CODE

// Notes for robot movement
// x = left and right (not used)
// y = forward and backward
// z = twist (used for turning)

// Required Packages
package frc.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
// import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.cscore.VideoSink;
// import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */

public class Robot extends TimedRobot {
  //starttime
  private double startTime;

  // Controls are established here
  private DifferentialDrive m_myRobot;
  private Joystick m_joystick;

  // Motor controlers are established here
  /*
  private final MotorController m_leftMotor = new PWMSparkMax(0);
  private final MotorController m_rightMotor = new PWMSparkMax(1);
  */

  private static final int left1DeviceID = 1; 
  private CANSparkMax m_left1Motor;
  private static final int left2DeviceID = 2;
  private CANSparkMax m_left2Motor;
  private static final int right1DeviceID = 4;
  private CANSparkMax m_right1Motor; 
  private static final int right2DeviceID = 3;
  private CANSparkMax m_right2Motor; 
  
  // Set cameras
  VideoSink server;
  UsbCamera cam0 = CameraServer.startAutomaticCapture(0);
  UsbCamera cam1 = CameraServer.startAutomaticCapture(1);

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    
    m_left1Motor = new CANSparkMax(left1DeviceID, MotorType.kBrushed);
    m_left2Motor = new CANSparkMax(left2DeviceID, MotorType.kBrushed);
    m_right1Motor = new CANSparkMax(right1DeviceID, MotorType.kBrushed);
    m_right2Motor = new CANSparkMax(right2DeviceID, MotorType.kBrushed);

    MotorControllerGroup leftMotor = new MotorControllerGroup(m_left1Motor, m_left2Motor);
    MotorControllerGroup rightMotor = new MotorControllerGroup(m_right1Motor, m_right2Motor);

    rightMotor.setInverted(true);   // invert right motors
    
    m_myRobot = new DifferentialDrive(leftMotor, rightMotor);
    m_joystick = new Joystick(0);
    // m_controlor = new Joystick(1);

    // Make the cameras work
    server = CameraServer.getServer();

    

  }

  @Override
  public void teleopPeriodic() {

    //Untested Slider Code 
    
    /* 
    double axis_value = m_joystick.getRawAxis(3);
    double mutliplier = ((axis_value + 1)/2);
    System.out.format("%.2f%n",mutliplier);
    */

    m_myRobot.arcadeDrive(-m_joystick.getY(), m_joystick.getZ()*0.5);

    
    // Switch camera view?
    if (m_joystick.getRawButtonPressed(1)) {
      System.out.println("Setting camera 0");
      server.setSource(cam0);
    }
    else if (m_joystick.getRawButtonReleased(1)) {
      System.out.println("Setting camera 1");
      server.setSource(cam1);
    }


  }
  // Autonomous

  @Override
  public void autonomousInit() {
  startTime = Timer.getFPGATimestamp();

  }
  

  @Override
  public void autonomousPeriodic() {
  double time = Timer.getFPGATimestamp();
  if (time - startTime < 3) {
    m_myRobot.arcadeDrive(.4, 0); //Drive forward for 3 seconds at 40% speed
    
  } else {
    m_myRobot.arcadeDrive(0, 0);
  }

  }
  
     
  
}
