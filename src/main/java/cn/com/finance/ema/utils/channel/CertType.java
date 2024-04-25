package cn.com.finance.ema.utils.channel;


import cn.hutool.core.util.StrUtil;

public enum CertType {
    ID_CARD("IC", "身份证"),
    TEMP_ID_CARD("TIC", "临时身份证"),
    PASSPORT("PP", "护照"),
    RESIDENCE_BOOKLET("RB", "户口簿"),
    ARMY_OFFICER_CARD("AOC", "军官证"),
    ARMED_POLICE_CARD("APC", "武警证"),
    FOREIGNER_ID_CARD("FIC", "外交人员身份证 "),
    FOREIGNER_RESIDENCE("FR", "外国人永久居留证"),
    FOREIGN_PASSPORT("FPP", "外国护照"),
    HK_MC_PASS_H("HMPH", "港澳居民来往内地通行证-香港（证件号H开头）"),
    HK_MC_PASS_M("HMPM", "港澳居民来往内地通行证-澳门（证件号M开头）"),
    TW_PASS("TWP", "台湾居民来往大陆通行证/台胞证");


    private final String code;
    private final String message;

    private CertType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CertType getByCode(String code) {
        if(StrUtil.isBlank(code)) {
            return null;
        } else {
            CertType[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                CertType type = arr$[i$];
                if(type.getCode().equals(code)) {
                    return type;
                }
            }

            return null;
        }
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

