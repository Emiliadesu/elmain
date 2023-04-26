import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.douyin.SorterSupport;
import me.zhengjie.support.fuliPre.BaseSkuPackInfo;
import me.zhengjie.support.fuliPre.DocOrderPackingSummary;
import me.zhengjie.utils.BigDecimalUtils;
import me.zhengjie.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
public class CNTest {

    @Test
    public void testCN() {
        String a = "1.0";
        BigDecimal b = new BigDecimal(a);
        System.out.println(b.stripTrailingZeros().toPlainString());
    }

    @Test
    public void testCN2() throws Exception {
        SorterSupport support = new SorterSupport();
        support.setAccessKey("E141328A415F4A1681F36BFF264C75CC");
        support.setSecretKey("89DFDA1F5D981A747E57CD21A50246B8");
        support.setUrl("http://180.184.175.204:59987");


//        support.register();
//        support.login();
//        support.logout();
        support.login();
        support.getSort("SF1695982319607", new BigDecimal("1.8"));
        support.logout();
//        support.sortEvent("771102365425", "");
    }


    @Test
    public void testCN3() {
//        Map<String, Object> map = new HashMap<>();
//        String a = "35605,29653,35077,33163,34653,33471,34879,36355,29723,32566,35848,32886,35983,35510,29482,33582,27731,29724,32771,34466,32895,35118,28265,29737,31080,34574,30428,34672,35664,34393,27790,34486,36054,33375,36318,29623,32356,30453,34398,34802,32831,27864,30846,34489,35912,33101,36116,29346,32860,35077,35963,33115,35165,34653,34879,29723,34431,35848,31258,35983,29464,34662,34923,29724,32771,32895,28223,34574,35996,33292,36273,34672,34393,34970,36456,34476,32951,36054,29836,34591,36008,33375,29623,30453,34981,30846,34487,34622,36013,36334,35605,34400,35165,30274,34637,33440,36335,30487,34431,35848,31258,34533,29464,32124,34662,34923,34453,32886,35109,35992,33292,36273,35642,36456,27782,29737,32951,28274,29836,34591,36008,32248,34398,34981,27828,29285,36013,36334,29627,34400,35670,32860,31257,34637,33440,36335,29653,30487,28003,31258,34533,33163,36155,32124,33471,36355,34453,32886,35109,35510,29482,32172,35642,33582,32771,34466,31080,35996,32248,30428,34687,35664,34486,29285,29848,36318,29627,32356,34802,35670,32834";
//        map.put("ids", a);
//        System.out.println(String.valueOf(map.get("ids")));
//        BigDecimal oprice = new BigDecimal(12000);
//        BigDecimal price = oprice.divide(BigDecimalUtils.ONEHUNDRED, BigDecimal.ROUND_HALF_DOWN, 2);
//        BigDecimal totalPrice = oprice.divide(BigDecimalUtils.ONEHUNDRED, BigDecimal.ROUND_HALF_DOWN, 2);
//        BigDecimal detailTax=totalPrice.multiply(new BigDecimal("0.091"));
//        String s = totalPrice.add(detailTax).setScale(4,BigDecimal.ROUND_HALF_DOWN) + "";
//        System.out.println(s);
        WmsSupport wmsSupport = new WmsSupport();
        JSONObject jsonObject = wmsSupport.querySo("4957604458278513804", "31052022I586784155");
        System.out.println(jsonObject);
    }
}
