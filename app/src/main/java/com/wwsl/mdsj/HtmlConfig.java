package com.wwsl.mdsj;


public class HtmlConfig {

    //登录即代表同意服务和隐私条款
    public static final String LOGIN_PRIVCAY = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=4";

    //直播间贡献榜
    public static final String LIVE_LIST = AppConfig.HOST + "/index.php?g=Appapi&m=contribute&a=index&uid=";
    //个人主页分享链接
    public static final String SHARE_HOME_PAGE = AppConfig.HOST + "/index.php?g=Appapi&m=home&a=index&touid=";

    //提现记录
    public static final String CASH_VOTE_RECORD = AppConfig.HOST + "/index.php?g=Appapi&m=cash&a=index";

    public static final String CASH_COIN_RECORD = AppConfig.HOST + "/index.php?g=Appapi&m=cash&a=index_coin";
    //支付宝回调地址
    public static final String ALI_PAY_NOTIFY_URL = AppConfig.HOST + "/Appapi/Pay/notify_ali";
    //视频分享地址
    public static final String SHARE_VIDEO = AppConfig.HOST + "/index.php?g=appapi&m=video&a=index&videoid=";

    //活跃度等级
//    public static final String WEB_LINK_ACTIVE_LEVEL = AppConfig.HOST + "/index.php?g=Appapi&m=Exchangelevel&a=index&uid=103206&token=6a04cde29f30c985e8c480ae6c7e3678";
    public static final String WEB_LINK_ACTIVE_LEVEL = AppConfig.HOST + "/index.php?g=Appapi&m=Exchangelevel&a=index";
    //隐私协议
    public static final String WEB_LINK_PRIVACY_PROTOCOL = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=3";
    //关于毛豆
    public static final String WEB_LINK_ABOUT = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=2";
    //用户协议
    public static final String WEB_LINK_USER_PROTOCOL = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=5";
    //提现规则
    public static final String WEB_LINK_DEPOSIT_RULE = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=56";

    //兑换说明
    public static final String WEB_LINK_EXCHANGE_RULE = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=57";
    //充值协议
    public static final String WEB_LINK_CHARGE_RULE = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=58";
    //意见反馈
    public static final String WEB_LINK_REPORT = AppConfig.HOST + "/index.php?g=Appapi&m=feedback&a=index";

    //装扮
    public static final String WEB_LINK_DECORATION = AppConfig.HOST + "/index.php?g=Appapi&m=Mall&a=index&uid=102984&token=dde0718c8e3d3ba95f2d0a4fae39fce9";

    //VIP 开通须知
    public static final String WEB_LINK_BUY_VIP = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=59";

    //规则说明
    public static final String WEB_LINK_MD_RULE = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=64";
    //会员服务协议
    public static final String WEB_LINK_VIP_RULE = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=65";

    //订单投诉
    public static final String WEB_LINK_MD_REPORT_BASE = AppConfig.HOST + "/index.php?g=Appapi&m=Feedback&a=tousu";

    public static final String WEB_LINK_HY_DES = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=71";

    //开通直播
    public static final String WEB_LINK_OPEN_LIVE = AppConfig.HOST + "/index.php?g=Appapi&m=Auth&a=index";

    //商城福利说明
    public static final String WEB_LINK_MARKET_DES = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=73";
    //助农福利说明
    public static final String WEB_LINK_ZN_WELFARE__DES = AppConfig.HOST + "/index.php?g=portal&m=page&a=index&id=72";

    //农产申请
    public static final String WEB_LINK_ZN_APPLY = AppConfig.HOST + "/index.php?i=4&c=entry&m=ewei_shopv2&do=mobile&r=merch.register";

    //启动页视频
    public static final String LAUNCH_VIDEO_URL = "http://maodousj1.wenzuxz.com/qidong/1125.mp4";


}
