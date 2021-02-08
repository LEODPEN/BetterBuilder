import cn.mpy634.annotion.BetterBuilder;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc first test: to test the fluentSet.
 */
@BetterBuilder(fluentSet = true, fluentGet = false)
public class FluentSetTest {

    // 改为public是为测试需要
    public Long id;

    public String name;

    public Integer num;

    public FluentSetTest() {}

    public FluentSetTest(Long id, String name, Integer num) {
        this.id = id;
        this.name = name;
        this.num = num;
    }

}
