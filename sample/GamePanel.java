import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class GamePanel extends JPanel {
    private boolean[] keysPressed = new boolean[256];
    private boolean[] keysReleased = new boolean[256];
    private BufferedImage bg; //background
    private HashMap<String, Item> items = new HashMap<>(); //If the maximum number of items is known, use the optimization described in Character.
    private HashMap<String, ArrayList<Point>> droppedItems = new HashMap<>();
    //Maps dropped items (after death of monsters) to its location in game.
    private int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height-37;
    private int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    //dimensions of the bottom portion of the screen with all the buttons
    private int height = windowHeight/5;
    private int width = windowWidth;
    //player inventory will probably be removed in the future after testing
    private Inventory inventory = new Inventory();
    private InventoryPanel invent = new InventoryPanel(inventory);
    private PartyPanel party = new PartyPanel();
    private JTextArea environmentInfo = new JTextArea();
    Screen screen = new Screen();
    private JTextArea playerData;
    private JButton inventoryButton, menuButton, partyButton;
    
    private ArrayList<Drawable> screenEntities = new ArrayList <Drawable>();

    public GamePanel() {
	setLayout(null);
	setBounds(0, 0 , windowWidth, windowHeight);
	try {bg = ImageIO.read(new File("GUI Images/trimmed paper background.png"));}
	catch (Exception e) {Utilities.showErrorMessage(this, e);}
	setVisible(true);

	//Initalize HashMap of items
	try {
		items.put("Cake", new Item(ImageIO.read(new File("items/Cake.png")), "Cake", "It's a lie.", 9001, 9001, 9001));
	}
	catch (Exception e) {Utilities.showErrorMessage(this, e);}

    
	//add buttons
	inventoryButton = new JButton("Inventory");
	inventoryButton.setOpaque(false);
	inventoryButton.setBorderPainted(false);
	inventoryButton.setContentAreaFilled(false);
	inventoryButton.setVerticalTextPosition(SwingConstants.CENTER);
	inventoryButton.setHorizontalTextPosition(SwingConstants.CENTER);
	inventoryButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
	inventoryButton.setBounds(windowWidth/6*5-(width/70),windowHeight-height+(height/30),width/6,(height-(height/30))/3);
	//get button textures
	Image i1 = new ImageIcon("GUI Images/Button.png").getImage().getScaledInstance(inventoryButton.getWidth(),inventoryButton.getHeight(),java.awt.Image.SCALE_SMOOTH);
	Image i2 = new ImageIcon("GUI Images/Button1.png").getImage().getScaledInstance(inventoryButton.getWidth(),inventoryButton.getHeight(),java.awt.Image.SCALE_SMOOTH);
	inventoryButton.setIcon(new ImageIcon(i1));
	inventoryButton.setForeground(Color.white);
	inventoryButton.addActionListener(e -> {
		for(int i = 0; i < 256; i++){
		    if(keysPressed[i]){
			keysReleased[i] = true;
			keysPressed[i] = false;
		    }
		}
		//if it's there, take it out and give focus to screen, if it isn't put it in ,update items and take focus
		if (invent.isVisible()) {
		    invent.setVisible(false);
		    screen.requestFocusInWindow();
		    inventoryButton.setIcon(new ImageIcon(i1));
		}else if(!invent.isVisible()){
		    //nullifies all input before opening 
		    invent.setVisible(true);
		    invent.requestFocusInWindow();
		    //invent.updateInventory(inventory);
		    inventoryButton.setIcon(new ImageIcon(i2));
		}
	    });
	partyButton = new JButton("Party");
	partyButton.setOpaque(false);
	partyButton.setBorderPainted(false);
	partyButton.setContentAreaFilled(false);
	partyButton.setVerticalTextPosition(SwingConstants.CENTER);
	partyButton.setHorizontalTextPosition(SwingConstants.CENTER);
	partyButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
	partyButton.setBounds(windowWidth/6*5-(width/70),(windowHeight-height)+(height/4)+(height/30),width/6,(height-(height/30))/3);
	partyButton.setIcon(new ImageIcon(i1));
	partyButton.setForeground(Color.white);
	partyButton.addActionListener(e -> {
		if(party.isVisible()){
		    party.setVisible(false);
		    screen.requestFocusInWindow();
		    partyButton.setIcon(new ImageIcon(i1));
		}else if(!party.isVisible()){
		    for(int i = 0; i < 256; i++){
			if(keysPressed[i]){
			    keysReleased[i] = true;
			    keysPressed[i] = false;
			}
		    }
		    party.setVisible(true);
		    party.requestFocusInWindow();
		    partyButton.setIcon(new ImageIcon(i2));
		    party.updatePartyData(screen.characters);		       	
		}
	    });
	menuButton = new JButton("Menu");
	menuButton.setOpaque(false);
	menuButton.setBorderPainted(false);
	menuButton.setContentAreaFilled(false);
	menuButton.setVerticalTextPosition(SwingConstants.CENTER);
	menuButton.setHorizontalTextPosition(SwingConstants.CENTER);
	menuButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
	menuButton.setBounds(windowWidth/6*5-(width/70),(windowHeight-height)+(height/4*2)+(height/30),width/6,(height-(height/30))/3);
	menuButton.setIcon(new ImageIcon(i1));
	menuButton.setForeground(Color.white);
	menuButton.addActionListener(e -> {
		screen.requestFocusInWindow();
	    });

	//GRABS ALL PARTY DATA INCLUDING HP, MANA, ETC
	playerData = new JTextArea();
	playerData.setSize(width/6,height);
	playerData.setLocation(0+(width/50),(windowHeight/5*4)+(height/20));
	playerData.setOpaque(false);
	playerData.setVisible(true);
	playerData.setEditable(false);
	playerData.setHighlighter(null);
	playerData.setDragEnabled(false);
	playerData.setForeground(Color.BLACK);
	playerData.setFont(new Font("TimesRoman", Font.PLAIN, height/15));
	
	environmentInfo = new JTextArea();
	environmentInfo.setSelectedTextColor(Color.WHITE);
	
	
	//creates inventory panel
	invent.setSize(windowWidth/2, windowHeight/2);
	invent.setLocation(windowWidth/4, windowHeight/4);
	screen = new Screen();
    	add(party);
	add(invent);
	add(playerData);
	add(menuButton);
	add(inventoryButton);
	add(partyButton);
	add(screen);
	revalidate();
    }
	
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.drawImage(bg,0,windowHeight/5*4,width,height,null);
	String text = "";
	for(int i = 0; i < screen.characters.size(); i++){
	    if (i!= 0){text += "\n";}	
	    text += "Player " + (i +1) + " " + "(" + "LVL: " + screen.characters.get(i).getLVL() + "): " + "\nHP: " +  screen.characters.get(i).getHP()+"/"+screen.characters.get(i).getMaxHP()
		+ "\nMana: " + screen.characters.get(i).getMana()+"/"+screen.characters.get(i).getMaxMana();
	}
	playerData.setText(text);
    }

    public class Screen extends Canvas implements Runnable{
	private BufferedImage flickerStop;
	//FPS counter variables
	private static final int MAX_FPS = 60;
	private static final int MAX_FPS_1 = 60;
	private static final int FPS_SAMPLE_SIZE = 6;
	// The width and height of each tile in pixels
	private static final int TILE_SCALE = 60;
	//arraylists containing all entities on screen, painted by while loop in screen
	private ArrayDeque<AttackEvent> attacks = new ArrayDeque<>(); //Used as a stack
	private ArrayList<Player> characters = new ArrayList<>();
	private ArrayList<Character> ai = new ArrayList<>();
	private boolean running;
	private int averageFPS;
	private int averageFPS1;
	private int mapX = 0;
	private int mapY = 0;
	//SCREEN dimensions
	private int screenHeight = ((Toolkit.getDefaultToolkit().getScreenSize().height-37)/5*4);
	private int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	private LinkedList<Long> frames = new LinkedList<>();
	private LinkedList<Long> frames1 = new LinkedList<>();
	private long time; //global time
	private long prevTick = -1;
	private long prevTick1 = -1;
	private Map currentMap;

	private boolean shielded;
	//for testing putposes
	private Swordsman player = new Swordsman("sprites/swordsman down.png", screenWidth/2, screenHeight/2);
	private Swordsman player2 = new Swordsman("sprites/swordsman down.png", screenWidth/2, screenHeight/2);
	private Swordsman player3 = new Swordsman("sprites/swordsman down.png", screenWidth/2, screenHeight/2);
	private Thread thread;

	public Screen() {
	    setSize(screenWidth, screenHeight);
	    try{flickerStop =ImageIO.read(new File("GUI Images/flickerStop.png"));
	    }catch(Exception e){Utilities.showErrorMessage(this,e);}
	    characters.add(player);
	    characters.add(player2);
	    characters.add(player3);
	    

            currentMap = new Map();
	    addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) {keysPressed[e.getKeyCode()] = true;}
		    public void keyReleased(KeyEvent e) {
			keysPressed[e.getKeyCode()] = false;
			keysReleased[e.getKeyCode()] = true;
		    }
		    public void keyTyped(KeyEvent e) {}
		});
	    addMouseListener(new MouseListener() {
		    public void mouseClicked(MouseEvent e) {}
		    public void mouseEntered(MouseEvent e) {}
		    public void mouseExited(MouseEvent e) {}
		    public void mousePressed(MouseEvent e) {
			Character player = characters.get(0);
			e.translatePoint(-mapX, -mapY);
			attacks.push(new AttackEvent(player.getX() + player.getWidth()/2 - mapX, player.getY() + player.getWidth() / 2 - mapY, e.getX(), e.getY(), characters.get(0).getRange()));
		    }
		    public void mouseReleased(MouseEvent e) {}
		});
	    addMouseMotionListener(new MouseMotionListener() {
		    public void mouseDragged(MouseEvent e) {}
		    public void mouseMoved(MouseEvent e) {}
		});
	    addMouseWheelListener(new MouseWheelListener() {
		    public void mouseWheelMoved(MouseWheelEvent e) {}
		});
	    setVisible(true);
	}
	
	private class AttackEvent {
	    private double startX, startY, endX, endY, range;
		
	    public AttackEvent(double startX, double startY, double endX, double endY, double range) {
		this.startX = startX;
		this.startY = startY;
		double distance = Math.hypot(endX - startX, endY - startY), scaleFactor = range/distance;
		this.endX = endX;
		this.endY = endY;
		if (distance > range) {
		    this.endX = startX + scaleFactor*(endX - startX);
		    this.endY = startY + scaleFactor*(endY - startY);
		}
	    }
		
	    public double getEndX() {return endX;}
	    public double getEndY() {return endY;}
	    public double getrange() {return range;}
	    public double getStartX() {return startX;}
	    public double getStartY() {return startY;}
	}
	
	protected boolean intersectEllipseLineSegment(double x1, double y1, double x2, double y2, double h, double k, double a, double b) {//For the purposes of this game, a line segment that is in an ellipse but does not intersect it (completely contained in ellipse) counts as an intersection. x1, y1, x2, and y2 are points that define the **directed** ((x1, y1) to (x2, y2)) line segment to test. h and k are the x- and y-coordinates of the center of the ellipse, respectively. a and b are the same variables as they are in the equation of an ellipse
	    if ((((Math.pow(x1 - h, 2)/(a*a)) + (Math.pow(y1 - k, 2)/(b*b))) <= 1) && (((Math.pow(x2 - h, 2)/(a*a)) + (Math.pow(y2 - k, 2)/(b*b))) <= 1)) {return true;}
	    double m = (y2 - y1)/(x2 - x1), c = y1 - m*x1, d = c + m*h, e = c - k; //m is the slope of the **directed** line segment defined from (x1, y1) to (x2, y2). c is the y-intercept of that line segment, if it were extended to intersect the y-axis. d and e are additional variables to make the calculation shorter. Note that all this will not work if x1 = x2 (vertical line because of divison by zero) (will include a separate case for that).
	    double discriminant = a*a*m*m + b*b - d*d - k*k + 2*d*k, iX1 = 0, iY1 = 0, iX2 = 0, iY2 = 0; //Discriminant, like in the quadratic formula, is used to find the number of intersection points. iX1, iY1, iX2, iY2 represent the intersection points.
	    if (discriminant < 0) {return false;}
	    if (discriminant >= 0) {
		iX1 = (h*b*b - m*a*a*e + a*b*Math.sqrt(discriminant))/(a*a*m*m + b*b);
		iY1 = (b*b*d + k*a*a*m*m + a*b*m*Math.sqrt(discriminant))/(a*a*m*m + b*b);
	    }
	    if (discriminant > 0) { //Note that this case and the one above are not mutually exclusive
		iX2 = (h*b*b - m*a*a*e - a*b*Math.sqrt(discriminant))/(a*a*m*m + b*b);
		iY2 = (b*b*d + k*a*a*m*m - a*b*m*Math.sqrt(discriminant))/(a*a*m*m + b*b);
	    }
	    double lineSegmentDirection = Math.atan2(y2 - y1, x2 - x1);
	    double lineSegmentLength = Math.hypot(x2 - x1, y2 - y1);
	    return (((Math.hypot(iX1 - x1, iY1 - y1) <= lineSegmentLength) && (Math.atan2(iY1 - y1, iX1 - x1) == lineSegmentDirection)) || ((Math.hypot(iX2 - x1, iY2 - y1) <= lineSegmentLength) && (Math.atan2(iY2 - y1, iX2 - x1) == lineSegmentDirection)));
	}

	//renders the screen
	public void render(){
	    BufferStrategy bs = getBufferStrategy();
	    if(bs  == null){
		createBufferStrategy(3);
		return;
	    }
	    Graphics g= bs.getDrawGraphics();
	    //draws black screen to prevent layered images and flicker
	    g.drawImage(flickerStop,0,0,screenWidth,screenHeight, null);
	    // Draw the current map
	    drawMap(g);
	    //draw fps
	    g.setColor(Color.GREEN);

	    
	    //draws HP bars and then draws character
	    
	    for (Character character : ai) {
		if(character.getY()<=screenHeight/2){
		    g.setColor(Color.RED);
		    g.fillRect(character.getX(),character.getY()-10,80,7);
		    g.setColor(Color.GREEN);
		    if(character.getHP() > 0){
			g.fillRect(character.getX(),character.getY()-10,(int)(80.0*((double)character.getHP()/(double)character.getMaxHP())),7);
		    }
		    g.drawImage(character.getImage(), character.getX(), character.getY(), null);
		}
	    }
	    for (int i = characters.size()-1; i >= 0;  i--) {
		Character character = characters.get(i);
		g.setColor(Color.RED);
		g.fillRect(character.getX(),character.getY()-10,80,7);
		g.setColor(Color.GREEN);
		if(character.getHP()>0){
		    g.fillRect(character.getX(),character.getY()-10,(int)(80.0*((double)character.getHP()/(double)character.getMaxHP())),7);
		}
		g.drawImage(character.getImage(), character.getX(), character.getY(), null);
	    }
	    for (Character character : ai) {
		if(character.getY()>screenHeight/2){
		    g.setColor(Color.RED);
		    g.fillRect(character.getX(),character.getY()-10,80,7);
		    g.setColor(Color.GREEN);
		    if(character.getHP()>0){
			g.fillRect(character.getX(),character.getY()-10,(int)(80.0*((double)character.getHP()/(double)character.getMaxHP())),7);
		    }
		    g.drawImage(character.getImage(), character.getX(), character.getY(), null);
		}
	    }
	    droppedItems.forEach((itemName, locations) -> {
			Item item = items.get(itemName);
			locations.forEach(location -> {g.drawImage(item.getImage(), (int) location.getX(), (int) location.getY(), null);});
		});

	    g.drawString("TPS: " + averageFPS, 0, 20);
	    g.drawString("FPS: " + averageFPS1,0, 31);
	    g.drawString("Global Time: " + time, 0, 50);

	    g.dispose();
	    bs.show();

	    //actual FPS limiter, greatly improves performance by not letting system max out processor with useless still frames
	    long pastTime = System.currentTimeMillis() - prevTick;

	    if (frames1.size() == FPS_SAMPLE_SIZE) {
		frames1.remove();
	    }
	    frames1.add(pastTime);
	    // Calculate average FPS
	    long sum = 0;
	    for (long frame : frames1){
		sum += frame;
	    }
	    long averageFrame = sum / FPS_SAMPLE_SIZE;
	    if (averageFrame == 0) {averageFrame = 1;}
	    if (averageFrame == 0) averageFrame = 1;  //IF STATEMENT
	    averageFPS1 = (int)(1000 / averageFrame); //NOTE: THERE'S AN ARITHMETIC ERROR. AND I THINK THIS LINE IS CRASHING 
	    prevTick1 = System.currentTimeMillis();
	    // Only if the time passed since the previous tick is less than one
	    // second divided by the number of maximum FPS allowed do we delay
	    // ourselves to give Time time to catch up to our rendering.
	    if (pastTime < 1000.0 / MAX_FPS_1) {
		try {
		    Thread.sleep((long)(1000.0 / MAX_FPS_1)-pastTime);
		} catch (InterruptedException e) {
		    Utilities.showErrorMessage(this, e);
		}
	    }
	}

        // Renders the tilemap of the current map to the screen
        private void drawMap(Graphics g) {
            Tile[][] tilemap = currentMap.getTileMap();
            for (int i = 0; i < tilemap.length; i++) {
                for (int j = 0; j < tilemap[i].length; j++) {
                    drawTile(i, j, tilemap[i][j], g);
                }
            }
        }

        private void drawTile(int x, int y, Tile tile, Graphics g) {
	    if((x * TILE_SCALE + mapX + TILE_SCALE >0)&&(y*TILE_SCALE + mapY +TILE_SCALE>0)
	       &&(x * TILE_SCALE + mapX<windowWidth+1) && ( y * TILE_SCALE + mapY < windowHeight+1)){
		Image texture = currentMap.getTexture(tile);
		g.drawImage(texture, x * TILE_SCALE + mapX, y * TILE_SCALE + mapY,
			    TILE_SCALE, TILE_SCALE, null);
	    }
        }
    
	public void run() {
	    while (running) {
		time++;
		tick();
		render();
	    }
	}
	
	public synchronized void start() {
	    time = 0;
	    running = true;
	    thread = new Thread(this);
	    thread.start();
	}
	
	public synchronized void stop() {
	    try {thread.join();}
	    catch (InterruptedException e) {Utilities.showErrorMessage(this, e);}
	}

	//chance of spawning each monster is 0.1% and it will spawn in one of the four sides of the screen
	public void chanceOfSpawn(){
	    int[][]side = {{0,(int)(Math.random()*screenHeight)},
			   {screenWidth,(int)(Math.random()*screenHeight)},
			   {(int)(Math.random()*screenWidth),0},
			   {(int)(Math.random()*screenWidth),screenHeight}};
	    double chance = Math.random();
	    int temp = (int)(Math.random()*4);
	    if (chance > 0.003)
		return;
	    else{
		Player plyr;
		if (chance > 0.002){
		    plyr = new Swordsman("sprites/swordsman down.png",side[temp][0],side[temp][1]);
		    ai.add(plyr);
		    plyr.setTimeStarted(time);
		}else if (chance > 0.001){
		    plyr = new Bird("sprites/bird down.png",side[temp][0],side[temp][1]);
		    ai.add(plyr);
		    plyr.setTimeStarted(time);
		}else{
		    plyr = new Slime("sprites/slime down.png",side[temp][0],side[temp][1]);
		    ai.add(plyr);
		    plyr.setTimeStarted(time);
		}
	    }
	}

	//updates game screen data
	public void tick() {
	    if(keysPressed[VK_SHIFT] ){
		shielded = true;
	    }
	    if (keysPressed[VK_W] && ableToMove("up")) {
		if(!shielded){
		    mapY+=characters.get(0).getSpeed();
		    characters.get(0).setUpAnimated();
		}else{
		    mapY+= characters.get(0).getSpeed()/2;
		    characters.get(0).setUpShieldAnimated();
		}
		for (Character monster : ai)
		    monster.setY(monster.getY()+characters.get(0).getSpeed());
		for (int i = 1; i< characters.size(); i++)
		    characters.get(i).setY(characters.get(i).getY()+characters.get(0).getSpeed());
	    }
	    if (keysPressed[VK_S] && ableToMove("down")) {
		if(!shielded){
		    mapY-=characters.get(0).getSpeed();
		    characters.get(0).setDownAnimated();
		}else{
		    mapY-= characters.get(0).getSpeed()/2;
		    characters.get(0).setDownShieldAnimated();
		}
		for (Character monster : ai)
		    monster.setY(monster.getY()-characters.get(0).getSpeed());
		for (int i = 1; i< characters.size(); i++)
		    characters.get(i).setY(characters.get(i).getY()-characters.get(0).getSpeed());
	    }
	    if (keysPressed[VK_A] && ableToMove("left")) {
		if(!shielded){
		    mapX+=characters.get(0).getSpeed();
		    characters.get(0).setLeftAnimated();
		}else{
		    mapX+= characters.get(0).getSpeed()/2;
		    characters.get(0).setLeftShieldAnimated();
		}
		for (Character monster : ai)
		    monster.setX(monster.getX()+characters.get(0).getSpeed());
		for (int i = 1; i< characters.size(); i++)
		    characters.get(i).setX(characters.get(i).getX()+characters.get(0).getSpeed());
	    }
	    if (keysPressed[VK_D] && ableToMove("right")) {
		if(!shielded){
		    mapX-=characters.get(0).getSpeed();
		    characters.get(0).setRightAnimated();
		}else{
		    mapX-= characters.get(0).getSpeed()/2;
		    characters.get(0).setRightShieldAnimated();
		}
		for (Character monster : ai)
		    monster.setX(monster.getX()-characters.get(0).getSpeed());
		for (int i = 1; i< characters.size(); i++)
		    characters.get(i).setX(characters.get(i).getX()-characters.get(0).getSpeed());
	    }
	
	    //reset player to idle mode after done moving
	    if(keysReleased[VK_SHIFT]){
		shielded = false;
	    }
	    if(keysReleased[VK_W]){
		if(!shielded){
		    characters.get(0).setUp();
		}else{
		    characters.get(0).setUpShield();
		}
		keysReleased[VK_W] = false;
	    }
	    if(keysReleased[VK_S]){
		if(!shielded){
		    characters.get(0).setDown();
		}else{
		    characters.get(0).setDownShield();
		}
		keysReleased[VK_S] = false;
	    }
	    if(keysReleased[VK_A]){
		if(!shielded){
		    characters.get(0).setLeft();
		}else{
		    characters.get(0).setLeftShield();
		}
		keysReleased[VK_A] = false;
	    }
	    if(keysReleased[VK_D]){
		if(!shielded){	
		    characters.get(0).setRight();
		}else{
		    characters.get(0).setRightShield();
		}
		keysReleased[VK_D] = false;
	    }
	    while (!(attacks.isEmpty())) {
		AttackEvent attack = attacks.pop();
		//System.out.println("\nStart: (" + attack.getStartX() + ", " + attack.getStartY() + "); End: (" + attack.getEndX() + ", " + attack.getEndY() + ")");
		for (Character character : ai) {
		    //System.out.println(character + " @ (" + (character.getX() + character.getWidth()/2 - mapX) + ", " + (character.getY() + character.getHeight()/2 - mapY) + "); Width: " + character.getWidth() + "; Height: " + character.getHeight() + " " + intersectEllipseLineSegment(attack.getStartX(), attack.getStartY(), attack.getEndX(), attack.getEndY(), (character.getX() + character.getWidth()/2 - mapX), (character.getY() + character.getHeight()/2 - mapY), character.getWidth(), character.getHeight()));
		    if (intersectEllipseLineSegment(attack.getStartX(), attack.getStartY(), attack.getEndX(), attack.getEndY(), (character.getX() + character.getWidth()/2 - mapX), (character.getY() + character.getHeight()/2 - mapY), character.getWidth(), character.getHeight())) {characters.get(0).attack(character);}
		}
	    }

	    chanceOfSpawn(); //chance of spawn

	    //AI code
	    for (Character character : ai){
		if (character.getDist(characters.get(2)) <= character.getDist(characters.get(1)) && character.getDist(characters.get(2)) <= character.getDist(characters.get(0)))
		    character.setTarget(characters.get(2));
		else if (character.getDist(characters.get(1)) <= character.getDist(characters.get(2)) && character.getDist(characters.get(1)) <= character.getDist(characters.get(0)))
		    character.setTarget(characters.get(1));
		else
		    character.setTarget(characters.get(0));
		if (character.getDist(character.getTarget()) < character.getRange()){
		    if (Math.abs(character.getChangeX()) > Math.abs(character.getChangeY())){
			if (character.getChangeX() > character.getChangeY())
			    character.setRight();
			else if (character.getChangeX() < character.getChangeY())
			    character.setLeft();
		    }else{
			if (character.getChangeY() > character.getChangeX())
			    character.setDown();
			else if (character.getChangeY() < character.getChangeX())
			    character.setUp();
		    }
		    if ((character.getTimeStarted()-time)%character.getATKspeed() != 0){}
		    else character.attack(character.getTarget());
		}else{
		    character.setX(character.getX() + (int)(character.getSpeed()*character.getChangeX()/character.getDist(character.getTarget())));
		    character.setY(character.getY() + (int)(character.getSpeed()*character.getChangeY()/character.getDist(character.getTarget())));
		    if (Math.abs(character.getChangeX()) > Math.abs(character.getChangeY())){
			if (character.getChangeX() > character.getChangeY())
			    character.setRightAnimated();
			else if (character.getChangeX() < character.getChangeY())
			    character.setLeftAnimated();
		    }else{
			if (character.getChangeY() > character.getChangeX())
			    character.setDownAnimated();
			else if (character.getChangeY() < character.getChangeX())
			    character.setUpAnimated();
		    }
		}
	    }
	    for (int j = 1; j < characters.size(); j++){
		if (ai.size() <= 0){
		    if(characters.get(j).getDist(characters.get(0)) > (50*j)){
			characters.get(j).setX(characters.get(j).getX() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeX()/characters.get(j).getDist(characters.get(0))));
			characters.get(j).setY(characters.get(j).getY() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeY()/characters.get(j).getDist(characters.get(0))));
			//when the characters are moving
			if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
			    if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				characters.get(j).setRightAnimated();
			    else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				characters.get(j).setLeftAnimated();
			}else{
			    if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				characters.get(j).setDownAnimated();
			    else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				characters.get(j).setUpAnimated();
			}
			//when the characters are idle
		    }else{
			if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
			    if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				characters.get(j).setRight();
			    else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				characters.get(j).setLeft();
			}else{
			    if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				characters.get(j).setDown();
			    else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				characters.get(j).setUp();
			}
		    }
		}else{
		    if (characters.get(j).getTarget() == null)
			characters.get(j).setTarget(ai.get((int)(Math.random()*ai.size()))); //random monster on screen
		    else if (characters.get(j).getTarget().getHP() <= 0)
			characters.get(j).setTarget(null);
		    try{
			if (characters.get(j).getDist(characters.get(j).getTarget()) < characters.get(j).getRange()){
			    if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
				if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				    characters.get(j).setRight();
				else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				    characters.get(j).setLeft();
			    }else{
				if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				    characters.get(j).setDown();
				else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				    characters.get(j).setUp();
			    }
			    if ((characters.get(j).getTimeStarted()-time)%characters.get(j).getATKspeed() != 0){}
			    else characters.get(j).attack(characters.get(j).getTarget());
			    if (characters.get(j).getTarget().getHP() <= 0)
				characters.get(j).setEXP(characters.get(j).getEXP()+characters.get(j).getTarget().getEXP());
			    if (characters.get(j).getEXP() >= characters.get(j).getLVLreq())
				characters.get(j).LVLup();
			}else{
			    characters.get(j).setX(characters.get(j).getX() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeX()/characters.get(j).getDist(characters.get(j).getTarget())));
			    characters.get(j).setY(characters.get(j).getY() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeY()/characters.get(j).getDist(characters.get(j).getTarget())));
			    if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
				if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				    characters.get(j).setRightAnimated();
				else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				    characters.get(j).setLeftAnimated();
			    }else{
				if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				    characters.get(j).setDownAnimated();
				else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				    characters.get(j).setUpAnimated();
			    }
			}
		    }catch(/*IndexOutOfBounds*/Exception e){
			if (characters.get(j).getDist(characters.get(0)) > 50){
			    characters.get(j).setX(characters.get(j).getX() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeX()/characters.get(j).getDist(characters.get(0))));
			    characters.get(j).setY(characters.get(j).getY() + (int)(characters.get(j).getSpeed()*characters.get(j).getChangeY()/characters.get(j).getDist(characters.get(0))));
			    if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
				if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				    characters.get(j).setRightAnimated();
				else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				    characters.get(j).setLeftAnimated();
			    }else{
				if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				    characters.get(j).setDownAnimated();
				else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				    characters.get(j).setUpAnimated();
			    }
			}else{
			    if (Math.abs(characters.get(j).getChangeX()) > Math.abs(characters.get(j).getChangeY())){
				if (characters.get(j).getChangeX() > characters.get(j).getChangeY())
				    characters.get(j).setRight();
				else if (characters.get(j).getChangeX() < characters.get(j).getChangeY())
				    characters.get(j).setLeft();
			    }else{
				if (characters.get(j).getChangeY() > characters.get(j).getChangeX())
				    characters.get(j).setDown();
				else if (characters.get(j).getChangeY() < characters.get(j).getChangeX())
				    characters.get(j).setUp();
			    }
			}
		    }
		}
	    }

	    //kill characters and players with <= 0 hp
	    for (int i = 0; i < ai.size(); i++){
		if (ai.get(i).getHP() <= 0){
		    characters.get(0).setEXP(characters.get(0).getEXP()+ai.get(i).getEXP());
		    if (characters.get(0).getEXP() >= characters.get(0).getLVLreq())
			characters.get(0).LVLup();
			Character deadCharacter = ai.get(i);
		    HashMap<String, Double> drops = ai.get(i).getDrops();
			double dropChance = Math.random();
		    drops.forEach((itemName, chance) -> {
				if (dropChance <= chance) {
					if (droppedItems.get(itemName) == null) {droppedItems.put(itemName, new ArrayList<>());}
					droppedItems.get(itemName).add(new Point(deadCharacter.getX(), deadCharacter.getY()));
				}
			});
		    ai.remove(i);
		    i--;
		}
	    }
	    for (int i = 0; i < characters.size(); i++){
		if (characters.get(i).getHP() <= 0){}
	    }

	    long pastTime = System.currentTimeMillis() - prevTick;

	    if (frames.size() == FPS_SAMPLE_SIZE) {
		frames.remove();
	    }
	    frames.add(pastTime);

	    // Calculate average FPS
	    long sum = 0;
	    for (long frame : frames) {
		sum += frame;
	    }
	    long averageFrame = sum / FPS_SAMPLE_SIZE;
	    averageFPS = (int)(1000 / averageFrame);
	    prevTick = System.currentTimeMillis();
	    // Only if the time passed since the previous tick is less than one
	    // second divided by the number of maximum FPS allowed do we delay
	    // ourselves to give Time time to catch up to our rendering.
	    if (pastTime < 1000.0 / MAX_FPS) {
		try {
		    Thread.sleep((long)(1000.0 / MAX_FPS)-pastTime);
		} catch (InterruptedException e) {
		    Utilities.showErrorMessage(this, e);
		}
	    }
	}
    }

    private boolean ableToMove(String direction) {
        return true;
    }
}

