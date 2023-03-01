// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// THIS IS 2023 ROBOT CODE

// Notes for robot movement
// x = left and right (not used)
// y = forward and backward
// z = twist (used for turning)

// Notes for Logitech Gamepad F310 Buttons
// A = 1, B = 2, X = 3, Y = 4, Left Bumper = 5, Right Bumper = 6, Back = 7, Start = 8, 
// Left Joystick Button = 9, Right Joystick Button= 10
// Joysticks have X and Y axes, Triggers are axes

// Required Packages
package frc.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

//import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import java.util.List;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
/* 
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
*/

public class Robot extends TimedRobot {
  //starttime
  private double startTime;
  private ShuffleboardTab tab;
  // Controls are established here
  private DifferentialDrive m_myRobot;
  private Joystick m_joystick;

  // logitech gamepad F310 controller established (untested)
  private Joystick controller;
  // end
  
  // Variables for Motors and Cameras are established below
  

    // Drive Motor Variables
  private static final int left1DeviceID = 1; 
  private CANSparkMax m_left1Motor;
  private static final int left2DeviceID = 2;
  private CANSparkMax m_left2Motor;
  private static final int right1DeviceID = 4;
  private CANSparkMax m_right1Motor; 
  private static final int right2DeviceID = 3;
  private CANSparkMax m_right2Motor; 

    // Arm Moter Variables
  private static final int bot_pivDeviceID = 5; //Bottom of arm motor pivot 
  private CANSparkMax bot_pivMotor;
  private RelativeEncoder bot_pivEncoder;
  private SparkMaxPIDController bot_pivPID;
  private static final int top_pivDeviceID = 6; //Top of arm motor pivot 
  private CANSparkMax top_pivMotor;
  private RelativeEncoder top_pivEncoder;
  private SparkMaxPIDController top_pivPID;
  private static final int teleDeviceID = 7; //Telescoping section of arm 
  private CANSparkMax teleMotor;
  private static final int grabDeviceID = 8; //Grabber motor for arm 
  private CANSparkMax grabMotor;
  
    // Set cameras
  VideoSink server;
  UsbCamera cam0 = CameraServer.startAutomaticCapture(0);
  UsbCamera cam1 = CameraServer.startAutomaticCapture(1);

  boolean ledToggle = false; 


  @Override
  public void robotInit() {
    tab = Shuffleboard.getTab("SmartDashboard");
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    
    // Robot Drive is established here 

      // Drive Motor variables are established
    m_left1Motor = new CANSparkMax(left1DeviceID, MotorType.kBrushed);
    m_left2Motor = new CANSparkMax(left2DeviceID, MotorType.kBrushed);
    m_right1Motor = new CANSparkMax(right1DeviceID, MotorType.kBrushed);
    m_right2Motor = new CANSparkMax(right2DeviceID, MotorType.kBrushed);
      // Left and Right motors are put into a group so they move the same direction and speed for the arcade drive
    MotorControllerGroup leftMotor = new MotorControllerGroup(m_left1Motor, m_left2Motor);
    MotorControllerGroup rightMotor = new MotorControllerGroup(m_right1Motor, m_right2Motor);
      // Inverts the right moters so they move the opposite direction (makes the robot go forward instead of turn)
    rightMotor.setInverted(true);   
    // Drive is set to use the left and right motors
    m_myRobot = new DifferentialDrive(leftMotor, rightMotor);

      // Arm Motor variables are established
    bot_pivMotor = new CANSparkMax(bot_pivDeviceID, MotorType.kBrushless);
    bot_pivMotor.setInverted(true);
    bot_pivEncoder = bot_pivMotor.getEncoder();
    top_pivMotor = new CANSparkMax(top_pivDeviceID, MotorType.kBrushless);
    top_pivEncoder = top_pivMotor.getEncoder();
    teleMotor = new CANSparkMax(teleDeviceID, MotorType.kBrushed);
    grabMotor = new CANSparkMax(grabDeviceID, MotorType.kBrushed);


    // Establishes controllers
    m_joystick = new Joystick(0);

    // logitech gamepade F310 (untested)
    controller = new Joystick(1);
    

    // Make the cameras work
    server = CameraServer.getServer();

    // startTime = Timer.getFPGATimestamp(); !
    // SlewRateLimiter l = new SlewRateLimiter(0.5);
    bot_pivEncoder.setPosition(0);
    top_pivEncoder.setPosition(0);

    
  }
  private void move_to_position(double set_point, double current_point, CANSparkMax motor) {
    if(current_point<set_point){
      motor.set(0.05);
    }
    else{
      motor.set(0);
    }
  }
  private void move_to_rest(double rest_point, double current_point, CANSparkMax motor) {
    if(current_point>rest_point){
      motor.set(-0.05);
    }
    else{
      motor.set(0);
    }
  }
  



