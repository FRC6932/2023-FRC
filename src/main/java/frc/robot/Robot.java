// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// THIS IS 2023 ROBOT CODE

package frc.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
// import edu.wpi.first.wpilibj.motorcontrol.MotorController;
// import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax; 

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  // Controls are established here
  private DifferentialDrive m_myRobot;
  private Joystick m_joystick;
  // private Joystick m_controlor;
  //
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

    rightMotor.setInverted(true);
    //m_right1Motor.set(50);
    //m_right2Motor.set(50);

    m_myRobot = new DifferentialDrive(leftMotor, rightMotor);
    m_joystick = new Joystick(0);
    // m_controlor = new Joystick(1);
  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.arcadeDrive(-m_joystick.getY(), m_joystick.getZ()*0.5);
  }
}
