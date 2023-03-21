// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// 2023 WORM-E Robot Code

  // Notes for robot movement
  // x = left and right (not used)
  // y = forward and backward
  // z = twist (used for turning)

  // Notes for Logitech Gamepad F310 Buttons
  // A = 1, B = 2, X = 3, Y = 4, Left Bumper = 5, Right Bumper = 6, Back = 7, Start = 8, 
  // Left Joystick Button = 9, Right Joystick Button= 10
  // Joysticks have X and Y axes, Triggers are axes

// External Imports
package frc.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
//import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.util.Color;
//import com.revrobotics.ColorSensorV3;
//import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.kauailabs.navx.frc.AHRS; 

/* 

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
*/


public class Robot extends TimedRobot {

  SMART_Custom_Methods cMethods = SMART_Custom_Methods.getInstance();
   
  AHRS ahrs = new AHRS(SerialPort.Port.kMXP);

  // set control variables
  private DifferentialDrive m_myRobot;
  private Joystick m_joystick;
  private Joystick controller;
  //private final I2C.Port i2cPort = I2C.Port.kOnboard;
  //private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

  // set drive motor variables
  private static final int left1DeviceID = 1; 
  private CANSparkMax m_left1Motor;
  private static final int left2DeviceID = 2;
  private CANSparkMax m_left2Motor;
  private static final int right1DeviceID = 4;
  private CANSparkMax m_right1Motor; 
  private static final int right2DeviceID = 3;
  private CANSparkMax m_right2Motor; 

  // set arm motor variables
  private static final int bot_pivDeviceID = 5; // Bottom pivot of the arm 
  private CANSparkMax bot_pivMotor;
  private RelativeEncoder bot_pivEncoder;
  private static final int top_pivDeviceID = 6; // Top pivot of the arm 
  private CANSparkMax top_pivMotor;
  private RelativeEncoder top_pivEncoder;
  private static final int teleDeviceID = 7; // Telescoping section of arm 
  private CANSparkMax teleMotor;
  private static final int grabDeviceID = 8; // Grabber motor for arm 
  private CANSparkMax grabMotor;

// Test motor
//private static final int testDeviceID = 9;
//private CANSparkMax test_motor;
//private RelativeEncoder test_encoder;

  // establishes pid contants
  
  // set cameras
  VideoSink server;
  UsbCamera cam0 = CameraServer.startAutomaticCapture(0);
  UsbCamera cam1 = CameraServer.startAutomaticCapture(1);

  // Set LEDs
  /* 
  private static final String red = "Red";
  private static final String blue = "Blue";
  private String colorSelected;
  private final SendableChooser<String> colorchooser = new SendableChooser<>();

  DigitalInput ledToggle = new DigitalInput(1);
  */

  // Sets arm mode toggle
  private boolean manualPositionToggle;
  private boolean autoPositionToggle;
  private boolean diognosticToggle;

  // Set telescoping limits
  DigitalInput retractLimit = new DigitalInput(0);
  DigitalInput extendLimit = new DigitalInput(2);
  DigitalInput absoluteTopZero = new DigitalInput(3);
  DigitalInput absoluteBotZero = new DigitalInput(4);

  // Sets autos
  private static final String defaultAuto = "Default";  // Just drives out
  private static final String Auto1 = "Auto1";  // Drives onto charger station and stays on
  private static final String Auto2 = "Auto2";  // Places cube on middle and drives out
  private static final String Auto3 = "Auto3";  // Places cube on high and drives out
  private String autoSelected;
  private final SendableChooser<String> autochooser = new SendableChooser<>();
    
  

  // set timer
  double startTime;
  Timer grabTimer;

