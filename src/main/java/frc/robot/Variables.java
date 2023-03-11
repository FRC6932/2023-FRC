package frc.robot;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

public class Variables{

        public static Variables instance = new Variables();

        // Establishes the device ids and variables for the drive motors
        private static final int left1DeviceID = 1; 
        private CANSparkMax m_left1Motor;
        private static final int left2DeviceID = 2;
        private CANSparkMax m_left2Motor;
        private static final int right1DeviceID = 4;
        private CANSparkMax m_right1Motor; 
        private static final int right2DeviceID = 3;
        private CANSparkMax m_right2Motor; 

        // Establishes the device ids and variables for the arm motors and encoders
        private static final int bot_pivDeviceID = 5; //Bottom of arm motor pivot 
        private CANSparkMax bot_pivMotor;
        private RelativeEncoder bot_pivEncoder;
        private static final int top_pivDeviceID = 6; //Top of arm motor pivot 
        private CANSparkMax top_pivMotor;
        private RelativeEncoder top_pivEncoder;
        private static final int teleDeviceID = 7; //Telescoping section of arm 
        private CANSparkMax teleMotor;
        private static final int grabDeviceID = 8; //Grabber motor for arm 
        private CANSparkMax grabMotor;

        //
        public static Variables getVariables() {
                return instance;
        }
















}



