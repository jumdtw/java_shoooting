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
        //
        Jiki jiki;
        //
        Tama tama;
        //
        Teki[] tekis;
        //
        int max_enemy_num;
        //
        int enemy_num;
        Thread gameLoop;
        int sleep_time = 10;
        int enemy_alive_time = 1;
        public MainPanel(){
            jiki = new Jiki();
            tama = new Tama(0,-1000);
            max_enemy_num = 5;
            tekis = new Teki[max_enemy_num];
            for(int i=0;i<tekis.length;i++){
                tekis[i] = null;
            }
            enemy_num = 0;
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            addKeyListener(this);

            gameLoop = new Thread(this);
            gameLoop.start();
        }

        public void run(){
            for(;;){

                get_enemy();
                //
                jiki.update(keyleft,keyright);
                //
                if(tama.get_Tama_y()>-20&&tama.get_Tama_y()<500){tama.update();}
                //
                for(int i=0;i<tekis.length;i++){
                    if(tekis[i]!=null){
                        tekis[i].update();
                        if(tekis[i].get_enemy_time() >= sleep_time * 100 * enemy_alive_time){
                            tekis[i] = null;
                        }
                    }
                }
                repaint();
                try{
                    Thread.sleep(sleep_time);
                }catch(InterruptedException e){
                    System.out.print("Thread_err");
                }
            }
        }

        public void get_enemy(){
            for(int i=0;i<tekis.length;i++){
                if(tekis[i]==null){
                    Random rand = new Random();
                    int num = rand.nextInt(10);
                    if(num%6==0){
                        tekis[i] = new Teki(sleep_time);
                    }
                    
                }
            }
        }

        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_LEFT){keyleft=true;}
            if(keyCode == KeyEvent.VK_RIGHT){keyright=true;}
            if(keyCode == KeyEvent.VK_SPACE){
                if(tama.get_Tama_y()<0||tama.get_Tama_y()>500){
                    tama = new Tama(jiki.get_jiki_x(),jiki.get_jiki_y());
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
            g.drawImage(jiki.get_jiki_img(),jiki.get_jiki_x(),jiki.get_jiki_y(),this);
            g.drawImage(tama.get_Tama_img(),tama.get_Tama_x(),tama.get_Tama_y(),this);
            for(int i=0;i<tekis.length;i++){
                if(tekis[i]!=null){g.drawImage(tekis[i].get_enemy_img(),tekis[i].get_enemy_x(),tekis[i].get_enemy_y(),this);}
            }
        }
    }


    public class Jiki extends Applet{

        private int x;
        private int y;
        private Image img;
        int width;
        int height;

        Jiki(){
            x = 250;
            y = 350;
            img = Toolkit.getDefaultToolkit().getImage("./images/jiki.gif");
            //width = img.getWidth();
            //height = img.getHeight();    
        }

        public int get_jiki_x(){
            return this.x;
        }

        public int get_jiki_y(){
            return this.y;
        }

        public Image get_jiki_img(){
            return this.img;
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

    public class Teki extends Applet{
        private int x;
        private int y;
        private Image img;
        private int time;
        Random rand = new Random();
        int num = rand.nextInt(10);
        int count_time;

        Teki(int sleeptime){
            x = rand.nextInt(500);
            y = rand.nextInt(300);
            //
            time = 0;
            count_time = sleeptime;
            img = Toolkit.getDefaultToolkit().getImage("./images/teki.gif");
        }

        public int get_enemy_x(){
            return this.x;
        }

        public int get_enemy_y(){
            return this.y;
        }

        public Image get_enemy_img(){
            return this.img;
        }

        public int get_enemy_time(){
            return this.time;
        }

        public void update(){
            this.time += count_time;
        }

        public void remove(){

        }

        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this);
        }
    }

    public class Tama extends Applet{
        private int x;
        private int y;
        private Image img;

        Tama(int px,int py){
            this.x = px;
            this.y = py;
            img = Toolkit.getDefaultToolkit().getImage("./images/tama.gif");
        }
        
        public int get_Tama_x(){
            return this.x;
        }
        public int get_Tama_y(){
            return this.y;
        }

        public Image get_Tama_img(){
            return this.img;
        }

        public void update(){
            this.y -= 5;
        } 
        
        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this);
        }

    }
    
}