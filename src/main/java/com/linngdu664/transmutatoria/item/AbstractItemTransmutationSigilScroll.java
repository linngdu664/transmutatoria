package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import net.minecraft.resources.Identifier;

public class AbstractItemTransmutationSigilScroll extends AbstractItemTransmutationScroll {
    // 不会过期的印记卷轴
    protected AbstractItemTransmutationSigilScroll(Identifier id) {
        super(id);
    }

    // 会过期的印记卷轴
    protected AbstractItemTransmutationSigilScroll(Identifier id, ExpireInfo expireInfo) {
        super(id, expireInfo);
    }
}
