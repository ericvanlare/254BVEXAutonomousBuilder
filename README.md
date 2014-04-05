254BVEXAutonomousBuilder
========================

254B's VEX Autonomous Builder GUI to create autonomous modes and export the RobotC 

Features
   -GUI for inputting autonomous mode sequences
   -Tailored to common robot setup, requires a few user-made methods
   -Output as task autonomous, copy paste into RobotC IDE
   -Uses speed values of robot measured in time to for hard coding

What's new in v1.0.1
   -More intuitive interface
      -Menu bar implemented
   -Save/Load feature for autonomous and robot configurations
   -Major bug fixes
   -Code streamlining for improved runtime

Coming soon (or at some point in 2014-2015 game)
   -Timeline of events instead of blocks for more accuracy
   -More visual improvements
   -Markers for all actions
   -Option for values based on encoders
   -More comprehensive robot configuration including setup wizard for ports
   -More comprehensive code output, eventually complete RobotC file

———————————————————————
Instructions
1. Open terminal window
2. ‘cd’ to the lib directory
3. Run ‘java VexAutonBuilder’
4. Input values for robot speed
5. Add tasks for front and back autonomous
6. Print code to RobotC
7. copy paste code into RobotC IDE