import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final int SCEEN_WIDTH = 640;
    public static final int SCEEN_HEIGHT = 360;

    private Image screenImage;
    private Graphics screenGraphic;

    private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("/images/menuBar.png")));

    private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("/images/exitBtnEntered.png"));
    private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("/images/exitBtnBasic.png"));
    private JButton exitButton = new JButton(exitButtonBasicImage);

    private ImageIcon startButtonEnteredImage = new ImageIcon(Main.class.getResource("/images/startBtnEntered.png"));
    private ImageIcon startButtonBasicImage = new ImageIcon(Main.class.getResource("/images/startBtnBasic.png"));
    private JButton startButton = new JButton(startButtonBasicImage);

    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;

    private Image background = new ImageIcon(Main.class.getResource("/images/introBackground.png")).getImage();
    private int mouseX, mouseY;
    public HalliGalli hg;

    //private boolean gameStart = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        setUndecorated(true);
        setTitle("Play HalliGalli");
        setResizable(false);
        setBounds(100, 100, Main.SCEEN_WIDTH, Main.SCEEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0)); // paincomponent했을 때 배경 전부 흰색
        getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("User Name");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setForeground(Color.LIGHT_GRAY);
        lblNewLabel.setBounds(72, 287, 82, 33);
        getContentPane().add(lblNewLabel);

        txtUserName = new JTextField();
        txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
        txtUserName.setBounds(166, 287, 116, 33);
        getContentPane().add(txtUserName);
        txtUserName.setColumns(10);

        JLabel lblIpAddress = new JLabel("IP Address");
        lblIpAddress.setHorizontalAlignment(SwingConstants.RIGHT);
        lblIpAddress.setForeground(Color.LIGHT_GRAY);
        lblIpAddress.setBounds(294, 244, 82, 33);
        getContentPane().add(lblIpAddress);

        txtIpAddress = new JTextField();
        txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
        txtIpAddress.setText("127.0.0.1");
        txtIpAddress.setColumns(10);
        txtIpAddress.setBounds(388, 244, 116, 33);
        getContentPane().add(txtIpAddress);

        JLabel lblPortNumber = new JLabel("Port Number");
        lblPortNumber.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPortNumber.setForeground(Color.LIGHT_GRAY);
        lblPortNumber.setBounds(72, 244, 82, 33);
        getContentPane().add(lblPortNumber);

        txtPortNumber = new JTextField();
        txtPortNumber.setText("30000");
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setColumns(10);
        txtPortNumber.setBounds(166, 244, 116, 33);
        getContentPane().add(txtPortNumber);

        Myaction action = new Myaction();

        startButton.setBounds(316, 280, 200, 40); //메뉴바 가장 오른쪽에
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//마우스 올라갔을 때
                startButton.setIcon(startButtonEnteredImage);
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); //커서가 손가락 모양으로
                Music2 startButtonEnteredMusic = new Music2("startButtonEntered.mp3",false);
                startButtonEnteredMusic.start();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setIcon(startButtonBasicImage);
                startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
            @Override
            public void mousePressed(MouseEvent e) {
                Music2 startButtonPressedMusic = new Music2("startButtonPressed.mp3",false);
                startButtonPressedMusic.start();
                try {
                    Thread.sleep(500); //소리 나오고 1초 후에 종료 되도록
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                startButton.addActionListener(action);
            }
        });
        getContentPane().add(startButton);


        //JButton btnConnect = new JButton("게임 시작");
        //btnConnect.setBounds(385, 275, 205, 38);
        //getContentPane().add(btnConnect);

        //btnConnect.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);

        exitButton.setBounds(600, 0, 24, 24); //메뉴바 가장 오른쪽에
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//마우스 올라갔을 때
                exitButton.setIcon(exitButtonEnteredImage);
                exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); //커서가 손가락 모양으로
                Music2 buttonEnteredMusic = new Music2("exitButtonEntered.mp3",false);
                buttonEnteredMusic.start();

            }
            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setIcon(exitButtonBasicImage);
                exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
            @Override
            public void mousePressed(MouseEvent e) {
                Music2 buttonPressedMusic = new Music2("exitButtonPressed.mp3",false);
                buttonPressedMusic.start();
                try {
                    Thread.sleep(1000); //소리 나오고 1초 후에 종료 되도록
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                System.exit(0); //클릭했을 때 게임 종료
            }
        });
        getContentPane().add(exitButton);

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
        getContentPane().add(menuBar);
    }

    public void paint(Graphics g) { // 약속된 메소드
        screenImage = createImage(Main.SCEEN_WIDTH, Main.SCEEN_HEIGHT);
        screenGraphic = screenImage.getGraphics();
        screenDraw(screenGraphic);
        g.drawImage(screenImage, 0, 0, null); // 화면에 screenImage그려짐

    }

    public void screenDraw(Graphics g) {
        g.drawImage(background, 0, 0, null); // 0,0에 screeImage에 그려지도록

		/*if(gameStart) {
			hg.screenDraw(g);
		}*/

        paintComponents(g);
        this.repaint();// 처음으로 화면에 그려주는, 다시 paint함수 불러와, 즉 프로그램 종료 될 때 까지 매순간마다 계속

    }

    class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();

            //gameStart = true;
            hg = new HalliGalli(username, ip_addr, port_no); //view로 넘어감
            setVisible(false);
        }
    }

}
