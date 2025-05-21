package com.kenanai.shop.checker;

import com.kenanai.shop.dto.OrderCreateContext;

public abstract class AbstractOrderCreateChecker implements OrderCreateChecker {
    private OrderCreateChecker next;

    public AbstractOrderCreateChecker linkWith(OrderCreateChecker next) {
        this.next = next;
        return (AbstractOrderCreateChecker) next;
    }

    @Override
    public void check(OrderCreateContext context) {
        doCheck(context);
        if (next != null) {
            next.check(context);
        }
    }

    protected abstract void doCheck(OrderCreateContext context);
}