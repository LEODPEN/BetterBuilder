import cn.mpy634.annotation.BetterBuilder;

/**
 * @author LEO D PEN
 * @date 2021/2/7
 * @desc
 */
@BetterBuilder(fluentGet= true, fluentSet = false, noBuilder = true)
public class FluentGet {

    private int primitiveNum;

    private Integer boxedNum;

    private String name;

    private FluentSet fluentSet;

    public FluentGet(int primitiveNum, Integer boxedNum, String name, FluentSet fluentSet) {
        this.primitiveNum = primitiveNum;
        this.boxedNum = boxedNum;
        this.name = name;
        this.fluentSet = fluentSet;
    }

}
