import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.lang.NumberFormatException;
import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.text.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import com.apple.eawt.Application;

/**
 * VEX Autonomous Builder GUI for the 2013-2014 Toss Up game, will be updated
 * soon after Worlds for 2014-2015 game.
 *
 * Features
 *   -GUI for inputting autonomous mode sequences
 *   -Tailored to common robot setup, requires a few user-made methods
 *   -Output as task autonomous, copy paste into RobotC IDE
 *   -Uses speed values of robot measured in time to for hard coding
 *
 * What's new in v1.0.1
 *   -More intuitive interface
 *      -Menu bar implemented
 *   -Save/Load feature for autonomous and robot configurations
 *   -Major bug fixes
 *   -Code streamlining for improved runtime
 *
 * Coming soon (or at some point in 2014-2015 game)
 *   -Timeline of events instead of blocks for more accuracy
 *   -More visual improvements
 *   -Markers for all actions
 *   -Option for values based on encoders
 *   -More comprehensive robot configuration including setup wizard for ports
 *   -More comprehensive code output, eventually complete RobotC file
 * 
 * @author	Eric Van Lare
 */
public class VexAutonBuilder {
		/**
		 * Intereactive field frame
		 */
		static class DrawingComponent extends JComponent {
			private ArrayList<Line2D> shapes;
			private ArrayList<ArrayList<Line2D>> squares = new ArrayList<ArrayList<Line2D>>();
			private Line2D currentShape = null;
			private Point2D lastPoint = new Point2D.Double(544.0,256.0);
			private ArrayList<Point2D.Double> starts = new ArrayList<Point2D.Double>();
			private int squareNo = 0;
			private ArrayList<JRadioButton> buttons;
			private ArrayList<ArrayList<String>> lineDirs = new ArrayList<ArrayList<String>>();

