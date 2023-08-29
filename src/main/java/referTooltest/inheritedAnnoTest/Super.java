package referTooltest.inheritedAnnoTest;

//作为父类
@Atable
public class Super {
    private int superx;
    public int supery;

    public Super() {
    }
    //私有
    private int superX(){
        return 0;
    }
    //公有
    public int superY(){
        return 0;
    }

}