  @Override
  public void teleopPeriodic() {
  
      // Establishes variables
    boolean A = controller.getRawButton(1);
    boolean B = controller.getRawButton(2);
    boolean X = controller.getRawButton(3);
    boolean Y = controller.getRawButton(4);
    boolean LB = controller.getRawButton(5);
    boolean RB = controller.getRawButton(6);

    double bot_pivPosition = (bot_pivEncoder.getPosition())*-1;
    System.out.println(bot_pivPosition);
    double top_pivPosition = top_pivEncoder.getPosition();
    //System.out.println(top_pivEncoder.getPosition());

    //Untested Slider Code 
    
    /* 
    double axis_value = m_joystick.getRawAxis(3);
    double mutliplier = ((axis_value + 1)/2);
    System.out.format("%.2f%n",mutliplier);
    */
   // Robot drive 
      // Uses the joystick for driving
    m_myRobot.arcadeDrive(-m_joystick.getY(), m_joystick.getZ()*0.5);

    // Cameras
      // When the trigger on the joystick is held, the camera shown will change from the drive to the arm camera
    if (m_joystick.getRawButtonPressed(1)) {
      System.out.println("Setting camera 0");
      server.setSource(cam0);
    }
    else if (m_joystick.getRawButtonReleased(1)) {
      System.out.println("Setting camera 1");
      server.setSource(cam1);
    }
    
    if (controller.getRawButtonPressed(7)){
      bot_pivEncoder.setPosition(0);
      top_pivEncoder.setPosition(0);
    }
    
    //
    
    // Arm Controls (edit later when design and motors ready)
      // toggle method, hold button to extend and let go to retract (may change)

      // Moves the arm to Floor height scoring/pickup position (A button)
   
    if(A||B||X||Y){
      if(A){
        move_to_position(5, bot_pivPosition, bot_pivMotor);
        //move_to_position(5, top_pivPosition, top_pivMotor);
      }
      else if(B){
        move_to_position(10, bot_pivPosition, bot_pivMotor);
        //move_to_position(10, top_pivPosition, top_pivMotor);        
      }
    }
    else{
      move_to_rest(0, bot_pivPosition, bot_pivMotor);
      //move_to_rest(0, top_pivPosition, top_pivMotor);
    }
    /*
    
    
  
    
      // Moves the arm to Medium height scoring position (B button)
     
    if(X==true){
      bot_pivMotor.set(0.15); //top piv at 0.5 , bot piv at 0.15
    }
    else if(B==true){
      bot_pivMotor.set(-0.15); // top piv at -0.25 , bot piv at -0.15
    }
    else{
      bot_pivMotor.set(0);
    }

    
      //Temporary telescoping code
    
    
    if(LB==true){
      teleMotor.set(0.75);
    }
    else if(RB==true){
      teleMotor.set(-0.75);
    }
    else{
      teleMotor.set(0);
    }
    
    
         
      
    /* 
    
    

      // Moves the arm to Shelf pickup position (X button)
    if(controller.getRawButton(3)){
      if (bot_pivEncoder.getPosition() < 5){
        bot_pivMotor.set(0.35);
      }
      else{
        bot_pivMotor.set(0);
      }
    }
    else{
      bot_pivMotor.set(0);
    }

      // Moves the arm to High height scoring position (Y button)
    if(controller.getRawButton(4)){
      if (bot_pivEncoder.getPosition() < 5){
        bot_pivMotor.set(0.35);
      }
      else{
        bot_pivMotor.set(0);
      }
    }
    else{
      bot_pivMotor.set(0);
    }
    */
    /*  LED Code
    if (m_joystick.getRawButton(12)){

      

    }
    */      

    
    


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

      


/*
  @Override
  public void teleopInit() {
  if (m_autonomousCommand != null) {
    m_autonomousCommand.cancel();
  }
*/
  } 
  
}