			/*To make starting point: 18 in^2 robot is 75 px^2, must be within:
			Red Front:
				496 < x < 593
				219 < y < 302
			Red Back:
				496 < x < 593
				105 < y < 189
			Blue Front:
				8 < x < 103
				219 < y < 302
			Blue Back:
				8 < x < 103
				105 < y < 189*/
			{
				MouseAdapter mouseAdapter = new MouseAdapter () {
					public void mousePressed ( MouseEvent e ) {
						if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
							if (currentShape == null) {
								currentShape = new Line2D.Double ( lastPoint, e.getPoint () );
								shapes.add (currentShape);
								if (buttons.get(0).isSelected())
									lineDirs.get(squareNo).add("F");
								else
									lineDirs.get(squareNo).add("B");
							}
							else {
								Line2D shape = (Line2D) currentShape;
								shape.setLine (shape.getP1(), e.getPoint());
								if (buttons.get(0).isSelected())
									lineDirs.get(squareNo).set(lineDirs.get(squareNo).size()-1,"F");
								else
									lineDirs.get(squareNo).set(lineDirs.get(squareNo).size()-1,"B");
							}
							repaint ();
						}
	            	}
	            	public void mouseDragged ( MouseEvent e ) {
	            		if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
							Line2D shape = (Line2D) currentShape;
							shape.setLine (shape.getP1(), e.getPoint());
							repaint ();
						}
					}
				};
				addMouseListener (mouseAdapter);
				addMouseMotionListener (mouseAdapter);
			}

			public DrawingComponent(ArrayList<JRadioButton> arr) {
				starts.add(new Point2D.Double(544.0,256.0)); //rf
				starts.add(new Point2D.Double(544.0,146.0)); //rb
				//starts.add(new Point2D.Double(56.0,256.0)); //bf
				//starts.add(new Point2D.Double(56.0,146.0)); //bb
				for (int i = 0; i < 2; i++) {
					squares.add(i, new ArrayList<Line2D>());
					lineDirs.add(i, new ArrayList<String>());
				}
				shapes = squares.get(0);
				buttons = arr;
			}

			protected void paintComponent (Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(4));
				g2d.setPaint (Color.RED);
				for (int i = 0; i < shapes.size(); i++) {
					if (lineDirs.get(squareNo).size() > i && lineDirs.get(squareNo).get(i).equals("B")) {
							g2d.draw (shapes.get(i));
					}
				}
				g2d.setStroke(new BasicStroke(2));
				g2d.setPaint(Color.GREEN);
				for (int i = 0; i < shapes.size(); i++) {
					if (lineDirs.get(squareNo).size() > i && lineDirs.get(squareNo).get(i).equals("F")) {
							g2d.draw (shapes.get(i));
					}
				}
				g2d.setStroke(new BasicStroke(2));
				g2d.setPaint (Color.ORANGE);
				for ( int i = 0; i < 2; i++) {
					if (i != squareNo)
						for (int j = 0; j < squares.get(i).size(); j++)
							g2d.draw(squares.get(i).get(j));
				}
				setBounds(390,10,599,599);
			}

			public void clear() {
				while (shapes.size() > 0) {
					lineDirs.get(squareNo).remove(0);
					shapes.remove(0);
				}
				currentShape = null;
				lastPoint = starts.get(squareNo);
				repaint();
			}

			public void next() {
				squareNo = ((squareNo*2-1)*-1+1)/2;
				System.out.println("SQR"+squareNo);
				/*if (squareNo == 1) {
					JOptionPane.showMessageDialog(null,"Input for Far Square");
				}
				else {
					squareNo = 0;
					JOptionPane.showMessageDialog(null,"Input for Middle Square");
				}*/
				shapes = squares.get(squareNo);
				currentShape = null;
				if (squares.get(squareNo).size() == 0)
					lastPoint = starts.get(squareNo);
				else
					lastPoint = squares.get(squareNo).get(squares.get(squareNo).size()-1).getP2();
				repaint();
			}

			public void nextTask() {
				System.out.println("Turn: " + getLastTurn() + ", Length: " + getLastLength());
				if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
					if (currentShape != null)
						lastPoint = currentShape.getP2();
					System.out.println(lastPoint);
					currentShape = null;
					repaint();
				}
				currentShape = null;
			}

			public void makeLine(int taskIndex, Point2D endPoint) {
				if (taskIndex == 0 || taskIndex == 1) {
					if (currentShape == null) {
						currentShape = new Line2D.Double ( lastPoint, endPoint );
						shapes.add (currentShape);
						lastPoint = currentShape.getP2();
						if (taskIndex == 0)
							lineDirs.get(squareNo).add("F");
						else
							lineDirs.get(squareNo).add("B");
					}
					else {
						Line2D shape = (Line2D) currentShape;
						shape.setLine (shape.getP2(), endPoint);
						if (taskIndex == 0)
							lineDirs.get(squareNo).set(lineDirs.get(squareNo).size()-1,"F");
						else
							lineDirs.get(squareNo).set(lineDirs.get(squareNo).size()-1,"B");
					}
					repaint ();
				}
			}

			//in inches
			public double getLastLength() {
				if (currentShape != null) {
					double x = currentShape.getX2()-currentShape.getX1();
					double y = currentShape.getY2()-currentShape.getY1();
					return Math.sqrt(x*x+y*y)*3.0/25.0;
				}
				return 0.0;
			}

			//in degrees
			public double getLastTurn() {
				if (currentShape != null && shapes.size() > 1 && shapes.get(shapes.size()-2) != null) {
					double x1 = currentShape.getX2()-currentShape.getX1();
					double y1 = currentShape.getY2()-currentShape.getY1();
					if (lineDirs.get(squareNo).get(lineDirs.get(squareNo).size()-1).equals(
						lineDirs.get(squareNo).get(lineDirs.get(squareNo).size()-2))) {
						x1 *= -1;
						y1 *= -1;
					}
					double x2 = shapes.get(shapes.size()-2).getX1()-shapes.get(shapes.size()-2).getX2();
					double y2 = shapes.get(shapes.size()-2).getY1()-shapes.get(shapes.size()-2).getY2();
					double degrees = (Math.atan2(y1,x1) - Math.atan2(y2,x2))*57.2957795;
					if (degrees > 180)
						degrees -= 360;
					else if (degrees < -180)
						degrees += 360;
					return degrees;
				}
				return 0.0;
			}

			public Point2D getLastPoint() {
				return currentShape.getP2();
			}

			public int squareNo() {
				return squareNo;
			}

			public void currToNull() {
				if (currentShape != null) {
					shapes.remove(currentShape);
					currentShape = null;
					lineDirs.get(squareNo).remove(lineDirs.get(squareNo).size()-1);
					repaint();
				}
			}
		}


	final static private JFrame frame = new JFrame("VEX Autonomous Builder");
	final static private ArrayList<Double> importantVars = new ArrayList<Double>();
	final static private ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
	final static private DrawingComponent drawings = new DrawingComponent(buttons);
	final static private ArrayList<ArrayList<RoboTask>> tasksList = new ArrayList<ArrayList<RoboTask>>();
	final static private ArrayList<ArrayList<RoboTask>> fillerTasksList = new ArrayList<ArrayList<RoboTask>>();
	final static private JPanel taskList = new JPanel();
	final static private JPanel innerTaskList = new JPanel(); //Used to be InnerPanel, just for testing but nope
	final static private JScrollPane scroller = new JScrollPane(innerTaskList);
	final static private ArrayList<String> buttonNames = new ArrayList<String>();
	final static private JPanel robot = new JPanel();
	final static private ArrayList<String> input = new ArrayList<String>();
	final static private JButton speed1 = new JButton("Full Power:");
	final static private JButton speed2 = new JButton("Half Power:");
	final static private JButton lift1 = new JButton("Full Power:");
	final static private JButton lift2 = new JButton("Half Power:");

	public static void main(String[] args) {
		frame.setIconImage(new ImageIcon("images/vex_robotics.png").getImage());
		Application application = Application.getApplication();
		Image image = Toolkit.getDefaultToolkit().getImage("images/254B_Swoosh.png");
		application.setDockIconImage(image);

		//Make it feel like a Mac App
		if (System.getProperty("os.name").equals("Mac OS X")) {
			try {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(ClassNotFoundException e) {
				System.out.println("ClassNotFoundException: " + e.getMessage());
			}
			catch(InstantiationException e) {
				System.out.println("InstantiationException: " + e.getMessage());
			}
			catch(IllegalAccessException e) {
				System.out.println("IllegalAccessException: " + e.getMessage());
			}
			catch(UnsupportedLookAndFeelException e) {
				System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
			}
		}

		JMenuBar menuBar = new JMenuBar();
		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Field");
		JMenu menu3 = new JMenu("Robot");
		JMenu taskMenu = new JMenu("Task");
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		menuBar.add(taskMenu);
		frame.setJMenuBar(menuBar);

		frame.add(drawings);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		int ySize = 665;
		if (System.getProperty("os.name").equals("Mac OS X"))
			ySize = 645;
		frame.setSize(1000,ySize);
		frame.setVisible(true);

		//Robot Box Layout---------------------------------------------------------------------------------------------------------------------
		robot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Robot"));

		//Drive speed full, half, Lift time full, half, time to spin 360
		for (int i = 0; i < 5; i++){
			importantVars.add(0.0);
			input.add("add");
		}
		//Default values:
		//REMOVE THESE BEFORE RELEASE
		importantVars.set(0,new Double(10.0));
		importantVars.set(1,new Double(5.0));
		importantVars.set(2,new Double(1.0));
		importantVars.set(3,new Double(2.0));
		//Time to complete full circle, add changer to GUI soon
		importantVars.set(4,new Double(3.0));

		JLabel robot1 = new JLabel("Drive Speed:");
		robot1.setFont(robot1.getFont().deriveFont(12.0f));
		robot1.setBounds(20,20,100,25);
		robot.add(robot1);

		for (int i = 0; i < 4; i++)
			tasksList.add(new ArrayList<RoboTask>());
		for (int i = 0; i < 4; i++)
			fillerTasksList.add(new ArrayList<RoboTask>());
		
		speed1.setBounds(35,45,130,17);
		speed1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				input.set(0, JOptionPane.showInputDialog(null,"Enter drive speed at full power in inches per second: "));

				boolean isNumber = false;
				try  {
					importantVars.set(0,new Double(Double.parseDouble(input.get(0))));
					isNumber = true;
				} catch (NumberFormatException nfe) {
					isNumber = false;
				}
				while (!isNumber) {
					try {
						importantVars.set(0,new Double(Double.parseDouble(input.get(0))));
						isNumber = true;
					} catch (NumberFormatException nfe) {
						input.set(0, JOptionPane.showInputDialog(null,"That's not a number, enter drive speed at full power"+
																	"\nin inches per second: "));
						isNumber = false;
					}
				}
				robot.remove(speed1);
				speed1.setText("Full Power: "+input.get(0));
				speed1.setBounds(35,45,130,17);
				frame.repaint(100,0,0,500,500);
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < tasksList.get(i).size(); j++) {
						tasksList.get(i).get(j).update(importantVars);
					}
				}
				robot.add(speed1);
			}
		});
		robot.add(speed1);

		speed2.setBounds(35,65,130,17);
		speed2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.set(1, JOptionPane.showInputDialog(null,"Enter drive speed at half power in inches per second: "));
				boolean isNumber = false;
				try  {
					importantVars.set(1,new Double(Double.parseDouble(input.get(1))));
					isNumber = true;
				} catch (NumberFormatException nfe) {
					isNumber = false;
				}
				while (!isNumber) {
					try {
						importantVars.set(1,new Double(Double.parseDouble(input.get(1))));
						isNumber = true;
					} catch (NumberFormatException nfe) {
						input.set(1, JOptionPane.showInputDialog(null,"That's not a number, enter drive speed at half power"+
																	"\nin inches per second: "));
						isNumber = false;
					}
				}
				robot.remove(speed2);
				speed2.setText("Half Power: "+input.get(1));
				speed2.setBounds(35,65,130,17);
				frame.repaint(100,0,0,500,500);
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < tasksList.get(i).size(); j++) {
						tasksList.get(i).get(j).update(importantVars);
					}
				}
				robot.add(speed2);
			}
		});
		robot.add(speed2);

		JLabel robot2 = new JLabel("Lift to Full:");
		robot2.setFont(robot2.getFont().deriveFont(12.0f));
		robot2.setBounds(20,90,100,25);
		robot.add(robot2);

		lift1.setBounds(35,115,130,17);
		lift1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				input.set(2, JOptionPane.showInputDialog(null,"Enter time to lift from fully compressed to fully extended"+
															"\nat full power in seconds: "));

				boolean isNumber = false;
				try  {
					importantVars.set(2,new Double(Double.parseDouble(input.get(2))));
					isNumber = true;
				} catch (NumberFormatException nfe) {
					isNumber = false;
				}
				while (!isNumber) {
					try {
						importantVars.set(2,new Double(Double.parseDouble(input.get(2))));
						isNumber = true;
					} catch (NumberFormatException nfe) {
						input.set(2, JOptionPane.showInputDialog(null,"That's not a number, enter time to lift from fully"+
																	"\ncompressed to fully extended at full power in seconds: "));
						isNumber = false;
					}
				}
				robot.remove(lift1);
				lift1.setText("Full Power: "+input.get(2));
				lift1.setBounds(35,115,130,17);
				frame.repaint(100,0,0,500,500);
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < tasksList.get(i).size(); j++) {
						tasksList.get(i).get(j).update(importantVars);
					}
				}
				robot.add(lift1);
			}
		});
		robot.add(lift1);

		lift2.setBounds(35,135,130,17);
		lift2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.set(3, JOptionPane.showInputDialog(null,"Enter time to lift from fully compressed to fully extended"+
															"\nat half power in seconds: "));
				boolean isNumber = false;
				try  {
					importantVars.set(3,new Double(Double.parseDouble(input.get(3))));
					isNumber = true;
				} catch (NumberFormatException nfe) {
					isNumber = false;
				}
				while (!isNumber) {
					try {
						importantVars.set(3,new Double(Double.parseDouble(input.get(3))));
						isNumber = true;
					} catch (NumberFormatException nfe) {
						input.set(3, JOptionPane.showInputDialog(null,"That's not a number, enter time to lift from fully"+
																	"\ncompressed to fully extended at half power in seconds: "));
						isNumber = false;
					}
				}
				robot.remove(lift2);
				lift2.setText("Half Power: "+input.get(3));
				lift2.setBounds(35,135,130,17);
				frame.repaint(100,0,0,500,500);
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < tasksList.get(i).size(); j++) {
						tasksList.get(i).get(j).update(importantVars);
					}
				}
				robot.add(lift2);
			}
		});
		robot.add(lift2);

		JButton instructions = new JButton("Instructions");
		instructions.setBounds(267,170,100,25);
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
					"Instructions:\n\n\n"+
					"This app will take you through the completion of building your own VEX\n"+
					"autonomous code.\n\n"+
					"It will start your robot in the middle red square and work its way through\n"+
					"the four different starting positions.\n\n"+
					"To add an action, click on the task type you wish to perform and fill out\n"+
					"the necessary information.\n\n"+
					"If you wish to move on the map, click your destination.\n\n"+
					"When you are done, click the \"Print Code\" button."
				);
			}
		});
		robot.add(instructions);

		robot.setBounds(10,3,370,200);
		frame.add(robot);
		frame.repaint(100,0,0,500,500);


		//Field Panel--------------------------------------------------------------------------------------------------------------------------
		ImagePanel field = new ImagePanel(new ImageIcon("images/tossupfieldobjects.jpg").getImage());
		field.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		field.setBounds(390,10,600,600);
		frame.add(field);


		//Task Panel---------------------------------------------------------------------------------------------------------------------------
		

		JPanel task = new JPanel();
		task.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Task"));
		task.setBounds(10,206,370,200);
		frame.add(task);

		//For the task scroller, needed to be initialized here
		//Gonna have to make this a custom class
		//DELETE ME EVENTUALLY
		class InnerPanel extends JPanel {
			private int width;
			private int height;

			public InnerPanel(int width, int height) {
				this.width = width;
				this.height = height;
			}

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setPaint(Color.WHITE);
				g2d.draw(new Rectangle(5,5,width, height));
			}
		}
		taskList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Task List"));
		frame.add(taskList);

		//Rectangle rect = new Rectangle(1000,100);
		//innerTaskList.add(rect);
		innerTaskList.setBounds(0,0,100,100);

		buttonNames.add(0,"Drive Forward");
		buttonNames.add(1,"Drive Backward");
		buttonNames.add(2,"Lift");
		buttonNames.add(3,"Lower");
		buttonNames.add(4,"Intake");
		buttonNames.add(5,"Outtake");
		buttonNames.add(6,"Wait");

		JButton addTask = new JButton("Add Task");
		addTask.setBounds(267,170,100,25);
		addTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double length = drawings.getLastLength();
				double turn = drawings.getLastTurn();
				int buttonPressed = -1;
				for (int i = 0; i < 7; i++)
					if (buttons.get(i).isSelected())
						buttonPressed = i;
				addTask(buttonPressed, length, turn);
			}
		});
		task.add(addTask);

		JButton load = new JButton("Load");
		load.setBounds(170,170,100,25);
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File saveFile = new File("vexCode.c");
					if (!saveFile.exists()) {
						JOptionPane.showMessageDialog(null,"No save found");
					}
					BufferedReader br = new BufferedReader(new FileReader("vexCode.c"));
					int fillerI = 0;
					int phase = 0;
					boolean switchBack = true;
					String[] lineArr;
					for (String line = br.readLine(); line != null; fillerI++){
						if (line.contains("/* * * * * * * * * * * * * * * * * * * * * * * * * *")) {
							phase = 1;
						}
						else if (line.contains("* * * * * * * * * * * * * * * * * * * * * * * * */")) {
							phase = 0;
							if (switchBack)
								nxtSqr();
						}
						else if (phase == 1 && line.contains("iv")) {
							lineArr = line.split(" ");
							for (String str : lineArr)
								System.out.print(str + ",");
							System.out.println();
							for (int i = 3; i < 8; i++)
								importantVars.set(i-3, Double.parseDouble(lineArr[i]));
						}
						else if (phase == 1 && line.contains("sqr 0")) {
							phase = 2;
							if (drawings.squareNo() == 1) {
								nxtSqr();
								switchBack = false;
							}
							drawings.clear();
						}
						else if (phase == 2 && line.contains("sqr 1")) {
							nxtSqr();
							drawings.clear();
						}
						else if (phase == 2) {
							lineArr = line.split(" ");
							int buttonPressed = Integer.parseInt(lineArr[2]);
							double length = 0.0;
							double turn = 0.0;
							if (buttonPressed < 2) {
								length = Double.parseDouble(lineArr[3]);
								turn = Double.parseDouble(lineArr[4]);
								System.out.println(line.substring(line.indexOf("[")+1, line.indexOf(",")) + ", " +
												line.substring(line.indexOf(",")+2, line.indexOf("]")));
								Point2D endPoint = new Point2D.Double(Double.parseDouble(line.substring(line.indexOf("[")+1, line.indexOf(","))),
												Double.parseDouble(line.substring(line.indexOf(",")+2, line.indexOf("]"))));
								drawings.makeLine(buttonPressed, endPoint);
							}
							addTask(buttonPressed, length, turn);
						}
						line = br.readLine();
					}
					br.close();
					JOptionPane.showMessageDialog(null,"Configuration Loaded Successfully");
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		});
		task.add(load);
		/*
		JButton save = new JButton("Save");
		save.setBounds(73,170,100,25);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File saveFile = new File("save.txt");
					if (!saveFile.exists()) {
						saveFile.createNewFile();
					}
					saveFile.setWritable(true);
					FileWriter fw = new FileWriter(saveFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(configSave(importantVars, tasksList, fillerTasksList));
					bw.close();
					saveFile.setWritable(false);
					JOptionPane.showMessageDialog(null,"Configuration Saved");
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		});
		task.add(save);*/

		ButtonGroup buttonGroup = new ButtonGroup();
		for (int i = 0; i < 7; i++) {
			buttons.add(i, new JRadioButton(buttonNames.get(i)));
			buttons.get(i).setBounds(25,(25+20*i),150,20);
			buttonGroup.add(buttons.get(i));
			task.add(buttons.get(i));
		}

		//Task List Panel-------------------------------------------------------------------------------------------------------------------
		

		//Scroller Testing, add images to test
		try {
			BufferedImage blank = ImageIO.read(new File("images/Blank.png"));
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					fillerTasksList.get(j).add(new RoboTask(new ImageIcon(blank),-1,importantVars));
					if (j == drawings.squareNo())
						innerTaskList.add(fillerTasksList.get(j).get(i));
					tasksList.get(j).add(fillerTasksList.get(j).get(i));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		scroller.setViewportView(innerTaskList);
		scroller.setBounds(10,20,347,150);
		//scroller.setPreferredSize(new Dimension(1000,100));
		taskList.add(scroller);
		scroller.getHorizontalScrollBar().setValue(1);
		scroller.getHorizontalScrollBar().setValue(0);

		JButton printCode = new JButton("Print Code");
		printCode.setBounds(267,173,100,25);
		printCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File vexCode = new File("VexCode.c");
					if (!vexCode.exists()) {
						vexCode.createNewFile();
					}
					FileWriter fw = new FileWriter(vexCode.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(configSave(importantVars, tasksList, fillerTasksList) + "\n" + configAuton(importantVars, tasksList));
					bw.close();
					JOptionPane.showMessageDialog(null,"Code Printed to VexCode.c");
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		});
		taskList.add(printCode);

		JButton clearTasks = new JButton("Clear");
		clearTasks.setBounds(170,173,100,25);
		clearTasks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearField();
			}
		});
		taskList.add(clearTasks);

		JButton nextSquare = new JButton("Next Square");
		nextSquare.setBounds(73,173,100,25);
		nextSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nxtSqr();
			}
		});
		taskList.add(nextSquare);

		//Menu Bar------------------------------------------------------------------------------------------------------------------------
		JMenuItem printMenuItem = new JMenuItem("Print Code");
		printMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File vexCode = new File("VexCode.c");
					if (!vexCode.exists()) {
						vexCode.createNewFile();
					}
					FileWriter fw = new FileWriter(vexCode.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(configSave(importantVars, tasksList, fillerTasksList) + "\n" + configAuton(importantVars, tasksList));
					bw.close();
					JOptionPane.showMessageDialog(null,"Code Printed to VexCode.c");
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		});
		printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu1.add(printMenuItem);

		JMenuItem nextMenuItem = new JMenuItem("Next Square");
		nextMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nxtSqr();
			}
		});
		nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu2.add(nextMenuItem);

		JMenuItem clearMenuItem = new JMenuItem("Clear");
		clearMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearField();
			}
		});
		clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu2.add(clearMenuItem);

		JMenuItem addMenuItem = new JMenuItem("Add Task");
		addMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double length = drawings.getLastLength();
				double turn = drawings.getLastTurn();
				int buttonPressed = -1;
				for (int i = 0; i < 7; i++)
					if (buttons.get(i).isSelected())
						buttonPressed = i;
				addTask(buttonPressed, length, turn);
			}
		});
		addMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu3.add(addMenuItem);

		JMenuItem mi1 = new JMenuItem("Forward");
		mi1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(0).setSelected(true);
			}
		});
		mi1.setAccelerator(KeyStroke.getKeyStroke('1'));
		taskMenu.add(mi1);

		JMenuItem mi2 = new JMenuItem("Backward");
		mi2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(1).setSelected(true);
			}
		});
		mi2.setAccelerator(KeyStroke.getKeyStroke('2'));
		taskMenu.add(mi2);

		JMenuItem mi3 = new JMenuItem("Lift");
		mi3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(2).setSelected(true);
			}
		});
		mi3.setAccelerator(KeyStroke.getKeyStroke('3'));
		taskMenu.add(mi3);

		JMenuItem mi4 = new JMenuItem("Lower");
		mi4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(3).setSelected(true);
			}
		});
		mi4.setAccelerator(KeyStroke.getKeyStroke('4'));
		taskMenu.add(mi4);

		JMenuItem mi5 = new JMenuItem("Intake");
		mi5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(4).setSelected(true);
			}
		});
		mi5.setAccelerator(KeyStroke.getKeyStroke('5'));
		taskMenu.add(mi5);

		JMenuItem mi6 = new JMenuItem("Outtake");
		mi6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(5).setSelected(true);
			}
		});
		mi6.setAccelerator(KeyStroke.getKeyStroke('6'));
		taskMenu.add(mi6);

		JMenuItem mi7 = new JMenuItem("Wait");
		mi7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttons.get(6).setSelected(true);
			}
		});
		mi7.setAccelerator(KeyStroke.getKeyStroke('7'));
		taskMenu.add(mi7);

		/*frame.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if ("1234567".contains(String.valueOf(e.getKeyChar()))) {
					int i = "1234567".indexOf(String.valueOf(e.getKeyChar()));
					buttons.get(i).setSelected(true);
				}
			}

			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});*/

		taskList.setBounds(10,409,370,203);
		task.repaint();
		taskList.repaint();
		field.repaint();
	}

	/**
	 * TODO WITH WRITING
	 * 
	 * -Create code frame in file, fill in with sensor locations
	 * -For now, call it good with having a file prewritten with the config and
	 *  just return the first x lines
	 * -Do this for drive as well and the basic functions
	 * -For auton, create function to go through tasks and handle the time
	 *  needed
	 */
	public String configSensors(ArrayList<Double> importantVars) {
		//Find the file with the #pragmas
		return "";
	}

	public static String configAuton(ArrayList<Double> importantVars, ArrayList<ArrayList<RoboTask>> tasksList) {
		String returnMe = "task autonomous() {\n\t//insert definition of zone and side here\n";
		for (int j = 0; j < 2; j++) {
			if (j == 0)
				returnMe += "\tif (zone == 0) {\n";
			else if (j == 1)
				returnMe += "\tif (zone == 1) {\n";
			for (int i = 0; i < tasksList.get(j).size(); i++) {
				if (tasksList.get(j).get(i).getTaskIndex() > -1) {
					if (tasksList.get(j).get(i).getType() == 'd' && i < tasksList.get(j).size()-1
							&& tasksList.get(j).get(i+1).getType() == 'l') {
						if (tasksList.get(j).get(i).getTimeNeeded() >= tasksList.get(j).get(i+1).getTimeNeeded()) {
							returnMe += tasksList.get(j).get(i).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i).getTimeNeeded()-tasksList.get(j).get(i+1).getTimeNeeded())
										+ ");\n" + tasksList.get(j).get(i+1).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i+1).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i+1).getTaskEnd() + tasksList.get(j).get(i).getTaskEnd();
						}
						else {
							returnMe += tasksList.get(j).get(i+1).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i+1).getTimeNeeded()-tasksList.get(j).get(i).getTimeNeeded())
										+ ");\n" + tasksList.get(j).get(i).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i).getTaskEnd() + tasksList.get(j).get(i+1).getTaskEnd();
						}
						i++;
					}
					else if (tasksList.get(j).get(i).getType() == 'l' && i < tasksList.get(j).size()-1
							&& tasksList.get(j).get(i+1).getType() == 'd') {
						if (tasksList.get(j).get(i).getTimeNeeded() <= tasksList.get(j).get(i+1).getTimeNeeded()) {
							returnMe += tasksList.get(j).get(i).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i).getTimeNeeded()-tasksList.get(j).get(i+1).getTimeNeeded())
										+ ");\n" + tasksList.get(j).get(i+1).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i+1).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i+1).getTaskEnd() + tasksList.get(j).get(i).getTaskEnd();
						}
						else {
							returnMe += tasksList.get(j).get(i+1).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i+1).getTimeNeeded()-tasksList.get(j).get(i).getTimeNeeded())
										+ ");\n" + tasksList.get(j).get(i).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i).getTaskEnd() + tasksList.get(j).get(i+1).getTaskEnd();
						}
						i++;
					}
					else {
						returnMe += tasksList.get(j).get(i).getTaskStart();
						returnMe += "\t\twait1Msec(" + tasksList.get(j).get(i).getTimeNeeded() + ");\n" + tasksList.get(j).get(i).getTaskEnd();
					}
				}
			}
			returnMe += "\t}\n";
		}
		return returnMe + "}\n";
	}

	public static String configSave(ArrayList<Double> importantVars, ArrayList<ArrayList<RoboTask>> tasksList, 
		ArrayList<ArrayList<RoboTask>> fillerTasksList) {
		String returnMe = "/* * * * * * * * * * * * * * * * * * * * * * * * * *\n" + 
						" * save load data, do not modify\n * iv ";
		for (int i = 0; i < importantVars.size(); i++) {
			returnMe += importantVars.get(i).doubleValue();
			if (i < importantVars.size()-1)
				returnMe += " ";
			else
				returnMe += "\n * ";
		}
		for (int i = 0; i < 2; i++) {
			returnMe += "sqr " + i + "\n * ";
			for (int j = 0; j < tasksList.get(i).size()-fillerTasksList.get(i).size(); j++) {
				returnMe += tasksList.get(i).get(j).getTaskIndex();
				if (tasksList.get(i).get(j).getTaskIndex() < 2) {
					returnMe += " " + tasksList.get(i).get(j).getLength() + " " + tasksList.get(i).get(j).getTurn() + " " +
								tasksList.get(i).get(j).getEndPoint();
				}
				returnMe += "\n * ";
			}
		}
		return returnMe + "* * * * * * * * * * * * * * * * * * * * * * * * */";
	}

	private static void addTask(int buttonPressed, double length, double turn) {
		boolean doIt = true;
		boolean hasZeros = false;
		for (int i = 0; i < importantVars.size(); i++)
			if (importantVars.get(i) == 0.0)
				hasZeros = true;
		if (hasZeros)
			JOptionPane.showMessageDialog(null, "Fill out robot speed variables before continuing to tasks");
		else {
			if (buttonPressed != -1) {
				try {
					if (buttonPressed == 0 || buttonPressed == 1) {
						if (drawings.getLastLength() == 0.0) {
							doIt = false;
						}
						else {
							Point2D endPoint = drawings.getLastPoint();
							if (fillerTasksList.get(drawings.squareNo()).size() == 0) {
								tasksList.get(drawings.squareNo()).add(new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
									buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars, 
									length, turn, endPoint));
								tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1).setBounds(0,0,100,100);
								innerTaskList.add(tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1));
							} else {
								tasksList.get(drawings.squareNo()).set((4-fillerTasksList.get(drawings.squareNo()).size()),
									new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
									buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars,
									length, turn, endPoint));
								innerTaskList.remove(4-fillerTasksList.get(drawings.squareNo()).size());
								tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()).setBounds(0,0,100,100);
								innerTaskList.add(tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()),
									4-fillerTasksList.get(drawings.squareNo()).size());
								fillerTasksList.get(drawings.squareNo()).remove(0);
							}
						}
					}
					else {
						drawings.currToNull();
						if (fillerTasksList.get(drawings.squareNo()).size() == 0) {
							tasksList.get(drawings.squareNo()).add(new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
								buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars));
							tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1).setBounds(0,0,100,100);
							innerTaskList.add(tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1));
						} else {
							tasksList.get(drawings.squareNo()).set((4-fillerTasksList.get(drawings.squareNo()).size()),
								new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
								buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars));
							innerTaskList.remove(4-fillerTasksList.get(drawings.squareNo()).size());
							tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()).setBounds(0,0,100,100);
							innerTaskList.add(tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()),
								4-fillerTasksList.get(drawings.squareNo()).size());
							fillerTasksList.get(drawings.squareNo()).remove(0);
						}
					}
					int op = scroller.getHorizontalScrollBar().getValue();
					scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()+1);
					scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()-1);
					scroller.getHorizontalScrollBar().setValue(op);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				System.out.println("\nImportant variables: " + importantVars);
				if (doIt)
					drawings.nextTask();
				else
					JOptionPane.showMessageDialog(null, "Draw line on field for robot movement first.");
			}
		}
	}

	private static void nxtSqr() {
		drawings.currToNull();
		int prevSquare = drawings.squareNo();
		drawings.next();
		ArrayList<RoboTask> backwards = new ArrayList<RoboTask>();
		for (RoboTask task : tasksList.get(drawings.squareNo())) {
			backwards.add(0,task);
		}
		for (RoboTask task : backwards) {
			innerTaskList.add(task,0);
		}
		for (RoboTask task : tasksList.get(prevSquare)) {
			innerTaskList.remove(tasksList.get(drawings.squareNo()).size());
		}
		int op = scroller.getHorizontalScrollBar().getValue();
		scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()+1);
		scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()-1);
		scroller.getHorizontalScrollBar().setValue(op);
	}

	private static void clearField() {
		try {
			drawings.clear();
			BufferedImage blank1 = ImageIO.read(new File("images/Blank.png"));
			fillerTasksList.set(drawings.squareNo(), new ArrayList<RoboTask>());
			int removeMe = tasksList.get(drawings.squareNo()).size();
			tasksList.set(drawings.squareNo(), new ArrayList<RoboTask>());
			for (int i = 0; i < 4; i++) {
				for (int j = drawings.squareNo(); j < drawings.squareNo()+1; j++) {
					fillerTasksList.get(j).add(new RoboTask(new ImageIcon(blank1),-1,importantVars));
					if (j == drawings.squareNo())
						innerTaskList.add(fillerTasksList.get(j).get(i));
					tasksList.get(j).add(fillerTasksList.get(j).get(i));
				}
			}
			while (removeMe > 0) {
				innerTaskList.remove(0);
				removeMe--;
			}
			int op = scroller.getHorizontalScrollBar().getValue();
			scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()+1);
			scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()-1);
			scroller.getHorizontalScrollBar().setValue(op);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

