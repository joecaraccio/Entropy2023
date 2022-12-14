package frc.robot.OI;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants;
import frc.robot.Enums.SwerveCardinal;
import frc.robot.OI.XboxController.Axis;
import frc.robot.OI.XboxController.Button;
import frc.robot.OI.XboxController.Side;
import frc.robot.util.LatchedBoolean;

public class OperatorInterface {
    private static OperatorInterface mInstance;

    // Instances of the Driver and Operator Controller
    private XboxController mDriverController;
    private XboxController mOperatorController;

    // Latched Booleans
    private LatchedBoolean mOperatorSelectButton = new LatchedBoolean();
    private LatchedBoolean mOperatorStartButton = new LatchedBoolean();
    private LatchedBoolean mLeftBumper = new LatchedBoolean();
    private LatchedBoolean mArmRotateUp = new LatchedBoolean();
    private LatchedBoolean mArmRotateDown = new LatchedBoolean();
    private LatchedBoolean mClimberTestPress = new LatchedBoolean();
    private LatchedBoolean mOperatorClimbApprovePress = new LatchedBoolean();

    private LatchedBoolean mExtensionUp = new LatchedBoolean();
    private LatchedBoolean mExtensionDown = new LatchedBoolean();
    private LatchedBoolean mExtensionSwitchMode = new LatchedBoolean();

    public static synchronized OperatorInterface getInstance() {
        if (mInstance == null) {
            mInstance = new OperatorInterface();
        }
        return mInstance;
    }
    
    private OperatorInterface() {
        mDriverController = new XboxController(Constants.Controllers.Driver.port);
        mOperatorController = new XboxController(Constants.Controllers.Operator.port);
    }

    public boolean getClimberTest(){
        return mClimberTestPress.update(mDriverController.getButton(Button.B));
    }

    public boolean getClimberTest2(){
        return mClimberTestPress.update(mDriverController.getButton(Button.Y));
    }

    public double getDriveThrottle() {
        return mDriverController.getJoystick(Side.LEFT, Axis.Y);
    }
    
    public double getDriveTurn() {
        return mDriverController.getJoystick(Side.RIGHT, Axis.X);
    }


    /* Swerve Drive Controls */
    public Translation2d getSwerveTranslation() {
        // joystick inputs
        double forwardAxis = mDriverController.getJoystick(Side.LEFT, Axis.Y);
        double strafeAxis = mDriverController.getJoystick(Side.LEFT, Axis.X);

        SmartDashboard.putNumber("Swerve Forward Axis", forwardAxis);
        SmartDashboard.putNumber("Swerve Strafe Axis", strafeAxis);


        forwardAxis = Constants.SwerveConstants.invertYAxis ? forwardAxis : -forwardAxis;
        strafeAxis = Constants.SwerveConstants.invertXAxis ? strafeAxis :-strafeAxis;

        Translation2d tAxes = new Translation2d(forwardAxis, strafeAxis);
        //Translation2d tAxes = new Translation2d(strafeAxis, forwardAxis);

        // optional code to deadband each one side


        if (Math.abs(tAxes.getNorm()) < Constants.Controllers.joystickDeadband) {
            return new Translation2d();
        } else {
            Rotation2d deadband_direction = new Rotation2d(tAxes.getX(), tAxes.getY());
            Translation2d deadband_vector = new Translation2d(Constants.Controllers.joystickDeadband, deadband_direction);

            double scaled_x = tAxes.getX() - (deadband_vector.getX()) / (1 - deadband_vector.getX());
            double scaled_y = tAxes.getY() - (deadband_vector.getY()) / (1 - deadband_vector.getY());

            return new Translation2d(scaled_x, scaled_y).times(Constants.SwerveConstants.maxSpeed);
        }
    }

    public double getSwerveRotation() {
        double rotAxis = mDriverController.getJoystick(Side.RIGHT, Axis.X);
        rotAxis = Constants.SwerveConstants.invertRotateAxis ? rotAxis : -rotAxis;

        if (Math.abs(rotAxis) < Constants.Controllers.joystickDeadband) {
            return 0.0;
        } else {
            return Constants.SwerveConstants.maxAngularVelocity * (rotAxis - (Math.signum(rotAxis) * Constants.Controllers.joystickDeadband)) / (1 - Constants.Controllers.joystickDeadband);
        }
    }

