import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
    
public class MenuPanel extends JPanel {
    private int height =Toolkit.getDefaultToolkit().getScreenSize().height-37, width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private JButton optionsButton = new JButton("Options") , startButton = new JButton ("Start");
    private Image img;
    private BufferedImage BG;

    public MenuPanel() {
		setLayout(null);
		setSize(width, height);
		try {BG = ImageIO.read(new File("stuyrim.png"));}
		catch (Exception e) {Utilities.showErrorMessage(this, e);}
		
		optionsButton.addActionListener(e -> {});
		optionsButton.setOpaque(false);
		optionsButton.setBorderPainted(false);
		optionsButton.setContentAreaFilled(false);
		optionsButton.setSize(width/10,height/20);
		optionsButton.setVerticalTextPosition(SwingConstants.CENTER);
		optionsButton.setHorizontalTextPosition(SwingConstants.CENTER);
		optionsButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		optionsButton.setLocation((width/3)-(width/20), (height/7*6)-(height/40));
		img = new ImageIcon("GUI Images/Button.png").getImage().getScaledInstance(optionsButton.getWidth(),optionsButton.getHeight(),java.awt.Image.SCALE_SMOOTH);
		optionsButton.setIcon(new ImageIcon(img));
		optionsButton.setForeground(Color.white);

		startButton.addActionListener(e -> {
				Screen screen = new Screen();
				startButton.getParent().getParent().add(screen);
				screen.requestFocusInWindow();
				startButton.getParent().getParent().add(new GamePanel());
				startButton.getParent().getParent().repaint();
				startButton.getParent().getParent().remove(this);
		});
		startButton.setOpaque(false);
		startButton.setBorderPainted(false);
		startButton.setContentAreaFilled(false);
		startButton.setSize(width/10,height/20);
		startButton.setVerticalTextPosition(SwingConstants.CENTER);
		startButton.setHorizontalTextPosition(SwingConstants.CENTER);
		startButton.setFont(new Font("TimesRoman",Font.PLAIN, 20));
		startButton.setLocation((width/3*2)-(width/20), (height/7*6)-(height/40));
		startButton.setIcon(new ImageIcon(img));
		startButton.setForeground(Color.white);

		add(optionsButton);
		add(startButton);
		setVisible(true);
		revalidate();
		repaint();
	}
 
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(BG,0,0,width,height, null);
    }
}
