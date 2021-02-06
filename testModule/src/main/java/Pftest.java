import cn.mpy634.annotion.BetterBuilder;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc
 */
@BetterBuilder(fluent = true)
public class Pftest {

    private Long id;

    private String name;

    public Integer num;


    public static void main(String[] args) {
        Pftest pfTest = new Pftest();
        pfTest.num(1);
        System.out.println(pfTest.num);
    }
}
