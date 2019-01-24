import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.*;

public class Shooting{
    
    public static void main(String[] args){
        Shooting shooting = new Shooting();
        GameWindow gw = shooting.new GameWindow("aaa",500,500);
        Jiki jk = shooting.new Jiki();
        MainPanel mainpanel = shooting.new MainPanel(); 
        gw.add(mainpanel);
        gw.setVisible(true);
    }

    
    public class GameWindow extends JFrame{

        public GameWindow(String title,int width,int height){
            super(title);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(width,height);
            setLocationRelativeTo(null);
            setResizable(false);
        }

    }

    public class MainPanel extends JPanel implements Runnable,KeyListener{
        private static final int WIDTH = 240;
        private static final int HEIGHT = 240;
        boolean keyleft = false;
        boolean keyright = false;
        Jiki jiki;
        Thread gameLoop;
        public MainPanel(){
            jiki = new Jiki();
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            addKeyListener(this);

            gameLoop = new Thread(this);
            gameLoop.start();
        }


        public void run(){
            for(;;){
                jiki.update(keyleft,keyright);
                repaint();
                try{
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    System.out.print("Thread_err");
                }
            }
        }

        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_LEFT){keyleft=true;}
            if(keyCode == KeyEvent.VK_RIGHT){keyright=true;}
        } 


        public void keyReleased(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_LEFT){keyleft=false;}
            if(keyCode == KeyEvent.VK_RIGHT){keyright=false;}
        } 

        
        public void keyTyped(KeyEvent e) {
        }

        public void paintComponent(Graphics g) {
            
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(jiki.img,jiki.x,jiki.y,this);
        }
    }


    public class Jiki extends Applet{

        int x;
        int y;
        Image img;

        Jiki(){
            x = 250;
            y = 350;
            img = Toolkit.getDefaultToolkit().getImage("./images/jiki.gif");
        }

        
        public void update(boolean keyleft,boolean keyright){
            if(keyleft){this.x -= 5;}
            if(keyright){this.x += 5;}
            if(x<0){x = 0;}
            if(x>500){x = 500;}
        } 
        

        public void paint(Graphics g){
            g.drawImage(img,x,y,this);
        }
    }

    
}