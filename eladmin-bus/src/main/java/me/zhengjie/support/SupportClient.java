package me.zhengjie.support;


import lombok.extern.slf4j.Slf4j;
import me.zhengjie.support.aikucun.AikucunSupport;
import me.zhengjie.support.beidian.BeiDianSupport;
import me.zhengjie.support.cainiao.CaiNiaoSupport;
import me.zhengjie.support.chinaPost.EMSSupport;
import me.zhengjie.support.guomei.GuomeiSupport;
import me.zhengjie.support.jackYun.JackYunSupport;
import me.zhengjie.support.jingdong.JDSupport;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.support.meituan.MeiTuanSupport;
import me.zhengjie.support.moGuJie.MoGuJieSupport;
import me.zhengjie.support.oms.OmsSupport;
import me.zhengjie.support.pdd.PDDSupport;
import me.zhengjie.support.ruoYuChen.RuoYuChenSupport;
import me.zhengjie.support.ymatou.YmatouSupport;
import me.zhengjie.support.youzan.YouZanSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SupportClient {

    @Bean
    public KJGSupport kjgSupport() {
        return new KJGSupport();
    }

    @Bean
    public GZTOSupport gztoSupport() {
        return new GZTOSupport();
    }

    @Bean
    public PDDSupport pddSupport() {
        return new PDDSupport();
    }

    @Bean
    public WmsSupport wmsSupport() {
        return new WmsSupport();
    }

    @Bean
    public YouZanSupport youZanSupport() throws Exception {
        return YouZanSupport.initSupport();
    }

    @Bean
    public BeiDianSupport beiDianSupport() {
        return new BeiDianSupport();
    }

    @Bean
    public YmatouSupport ymatouSupport() {
        return new YmatouSupport();
    }

    @Bean
    public MeiTuanSupport meiTuanSupport() {
        return new MeiTuanSupport();
    }

    @Bean
    public MoGuJieSupport moGuJieSupport(){
        return new MoGuJieSupport();
    }

    @Bean
    public JDSupport jdSupport(){
        return new JDSupport();
    }

    @Bean
    public EMSSupport emsSupport(){return new EMSSupport();}

    @Bean
    public OmsSupport omsSupport(){return new OmsSupport();}

    @Bean
    public RuoYuChenSupport ruoYuChenSupport(){return new RuoYuChenSupport();}

    @Bean
    public GuomeiSupport guomeiSupport(){
        return new GuomeiSupport();
    }

    @Bean
    public JackYunSupport jackYunSupport(){
        return new JackYunSupport();
    }

    @Bean
    public AikucunSupport aikucunSupport(){
        return new AikucunSupport();
    }

    @Bean
    public CaiNiaoSupport caiNiaoSupport(){
        return CaiNiaoSupport.initSupport();
    }
}
