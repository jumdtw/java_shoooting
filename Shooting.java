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
        Tama tama;
        Thread gameLoop;
        public MainPanel(){
            jiki = new Jiki();
            tama = new Tama(0,-1000);
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            addKeyListener(this);

            gameLoop = new Thread(this);
            gameLoop.start();
        }

        public void run(){
            for(;;){
                jiki.update(keyleft,keyright);
                if(tama.y>-20&&tama.y<500){tama.update();}
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
            if(keyCode == KeyEvent.VK_SPACE){
                if(tama.y<0||tama.y>500){
                    tama = new Tama(jiki.x,jiki.y);
                }
            }
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
            g.drawImage(tama.img,tama.x,tama.y,this);
        }
    }


    public class Jiki extends Applet{

        int x;
        int y;
        Image img;
        int width;
        int height;

        Jiki(){
            x = 250;
            y = 350;
            img = Toolkit.getDefaultToolkit().getImage("./images/jiki.gif");
            //width = img.getWidth();
            //height = img.getHeight();    
        }

        
        public void update(boolean keyleft,boolean keyright){
            if(keyleft){this.x -= 5;}
            if(keyright){this.x += 5;}
            if(this.x<0){x = 0;}
            if(this.x>500){x = 500;}
        } 
        

        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this);
        }
    }

    public class Tama extends Applet{
        int x;
        int y;
        Image img;

        Tama(int px,int py){
            this.x = px;
            this.y = py;
            img = Toolkit.getDefaultToolkit().getImage("./images/tama.gif");
        }

        public void update(){
            this.y -= 5;
        } 
        
        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this);
        }

    }
    
}