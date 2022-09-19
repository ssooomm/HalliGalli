import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class HalliGalli extends JFrame{
    private static final long serialVersionUID = 1L;

    public static final int CARD_WIDTH = 80;
    public static final int CARD_HEIGHT = 110;

    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Image screenImage;
    private Graphics screenGraphic;

    private Image background = new ImageIcon(Main.class.getResource("/images/gameBackground.png")).getImage();

    private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("/images/exitBtnEntered.png"));
    private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("/images/exitBtnBasic.png"));

    private ImageIcon heartImage = new ImageIcon(Main.class.getResource("/images/heart.png"));
    private ImageIcon heartEmptyImage = new ImageIcon(Main.class.getResource("/images/heartEmpty.png"));

    private ImageIcon count3 = new ImageIcon(Main.class.getResource("/images/count3.png"));
    private ImageIcon count2 = new ImageIcon(Main.class.getResource("/images/count2.png"));
    private ImageIcon count1 = new ImageIcon(Main.class.getResource("/images/count1.png"));

    private ImageIcon cardOpenImage = null;
    private ImageIcon tapImage = new ImageIcon(Main.class.getResource("/images/tap.png"));

    private JButton exitButton = new JButton(exitButtonBasicImage);
    private JButton bell = new JButton(new ImageIcon(Main.class.getResource("/images/bell.png")));

    private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("/images/menuBar.png")));
    private JLabel tap = new JLabel();
    private JLabel informArea = new JLabel();
    private JLabel playerNameLa[] = new JLabel[4];
    private JLabel cardBackArr[] = new JLabel[4];
    private JLabel cardOpenArr[] = new JLabel[4];
    private JLabel hearts[][] = new JLabel[4][5];
    private JLabel countNumber = new JLabel();

    private ArrayList<Player> playerList = new ArrayList<Player>();
    private ArrayList<Card> cardList = new ArrayList<Card>();
    private Player player = null;

    private String[] playerImageArr = new String[] {"chrctCat.png","chrctBoogi.png","chrctFrog.png","chrctChicken.png"};
    private String UserName;
    private String NewUserName;
    private String pressUser;
    private String outUser;

    private boolean bellRing = false;
    private boolean running = true;
    private boolean bellFlag = false;

    private static int cnt = 0;

    private boolean playMusic = false;
    private int mouseX, mouseY; //현재 prg안에서 마우스 좌표

    private int[] playerX = new int[] {15,520,15,520};
    private int[] playerY = new int[] {20,20,240,240};
    private int[] nameY = new int[] {100,100,320,320};
    private int[] heartX = new int[] {15,520,15,520};
    private int[] heartY = new int[] {110,110,330,330};
    private int[] cardX = new int[] {175,386,386,175};
    private int[] cardY = new int[] {50,50, 207, 207};
    private int[] overX = new int[] {40,545,40,545};
    private int[] overY = new int[] {45,45,270,270};

    private Music gamePlaying;

    long pretime;
    int delay;

    public HalliGalli(String username, String ip_addr, String port_no) {
        setUndecorated(true);
        setTitle("Play HalliGalli");
        setResizable(false);
        setBounds(0, 0, Main.SCEEN_WIDTH, Main.SCEEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0)); // paincomponent했을 때 배경 전부 흰색
        setLayout(null);
        setVisible(true); // 우리 눈에 보이도록
        setBackground(new Color(0, 0, 0, 0)); // paincomponent했을 때 배경 전부 흰색

        for(int i=0;i<4;i++) {
            playerNameLa[i] = new JLabel();
            playerNameLa[i].setFont(new Font("Serif",Font.PLAIN,15));
            playerNameLa[i].setOpaque(true);
            playerNameLa[i].setBackground(Color.GRAY);
            playerNameLa[i].setForeground(Color.WHITE);
            playerNameLa[i].setHorizontalAlignment(JLabel.CENTER);

            cardOpenArr[i] = new JLabel();
            cardBackArr[i] = new JLabel(new ImageIcon(Main.class.getResource("/images/cardBack.png")));
            cardBackArr[i].setBounds(cardX[i], cardY[i], 80, 110);

            for(int j=0;j<5;j++) {
                hearts[i][j] = new JLabel();
            }
        }

        informArea.setBounds(160, 50, 320, 50);
        informArea.setOpaque(true);
        informArea.setBackground(Color.white);
        informArea.setHorizontalAlignment(JLabel.CENTER);

        countNumber.setBounds(257, 117, 128, 128);
        add(countNumber);

        tap.setBounds(303, 148, 64, 64);
        add(tap, 1);

        bell.setBounds(278, 141, 86, 83);
        bell.setBorderPainted(false);
        bell.setContentAreaFilled(false);
        bell.setFocusPainted(false);
        bell.setEnabled(false);
        bell.addMouseListener(new MouseAdapter() { 		//종눌렸을 때
            @Override
            public void mousePressed(MouseEvent e) {
                if(bellFlag) {
                    Music ringingMusic = new Music("ringing.mp3",false);
                    ringingMusic.start();
                    tap.setIcon(tapImage);
                    pressBell();
                }

            }
        });
        bell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(bellFlag) {
                        Music ringingMusic = new Music("ringing.mp3",false);
                        ringingMusic.start();
                        tap.setIcon(tapImage);
                        pressBell();
                    }
                }
            }
        });

        exitButton.setBounds(600, 0, 24, 24); //메뉴바 가장 오른쪽에
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//마우스 올라갔을 때
                exitButton.setIcon(exitButtonEnteredImage);
                exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); //커서가 손가락 모양으로
            }
            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setIcon(exitButtonBasicImage);
                exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
            @Override
            public void mousePressed(MouseEvent e) {
                System.exit(0); //클릭했을 때 게임 종료
            }
        });
        add(exitButton);

        menuBar.setBounds(0, 0, 640, 25); // jframe에 menubar추가
        menuBar.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) { //이벤트 발생했을 때의 좌표
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        menuBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {//drag할때마다 좌표 얻어옴, 현재 jframe위치 바꿔줌, 메뉴바 잡고 이동 가능해짐
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x-mouseX, y-mouseY);
            }
        });
        add(menuBar);

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            UserName = username;
            if(UserName.matches("user1")) {
                playMusic = true;
            }
            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
            SendObject(obcm);

            ListenNetwork net = new ListenNetwork();
            net.start();

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            System.out.println("connect error");
        }

    }

    class ListenNetwork extends Thread {
        int cnt=1;
        public void run() {
            while (running) {
                try {
                    Object obcm = null;
                    ChatMsg cm;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {

                        e.printStackTrace();
                        break;
                    }
                    if (obcm == null)
                        break;
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                    } else
                        continue;

                    switch (cm.code) {
                        case "105": //로그인 된거면
                            newPlayer(cm.data);
                            break;
                        case "110": //기존에 있던 player들 보여줌
                            oldPlayer(cm.data);
                            break;
                        case "115": //추가
                            addPlayer(cm.data);
                            break;
                        case "200":
                            break;
                        case "310": //종울렸다는 소식 받음
                            if(playMusic) {gamePlaying.stopMusic();}
                            informArea.setForeground(Color.BLACK);
                            ringedBell(cm.data);
                            break;
                        case "330":
                            try {
                                Thread.sleep(2000); //2초후에 결과 알려주기
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            ringResult(cm.data);
                            break;
                        case "400":
                            if(playMusic) {gamePlaying.stopMusic();}
                            try {
                                Thread.sleep(100); //동기화
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            informArea.setForeground(Color.BLACK);
                            gameOverOther(cm.data);
                            break;
                        case "440":
                            if(playMusic) {gamePlaying.stopMusic();}
                            remove(informArea);
                            gameOver(cm.data);
                            break;
                        case "500": //서버에서 메세지 옴
                            remove(informArea);
                            showGame();
                            delay = 17;
                            pretime=System.currentTimeMillis();
                            initCard();
                            if(System.currentTimeMillis()-pretime<delay) {
                                try {
                                    Thread.sleep(delay-System.currentTimeMillis()+pretime);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            gameReady();
                            break;
                        case "550": //카드 날라옴
                            try {
                                Thread.sleep(500); //동기화
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            openCard(cm.data);
                            break;
                        case "555": //다시 시작
                            try {
                                Thread.sleep(500); //동기화
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            remove(informArea);
                            reStart();
                            break;
                        case "700":
                            remove(informArea);
                            gameEnd();
                            break;
                        case "900":
                            informArea.setForeground(Color.BLACK);
                            outPlayer(cm.data);
                            break;
                    }
                } catch (IOException e) {
                    try {
                        ois.close();
                        oos.close();
                        socket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    } // catch
                } // catch

            }
        }
    }

    public void newPlayer(String msg) {
        informArea.setText("Welcome "+UserName);
        add(informArea,1);
        player = new Player(msg);
        playerList.add(player); //리스트에 추가
        showOldPlayer();
    }

    public void oldPlayer(String msg) {
        playerList.add(new Player(msg)); //새로 입장한 player추가
    }

    public void addPlayer(String msg) {
        int member = playerList.size();
        playerList.add(new Player(msg));
        NewUserName = msg;

        informArea.setText("Enter "+NewUserName);
        showNewPlayer(member);
    }

    public void gameReady() {

        countDown();

        try {
            ChatMsg obcm = new ChatMsg(UserName, "510", "ready");
            oos.writeObject(obcm);
        } catch (IOException e) {

            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {

                e1.printStackTrace();
                System.exit(0);
            }
        }

    }

    public void countDown() {
        Music beep1 = new Music("beep.mp3",false);
        Music beep2 = new Music("beep.mp3",false);
        Music beep3 = new Music("beep.mp3",false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if(playMusic) {beep3.start();}
        countNumber.setIcon(count3);
        try {
            Thread.sleep(1500); //동기화
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if(playMusic) {beep2.start();}
        countNumber.setIcon(count2);
        try {
            Thread.sleep(1500); //동기화
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if(playMusic) {beep1.start();}
        countNumber.setIcon(count1);
        try {
            Thread.sleep(1500); //동기화
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        countNumber.setIcon(null);
        if(playMusic) {
            gamePlaying = new Music("gamePlaying.mp3",true);
            gamePlaying.start();
        }
        bell.setEnabled(true);
        bellFlag=true;
    }

    public void openCard(String data) {
        if(playMusic) {
            Music flip = new Music("cardFlip.mp3",false);
            flip.start();
        }
        String[] arr = data.split("-");
        int startCdn=Integer.valueOf(arr[0]); //오픈될 좌표
        int nowKind = Integer.valueOf(arr[1]);
        if(nowKind == 10) {
            cardOpenImage = new ImageIcon(Main.class.getResource("/images/cardBack.png"));
        }
        else {
            cardOpenImage = new ImageIcon(Main.class.getResource("/images/card"+arr[1]+arr[2]+".png"));
        }
        cardOpenArr[startCdn].setIcon(cardOpenImage);
        cardOpenArr[startCdn].setBounds(cardX[startCdn], cardY[startCdn], CARD_WIDTH, CARD_HEIGHT);
        try {
            Thread.sleep(2000); //동기화
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if(!bellRing) {
            recvCard();
        }

    }

    public void recvCard() {
        try {
            ChatMsg obcm = new ChatMsg(UserName, "550", "receive");
            oos.writeObject(obcm);
        } catch (IOException e) {

            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }


    public void pressBell() { //종울린거 알려주기
        try {
            ChatMsg obcm = new ChatMsg(UserName, "300", "ring");
            oos.writeObject(obcm);
        } catch (IOException e) {
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void ringedBell(String name) {
        pressUser = name;
        tap.setIcon(null);
        bell.setEnabled(false);
        bellFlag=false;
        String str = "["+pressUser+"]님이 종을 울렸습니다.";
        informArea.setText(str);
        add(informArea,1);
    }

    public void ringResult(String result) {
        if(result.matches("성공")) {
            informArea.setForeground(Color.blue);
            informArea.setText(result);
        }
        else if(result.matches("실패")){
            informArea.setForeground(Color.red);
            informArea.setText(result);
        }
        updateHeart(result);

        try {
            Thread.sleep(2000); //2초후에 재시작을 하든 탈락 알림을 받든
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void updateHeart(String result) {
        if(result.matches("성공")) {
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getUserName().matches(pressUser)) {
                    continue;
                }
                playerList.get(i).decrHeart();
            }
        }
        else if(result.matches("실패")){
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getUserName().matches(pressUser)) {
                    playerList.get(i).decrHeart();
                    break;
                }
            }
        }
        changeHeart();
    }

    public void changeHeart() {
        for(int i=0;i<playerList.size();i++) {
            int heartNum = playerList.get(i).getHearts();
            if(heartNum<0) {
                continue;
            }
            else if(heartNum<5) {
                if(playMusic) {
                    Music decrHeart = new Music("decrHeart.mp3",false);
                    decrHeart.start();
                }
                hearts[i][heartNum].setIcon(heartEmptyImage);
            }
            try {
                Thread.sleep(100);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    public synchronized void gameOverOther(String name) {
        if(UserName.matches("user1")) {
            Music out = new Music("over.mp3",false);
            out.start();
        }
        outUser = name;
        informArea.setText("["+outUser+"]님이 탈락했습니다.");

        for(int i=0;i<playerList.size();i++) {
            String getName = playerList.get(i).getUserName();
            if(getName.matches(outUser)) {
                JLabel outIcon = new JLabel(new ImageIcon(Main.class.getResource("/images/gameOverIcon.png")));
                outIcon.setBounds(overX[i], overY[i], 64, 64);
                add(outIcon,2);
                break;
            }
        }

        try {
            Thread.sleep(2000); //2초 후에 다음 작업 실행
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void gameOver(String name) {
        JLabel out = new JLabel(new ImageIcon(Main.class.getResource("/images/gameOver.png")));
        out.setBounds(192, 84, 256, 256);
        add(out,2);
    }

    public void reStart() { //재시작
        countDown();
        try {
            ChatMsg obcm = new ChatMsg(UserName, "555", "restart");
            oos.writeObject(obcm);
        } catch (IOException e) {

            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {

                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void showNewPlayer(int cnt) {
        int heartNum = playerList.get(cnt).getHearts();
        String playerName = playerList.get(cnt).getUserName();

        for(int j=0;j<heartNum;j++) {
            hearts[cnt][j].setIcon(heartImage);
            hearts[cnt][j].setBounds(heartX[cnt]+(j*20), heartY[cnt], 24, 24);
            add(hearts[cnt][j]);
        }

        playerNameLa[cnt].setText(playerName);
        playerNameLa[cnt].setBounds(playerX[cnt],nameY[cnt],100,15);
        add(playerNameLa[cnt]);

        JLabel player = new JLabel(new ImageIcon(Main.class.getResource("/images/"+playerImageArr[cnt])));
        player.setBounds(playerX[cnt], playerY[cnt], 100, 100);
        add(player);
    }


    public void showOldPlayer() {
        for(int i=0;i<playerList.size();i++) {
            int heartNum = playerList.get(i).getHearts();
            for(int j=0;j<heartNum;j++) {
                hearts[i][j].setIcon(heartImage);
                hearts[i][j].setBounds(heartX[i]+(j*20), heartY[i], 24, 24);
                add(hearts[i][j]);
            }

            //이름도 출력
            String playerName = playerList.get(i).getUserName();
            playerNameLa[i].setText(playerName);
            playerNameLa[i].setBounds(playerX[i],nameY[i],100,15);
            add(playerNameLa[i]);

            JLabel player = new JLabel(new ImageIcon(Main.class.getResource("/images/"+playerImageArr[i])));
            player.setBounds(playerX[i], playerY[i], 100, 100);
            add(player);

        }
    }

    public void showGame() {
        add(bell);
        for(int i=0;i<4;i++) {
            add(cardOpenArr[i]);
            add(cardBackArr[i]);
        }
    }

    public void gameEnd() {
        Music end = new Music("end.mp3",false);
        end.start();
        JLabel out = new JLabel(new ImageIcon(Main.class.getResource("/images/gameWinner.png")));
        out.setBounds(192, 59, 256, 256);
        add(out,2);
        running = false;
    }

    public void outPlayer(String data) {
        informArea.setText(data);
        add(informArea,1);
    }

    public void initCard() {
        int fruitKind;
        int fruitCnt;

        for(fruitKind=0;fruitKind<4;fruitKind++) { //0-바나나, 1-딸기...
            for(fruitCnt = 0;fruitCnt<5;fruitCnt++) { //과일 1개 5장
                cardList.add(new Card(fruitKind,1));
            }
            for(fruitCnt = 0;fruitCnt<3;fruitCnt++) {//2개 3장
                cardList.add(new Card(fruitKind,2));
            }
            for(fruitCnt = 0;fruitCnt<3;fruitCnt++) { //과일 3개 3장
                cardList.add(new Card(fruitKind,3));
            }
            for(fruitCnt = 0;fruitCnt<2;fruitCnt++) { //과일 4개 2장
                cardList.add(new Card(fruitKind,4));
            }
            cardList.add(new Card(fruitKind,5));//과일5개 1장
        }
    }

    public void paint(Graphics g) { // 약속된 메소드
        screenImage = createImage(Main.SCEEN_WIDTH, Main.SCEEN_HEIGHT);
        screenGraphic = screenImage.getGraphics();
        screenDraw((Graphics2D)screenGraphic);
        g.drawImage(screenImage, 0, 0, null); // 화면에 screenImage그려짐

    }

    public void screenDraw(Graphics g) {
        g.drawImage(background, 0, 0, null); // 0,0에 screeImage에 그려지도록

        paintComponents(g);
        try {
            Thread.sleep(5);
        }catch(Exception e) {
            e.printStackTrace();
        }
        this.repaint();
    }

    public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {

            System.out.println("SendObject Error");
        }
    }

}
