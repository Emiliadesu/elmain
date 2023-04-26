/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.quartz.task;

import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.StringUtil;
import org.springframework.stereotype.Component;

/**
 * 测试用
 * @author Zheng Jie
 * @date 2019-01-08
 */
@Slf4j
@Component
public class TestTask {
    private boolean preStarted=true;

    public void run(){
        log.info("run 执行成功");
    }

    public void run1(String str){
        log.info("run1 执行成功，参数为： {}" + str);
    }

    public void run2(){
        log.info("run2 执行成功");
    }

    public void testPre(String mailsStr){
        if (StringUtil.isBlank(mailsStr))
            return;
        String []mails=mailsStr.split(",");
        String resp= HttpUtil.post("http://10.0.5.100:8080/api/el-admin/pre-test","");
        if (!StringUtil.equals(resp,"Backend service started successfully")&&preStarted){
            //pre服务器挂掉并且erp还未预警，发送失效邮件
            for (String mail : mails) {
                try {
                    MailUtil.send(mail,"pre服务器宕机预警","pre服务器宕机预警通知",false);
                    preStarted=false;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if (StringUtil.equals(resp,"Backend service started successfully")&&!preStarted){
            //pre服务器恢复正常并且erp已经预警，发送恢复通知邮件
            for (String mail : mails) {
                try {
                    MailUtil.send(mail,"pre服务器恢复正常通知","pre服务器已恢复正常",false);
                    preStarted=true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
