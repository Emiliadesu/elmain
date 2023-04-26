package me.zhengjie.service.dto;

import java.util.Objects;

public class ReceivingTransaction extends DocAsnDetail {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceivingTransaction that = (ReceivingTransaction) o;
        return getAsnno().equals(that.getAsnno()) &&
                getSku().equals(that.getSku()) &&
                Objects.equals(getLotatt02(), that.getLotatt02());
    }
}
