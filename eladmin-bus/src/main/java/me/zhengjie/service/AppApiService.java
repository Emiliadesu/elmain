package me.zhengjie.service;

import me.zhengjie.domain.PackCheck;
import me.zhengjie.service.dto.StockDto;

import java.util.List;

public interface AppApiService {

    PackCheck packCheckInit(String mailNo);

    PackCheck packCheck(Long checkId, String barCode);

    void packCheckSubmit(Long checkId);

    List<StockDto> queryStock(String barCode);
}
