import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Canvas;

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
            //右上✖ボタンでウィンドウを消す
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            //ウィンドウの大きさ
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
        // 自機
        Jiki jiki;
        //自機が出す弾
        Tama tama;
        //敵の配列
        Teki[] tekis;
        //敵の最大出現数
        int max_enemy_num = 5;
        //現在の敵の数
        int enemy_num = 0;
        Thread gameLoop;
        //描画時間　fps的なもの
        int sleep_time = 10;
        //敵が画面にいる時間
        int enemy_alive_time = 1;
        //打ち出した弾の個数
        int shoted_tama_counter = 0;

        //mainのパネル　これをthread 処理することでキャラクター達を動かしている
        public MainPanel(){
            jiki = new Jiki();
            tama = new Tama(-1000,-1000);
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
                //ランダムに敵を出現させる
                get_enemy();
                //キーイベントを取得し、自機を動かす
                jiki.update(keyleft,keyright);
                //弾がウィンドウ内にあった場合updateを実行する
                if(tama.get_Tama_y()>-90&&tama.get_Tama_y()<500){tama.update();}
                //配列内の敵がnull(空)でなかったらupdateする
                for(int i=0;i<tekis.length;i++){
                    if(tekis[i]!=null){
                        tekis[i].update();
                        hit_judge(tekis[i]);
                        //sleep している時間が0.01sなので*100で1sになる
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
                    // 1/100の確率で敵が出現する
                    int num = rand.nextInt(150);
                    if(num==0){
                        tekis[i] = new Teki(sleep_time);
                    }
                    
                }
            }
        }

        public void hit_judge(Teki teki){
            
        }

        //
        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_LEFT){keyleft=true;}
            if(keyCode == KeyEvent.VK_RIGHT){keyright=true;}
            if(keyCode == KeyEvent.VK_SPACE){
                if(tama.get_Tama_y()<0||tama.get_Tama_y()>500){
                    shoted_tama_counter++;
                    tama = new Tama(jiki.get_jiki_x(),jiki.get_jiki_y());
                }
            }
        } 

        //
        public void keyReleased(KeyEvent e){
            int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_LEFT){keyleft=false;}
            if(keyCode == KeyEvent.VK_RIGHT){keyright=false;}
        } 

        
        public void keyTyped(KeyEvent e) {
        }

        //画像の初期描画
        public void paintComponent(Graphics g) {
            //画面背景を黒にぬりつぶす
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            //自機の初期描画
            Image image = jiki.get_jiki_img();
            int px = jiki.get_jiki_x();
            int py = jiki.get_jiki_y();
            int image_size = jiki.get_jiki_image_size();
            g.drawImage(image,px,py,image_size,image_size,this);
            //自機弾の初期描画　最初は画面外側においてある
            image = tama.get_Tama_img();
            px = tama.get_Tama_x();
            py = tama.get_Tama_y();
            image_size = tama.get_tama_img_size();
            g.drawImage(image,px,py,image_size,image_size,this);
            //敵画像の初期描画
            for(int i=0;i<tekis.length;i++){
                if(tekis[i]!=null){
                    image = tekis[i].get_enemy_img();
                    px = tekis[i].get_enemy_x();
                    py = tekis[i].get_enemy_y();
                    image_size = tekis[i].get_enemy_img_size();
                    g.drawImage(image,px,py,image_size,image_size,this);
                }
            }
        }
    }

    //自機クラス
    public class Jiki extends Canvas{
        //この座標は画像左上なので注意
        private int x;
        private int y;
        //画像本体
        private Image img;
        //自機画像の大きさ
        private int jiki_img_width_height;

        Jiki(){
            this.x = 250;
            this.y = 400;
            this.img = Toolkit.getDefaultToolkit().getImage("./images/test.png");
            this.jiki_img_width_height = 40;
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

        public int get_jiki_image_size(){
            return this.jiki_img_width_height;
        }
        
        public void update(boolean keyleft,boolean keyright){
            if(keyleft){this.x -= 5;}
            if(keyright){this.x += 5;}
            if(this.x<0){x = 0;}
            if(this.x>500-this.jiki_img_width_height){x = 500-this.jiki_img_width_height;}
        } 
        

        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this.jiki_img_width_height,this.jiki_img_width_height,this);
        }
    }

    //敵クラス
    public class Teki extends Canvas{
        //この座標は画像左上なので注意
        private int x;
        private int y;
        //画像中心座標
        private int center_px;
        private int center_py;
        //画像本体
        private Image img;
        //生存時間
        private int time;
        //敵の保持するポイント
        private int enemy_point;
        //敵画像の大きさ
        private int teki_img_width_height;
        //生存時間のカウント用
        private int count_time;

        Random rand = new Random();
        int num;
        Teki(int sleeptime){
            this.teki_img_width_height = 80;
            this.x = rand.nextInt(500-teki_img_width_height);
            this.y = rand.nextInt(300);
            this.time = 0;
            this.count_time = sleeptime;
            num = rand.nextInt(3);
            switch(num){
                case 0: this.img = Toolkit.getDefaultToolkit().getImage("./images/maru.png");this.enemy_point=3;break;
                case 1: this.img = Toolkit.getDefaultToolkit().getImage("./images/sannkaku.png");this.enemy_point=2;break;
                case 2: this.img = Toolkit.getDefaultToolkit().getImage("./images/sikaku.png");this.enemy_point=1;break;
            }
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

        public int get_enemy_img_size(){
            return this.teki_img_width_height;
        }

        public int get_enemy_center_x(){
            return this.center_px;
        }

        public int get_enemy_center_y(){
            return this.center_py;
        }

      

        //画面にいる時間をここで計測している
        public void update(){
            this.time += count_time;
        }

        public void remove(){

        }

        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this.teki_img_width_height,this.teki_img_width_height,this);
        }
    }


    //弾クラス
    public class Tama extends Canvas{
        //この座標は画像左上なので注意
        private int x;
        private int y;
        //画像中心座標
        private int center_px;
        private int center_py;
        //弾の速度
        private int vy;
        //弾画像本体
        private Image img;
        //弾画像の大きさ
        private int tama_img_width_height;

        Tama(int px,int py){
            this.x = px;
            this.y = py;
            this.vy = 8;
            this.tama_img_width_height = 40;
            this.center_px = this.x + this.tama_img_width_height/2;
            this.center_py = this.y + this.tama_img_width_height/2;
            img = Toolkit.getDefaultToolkit().getImage("./images/test.png");
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

        public int get_tama_img_size(){
            return this.tama_img_width_height;
        }

        public int get_tama_center_x(){
            return this.center_px;
        }

        public int get_tama_center_y(){
            return this.center_py;
        }

        //画面上方向への移動はマイナス
        public void update(){
            this.y -= vy;
        } 

        public void remove(){
            this.y = -9999;
        }
        
        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this.tama_img_width_height,this.tama_img_width_height,this);
        }

    }
    
}