import java.awt.*;
import javax.swing.*;
import java.io.*;

public class Paint extends Driver{
    public void paint(Graphics g){
	super.paint(g);
	Graphics2D g2d = (Graphics2D) g;
	g2d.drawImage(player,getWidth()/2,getHeight()/2,this);
	Toolkit.getDefaultToolkit().sync();
	g.dispose();
	repaint();

    }
}