  @Override
  public void robotInit() {

    // establish drive motor variables
    m_left1Motor = new CANSparkMax(left1DeviceID, MotorType.kBrushed);
    m_left2Motor = new CANSparkMax(left2DeviceID, MotorType.kBrushed);
    m_right1Motor = new CANSparkMax(right1DeviceID, MotorType.kBrushed);
    m_right2Motor = new CANSparkMax(right2DeviceID, MotorType.kBrushed);

    // group Left and Right motors so they move the same direction and speed for the arcade drive
    MotorControllerGroup leftMotor = new MotorControllerGroup(m_left1Motor, m_left2Motor);
    MotorControllerGroup rightMotor = new MotorControllerGroup(m_right1Motor, m_right2Motor);

    // invert right side of the drivetrain so that positive voltage results 
    // in both sides moving forward (forward instead of turn)
    rightMotor.setInverted(true);   
    // drive is set to use the left and right motors
    m_myRobot = new DifferentialDrive(leftMotor, rightMotor);

    // establish arm motor variables
    bot_pivMotor = new CANSparkMax(bot_pivDeviceID, MotorType.kBrushless);
    bot_pivMotor.setInverted(true);
    bot_pivEncoder = bot_pivMotor.getEncoder();
    top_pivMotor = new CANSparkMax(top_pivDeviceID, MotorType.kBrushless);
    top_pivMotor.setInverted(true);
    top_pivEncoder = top_pivMotor.getEncoder();
    teleMotor = new CANSparkMax(teleDeviceID, MotorType.kBrushed);
    grabMotor = new CANSparkMax(grabDeviceID, MotorType.kBrushed);

    // Test motor
    //test_motor = new CANSparkMax(testDeviceID, MotorType.kBrushless);
    //test_encoder = test_motor.getEncoder();

    // establish controller variablse
    m_joystick = new Joystick(0);
    controller = new Joystick(1);

    // make cameras work
    server = CameraServer.getServer();

    manualPositionToggle = false;
    autoPositionToggle = true;
    diognosticToggle = false;

    bot_pivEncoder.setPosition(0);
    top_pivEncoder.setPosition(0);

    autochooser.setDefaultOption("Just Drive Out", defaultAuto);
    autochooser.addOption("Over Charge Station", Auto1);
    autochooser.addOption("Place Cube on Middle and Drive Out", Auto2);
    autochooser.addOption("Testing grabber", Auto3);
    SmartDashboard.putData("Auto choices", autochooser);
    /* 
    colorchooser.setDefaultOption("Red Alliance",red);
    colorchooser.addOption("Blue Alliance",blue);
    SmartDashboard.putData("Color Choices", colorchooser);
    */
    
  }



