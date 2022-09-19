import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class Music2 extends Thread{
    private Player player;
    private boolean isLoop; //무한반복할지 말지
    private File file;
    private FileInputStream fis;
    private BufferedInputStream bis;

    public Music2(String name, boolean isLoop) {
        try {
            this.isLoop = isLoop;
            file=new File(Main.class.getResource("/music/"+name).toURI());
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public int getTime() { //현재 어떤 위치에서 음악 실행 중인지
        //10s -> 10000, 0.001s단위 까지
        if(player == null) {
            return 0;
        }
        return player.getPosition();
    }

    public void close() { //음악 항상 종료 할 수 있도록
        isLoop = false;
        player.close();
        this.interrupt(); //해당 스레드 중지 상태로, 곡 중지
        //스레드는 하나의 작은 프로그램
    }

    @Override
    public void run() { //스레드 사용시 반드시 사용하는 함수
        try {
            do { //여기에 들어가는게 핵심 -> 곡 실행
                player.play();
                fis=new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                player = new Player(bis);
            }while(isLoop); //isLoop가 true면 무한 반복
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}