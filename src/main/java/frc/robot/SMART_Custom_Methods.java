package frc.robot;

// Imports
import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.CANSparkMax;



public class SMART_Custom_Methods {
    
    // 2023 WORM-E Methods
        // Inputs a desired value to meet, the current value, a specified motor, and motor speed. 
        // Sets the motor to the motorspeed when current value is less than the desired and the motor to zero when it is > or =.
    public void move_to_position(double set_point, double current_point, CANSparkMax motor, double motorspeed, boolean inputCondition) {
        if(current_point<set_point&&inputCondition){
          motor.set(motorspeed);
        }
        else{
          motor.set(0);
        }
      }
        // Inputs a desired 0 value to meet, the current value, a specified motor, and motor speed.
        // Sets the motor to the motorspeed when current value is greater than the 0 and the motor to 0 when it is < or =.
      public void move_to_rest(double rest_point, double current_point, CANSparkMax motor, double motorspeed, boolean inputCondition) {
        if(current_point>rest_point&&inputCondition){
          motor.set(motorspeed); //input a negative
        }
        else{
          motor.set(0);
        }
      }
        // Inputs a limit (such as the value from a limit switch or sensor), a motor, a motor speed, and an input condition.
        // Sets the motor to the motor speed if the limit is not meet (false) and the input condition is true and to 0 of the either is false.
      public void limit_hit(Boolean limit, CANSparkMax motor, double motorspeed, boolean inputCondition) {
        if(limit==false&&inputCondition){
          motor.set(motorspeed);
        }
        else{
          motor.set(0);
        }
      }
        // Inputs a desired angle and device.
        // Outputs a boolean value of true if the desired angle is the same as the measured angle and false if it is different.
      public boolean POVAngle(int angle, Joystick input_device) {
        if(input_device.getPOV()==angle){
          return true;
        }
        else{
          return false;
        }
      }
        // Inputs a condition to be considered and a toggle value
        // Outputs true if the toggle is active and the input condition if it is not
      public boolean diognosticConditions(boolean inputCondition, boolean toggle) { // Make toggle diognosticToggle whenever called
        if(toggle){
          return true;
        }
        else{
          return inputCondition;
        }
      }







}