  @Override
  public void teleopPeriodic() {

    /*
    if(m_joystick.getRawButton(11)){
      cMethods.TEST_move_to_position(10, test_encoder.getPosition(), test_motor, 0.25, true,0);
    }
     
    else if(m_joystick.getRawButton(10)){
      cMethods.move_to_rest(0, test_encoder.getPosition(), test_motor, -0.25, true, "N/A");
    }
    
    if(m_joystick.getRawButton(9)){
      test_encoder.setPosition(0);
    }
    SmartDashboard.putNumber("Test Position", test_encoder.getPosition());
    */
    //SmartDashboard.putNumber("Motor speed", kDefaultPeriod)
    /* 
    colorSelected = colorchooser.getSelected();

    if(colorSelected==red){
      //ledToggle.se;
    }
    else if(colorSelected==blue){
      //ledToggle=false;
    }
    */

    // establish variables
    boolean A = controller.getRawButton(1);
    boolean B = controller.getRawButton(2);
    boolean X = controller.getRawButton(3);
    boolean Y = controller.getRawButton(4);
    boolean LB = controller.getRawButton(5);
    boolean RB = controller.getRawButton(6); // change to getRawButtonPressed
    boolean padUp = cMethods.POVAngle(0, controller);
    boolean padRight = cMethods.POVAngle(90, controller);
    boolean padDown = cMethods.POVAngle(180, controller);
    boolean padLeft = cMethods.POVAngle(270, controller);
    double bot_pivPosition = bot_pivEncoder.getPosition();
    double top_pivPosition = top_pivEncoder.getPosition();
    String current_game_piece = cMethods.detectGamePiece(m_joystick);
    //Color detectedColor = m_colorSensor.getColor();
    //System.out.println(detectedColor.hashCode());
    SmartDashboard.putNumber("Top Pivot Position", top_pivPosition);
    SmartDashboard.putNumber("Bottom Pivot Position", bot_pivPosition);
    SmartDashboard.putBoolean("Retracted", retractLimit.get());
    SmartDashboard.putBoolean("Extended", extendLimit.get());
    SmartDashboard.putString("Game Piece Mode",cMethods.detectGamePiece(m_joystick));
    SmartDashboard.putNumber("Pitch", ahrs.getPitch());

    /* 
    if(detectedColor.hashCode()>1900000000&&detectedColor.hashCode()<2100000000){ // is the hash code for purple
      SmartDashboard.putString("Game Piece","Cube");
    }
    else if(detectedColor.hashCode()>-1600000000&&detectedColor.hashCode()<-1400000000){
      SmartDashboard.putString("Game Piece", "Cone");
    }
    else{
      SmartDashboard.putString("Game Piece", "N/A");
    }
    */ 

    /* Untested Slider Code 
    double axis_value = m_joystick.getRawAxis(3);
    double mutliplier = ((axis_value + 1)/2);
    System.out.format("%.2f%n",mutliplier);
    */

    
   // Robot drive 
      // Use joystick for driving
    if(m_joystick.getRawButton(2)){
      m_myRobot.arcadeDrive(-m_joystick.getY()*0.4, m_joystick.getZ()*0.4);
    }
    else{
      m_myRobot.arcadeDrive(-m_joystick.getY(), m_joystick.getZ()*0.5);
    }
    

    // Cameras
      // When the trigger on the joystick is held, display will change from drive camera to arm camera
    if (m_joystick.getRawButtonPressed(1)) {
      System.out.println("Setting camera 0");
      server.setSource(cam0);
    }
    else if (m_joystick.getRawButtonReleased(1)) {
      System.out.println("Setting camera 1");
      server.setSource(cam1);
    }

    // Reset encoder values ("left joystick" button)
    if (controller.getRawButtonPressed(9)){
      bot_pivEncoder.setPosition(0);
      top_pivEncoder.setPosition(0);
    }

    if(controller.getRawButtonPressed(7)){
      manualPositionToggle = true;
      autoPositionToggle = false;
    }

    if(controller.getRawButtonPressed(8)){
      autoPositionToggle = true;
      manualPositionToggle = false;
    }

    if(controller.getRawButtonPressed(10)){
      diognosticToggle = !diognosticToggle;
    }
    
    if(absoluteTopZero.get()==true&&top_pivPosition!=0){
      top_pivEncoder.setPosition(0);
    }
    /* 
    if(absoluteBotZero.get()==true){
      bot_pivEncoder.setPosition(0);
    }
    */
    

    if(autoPositionToggle){
      if(A||B||X||Y){
        if(A){        // Moves the arm to Floor height scoring/pickup position (A button)
          cMethods.move_to_position(18, top_pivPosition, top_pivMotor, 0.3,true);
          cMethods.move_to_position(5, bot_pivPosition, bot_pivMotor, 0.3, top_pivPosition>=17);
          cMethods.limit_hit(extendLimit.get(), teleMotor, 0.75, bot_pivPosition>=5);

        }
        else if(B){   // Moves the arm to Medium height scoring position (B button)
          if(current_game_piece=="cube"){
            cMethods.move_to_position(66, top_pivPosition, top_pivMotor, 0.3,true);
            cMethods.limit_hit(extendLimit.get(), teleMotor, 0.75,top_pivPosition>=66);
          }
          else if(current_game_piece=="cone"){
            cMethods.move_to_position(86, top_pivPosition, top_pivMotor, 0.3, true);
            cMethods.move_to_position(8, bot_pivPosition, bot_pivMotor, 0.15, top_pivPosition>=76);
            cMethods.limit_hit(extendLimit.get(), teleMotor, 0.75, top_pivPosition>=86);
          }
          else{
            cMethods.move_to_position(40, top_pivPosition, top_pivMotor, 0.25, true);
          }
   
        }
        else if(X){   // Moves the arm to Shelf pickup position (X button)
          cMethods.move_to_position(60, top_pivPosition, top_pivMotor, 0.4,true); 
          //cMethods.limit_hit(retractLimit.get(), teleMotor, -0.75,true);       
        }
        else if(Y){   // Moves the arm to High height scoring position (Y button)
          cMethods.move_to_position(128, top_pivPosition, top_pivMotor, 0.2, true);
          cMethods.move_to_position(42, bot_pivPosition, bot_pivMotor, 0.15,top_pivPosition>=64);
          cMethods.limit_hit(extendLimit.get(), teleMotor, 0.75,bot_pivPosition>=42);        
        }
      }
      else{   // Moves the arm back to its resting position (No button)
        cMethods.limit_hit(retractLimit.get(), teleMotor, -0.75,true);
        cMethods.move_to_rest(0, bot_pivPosition, bot_pivMotor, -0.25,retractLimit.get(),"N/A");
        cMethods.move_to_rest(0, top_pivPosition, top_pivMotor, -0.25,bot_pivPosition<5,cMethods.detectGamePiece(m_joystick));
      }
    }
    else if(manualPositionToggle){
      if(padUp){
        top_pivMotor.set(0.4);
      }
      else if(padDown&&cMethods.diognosticConditions(top_pivPosition>0, diognosticToggle)){
        top_pivMotor.set(-0.3);
      }
      else{
        top_pivMotor.set(0);
      }

      if(padLeft){
        bot_pivMotor.set(0.1);
      }
      else if(padRight&&cMethods.diognosticConditions(bot_pivPosition>0, diognosticToggle)){
        bot_pivMotor.set(-0.25);
      }
      else{
        bot_pivMotor.set(0);
      }
      
      if(LB&&extendLimit.get()==false){
        teleMotor.set(0.75);
      }
      else if(controller.getRawAxis(2)>0.5&&cMethods.diognosticConditions(retractLimit.get()==false, diognosticToggle)){
        teleMotor.set(-0.75);
      }
      else{
        teleMotor.set(0);
      }
    }
    
    
    if(RB){
      grabMotor.set(0.4);
      //cMethods.graberMove(current_game_piece, grabMotor, grabTimer, "closed");
    } 
    else if(controller.getRawAxis(3)>0.5){
      grabMotor.set(-0.3);
    }
    else{
      grabMotor.set(0);
      //cMethods.graberMove(current_game_piece, grabMotor, grabTimer, "open");
    }
  
    
  } 
  // Autonomous

