
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Shooting{

    public static void main(String[] args){
        Shooting shooting = new Shooting();
        GameWindow gw = shooting.new GameWindow("shooting",500,500);
        MainPanel mainpanel = shooting.new MainPanel();
        mainpanel.setLayout(null);
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
        //startbutton
        StartButton startbutton;
        //rankbutton
        RankButton rankbutton;
        // 自機
        Jiki jiki;
        //自機が出す弾
        Tama tama;
        //敵の配列
        Teki[] tekis;
        //敵の最大出現数
        int max_enemy_num = 5;
        Thread gameLoop;
        //描画時間　fps的なもの
        int sleep_time = 10;
        //敵が画面にいる時間
        int enemy_alive_time = 5;
        //打ち出した弾の個数
        int shoted_tama_counter = 0;
        //弾の制限　打ち出した瞬間にカウントしているため
        int limit_shoot = 2;
        //必要単位数
        int limit_point;
        //スコア
        int score;
        //制限時間
        int limit_time;
        //現在までの経過時間
        int count_time;
        JLabel ten;
        //背景画像
        BackGround background;
        //文字の位置　得点と時間
        int print_char_position=999;
        //行間
        int print_char2char=15;
        //player name
        String nickname;
        //成績の文字列の場所
        int print_score_char_xpositon = 0;
        int print_score_char_ypositon = 999;
        //threadを止めるためのフラッグ
        boolean gameFlag = true;
        //名前入力用のテキストフィールド
        JTextField nametext;
        //テキストフィールドの場所
        int nametext_yposition = 150;
        //画像と名前の場所
        int number1_yposition = 9999;
        int number2_yposition = 9999;
        int number3_yposition = 9999;
        //王冠の画像
        Image number1_image = Toolkit.getDefaultToolkit().getImage("./images/1.png");;
        Image number2_image = Toolkit.getDefaultToolkit().getImage("./images/2.png");;
        Image number3_image = Toolkit.getDefaultToolkit().getImage("./images/3.png");;
        //ランキング上位者のnickname
        String number1_name = "default1";
        String number2_name = "default2";
        String number3_name = "default3";
        //ランキング上位者の得点
        int number1_score = 64;
        int number2_score = 64;
        int number3_score = 64;
        
        //mainのパネル　これをthread 処理することでキャラクター達を動かしている
        public MainPanel(){
            //スタートボタンを生成
            startbutton = new StartButton();
            //ランキングボタンを生成
            rankbutton = new RankButton();
            //名前入力用のテキストフィールド生成 10文字まで
            nametext = new JTextField(10);
            //テキストフィールドの場所と大きさを設定
            nametext.setBounds(250-150/2,nametext_yposition,150,30);
            //テキストフィールドのフォントと文字の太さを設定
            nametext.setFont(new Font("Dialog",Font.BOLD,14));
            //自機の画像を画面買いに置きjikiクラスを初期化
            jiki = new Jiki(-9999,-9999);
            //弾の画像を画面外に置きtamaクラスを初期化
            tama = new Tama(-1000,-1000);
            //敵配列の大きさを設定
            tekis = new Teki[max_enemy_num];
            //ゲームクリアに必要な点数
            limit_point = 1;
            //スコアの初期化
            score = 0;
            //背景画像をコーガ君のやつに設定
            background = new BackGround(1);
            //敵配列の初期化
            for(int i=0;i<tekis.length;i++){
                tekis[i] = null;
            }
            //制限時間
            limit_time = 7;
            //経過した時間
            count_time = 0;
            //
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            //
            setFocusable(true);
            //キーイベントを受け付けるための設定
            addKeyListener(this);
            //画面に描画
            add(startbutton);
            add(rankbutton);
            add(nametext);
        }


        //画面を初期化　何も表示させないようにしている
        public void init(){
            jiki = new Jiki(-9999,-9999);
            tama = new Tama(-1000,-1000);
            tekis = new Teki[max_enemy_num];
            print_char_position = -9999;
            for(int i=0;i<tekis.length;i++){
                tekis[i] = null;
            }
        }

        public void run(){
            while(gameFlag){
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
                        if(tama.get_Tama_y()>0&&tama.get_Tama_y()<500){hit_judge(tekis[i]);}
                        //sleep している時間が0.01sなので*100で1sになる
                        if(tekis[i].get_enemy_time() >= sleep_time * 100 * enemy_alive_time){
                            tekis[i] = null;
                        }
                    }
                }
                repaint();
                
                count_time += sleep_time;
                if(shoted_tama_counter == limit_shoot ){count_time += sleep_time * 100 * limit_time;}
                if(count_time >= sleep_time * 100 * limit_time){
                    init();
                    end_game();
                    gameFlag = false;
                }
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
            
            int enemy_px = teki.get_enemy_center_x();
            int enemy_py = teki.get_enemy_center_y();
            int enemy_size = teki.get_enemy_img_size();
            int tama_px = tama.get_tama_center_x();
            int tama_py = tama.get_tama_center_y();
            int tama_size = tama.get_tama_img_size();
            //当たりえるxの距離 
            int hit_xdistance = enemy_size/2 + tama_size/2;
            //実際のxの距離
            int xdistance = Math.abs(enemy_px-tama_px);
            //当たりえるyの距離 
            int hit_ydistance = enemy_size/2 + tama_size/2;
            //実際のyの距離
            int ydistance = Math.abs(enemy_py-tama_py);
            if(hit_ydistance > ydistance){
                if(hit_xdistance > xdistance){
                    tama.remove();
                    teki.seter_enemy_time(enemy_alive_time);
                    score += teki.enemy_point;
                }
            }
        }

        public void end_game(){
            removeAll();
            repaint();
            //名前が未入力の場合はNonameをnicknameに入れる
            if(nametext.getText().toString().equals("")==true){
                nickname = "Noname";
            }else{
                nickname = nametext.getText();
            }
            nametext_yposition = 9999;
            //名前が未入力の場合は残八になる
            if(score >= limit_point && nickname!="Noname"){
                //成績発表用の文字列を表示
                print_score_char_xpositon = 80;
                print_score_char_ypositon = 160;
                background = new BackGround(2);
            }else{
                print_score_char_xpositon = 90;
                print_score_char_ypositon = 140;
                background = new BackGround(3);
            }
            write_ranking();
            ReturnButton b = new ReturnButton();
            add(b);
        }

        //----------------------------------------------------------------------------------        
        //----------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------
        //-------------------------------button---------------------------------------------
        //----------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------


        //startbuttonクラス
        public class StartButton extends JButton implements ActionListener{

            public StartButton(){
                super("START");//STARTとかかれたボタンが作れる
                setBounds(250-50,250-50,100,50);//x座標：250，ｙ座標：250の場所に幅：50，高さ：25のサイズのボタンができる
                addActionListener(this);//クリックされると呼びだす
            }
            public void actionPerformed(ActionEvent e){
                //アクションイベント(ボタンが押される)が発生すると、このactionPerformed メソッドが呼び出される
                start();
            }
        }

        //ランキングbuttonクラス
        public class RankButton extends JButton implements ActionListener{
            public RankButton(){
                super("ランキング");//ランキングとかかれたボタンが作れる
                setBounds(250-50,250+50,100,50);
                addActionListener(this);//クリックされると呼びだす
            }
            public void actionPerformed(ActionEvent e){
                //アクションイベント(ボタンが押される)が発生すると、このactionPerformed メソッドが呼び出される
                read_ranking();
                view_ranking();
            }
        }

        //returnbuttonクラス
        public class ReturnButton extends JButton implements ActionListener{
            public ReturnButton(){
                super("スタート画面に戻る");//ランキングとかかれたボタンが作れる
                setBounds(250-100,400,200,50);
                addActionListener(this);//クリックされると呼びだす
            }
            public void actionPerformed(ActionEvent e){
                //アクションイベント(ボタンが押される)が発生すると、このactionPerformed メソッドが呼び出される
                return_menu();
            }
        }

        public void start(){
            removeAll();
            repaint();
            jiki = new Jiki(250,400);
            tama = new Tama(-1000,-1000);
            tekis = new Teki[max_enemy_num];
            background = new BackGround(1);
            print_char_position = 15;
            for(int i=0;i<tekis.length;i++){
                tekis[i] = null;
            }
            count_time = 0;
            score = 0;
            shoted_tama_counter = 0;
            nickname = nametext.getText();
            nametext_yposition = 9999;
            gameFlag = true;
            gameLoop = new Thread(this);
            gameLoop.start();
        }

        public void view_ranking(){
            removeAll();
            repaint();
            background = new BackGround(1);
            number1_yposition = 200;
            number2_yposition = 270;
            number3_yposition = 340;
            nametext_yposition = 150;
            ReturnButton b = new ReturnButton();
            add(b);
        }



        /*
        csvfile format

        rank,name,score
        1,A,64
        2,B,63
        3,C,62
           .
           .
           .
           .


        rank
        name
        score
        1
        default1
        64
        2
        default2
        63
        3
        default3
        62
        */
        public void read_ranking(){
            try{
                //出力先を作成する
                File f = new File("rank.csv"); 
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                int c = 0;
                // 1行ずつCSVファイルを読み込む
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",", 0); // 行をカンマ区切りで配列に変換
                    for (String elem: data) {
                        if(c==4){number1_name = elem;}
                        if(c==5){number1_score = Integer.valueOf(elem).intValue();}
                        if(c==7){number2_name = elem;}
                        if(c==8){number2_score = Integer.valueOf(elem).intValue();}
                        if(c==10){number3_name = elem;}
                        if(c==11){number3_score = Integer.valueOf(elem).intValue();}
                        if(c>=12){break;}
                        c++;
                    }
                }
                //ファイルに書き出す
                br.close();
            }catch(IOException ex){
                //例外時処理
                System.out.print("read");
                ex.printStackTrace();
            }
        }

        public void write_ranking(){
            try{
                //読み込みを行いランキング何位か探す
                //出力先を作成する
                File f = new File("rank.csv"); 
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                String[] bufstr = new String[1000];
                int new_ranker=0;
                int new_score=0;
                int diff = 0;
                int c = 0;
                // 1行ずつCSVファイルを読み込む
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",", 0); // 行をカンマ区切りで配列に変換
                    for (String elem: data) {
                        if((c+1)%3==0&&(c+1)!=3){
                            if(Integer.valueOf(elem).intValue() <= score&&new_score==0){
                                new_ranker = c/3;
                                new_score = score;
                            }
                        }
                        //書き込み用にすべての文字列を格納する
                        bufstr[c] = elem;
                        c++;
                    }
                }
                //ファイルに書き出す
                br.close();
                
                if(new_ranker==0){
                    new_ranker = c/3;
                    new_score = score;
                }

                //出力先を作成する
                FileWriter fw = new FileWriter("rank.csv", false); 
                PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
                for(int i=0;i<1000;i+=3){
                    if(bufstr[i]==null){break;}
                    if(i==0){
                        pw.print("rank");pw.print(",");
                        pw.print("name");pw.print(",");
                        pw.print("score");pw.println();
                    }else if(i>=3){
                        if((i/3)==new_ranker){
                            pw.print(i/3);pw.print(",");
                            pw.print(nickname);pw.print(",");
                            pw.print(new_score);pw.println();
                            diff = 1;
                        }
                        
                        pw.print(Integer.valueOf(bufstr[i]).intValue() + diff);pw.print(",");
                        pw.print(bufstr[i+1]);pw.print(",");
                        pw.print(bufstr[i+2]);pw.println();
                    }
                }
                if(diff==0){
                    pw.print(new_ranker);pw.print(",");
                    pw.print(nickname);pw.print(",");
                    pw.print(new_score);pw.println();
                }
                //ファイルに書き出す
                pw.close();
            }catch(IOException ex){
                //例外時処理
                
                ex.printStackTrace();
            }
        }

        public void return_menu(){
            removeAll();
            repaint();
            //成績発表用の文字列の場所を画面外にする
            print_score_char_ypositon = 999;
            nametext_yposition = 150;
            //名前入力用のテキストフィールド生成 10文字まで
            nametext = new JTextField(10);
            //テキストフィールドの場所と大きさを設定
            nametext.setBounds(250-150/2,nametext_yposition,150,30);
            //テキストフィールドのフォントと文字の太さを設定
            nametext.setFont(new Font("Dialog",Font.BOLD,14));
            background = new BackGround(1);
            number1_yposition = 9999;
            number2_yposition = 9999;
            number3_yposition = 9999;
            StartButton sb = new StartButton();
            RankButton rb = new RankButton();
            add(sb);
            add(rb);
            add(nametext);
        }

        //----------------------------------------------------------------------------------        
        //----------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------
        //-------------------------------button---------------------------------------------
        //----------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------


        //キーボード押し込み時処理
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

        //キーボードを話した時の処理
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
            //背景画像
            g.drawImage(background.img,0,0,500,500,this);
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
            //得点の表示
            Font fo1 = new Font("Dialog",Font.BOLD,14);
            g.setFont(fo1);
            g.drawString("単位数: " + String.valueOf(score),10,print_char_position);
            g.drawString("残り時間: " + String.valueOf(limit_time-count_time/1000),10,print_char_position+print_char2char);
            Font fo2 = new Font("Dialog",Font.BOLD,20);
            g.setFont(fo2);
            g.setColor(Color.yellow);
            g.drawString(nickname + " は " +  String.valueOf(score) + "の単位を取りました",print_score_char_xpositon,print_score_char_ypositon);
            //ranking画面用
            g.drawImage(number1_image,30,number1_yposition-40,45,45,this);
            g.drawImage(number2_image,30,number2_yposition-40,45,45,this);
            g.drawImage(number3_image,30,number3_yposition-40,45,45,this);
            g.setFont(new Font("Dialog",Font.BOLD,30));
            g.setColor(new Color(218,179,0));
            g.drawString(number1_name,140,number1_yposition-3);
            g.drawString(String.valueOf(number1_score),300,number1_yposition-3);
            g.setColor(new Color(150,154,152));
            g.drawString(number2_name,140,number2_yposition-3);
            g.drawString(String.valueOf(number2_score),300,number2_yposition-3);
            g.setColor(new Color(196,112,34));
            g.drawString(number3_name,140,number3_yposition-3);
            g.drawString(String.valueOf(number3_score),300,number3_yposition-3);
        }
    }
    ///////ここまでがmainpanelクラス


    public class BackGround extends Canvas{
        Image img;
        BackGround(int flag){
            switch(flag){
                case 1:this.img = Toolkit.getDefaultToolkit().getImage("./images/gamegamen.png");break;
                case 2:this.img = Toolkit.getDefaultToolkit().getImage("./images/gameclear.png");break;
                case 3:this.img = Toolkit.getDefaultToolkit().getImage("./images/gameover.png");break;
            }
        }

        public void paint(Graphics g){
            g.drawImage(this.img,0,0,500,500,this);
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

        Jiki(int px,int py){
            this.x = px;
            this.y = py;
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
            this.center_px = this.x + this.teki_img_width_height/2;
            this.center_py = this.y + this.teki_img_width_height/2;
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

        public void seter_enemy_time(int t){
            this.time += t * 1000;
        }
      

        //画面にいる時間をここで計測している
        public void update(){
            this.time += count_time;
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
            this.center_py = this.y + this.tama_img_width_height/2;
        } 

        public void remove(){
            this.y = -9999;
        }
        
        public void paint(Graphics g){
            g.drawImage(this.img,this.x,this.y,this.tama_img_width_height,this.tama_img_width_height,this);
        }

    }
    
}