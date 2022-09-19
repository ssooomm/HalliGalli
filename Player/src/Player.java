public class Player {
    String userName;
    int hearts = 5;

    public Player(String userName) {
        super();
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void decrHeart() {
        this.hearts--;
    }

    public int getHearts() {
        return hearts;
    }

}
