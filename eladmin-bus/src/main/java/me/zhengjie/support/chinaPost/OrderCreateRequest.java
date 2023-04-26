package me.zhengjie.support.chinaPost;

import me.zhengjie.support.douyin.CBOrderListMain;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreateRequest {

    private OrderNormal orderNormal;

    public OrderNormal getOrderNormal() {
        return orderNormal;
    }

    public void setOrderNormal(OrderNormal orderNormal) {
        this.orderNormal = orderNormal;
    }

    public static class OrderNormal{

        private String created_time; //订单接入时间  yyyy-mm-dd hh:mm:ss

        private Character logistics_provider;  //物流承运方   A：邮务  B：速递

        private String ecommerce_no;   //渠道来源标识  大多渠道以拼音首字母为准，例如：仓库配送（CKPS），个别渠道以定义的标准为主，例如：大客户（DKH)

        private String ecommerce_user_id;  //电商客户标识

        private Integer sender_type;   //客户类型   0 散户 1协议客户（默认为1）

        private String sender_no;  //协议客户代码

        private Integer inner_channel;//内部订单来源标识  0：直接对接 1：邮务国内小包订单系统 2：邮务国际小包订单系统 3：速递国内订单系统 4：速递国际订单系统（shipping） 5：在线发货平台------默认为’0’

        private Integer delivery_password_flag;//是否密码投递  0:否  1:是

        private String delivery_password;//投递密码

        private String logistics_order_no;

        private String batch_no;//批次号

        private String waybill_no;//运单号

        private Integer one_bill_flag;//一票多件标识  0正常 1一票多件

        private String submail_no;//子单数量（reserved28）

        private Integer one_bill_fee_type;//一票多件计费方式   1主单统一计费 2分单免首重计费 3平均重量计费 4主分单单独计费

        private Integer contents_attribute;//内件性质  1：文件  3、物品  2：信函  4、包裹

        private String base_product_no;//基础产品代码  1：标准快递  2：快递包裹  3：代收/到付（标准快递）

        private String biz_product_no;//业务产品分类（可售卖产品代码）

        private BigDecimal weight;

        private BigDecimal volume;//体积

        private BigDecimal length;

        private BigDecimal width;

        private BigDecimal height;

        private BigDecimal postage_total;//邮费

        private String pickup_notes;//备注

        private Integer insurance_flag;//保险保价标志   1:基本2:保价

        private BigDecimal insurance_amount;//保价金额

        private Integer deliver_type; //投递方式  1:客户自提2:上门投递3:智能包裹柜4:网点代投

        private String deliver_pre_date;//投递预约时间

        private Integer pickup_type;// 揽收方式：0 客户送货上门，1 机构上门揽收

        private String pickup_pre_begin_time;//揽收预约起始时间

        private String pickup_pre_end_time;//揽收预约截至时间

        private Integer payment_mode;//付款方式  1:寄件人 2:收件人 3:第三方 4:收件人集中付费 5:免费 6:寄/收件人 7:预付卡

        private Integer cod_flag; //代收款标志  1:代收货款2:代缴费9:无

        private BigDecimal cod_amount; //代收款金额

        private String electronic_preferential_no; //电子优惠券号

        private BigDecimal electronic_preferential_amount; //电子优惠券金额

        private Integer valuable_flag; //贵品标识:0 无 1有

        private String sender_safety_code; //寄件人安全码

        private String receiver_safety_code; //收件人安全码

        private String note; //公安交管邮件必填

        private String project_id; //项目标识  山西公安户籍（SXGAHJ），公安网上车管（GAWSCG），苹果（APPLE）

        private EMSAddress sender; //寄件人信息

        private EMSAddress pickup; //发货人信息

        private EMSAddress receiver; //收件人信息

        private List<Cargo> cargos; //商品信息

        public static class Cargo{

            private String cargo_name;

            private String cargo_category; //商品类型

            private Integer cargo_quantity;  //商品数量

            private BigDecimal cargo_value; //商品单价

            private BigDecimal cargo_weight; //商品重量

            public String getCargo_name() {
                return cargo_name;
            }

            public void setCargo_name(String cargo_name) {
                this.cargo_name = cargo_name;
            }

            public String getCargo_category() {
                return cargo_category;
            }

            public void setCargo_category(String cargo_category) {
                this.cargo_category = cargo_category;
            }

            public Integer getCargo_quantity() {
                return cargo_quantity;
            }

            public void setCargo_quantity(Integer cargo_quantity) {
                this.cargo_quantity = cargo_quantity;
            }

            public BigDecimal getCargo_value() {
                return cargo_value;
            }

            public void setCargo_value(BigDecimal cargo_value) {
                this.cargo_value = cargo_value;
            }

            public BigDecimal getCargo_weight() {
                return cargo_weight;
            }

            public void setCargo_weight(BigDecimal cargo_weight) {
                this.cargo_weight = cargo_weight;
            }
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public Character getLogistics_provider() {
            return logistics_provider;
        }

        public void setLogistics_provider(Character logistics_provider) {
            this.logistics_provider = logistics_provider;
        }

        public String getEcommerce_no() {
            return ecommerce_no;
        }

        public void setEcommerce_no(String ecommerce_no) {
            this.ecommerce_no = ecommerce_no;
        }

        public String getEcommerce_user_id() {
            return ecommerce_user_id;
        }

        public void setEcommerce_user_id(String ecommerce_user_id) {
            this.ecommerce_user_id = ecommerce_user_id;
        }

        public Integer getSender_type() {
            return sender_type;
        }

        public void setSender_type(Integer sender_type) {
            this.sender_type = sender_type;
        }

        public String getSender_no() {
            return sender_no;
        }

        public void setSender_no(String sender_no) {
            this.sender_no = sender_no;
        }

        public Integer getInner_channel() {
            return inner_channel;
        }

        public void setInner_channel(Integer inner_channel) {
            this.inner_channel = inner_channel;
        }

        public Integer getDelivery_password_flag() {
            return delivery_password_flag;
        }

        public void setDelivery_password_flag(Integer delivery_password_flag) {
            this.delivery_password_flag = delivery_password_flag;
        }

        public String getDelivery_password() {
            return delivery_password;
        }

        public void setDelivery_password(String delivery_password) {
            this.delivery_password = delivery_password;
        }

        public String getLogistics_order_no() {
            return logistics_order_no;
        }

        public void setLogistics_order_no(String logistics_order_no) {
            this.logistics_order_no = logistics_order_no;
        }

        public String getBatch_no() {
            return batch_no;
        }

        public void setBatch_no(String batch_no) {
            this.batch_no = batch_no;
        }

        public String getWaybill_no() {
            return waybill_no;
        }

        public void setWaybill_no(String waybill_no) {
            this.waybill_no = waybill_no;
        }

        public Integer getOne_bill_flag() {
            return one_bill_flag;
        }

        public void setOne_bill_flag(Integer one_bill_flag) {
            this.one_bill_flag = one_bill_flag;
        }

        public String getSubmail_no() {
            return submail_no;
        }

        public void setSubmail_no(String submail_no) {
            this.submail_no = submail_no;
        }

        public Integer getOne_bill_fee_type() {
            return one_bill_fee_type;
        }

        public void setOne_bill_fee_type(Integer one_bill_fee_type) {
            this.one_bill_fee_type = one_bill_fee_type;
        }

        public Integer getContents_attribute() {
            return contents_attribute;
        }

        public void setContents_attribute(Integer contents_attribute) {
            this.contents_attribute = contents_attribute;
        }

        public String getBase_product_no() {
            return base_product_no;
        }

        public void setBase_product_no(String base_product_no) {
            this.base_product_no = base_product_no;
        }

        public String getBiz_product_no() {
            return biz_product_no;
        }

        public void setBiz_product_no(String biz_product_no) {
            this.biz_product_no = biz_product_no;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getVolume() {
            return volume;
        }

        public void setVolume(BigDecimal volume) {
            this.volume = volume;
        }

        public BigDecimal getLength() {
            return length;
        }

        public void setLength(BigDecimal length) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return width;
        }

        public void setWidth(BigDecimal width) {
            this.width = width;
        }

        public BigDecimal getHeight() {
            return height;
        }

        public void setHeight(BigDecimal height) {
            this.height = height;
        }

        public BigDecimal getPostage_total() {
            return postage_total;
        }

        public void setPostage_total(BigDecimal postage_total) {
            this.postage_total = postage_total;
        }

        public String getPickup_notes() {
            return pickup_notes;
        }

        public void setPickup_notes(String pickup_notes) {
            this.pickup_notes = pickup_notes;
        }

        public Integer getInsurance_flag() {
            return insurance_flag;
        }

        public void setInsurance_flag(Integer insurance_flag) {
            this.insurance_flag = insurance_flag;
        }

        public BigDecimal getInsurance_amount() {
            return insurance_amount;
        }

        public void setInsurance_amount(BigDecimal insurance_amount) {
            this.insurance_amount = insurance_amount;
        }

        public Integer getDeliver_type() {
            return deliver_type;
        }

        public void setDeliver_type(Integer deliver_type) {
            this.deliver_type = deliver_type;
        }

        public String getDeliver_pre_date() {
            return deliver_pre_date;
        }

        public void setDeliver_pre_date(String deliver_pre_date) {
            this.deliver_pre_date = deliver_pre_date;
        }

        public Integer getPickup_type() {
            return pickup_type;
        }

        public void setPickup_type(Integer pickup_type) {
            this.pickup_type = pickup_type;
        }

        public String getPickup_pre_begin_time() {
            return pickup_pre_begin_time;
        }

        public void setPickup_pre_begin_time(String pickup_pre_begin_time) {
            this.pickup_pre_begin_time = pickup_pre_begin_time;
        }

        public String getPickup_pre_end_time() {
            return pickup_pre_end_time;
        }

        public void setPickup_pre_end_time(String pickup_pre_end_time) {
            this.pickup_pre_end_time = pickup_pre_end_time;
        }

        public Integer getPayment_mode() {
            return payment_mode;
        }

        public void setPayment_mode(Integer payment_mode) {
            this.payment_mode = payment_mode;
        }

        public Integer getCod_flag() {
            return cod_flag;
        }

        public void setCod_flag(Integer cod_flag) {
            this.cod_flag = cod_flag;
        }

        public BigDecimal getCod_amount() {
            return cod_amount;
        }

        public void setCod_amount(BigDecimal cod_amount) {
            this.cod_amount = cod_amount;
        }

        public String getElectronic_preferential_no() {
            return electronic_preferential_no;
        }

        public void setElectronic_preferential_no(String electronic_preferential_no) {
            this.electronic_preferential_no = electronic_preferential_no;
        }

        public BigDecimal getElectronic_preferential_amount() {
            return electronic_preferential_amount;
        }

        public void setElectronic_preferential_amount(BigDecimal electronic_preferential_amount) {
            this.electronic_preferential_amount = electronic_preferential_amount;
        }

        public Integer getValuable_flag() {
            return valuable_flag;
        }

        public void setValuable_flag(Integer valuable_flag) {
            this.valuable_flag = valuable_flag;
        }

        public String getSender_safety_code() {
            return sender_safety_code;
        }

        public void setSender_safety_code(String sender_safety_code) {
            this.sender_safety_code = sender_safety_code;
        }

        public String getReceiver_safety_code() {
            return receiver_safety_code;
        }

        public void setReceiver_safety_code(String receiver_safety_code) {
            this.receiver_safety_code = receiver_safety_code;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getProject_id() {
            return project_id;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public EMSAddress getSender() {
            return sender;
        }

        public void setSender(EMSAddress sender) {
            this.sender = sender;
        }

        public EMSAddress getPickup() {
            return pickup;
        }

        public void setPickup(EMSAddress pickup) {
            this.pickup = pickup;
        }

        public EMSAddress getReceiver() {
            return receiver;
        }

        public void setReceiver(EMSAddress receiver) {
            this.receiver = receiver;
        }

        public List<Cargo> getCargos() {
            return cargos;
        }

        public void setCargos(List<Cargo> cargos) {
            this.cargos = cargos;
        }
    }

}
