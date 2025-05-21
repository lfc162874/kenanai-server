package com.kenanai.shop.checker;

import com.kenanai.shop.dto.CartAddContext;

/**
 * 加入购物车责任链抽象类，实现链式调用基础逻辑。
 */
public abstract class AbstractCartAddChecker implements CartAddChecker {
    private CartAddChecker next;

    /**
     * 链接下一个校验器
     */
    public AbstractCartAddChecker linkWith(CartAddChecker next) {
        this.next = next;
        return (AbstractCartAddChecker) next;
    }

    @Override
    public void check(CartAddContext context) {
        doCheck(context);
        if (next != null) {
            next.check(context);
        }
    }

    /**
     * 具体校验逻辑由子类实现
     */
    protected abstract void doCheck(CartAddContext context);
} 