import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class HalliGalliServer extends JFrame{
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;

    private ServerSocket socket; // 서버소켓
    private Socket client_socket; // accept() 에서 생성된 client 소켓
    private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

    private ArrayList<Card> cardList = new ArrayList<Card>();
    private ArrayList<Card> openCardList = new ArrayList<Card>();

    private static String pressUser = "";

    private static int startCdn=0; //오픈될 좌표
    private static int openCardCnt=0; //몇번째 카드가 오픈될 순서인지
    private int openCardOrder[]; //총 56개의 카드가 오픈될 순서

    private static boolean emptyPlayer  = true;
    private static boolean result = false;
    private static boolean ringing = false;
    private static boolean outPlayer = false;
    private static boolean firstRing = true;
    private static int cnt = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    HalliGalliServer frame = new HalliGalliServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public HalliGalliServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(112, 318, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);


        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
                    e1.printStackTrace();
                }
                AppendText("Game Server Running..");
                btnServerStart.setText("Game Server Running..");
                btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
                txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
                AcceptServer accept_server = new AcceptServer();
                accept_server.start();
            }
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        contentPane.add(btnServerStart);
    }

    class AcceptServer extends Thread{
        @SuppressWarnings("unchecked")
        public void run() {
            while (emptyPlayer) { // 사용자 접속을 계속해서 받기 위해 while문
                try {
                    AppendText("Waiting new clients ...");
                    client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
                    AppendText("새로운 참가자 from " + client_socket);
                    // User 당 하나씩 Thread 생성
                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user); // 새로운 참가자 배열에 추가
                    new_user.start(); // 만든 객체의 스레드 실행
                    AppendText("현재 참가자 수 " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                    // System.exit(0);
                }
            }
        }
    }

    public void AppendText(String str) {
        // textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public synchronized void AppendObject(ChatMsg msg) {
        // textArea.append("사용자로부터 들어온 object : " + str+"\n");
        textArea.append("code = " + msg.code + "\n");
        textArea.append("id = " + msg.UserName + "\n");
        textArea.append("data = " + msg.data + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // User 당 생성되는 Thread
    // Read One 에서 대기 -> Write All
    class UserService extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;

        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        private Socket client_socket;
        private Vector user_vc;
        public String UserName = "";
        public String UserStatus;

        private int hearts = 5;

        public UserService(Socket client_socket) {
            // 매개변수로 넘어온 자료 저장
            this.client_socket = client_socket;
            this.user_vc = UserVec;
            try {
                oos = new ObjectOutputStream(client_socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(client_socket.getInputStream());

            } catch (Exception e) {
                AppendText("userService error");
            }
        }

        public void Login() {
            AppendText("새로운 참가자 " + UserName + " 입장.");

            //기존에 있던 사람들 알려주기
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if (user != this && user.UserStatus == "O")
                    oldPlayer(user.UserName); //110
            }

            //환영
            newPlayer(UserName); //105

            //입장한거 다른 사람들에게도 알려주기
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if (user != this && user.UserStatus == "O")
                    user.addPlayer(UserName); //115
            }

            //4명이 모이면
            if(user_vc.size()==4) {
                AppendText("4명 모두 입장.");
                emptyPlayer = false;
                //gameStart();
                for (int i = 0; i < user_vc.size(); i++) {
                    UserService user = (UserService) user_vc.elementAt(i);
                    user.gameReady("ready"); //500
                }
            }
        }

        public void newPlayer(String str) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "105", str);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void oldPlayer(String str) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "110", str);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void addPlayer(String str) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "115", str);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void gameReady(String str) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "500", str);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void gameStart() {
            //카드 초기화
            initCard();
            mixCard();

            openCard();
        }

        public void gameReStart() {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "555", "");
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void gameEnd() {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "700", "end");
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public synchronized void pressBell(String name) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.sendRingOne(name); //310
            }

            result = checkCard();
            AppendText(pressUser+"결과"+result);

            String resultStr = changeHeart(result); //하트 수 변경

            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.sendResultOne(resultStr); //330
            }

            //목숨 몇개 남았는지 검사
            AppendText("탈락한 사람 있는지");
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if(user.hearts==0) {
                    outPlayer = true;
                    user.UserStatus="S";
                }
            }
            if(outPlayer) {
                for (int i = 0; i < user_vc.size(); i++) {
                    UserService user = (UserService) user_vc.elementAt(i);
                    if(user.UserStatus=="S") {
                        user.sendOverTo(); //440
                        sendOver(user.UserName);
                        UserVec.removeElement(user);
                        //user.Logout();
                        i--;
                    }
                }
            }
            outPlayer = false;

            if(user_vc.size()==1) { //1명밖에 안남으면 게임 끝
                gameEnd();
            }
            else {
                ///검사 결과 괜찮으면
                AppendText("카드 다시 뒤집기");
                if(result) {
                    //열러진 카드 없애줌
                    reCard();
                }
                //클라이언트 다시 시작하도록
                for (int i = 0; i < user_vc.size(); i++) {
                    UserService user = (UserService) user_vc.elementAt(i);

                    user.gameReStart(); //555
                }
            }
        }

        public String changeHeart(boolean result) {
            if(result) {
                for (int i = 0; i < user_vc.size(); i++) {
                    UserService user = (UserService) user_vc.elementAt(i);
                    if (user.UserName.matches(pressUser)) {
                        AppendText(user.UserName+"의 하트 수 : "+user.hearts);
                        continue;
                    }
                    user.hearts--;
                    //AppendText(user.UserName+user.hearts+"하트 수");
                }
                return "성공";
            }
            else {
                for (int i = 0; i < user_vc.size(); i++) {
                    UserService user = (UserService) user_vc.elementAt(i);
                    if (user.UserName.matches(pressUser)) {
                        user.hearts--;
                        AppendText(user.UserName+"의 하트 수 : "+user.hearts);
                        break;
                    }
                }
                return "실패";
            }

        }

        public void sendOver(String outPlayer) {
            AppendText(outPlayer+" out");
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if(user.UserStatus=="O") {
                    user.sendOverOther(outPlayer); //400
                }
            }
        }


        public void sendCard(int cdn, int nowKind, int nowNum) {
            String data = cdn + "-" + nowKind + "-" + nowNum; //여기서 startCdn좌표 증가시키면 user마다 값 증가
            AppendText(data);

            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.sendCardOne(data); //550
            }
        }


        public void sendRingOne(String msg) { //누가 종 울렸는지 알려줌
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "310", msg); //msg = 누른 사람
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void sendResultOne(String msg) { //성공 여부 알려주기
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "330", msg);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void sendOverOther(String name) { //아웃된 유저 알려주기
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "400", name);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void sendOverTo() {
            AppendText("해당 플레이어에게 탈락 메세지 보냄");
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "440", "");
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }
        public void sendCardOne(String data) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "550", data);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }

        public void sendOut(String data) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if (user.UserStatus == "O")
                    user.sendOutOne(data);
            }
        }

        public void sendOutOne(String data) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "900", data);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout(); // 에러가난 현재 객체를 벡터에서 지운다
            }
        }
        public synchronized void openCard() {
            int nowOpen = openCardOrder[openCardCnt++];
            if(openCardCnt==56){
                mixCard();
                openCardCnt=0;
            }
            Card nowCard = cardList.get(nowOpen);
            int nowKind = nowCard.getFruitKind();
            int nowNum = nowCard.getFruitNum();
            openCardList.set(startCdn, nowCard);
            sendCard(startCdn, nowKind, nowNum);
            startCdn++;
            if(startCdn==4)
                startCdn=0;
        }

        public boolean checkCard() {
            int banana = 0;
            int strawberry = 0;
            int lime=0;
            int plum=0;
            int fruitKind;
            int fruitNum;
            boolean gameResult = false;

            for(int i=0;i<4;i++) {
                fruitKind = openCardList.get(i).getFruitKind();
                fruitNum = openCardList.get(i).getFruitNum();
                if(fruitKind==0) {
                    banana+=fruitNum;
                }else if(fruitKind==1) {
                    strawberry+=fruitNum;
                }else if(fruitKind==2) {
                    lime+=fruitNum;
                }else if(fruitKind==3) {
                    plum+=fruitNum;
                }
            }

            if(banana==5||strawberry==5||lime==5||plum==5) {
                gameResult = true;

                if(banana==5) {
                    for(int j=0;j<4;j++) {
                        if(openCardList.get(j).getFruitKind() == 0) {
                            openCardList.set(j, new Card(10,10));
                        }
                    }
                }
                if(strawberry==5) {
                    for(int j=0;j<4;j++) {
                        if(openCardList.get(j).getFruitKind() == 1) {
                            openCardList.set(j, new Card(10,10));
                        }
                    }
                }
                if(lime==5) {
                    for(int j=0;j<4;j++) {
                        if(openCardList.get(j).getFruitKind() == 2) {
                            openCardList.set(j, new Card(10,10));
                        }
                    }
                }
                if(plum==5) {
                    for(int j=0;j<4;j++) {
                        if(openCardList.get(j).getFruitKind() == 3) {
                            openCardList.set(j, new Card(10,10));
                        }
                    }
                }
            }
            return gameResult;
        }

        public void reCard() {
            int fruitKind;
            //int fruitNum;
            for(int i=0;i<4;i++) {
                fruitKind = openCardList.get(i).getFruitKind();
                if(fruitKind == 10) {
                    sendCard(i,10,10);
                    openCardList.set(i, new Card(-1,-1));
                }
            }
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

                openCardList.add(new Card(-1,-1)); //초기화
            }
        }

        public void mixCard() { //오픈할 카드 순서 먼저 배열에 저장
            openCardOrder = new int[56];
            Random rd = new Random();
            for(int i=0;i<56;i++) {
                openCardOrder[i] = rd.nextInt(56);
                for(int j=0;j<i;j++) {
                    if(openCardOrder[i]==openCardOrder[j]) {
                        i--;
                    }
                }
            }
        }

        public void Logout() {
            String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
            UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운
            sendOut(msg);
            //sendOut(UserName);
            AppendText("현재 참가자 수 " + UserVec.size());
        }

        public void run() {
            while (true) {
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm = null;
                    if (socket == null)
                        break;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (obcm == null)
                        break;
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        //AppendObject(cm);
                    } else
                        continue;

                    if (cm.code.matches("100")) {
                        UserName = cm.UserName;
                        UserStatus = "O"; // Online 상태
                        Login();
                    }else if(cm.code.matches("300")) { //종 눌림
                        ringing = true;
                        if(firstRing) {
                            firstRing = false;
                            pressUser = cm.UserName;
                            AppendText("종눌림"+pressUser);
                            pressBell(pressUser);
                        }
                    } else if(cm.code.matches("510")) {
                        cnt++;
                        if(cnt==UserVec.size()) {
                            AppendText("준비 완료");
                            cnt=0;
                            gameStart();
                        }
                    }else if(cm.code.matches("550")) {	//카드 받음
                        cnt++;
                        if(cnt==UserVec.size()) {
                            AppendText("카드 받음");
                            cnt=0;
                            if(!ringing) {
                                openCard();
                            }
                        }
                    }else if(cm.code.matches("555")) {	//카드 받음
                        cnt++;
                        if(cnt==UserVec.size()) {
                            AppendText("재 시작");
                            firstRing = true;
                            ringing = false;
                            cnt=0;
                            openCard();
                        }
                    }else if(cm.code.matches("")) {

                    }
                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
                        ois.close();
                        oos.close();
                        client_socket.close();
                        Logout(); // 에러가난 현재 객체를 벡터에서 지운다
                        break;
                    } catch (Exception ee) {
                        break;
                    } // catch문 끝
                } // 바깥 catch문끝
            } // while
        } // run

    } //class

}
