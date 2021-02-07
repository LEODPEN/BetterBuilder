import cn.mpy634.annotion.BetterBuilder;

/**
 * @author LEO D PEN
 * @date 2021/2/7
 * @desc
 */
@BetterBuilder(fluentGet= true)
public class FluentGetTest {

    private int primitiveNum;

    private Integer boxedNum;

    private String name;

    private FluentSetTest fluentSetTest;

    public FluentGetTest(int primitiveNum, Integer boxedNum, String name, FluentSetTest fluentSetTest) {
        this.primitiveNum = primitiveNum;
        this.boxedNum = boxedNum;
        this.name = name;
        this.fluentSetTest = fluentSetTest;
    }

}
