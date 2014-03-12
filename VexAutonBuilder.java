import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
import acm.graphics.*;
import acm.program.*;
import java.lang.NumberFormatException;
import javax.imageio.*;

/**
 * VEX Autonomous Builder for six bars in Toss Up
 * 
 * @author	Eric Van Lare
 */
public class VexAutonBuilder {
	private boolean doStuff = true;

	public static void main(String[] args) {
		final JFrame frame = new JFrame("VEX Autonomous Builder");

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
							currentShape = new Line2D.Double ( lastPoint, e.getPoint () );
							shapes.add (currentShape);
							if (buttons.get(0).isSelected())
								lineDirs.get(squareNo).add("F");
							else
								lineDirs.get(squareNo).add("B");
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
					public void mouseReleased ( MouseEvent e ) {
						if (buttons.get(0).isSelected() || buttons.get(1).isSelected()) {
							lastPoint = e.getPoint();
							System.out.println(lastPoint);
							currentShape = null;
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
					if (lineDirs.get(squareNo).get(i).equals("B"))
						g2d.draw (shapes.get(i));
				}
				g2d.setStroke(new BasicStroke(2));
				g2d.setPaint(Color.GREEN);
				for (int i = 0; i < shapes.size(); i++) {
					if (lineDirs.get(squareNo).get(i).equals("F"))
						g2d.draw(shapes.get(i));
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
					JOptionPane.showMessageDialog(null,"Input for Far Red Square");
				}
				else if (squareNo == 2) {
					JOptionPane.showMessageDialog(null,"Input for Middle Blue Square");
				}
				else if (squareNo == 3) {
					JOptionPane.showMessageDialog(null,"Input for Far Blue Square");
				}
				else {
					squareNo = 0;
					JOptionPane.showMessageDialog(null,"Input for Middle Red Square");
				}
				shapes = squares.get(squareNo);
				currentShape = null;
				if (squares.get(squareNo).size() == 0)
					lastPoint = starts.get(squareNo);
				else
					lastPoint = squares.get(squareNo).get(squares.get(squareNo).size()-1).getP2();
				repaint();
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

		//Drive speed full, half, Lift time full, half
		final ArrayList<Double> importantVars = new ArrayList<Double>();
		final ArrayList<String> input = new ArrayList<String>();
		for (int i = 0; i < 4; i++){
			importantVars.add(0.0);
			input.add("add");
		}

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
		JPanel task = new JPanel();
		task.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Task"));
		task.setBounds(10,206,370,200);
		frame.add(task);

		JButton addTask = new JButton("Add Task");
		addTask.setBounds(267,170,100,25);
		task.add(addTask);

		ArrayList<String> buttonNames = new ArrayList<String>();
		buttonNames.add(0,"Drive Forward");
		buttonNames.add(1,"Drive Backward");
		buttonNames.add(2,"Lift");
		buttonNames.add(3,"Lower");
		buttonNames.add(4,"Intake");
		buttonNames.add(5,"Outtake");
		buttonNames.add(6,"Wait");
		ButtonGroup buttonGroup = new ButtonGroup();
		for (int i = 0; i < 7; i++) {
			buttons.add(i, new JRadioButton(buttonNames.get(i)));
			buttons.get(i).setBounds(25,(25+20*i),150,20);
			buttonGroup.add(buttons.get(i));
			task.add(buttons.get(i));
		}

		//Task List Panel----------------------------------------------------------------------------------------------------------------------------
		JPanel taskList = new JPanel();
		taskList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Task List"));
		frame.add(taskList);

		JPanel innerTaskList = new JPanel();
		innerTaskList.setBounds(0,0,1000,100);

		//Scroller Testing, add images to test
		try {
			BufferedImage myPicture = ImageIO.read(new File("images/DriveForward.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			picLabel.setBounds(0,0,100,100);
			innerTaskList.add(picLabel);

			BufferedImage myPicture2 = ImageIO.read(new File("images/driveBackward.png"));
			JLabel picLabel2 = new JLabel(new ImageIcon(myPicture2));
			picLabel2.setBounds(110,0,100,100);
			innerTaskList.add(picLabel2);

			BufferedImage myPicture3 = ImageIO.read(new File("images/DriveForward.png"));
			JLabel picLabel3 = new JLabel(new ImageIcon(myPicture3));
			picLabel3.setBounds(0,0,100,100);
			innerTaskList.add(picLabel3);

			BufferedImage myPicture4 = ImageIO.read(new File("images/driveBackward.png"));
			JLabel picLabel4 = new JLabel(new ImageIcon(myPicture4));
			picLabel4.setBounds(110,0,100,100);
			innerTaskList.add(picLabel4);

			BufferedImage myPicture5 = ImageIO.read(new File("images/DriveForward.png"));
			JLabel picLabel5 = new JLabel(new ImageIcon(myPicture5));
			picLabel5.setBounds(0,0,100,100);
			innerTaskList.add(picLabel5);

			BufferedImage myPicture6 = ImageIO.read(new File("images/driveBackward.png"));
			JLabel picLabel6 = new JLabel(new ImageIcon(myPicture6));
			picLabel6.setBounds(110,0,100,100);
			innerTaskList.add(picLabel6);
		} catch (IOException e) {}

		JScrollPane scroller = new JScrollPane(innerTaskList,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setBounds(10,20,347,150);
		scroller.setPreferredSize(new Dimension(100,1000));
		taskList.add(scroller);

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
					bw.write(
						"class FillerCode\n{\n"+
							"\tpublic FillerCode(){}\n\n"+
							"\tpublic String werk(String filename)\n\t{\n"+
								"\t\treturn true;\n"+
							"\t}\n}");
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
				drawings.clear();
			}
		});
		taskList.add(clearTasks);

		JButton nextSquare = new JButton("Next Square");
		nextSquare.setBounds(73,173,100,25);
		nextSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawings.next();
			}
		});
		taskList.add(nextSquare);

		taskList.setBounds(10,409,370,203);
	}
}

class RoboTask {
	private String task = "";

	public RoboTask(int taskIndex, ArrayList<Double> importantVars) {
		if (taskIndex == 0)
			task = "";
	}

	public RoboTask(int taskIndex, ArrayList<Double> importantVars, double length, double turn) {
		task = "";
	}

	public String toString() {
		return task;
	}
}

class Robot {
	private double[] driveSpeed; //at power 50, 70, 100, 127 measured in inches per second
	private double[] liftSpeed; //at power 50, 70, 100, 127 measured in degrees per second

	public Robot() {
		driveSpeed = new double[4];
		liftSpeed = new double[4];
	}

	public void setDriveSpeed(int powerIndex, double speed) {
		driveSpeed[powerIndex] = speed;
	}

	public void setLiftSpeed(int powerIndex, double speed) {
		liftSpeed[powerIndex] = speed;
	}
}

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