    // Get Quick Snap to a Direction
    public SwerveCardinal getSwerveSnap() {
        SwerveCardinal result = SwerveCardinal.NONE;

        // Forward, Backwards, Left, Right
        if (mDriverController.getButton(Button.Y)) {
            result = SwerveCardinal.FORWARDS;
        }
        if (mDriverController.getButton(Button.A)) {
            result = SwerveCardinal.BACKWARDS;
        }
        if (mDriverController.getButton(Button.X)) {
            result = SwerveCardinal.LEFT;
        }
        if (mDriverController.getButton(Button.B)) {
            result = SwerveCardinal.RIGHT;
        }

        return result;
    }

    public boolean getResetOdometry(){
        return mDriverController.getButton(Button.START);
    }
    
    public boolean getZeroGyro(){
        return mDriverController.getButton(Button.START);
    }

    public boolean getBrake(){
        return mDriverController.getButton(Button.LB);
    }

    public boolean getDriveAutoSteer(){
        return mDriverController.getTrigger(Side.RIGHT);
    }

    public boolean getDrivePrecisionSteer(){
        return mDriverController.getTrigger(Side.LEFT);
    }

    public void setOperatorRumble(boolean a){ 
        mOperatorController.setRumble(a);
    }

    public void setDriverRumble(boolean a){ 
        mDriverController.setRumble(a);
    }

  
    public boolean getArmEject() {
        return mOperatorController.getTrigger(Side.LEFT);
    }

    public boolean getGrasperIntakeManual() {
        return mLeftBumper.update(mOperatorController.getTrigger(Side.RIGHT));
    }

    /**
     * Switches the Robot Mode
     * On the Start Button of the Operator Controller
     * @return
     */
    public boolean getSwitchModePress(){
        return mOperatorStartButton.update(mOperatorController.getButton(Button.START));
    }

    public boolean getSelectButtonPress(){
        return mOperatorSelectButton.update(mOperatorController.getButton(Button.BACK));
    }

    public boolean getArmExtend() {
        int input = mOperatorController.getDPad();
        return input <= 45 && input >= 315;
    }

    public boolean getArmRetract() {
        int input = mOperatorController.getDPad();
        return input <= 225 && input >= 135;
    }

    public boolean getArmExtendManual() {
        return mOperatorController.getButton(Button.B);
    }

    public boolean getArmRetractManual() {
        return mOperatorController.getButton(Button.X);
    }

    public boolean getArmExtendPress(){
        return mExtensionUp.update(mOperatorController.getButton(Button.B));
    }

    public boolean getArmRetractPress(){
        return mExtensionDown.update(mOperatorController.getButton(Button.X));
    }

    public boolean getArmJogUp() {
        return mOperatorController.getButton(Button.Y);
    }

    public boolean getArmJogDown() {
        return mOperatorController.getButton(Button.A);
    }

    public boolean getArmRotateUp() {
        return mArmRotateUp.update(mDriverController.getButton(Button.Y));
    }

    public boolean getArmRotateDown() {
        return mArmRotateDown.update(mDriverController.getButton(Button.A));
    }

    public double getShoulderTargetX() {
        return mOperatorController.getJoystick(Side.RIGHT, Axis.X);
    }

    public double getShoulderTargetY() {
        return mOperatorController.getJoystick(Side.RIGHT, Axis.X);
    }

    public boolean getClimberTestExtend() {
        return mOperatorController.getButton(Button.RB);
    }

    public boolean getClimberTestRetract() {
        return mOperatorController.getButton(Button.LB);
    }

    public boolean getTestZeroPress(){
        return mOperatorController.getButton(Button.L_JOYSTICK);
    }

    public boolean getSwitchExtensionMode(){
        return mExtensionSwitchMode.update(mOperatorController.getButton(Button.R_JOYSTICK));
    }

    // Operator Presses to Approve Next Climbing Stage
    public boolean getOperatorClimbStageApprovePress() {
        return mOperatorClimbApprovePress.update(mOperatorController.getButton(Button.A));
    }

    public boolean getClimbCancel() {
        return mOperatorController.getButton(Button.BACK);
    }
    public boolean getRunShooterForward() {
        return mDriverController.getTrigger(Side.RIGHT);
    }
  
    public boolean getRunShooterBackward() {
        return mDriverController.getTrigger(Side.LEFT);
    }
    public boolean getFeedUp() {
        return mDriverController.getButton(Button.X);
    }
  
    public boolean getFeedDown() {
        return mDriverController.getButton(Button.A);
    }

    public boolean getFeedShooterUp() {
        return mDriverController.getButton(Button.Y);
    }
    public boolean getFeedShooterDown() {
        return mDriverController.getButton(Button.B);
    }
}
