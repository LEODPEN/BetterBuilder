import cn.mpy634.annotation.BetterBuilder;
import cn.mpy634.annotation.BetterBuilder.*;

import java.util.List;

/**
 * @author LEO D PEN
 * @date 2021/2/12
 * @desc
 */
@BetterBuilder
public class IgnoreGetSet {

    @IgnoreSet
    private String 牛;

    @IgnoreSet
    @IgnoreGet
    private Integer 年;

    @IgnoreGet
    private Student 大;

    private List<Boolean> 吉;
}
