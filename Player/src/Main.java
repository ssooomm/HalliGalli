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
        setBackground(new Color(0, 0, 0, 0)); // paincomponent?????? ??? ?????? ?????? ??????
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

        startButton.setBounds(316, 280, 200, 40); //????????? ?????? ????????????
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//????????? ???????????? ???
                startButton.setIcon(startButtonEnteredImage);
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); //????????? ????????? ????????????
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
                    Thread.sleep(500); //?????? ????????? 1??? ?????? ?????? ?????????
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                startButton.addActionListener(action);
            }
        });
        getContentPane().add(startButton);


        //JButton btnConnect = new JButton("?????? ??????");
        //btnConnect.setBounds(385, 275, 205, 38);
        //getContentPane().add(btnConnect);

        //btnConnect.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);

        exitButton.setBounds(600, 0, 24, 24); //????????? ?????? ????????????
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//????????? ???????????? ???
                exitButton.setIcon(exitButtonEnteredImage);
                exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); //????????? ????????? ????????????
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
                    Thread.sleep(1000); //?????? ????????? 1??? ?????? ?????? ?????????
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                System.exit(0); //???????????? ??? ?????? ??????
            }
        });
        getContentPane().add(exitButton);

        menuBar.setBounds(0, 0, 640, 25); // jframe??? menubar??????
        menuBar.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) { //????????? ???????????? ?????? ??????
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        menuBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {//drag???????????? ?????? ?????????, ?????? jframe?????? ?????????, ????????? ?????? ?????? ????????????
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x-mouseX, y-mouseY);
            }
        });
        getContentPane().add(menuBar);
    }

    public void paint(Graphics g) { // ????????? ?????????
        screenImage = createImage(Main.SCEEN_WIDTH, Main.SCEEN_HEIGHT);
        screenGraphic = screenImage.getGraphics();
        screenDraw(screenGraphic);
        g.drawImage(screenImage, 0, 0, null); // ????????? screenImage?????????

    }

    public void screenDraw(Graphics g) {
        g.drawImage(background, 0, 0, null); // 0,0??? screeImage??? ???????????????

		/*if(gameStart) {
			hg.screenDraw(g);
		}*/

        paintComponents(g);
        this.repaint();// ???????????? ????????? ????????????, ?????? paint?????? ?????????, ??? ???????????? ?????? ??? ??? ?????? ??????????????? ??????

    }

    class Myaction implements ActionListener // ?????????????????? ?????? ????????? ?????? ?????????
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();

            //gameStart = true;
            hg = new HalliGalli(username, ip_addr, port_no); //view??? ?????????
            setVisible(false);
        }
    }

}
