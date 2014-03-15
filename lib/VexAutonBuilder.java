import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import java.beans.*;
import java.math.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.lang.NumberFormatException;
import javax.imageio.*;
import com.apple.eawt.Application;

/**
 * VEX Autonomous Builder for six bars in Toss Up
 * 
 * @author	Eric Van Lare
 */
public class VexAutonBuilder {
	private boolean doStuff = true;

	public static void main(String[] args) {
		final JFrame frame = new JFrame("VEX Autonomous Builder");
		frame.setIconImage(new ImageIcon("vex_robotics.png").getImage());
		Application application = Application.getApplication();
		Image image = Toolkit.getDefaultToolkit().getImage("254B_Swoosh.png");
		application.setDockIconImage(image);

		class DrawingComponent extends JComponent {
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
					/*public void mouseReleased ( MouseEvent e ) {
						if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
							lastPoint = e.getPoint();
							System.out.println(lastPoint);
							currentShape = null;
							repaint ();
						}
					}*/
				};
				addMouseListener (mouseAdapter);
				addMouseMotionListener (mouseAdapter);
			}

			public DrawingComponent(ArrayList<JRadioButton> arr) {
				starts.add(new Point2D.Double(544.0,256.0)); //rf
				starts.add(new Point2D.Double(544.0,146.0)); //rb
				starts.add(new Point2D.Double(56.0,256.0)); //bf
				starts.add(new Point2D.Double(56.0,146.0)); //bb
				for (int i = 0; i < 4; i++) {
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
					if (lineDirs.get(squareNo).get(i).equals("B")/* || (shapes.get(i).equals(currentShape) && buttons.get(1).isSelected())*/) {
						//if (!(!buttons.get(0).isSelected() && buttons.get(1).isSelected() && shapes.get(i).equals(currentShape)))
							g2d.draw (shapes.get(i));
					}
				}
				g2d.setStroke(new BasicStroke(2));
				g2d.setPaint(Color.GREEN);
				for (int i = 0; i < shapes.size(); i++) {
					if (lineDirs.get(squareNo).get(i).equals("F")/* || (shapes.get(i).equals(currentShape) && buttons.get(0).isSelected())*/) {
						//if (!(!buttons.get(0).isSelected() && buttons.get(1).isSelected() && shapes.get(i).equals(currentShape)))
							g2d.draw (shapes.get(i)); //fuck idk
					}
				}
				g2d.setStroke(new BasicStroke(2));
				g2d.setPaint (Color.ORANGE);
				for ( int i = 0; i < 4; i++) {
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
				squareNo++;
				if (squareNo == 1) {
					JOptionPane.showMessageDialog(null,"Input for Far Square");
				}
				else if (squareNo == 2) {
					JOptionPane.showMessageDialog(null,"Input for Middle Blue Square");
				}
				else if (squareNo == 3) {
					JOptionPane.showMessageDialog(null,"Input for Far Blue Square");
				}
				else {
					squareNo = 0;
					JOptionPane.showMessageDialog(null,"Input for Middle Square");
				}
				shapes = squares.get(squareNo);
				currentShape = null;
				if (squares.get(squareNo).size() == 0)
					lastPoint = starts.get(squareNo);
				else
					lastPoint = squares.get(squareNo).get(squares.get(squareNo).size()-1).getP2();
				repaint();
			}

			public void nextTask() {
				System.out.println(getLastTurn()+" "+getLastLength());
				if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
					lastPoint = currentShape.getP2();
					System.out.println(lastPoint);
					currentShape = null;
					repaint();
				}
				currentShape = null;
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
				System.out.println("Nuthin");
				return 0.0;
			}