  @Override
  public void autonomousInit() {
    autoSelected = autochooser.getSelected();
    System.out.println("Auto Selected: "+ autoSelected);
    startTime = Timer.getFPGATimestamp();


  }


  @Override
  public void autonomousPeriodic() {
    // establish variables
    double top_pivPosition = top_pivEncoder.getPosition();
    double bot_pivPosition = bot_pivEncoder.getPosition();
    double timeRobot = Timer.getFPGATimestamp();
    switch(autoSelected){
      case Auto1:
        if((timeRobot - startTime > 1) && (timeRobot - startTime < 3.65)) { //put values back to (time > 12) && (time < 15) and add back else
        
          m_myRobot.arcadeDrive(0.4, 0,false); 
        } else {
          m_myRobot.arcadeDrive(0, 0,false); 

        }       
        break;
      case Auto2:
        if(timeRobot-startTime>=1&&timeRobot-startTime<=6){
          cMethods.move_to_position(60, top_pivPosition, top_pivMotor, 0.3, true);
          cMethods.limit_hit(extendLimit.get(), teleMotor, 0.75, top_pivPosition>=50);
        }
        else if(timeRobot-startTime>6&&timeRobot-startTime<=6.5){
          grabMotor.set(-0.2);
          cMethods.move_to_position(60, top_pivPosition, top_pivMotor, 0.3, true);
          //cMethods.graberMove("cube", grabMotor, grabTimer, "closed");
        }
        else if(timeRobot-startTime>6.5&&timeRobot-startTime<=10){
          //cMethods.graberMove("cube", grabMotor, grabTimer, "open");
          grabMotor.set(0);
          cMethods.limit_hit(retractLimit.get(), teleMotor, -0.75, true);
          cMethods.move_to_rest(0, top_pivPosition, top_pivMotor, -0.25, retractLimit.get()==true,cMethods.detectGamePiece(m_joystick));
        }
        else if(timeRobot-startTime>10&&timeRobot-startTime<=13){
          m_myRobot.arcadeDrive(-0.3, 0,false);
        }
        break;
      case Auto3:
        if((timeRobot-startTime>=1)&&(timeRobot-startTime<2)){
          grabMotor.set(0.3);
        }
        else if(timeRobot-startTime>2&&timeRobot-startTime<15){
          grabMotor.set(0);
        }
        break;
      case defaultAuto:
        if((timeRobot - startTime > 1) && (timeRobot - startTime < 4)) {
        
          m_myRobot.arcadeDrive(-0.3, 0,false); 
        } else {
          m_myRobot.arcadeDrive(0, 0,false); 

        }      
        break;
    }

  } 
  
}
