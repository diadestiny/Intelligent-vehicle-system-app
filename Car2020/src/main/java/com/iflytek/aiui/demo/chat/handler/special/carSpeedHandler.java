package com.iflytek.aiui.demo.chat.handler.special;

import com.iflytek.aiui.demo.chat.handler.Answer;
import com.iflytek.aiui.demo.chat.handler.IntentHandler;
import com.iflytek.aiui.demo.chat.model.SemanticResult;
import com.iflytek.aiui.demo.chat.ui.chat.ChatViewModel;
import com.iflytek.aiui.demo.chat.ui.chat.PlayerViewModel;
import com.iflytek.aiui.demo.chat.ui.common.PermissionChecker;
import com.test.xander.carplay.Data.NowData;

public class carSpeedHandler extends IntentHandler {

    public carSpeedHandler(ChatViewModel model, PlayerViewModel player, PermissionChecker checker) {
        super(model, player, checker);
    }

    @Override
    public Answer getFormatContent(SemanticResult result) {

        StringBuilder answer=new StringBuilder("当前车速是"+ NowData.getSpeed()+"\n");



        return new Answer(answer.toString());
    }
}
