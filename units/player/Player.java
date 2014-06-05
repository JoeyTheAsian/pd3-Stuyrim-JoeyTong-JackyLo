import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public abstract class Player extends Unit/* implements Serializable*/{
 
    //player should have three unique special attacks 
    //NOTE: sAttack1() = sAttack() from Unit class
  
    protected int LVL = 1;
    protected int LVLreq = (int)(Math.pow(2,1)) * 50;
    protected Item head = null,
	torso = null,
	legs = null,
	feet = null,
	hands = null,
	weapon = null,
	accessory = null,
	consumables = null;
    protected int CDS2 = 1000, CDS3 = 1000; //cooldown time for sAttack2 and sAttack3
    protected boolean isReady2 = true, isReady3 = true; //whether sAttack2 or sAttack3 is ready
    protected boolean isSet2 = false, isSet3 = false; //whether sAttack2 or sAttack3 is ready

    public Player(int x, int y){
	mapX = x;
	mapY = y;
    }

    public void setLVL(int level){ lvl = level; }

    public int getLVL(){ return LVL; }

    public void setHead(Item hd){ head = hd; }

    public Item getHead(){ return head; }

    public void setTorso(Item tso){ torso = tso; }

    public Item getTorso(){ return torso; }

    public void setLegs(Item lgs){ legs = lgs };

    public Item getLegs(){ return legs; }

    public void setFeet(Item ft){ feet = ft; }

    public Item getFeet(){ return feet; }

    public void setHands(Item hnd){ hands = hnd; }

    public Item getHands(){ return hands; }

    public void setWeapon(Item wpn){ weapon = wpn; }

    public Item getWeapon(){ return weapon; }

    public void setAccessory(Item acs){ accessory = acs; }

    public Item getAccessory(){ return accessory; }

    public void setConsumables(Item csm){ consumables = csm; }

    public Item getConsumables(){ return consumables; }

    public void setCDS2(int cds2){ CDS2 = cds2; }

    public int getCDS2(){ return CDS2; }

    public void setCDS3()(int cds3){ CDS3 = cds3; }

    public int getCDS3(){ return CDS3; }

    public abstract void sAttack2();
  
    public abstract void sAttack3();
    
    public void attack(Unit u){
	if (isSet1){
	    isSet1 = false;
	    sAttack();
	}else if (isSet2){
	    isSet2 = false;
	    sAttack2();
	}else if (isSet3){
	    isSet3 = false;
	    sAttack3();
	}else{
	    if ((int)(Math.random()*100) <= getLuk())
		u.setHP(u.getHP()+u.getDEF()-1.5*getATK());
	    else
		u.setHP(u.getHP()+u.getDEF()-getATK());
	}
    }

    public void LVLup(){
	while (EXP >= LVLreq){
	    EXP = EXP-LVLreq;
	    LVL++;
	    LVLReq = (int)(Math.pow(2,LVL)) * 50;
	}
    }

    public abstract void LVLupStats();

}
