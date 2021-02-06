import cn.mpy634.annotion.BetterBuilder;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc first test: to test the fluentSet.
 */
@BetterBuilder(fluentSet = true)
public class FluentSetTest {

    private Long id;

    private String name;

    public Integer num;


    public static void main(String[] args) {
        FluentSetTest fst = new FluentSetTest();
        fst.num(1);
        System.out.println(fst.num);
    }
}