/**
 * Task object that displays a task in the scroller and keeps track of when the
 * task should start and stop.
 */
class RoboTask extends JLabel {
	private String taskStart = "";
	private String taskEnd = "";
	private int timeNeeded = 0; //In milliseconds
	private int timeCount = 0;
	private char type = 'n';
	private int taskIndex;
	private double length;
	private double turn;
	private Point2D endPoint;
	private ArrayList<Double> importantVars;

	/**
	 * Constructor for tasks not consisting of driving forward or backward
	 */
	public RoboTask(Icon image, int taskIndex, ArrayList<Double> importantVars) {
		super(image);
		this.importantVars = importantVars;
		this.taskIndex = taskIndex;
		update(importantVars);
	}

	/**
	 * Constructor for tasks consisting of driving forward or backward
	 */
	public RoboTask(Icon image, int taskIndex, ArrayList<Double> importantVars, double length, double turn, Point2D endPoint) {
		super(image);
		this.endPoint = endPoint;
		this.length = length;
		this.turn = turn;
		this.taskIndex = taskIndex;
		update(importantVars);
	}

	public void update(ArrayList<Double> importantVars) {
		this.importantVars = importantVars;
		if (taskIndex == 0 || taskIndex == 1) {
			int timeForTurn = (int)(Math.abs(turn)*importantVars.get(4).doubleValue()/360.0*1000.0);
			if (taskIndex == 0) {
				taskStart = "\t\tturn((side*2-1)*127" /*+ (int)(turn)*/ + ");\n\t\twait1Msec(" + timeForTurn + ");\n\t\tdrive(127);\n";
			} else if (taskIndex == 1) {
				taskStart = "\t\tturn((side*2-1)*127" /*+ (int)(turn)*/ + ");\n\t\twait1Msec(" + timeForTurn + ");\n\t\tdrive(-127);\n";
			}
			timeNeeded = (int)(length/importantVars.get(0)*1000.0);
			taskEnd = "\t\tdrive(0);\n";
			type = 'd';
		}
		else if (taskIndex == 2) { //Lift
			taskStart = "\t\tlift(127);\n";
			timeNeeded = (int)(importantVars.get(2).doubleValue()*1000.0);
			taskEnd = "\t\tlift(0);\n";
			type = 'l';
		} else if (taskIndex == 3) { //Lower
			taskStart = "\t\tlift(-127);\n";
			timeNeeded = (int)(importantVars.get(2).doubleValue()*1000.0);
			taskEnd = "\t\tlift(0);\n";
			type = 'l';
		} else if (taskIndex == 4) { //Intake
			taskStart = "\t\tintake(127);\n";
			timeNeeded = 1500;
			taskEnd = "\t\tintake(0);\n";
			type = 'i';
		} else if (taskIndex == 5) { //Outtake
			taskStart = "\t\tintake(-127);\n";
			timeNeeded = 3000;
			taskEnd = "\t\tintake(0);\n";
			type = 'i';
		} else if (taskIndex == 6) { //Wait
			taskStart = "\t\twhile (SensorValue[go] == 0)\n"+
							"\t\t\twait1Msec(1);\n";
			timeNeeded = 0;
			taskEnd = "";
			type = 'w';
		}
	}

	public int getTimeNeeded() {
		return timeNeeded;
	}

	public int getTimeRemaining() {
		return timeCount;
	}

	public void startCount() {
		timeCount = timeNeeded;
	}

	public void minus100() {
		timeCount -= 100;
	}

	public String getTaskStart() {
		return taskStart;
	}

	public String getTaskEnd() {
		return taskEnd;
	}

	public char getType() {
		return type;
	}

	public int getTaskIndex() {
		return taskIndex;
	}

	public double getTurn() {
		return turn;
	}

	public double getLength() {
		return length;
	}

	public Point2D getEndPoint() {
		return endPoint;
	}
}

/**
 * Custom JPanel for displaying images, not really needed but whatevs, works
 * for the field at least
 */
class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(String img) {
		this(new ImageIcon(img).getImage());
	}

	public ImagePanel(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}