package frc.robot;

// Imports
import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.util.Color;

public class SMART_Custom_Methods {
    private static SMART_Custom_Methods instance = new SMART_Custom_Methods();

    // 2023 WORM-E Methods
        // Inputs a desired value to meet, the current value, a specified motor, and motor speed. 
        // Sets the motor to the motorspeed when current value is less than the desired and the motor to zero when it is > or =.
      public void move_to_position(double set_point, double current_point, CANSparkMax motor, double motorspeed, boolean inputCondition){
        if(current_point<set_point&&inputCondition){
          motor.set(motorspeed);
        }
        else{
          motor.set(0);
        }
      }
        // Inputs a desired 0 value to meet, the current value, a specified motor, and motor speed.
        // Sets the motor to the motorspeed when current value is greater than the 0 and the motor to 0 when it is < or =.
      public void move_to_rest(double rest_point, double current_point, CANSparkMax motor, double motorspeed, boolean inputCondition, String game_piece){
        
        if(game_piece=="cube"){
          rest_point += 13;
        }
        else if(game_piece=="cone"){
          rest_point += 18;
        }
        else if(game_piece=="N/A"){
          rest_point +=0;
        }
        
        if(current_point>rest_point&&inputCondition){
          motor.set(motorspeed); //input a negative
        }
        else{
          motor.set(0);
        }
      }
        // Inputs a limit (such as the value from a limit switch or sensor), a motor, a motor speed, and an input condition.
        // Sets the motor to the motor speed if the limit is not meet (false) and the input condition is true and to 0 of the either is false.
      public void limit_hit(Boolean limit, CANSparkMax motor, double motorspeed, boolean inputCondition){
        if(limit==false&&inputCondition){
          motor.set(motorspeed);
        }
        else{
          motor.set(0);
        }
      }

      /**
       * 
       * @param angle Input a desired angle
       * @param input_device Inputs controller or joystick to take the POV angle of
       * @return A boolean value of true if the desired angle is the same as the measured angle and false if it is different
       */
      public boolean POVAngle(int angle, Joystick input_device){
        if(input_device.getPOV()==angle){
          return true;
        }
        else{
          return false;
        }
      }

      /**
       * <p> Inputs a condition to be considered and a toggle value
       * @return Outputs true if the toggle is active and the input condition if it is not
      */
      public boolean diognosticConditions(boolean inputCondition, boolean toggle){ // Make toggle diognosticToggle whenever called
        if(toggle){
          return true;
        }
        else{
          return inputCondition;
        }
      }

      /** 
       * @param Inputs the color and associated data seen by the color sensor (REV Color Sensor V3)
       * @return A string value of Cube or Cone if the hash code is within the expected range and N/A if neither of the ranges are true
      */
      public String detectGamePiece(Joystick controller){ // if using color sensor add Color detectedColorCode parameter
        
        if(controller.getRawAxis(3)>0.5){
          return "cube";
        }
        else if(controller.getRawAxis(3)<-0.5){
          return "cone";
        }
        else{
          return"N/A";
        }
        
        /* 
        if(detectedColorCode.hashCode()>1900000000&&detectedColorCode.hashCode()<2100000000){ // The hash code range for cube color
          return "Cube";
        }
        else if(detectedColorCode.hashCode()>-1600000000&&detectedColorCode.hashCode()<-1400000000){ // The hash code range for cone color
          return "Cone";
        }
        else{ // If neither of the expected hash code ranges are found
          return "N/A";
        }
        */
      }

      /**
       * @param Inputs the type of game piece (cone or cube), the motor being moved, the timer variable, and the state of the grabber (open or closed)
       * @ If the game piece is a cube the motor moves at the motor speed for a set amount of time 
      */
      public void graberMove(String gamePiece, CANSparkMax motor, Timer grabTimer, String state){
        grabTimer.start(); // might not work
        if(gamePiece=="Cube"||gamePiece=="cube"){
          if(grabTimer.get()!=0&&grabTimer.get()>3&&state=="open"){
            motor.set(0.3);
          }
          else if(grabTimer.get()!=0&&grabTimer.get()>2&&state=="closed"){
            motor.set(-0.3);
          }
          else{
            grabTimer.stop();
            grabTimer.reset();
            motor.set(0);
          }
        }
        else if(gamePiece=="Cone"||gamePiece=="cone"){
          if(grabTimer.get()!=0&&grabTimer.get()>3&&state=="open"){
            motor.set(0.3);
          }
          else if(grabTimer.get()!=0&&grabTimer.get()>4&&state=="closed"){
            motor.set(-0.3);
          }
          else{
            grabTimer.stop();
            grabTimer.reset();
            motor.set(0);
          }
        }
      }
      public void TEST_move_to_position(double set_point, double current_point, CANSparkMax motor, double max_motorspeed, boolean inputCondition, double motorspeed){        
        if(current_point<set_point&&inputCondition){
          motorspeed = ((current_point-(set_point/2.1))/(set_point/1.9));
          motorspeed = motorspeed*motorspeed;
          motorspeed = (-motorspeed*max_motorspeed) + max_motorspeed;
          motor.set(motorspeed);
        }
        else{
          motor.set(0);
        }
      }
      /* 
      public void autoBalance() {
        
      }
      */
      public static SMART_Custom_Methods getInstance(){
        return instance;
      }







}
