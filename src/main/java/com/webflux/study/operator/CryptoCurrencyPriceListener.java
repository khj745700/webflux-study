package com.webflux.study.operator;

import java.util.List;

public interface CryptoCurrencyPriceListener {
    void onPrice(List<Integer> priceList);
    void onComplete();
}