			public int squareNo() {
				return squareNo;
			}
		}

		final ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
		final DrawingComponent drawings = new DrawingComponent(buttons);
		frame.add(drawings);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		//frame.setLayout(null);
		frame.setSize(1000,660);
		frame.setVisible(true);

		//Robot Box Layout---------------------------------------------------------------------------------------------------------------------------
		final JPanel robot = new JPanel();
		robot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Robot"));

		//Drive speed full, half, Lift time full, half, time to spin 360
		final ArrayList<Double> importantVars = new ArrayList<Double>();
		final ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 5; i++){
			importantVars.add(0.0);
			input.add("add");
		}
		//Default values:
		importantVars.set(4,new Double(3.0));

		JLabel robot1 = new JLabel("Drive Speed:");
		robot1.setFont(robot1.getFont().deriveFont(12.0f));
		robot1.setBounds(20,20,100,25);
		robot.add(robot1);
		
		final JButton speed1 = new JButton("Full Power:");
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
				robot.add(speed1);
			}
		});
		robot.add(speed1);

		final JButton speed2 = new JButton("Half Power:");
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
				robot.add(speed2);
			}
		});
		robot.add(speed2);

		JLabel robot2 = new JLabel("Lift to Full:");
		robot2.setFont(robot2.getFont().deriveFont(12.0f));
		robot2.setBounds(20,90,100,25);
		robot.add(robot2);

		final JButton lift1 = new JButton("Full Power:");
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
				robot.add(lift1);
			}
		});
		robot.add(lift1);

		final JButton lift2 = new JButton("Half Power:");
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


		//Field Panel--------------------------------------------------------------------------------------------------------------------------------
		ImagePanel field = new ImagePanel(new ImageIcon("tossupfieldobjects.jpg").getImage());
		field.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		field.setBounds(390,10,600,600);
		frame.add(field);


		//Task Panel---------------------------------------------------------------------------------------------------------------------------------
		final ArrayList<ArrayList<RoboTask>> tasksList = new ArrayList<ArrayList<RoboTask>>();
		for (int i = 0; i < 4; i++)
			tasksList.add(new ArrayList<RoboTask>());
		final ArrayList<ArrayList<RoboTask>> fillerTasksList = new ArrayList<ArrayList<RoboTask>>();
		for (int i = 0; i < 4; i++)
			fillerTasksList.add(new ArrayList<RoboTask>());

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
		final JPanel taskList = new JPanel();
		taskList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Task List"));
		frame.add(taskList);

		final JPanel innerTaskList = new JPanel(); //Used to be InnerPanel, just for testing but nope
		final JScrollPane scroller = new JScrollPane(innerTaskList);
		//Rectangle rect = new Rectangle(1000,100);
		//innerTaskList.add(rect);
		innerTaskList.setBounds(0,0,100,100);

		final ArrayList<String> buttonNames = new ArrayList<String>();
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
				int buttonPressed = -1;
				for (int i = 0; i < 7; i++)
					if (buttons.get(i).isSelected())
						buttonPressed = i;
				if (buttonPressed != -1) {
					try {
						if (buttonPressed == 0 || buttonPressed == 1) {
							System.out.println("STARTING");
							if (fillerTasksList.get(drawings.squareNo()).size() == 0) {
								double length = drawings.getLastLength();
								double turn = drawings.getLastTurn();
								tasksList.get(drawings.squareNo()).add(new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
									buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars, 
									length, turn));
								tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1).setBounds(0,0,100,100);
								innerTaskList.add(tasksList.get(drawings.squareNo()).get(tasksList.get(drawings.squareNo()).size()-1));
							} else {
								double length = drawings.getLastLength();
								double turn = drawings.getLastTurn();
								tasksList.get(drawings.squareNo()).set((4-fillerTasksList.get(drawings.squareNo()).size()),
									new RoboTask(new ImageIcon(ImageIO.read(new File("images/"+
									buttonNames.get(buttonPressed).replace(" ","")+".png"))), buttonPressed, importantVars,
									length, turn));
								innerTaskList.remove(4-fillerTasksList.get(drawings.squareNo()).size());
								tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()).setBounds(0,0,100,100);
								innerTaskList.add(tasksList.get(drawings.squareNo()).get(4-fillerTasksList.get(drawings.squareNo()).size()),
									4-fillerTasksList.get(drawings.squareNo()).size());
								fillerTasksList.get(drawings.squareNo()).remove(0);
							}
						}
						else {
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
					System.out.println(""+buttonPressed+", "+importantVars);
					drawings.nextTask();
				}
			}
		});
		task.add(addTask);

		ButtonGroup buttonGroup = new ButtonGroup();
		for (int i = 0; i < 7; i++) {
			buttons.add(i, new JRadioButton(buttonNames.get(i)));
			buttons.get(i).setBounds(25,(25+20*i),150,20);
			buttonGroup.add(buttons.get(i));
			task.add(buttons.get(i));
		}

		//Task List Panel----------------------------------------------------------------------------------------------------------------------------
		

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
					bw.write(configAuton(importantVars, tasksList));
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
				try {
					drawings.clear();
					BufferedImage blank1 = ImageIO.read(new File("images/Blank.png"));
					int subtractMe = 0;
					if (fillerTasksList.get(drawings.squareNo()).size() > 0)
						subtractMe = fillerTasksList.get(drawings.squareNo()).size();
					for (int i = tasksList.get(drawings.squareNo()).size()-1-subtractMe; i >= 0; i--) {
						if (i > 3) {
							tasksList.get(drawings.squareNo()).remove(i);
							innerTaskList.remove(i);
						} else {
							tasksList.get(drawings.squareNo()).remove(i);
							innerTaskList.remove(0);
							innerTaskList.add(new RoboTask(new ImageIcon(blank1),0,importantVars));
							fillerTasksList.get(drawings.squareNo()).add(new RoboTask(new ImageIcon(blank1),0,importantVars));
							tasksList.get(drawings.squareNo()).add(fillerTasksList.get(drawings.squareNo()).get(0));
						}
					}
					int op = scroller.getHorizontalScrollBar().getValue();
					scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()+1);
					scroller.getHorizontalScrollBar().setValue(scroller.getHorizontalScrollBar().getValue()-1);
					scroller.getHorizontalScrollBar().setValue(op);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		taskList.add(clearTasks);

		JButton nextSquare = new JButton("Next Square");
		nextSquare.setBounds(73,173,100,25);
		nextSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		});
		taskList.add(nextSquare);

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
		String returnMe = "task autonomous() {\n";
		for (int j = 0; j < 2; j++) {
			if (j == 0)
				returnMe += "\tif (zone == 0 && side == 0) {\n";
			else if (j == 1)
				returnMe += "\tif (zone == 1 && side == 0) {\n";
			else if (j == 2)
				returnMe += "\tif (zone == 0 && side == 1) {\n";
			else if (j == 3)
				returnMe += "\tif (zone == 1 && side == 1) {\n";
			for (int i = 0; i < tasksList.get(j).size(); i++) {
				if (tasksList.get(j).get(i).getTaskIndex() > -1) {
					if (tasksList.get(j).get(i).getType() == 'd' && i < tasksList.get(j).size()-1 && tasksList.get(j).get(i+1).getType() == 'l') {
						if (tasksList.get(j).get(i).getTimeNeeded() >= tasksList.get(j).get(i+1).getTimeNeeded()) {
							returnMe += tasksList.get(j).get(i).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i).getTimeNeeded()-tasksList.get(j).get(i+1).getTimeNeeded()) + ");\n"
										+ tasksList.get(j).get(i+1).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i+1).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i+1).getTaskEnd() + tasksList.get(j).get(i).getTaskEnd();
						}
						else {
							returnMe += tasksList.get(j).get(i+1).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i+1).getTimeNeeded()-tasksList.get(j).get(i).getTimeNeeded()) + ");\n"
										+ tasksList.get(j).get(i).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i).getTaskEnd() + tasksList.get(j).get(i+1).getTaskEnd();
						}
						i++;
					}
					else if (tasksList.get(j).get(i).getType() == 'l' && i < tasksList.get(j).size()-1 && tasksList.get(j).get(i+1).getType() == 'd') {
						if (tasksList.get(j).get(i).getTimeNeeded() <= tasksList.get(j).get(i+1).getTimeNeeded()) {
							returnMe += tasksList.get(j).get(i).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i).getTimeNeeded()-tasksList.get(j).get(i+1).getTimeNeeded()) + ");\n"
										+ tasksList.get(j).get(i+1).getTaskStart() + "\t\twait1Msec("
										+ tasksList.get(j).get(i+1).getTimeNeeded() + ");\n"
										+ tasksList.get(j).get(i+1).getTaskEnd() + tasksList.get(j).get(i).getTaskEnd();
						}
						else {
							returnMe += tasksList.get(j).get(i+1).getTaskStart();
							returnMe += "\t\twait1Msec(" + (tasksList.get(j).get(i+1).getTimeNeeded()-tasksList.get(j).get(i).getTimeNeeded()) + ");\n"
										+ tasksList.get(j).get(i).getTaskStart() + "\t\twait1Msec("
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
	private int tIndex;

	/**
	 * Constructor for tasks not consisting of driving forward or backward
	 */
	public RoboTask(Icon image, int taskIndex, ArrayList<Double> importantVars) {
		super(image);
		tIndex = taskIndex;
		if (taskIndex == 2) { //Lift
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

	/**
	 * Constructor for tasks consisting of driving forward or backward
	 */
	public RoboTask(Icon image, int taskIndex, ArrayList<Double> importantVars, double length, double turn) {
		super(image);
		int timeForTurn = (int)(Math.abs(turn)*importantVars.get(4).doubleValue()/360.0*1000.0);
		System.out.println(taskIndex);
		if (taskIndex == 0) {
			taskStart = "\t\tturn(" + (int)(turn) + ");\n\t\twait1Msec(" + timeForTurn + ");\n\t\tdrive(127);\n";
		} else if (taskIndex == 1) {
			taskStart = "\t\tturn(" + (int)(turn) + ");\n\t\twait1Msec(" + timeForTurn + ");\n\t\tdrive(-127);\n";
		}
		timeNeeded = (int)(length/importantVars.get(0)*1000.0);
		taskEnd = "\t\tdrive(0);\n";
		type = 'd';
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
		return tIndex;
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