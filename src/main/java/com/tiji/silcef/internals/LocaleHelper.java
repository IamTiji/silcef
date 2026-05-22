package com.tiji.silcef.internals;

import java.util.Map;

public class LocaleHelper {
    public static final Map<String, String> CEF_MAP = Map.<String, String>ofEntries(
            Map.entry("af_za",  "af"),
            Map.entry("am_et",  "am"),
            Map.entry("ar_sa",  "ar"),
            Map.entry("bg_bg",  "bg"),
            Map.entry("bn_bd",  "bn"),
            Map.entry("ca_es",  "ca"),
            Map.entry("cs_cz",  "cs"),
            Map.entry("da_dk",  "da"),
            Map.entry("de_de",  "de"),
            Map.entry("el_gr",  "el"),
            Map.entry("en_gb",  "en-GB"),
            Map.entry("en_us",  "en-US"),
            Map.entry("es_419", "es-419"),
            Map.entry("es_es",  "es"),
            Map.entry("et_ee",  "et"),
            Map.entry("fa_ir",  "fa"),
            Map.entry("fi_fi",  "fi"),
            Map.entry("fil_ph", "fil"),
            Map.entry("fr_fr",  "fr"),
            Map.entry("gu_in",  "gu"),
            Map.entry("he_il",  "he"),
            Map.entry("hi_in",  "hi"),
            Map.entry("hr_hr",  "hr"),
            Map.entry("hu_hu",  "hu"),
            Map.entry("id_id",  "id"),
            Map.entry("it_it",  "it"),
            Map.entry("ja_jp",  "ja"),
            Map.entry("kn_in",  "kn"),
            Map.entry("ko_kr",  "ko"),
            Map.entry("lt_lt",  "lt"),
            Map.entry("lv_lv",  "lv"),
            Map.entry("ml_in",  "ml"),
            Map.entry("mr_in",  "mr"),
            Map.entry("ms_my",  "ms"),
            Map.entry("no_no",  "nb"),
            Map.entry("nl_nl",  "nl"),
            Map.entry("pl_pl",  "pl"),
            Map.entry("pt_br",  "pt-BR"),
            Map.entry("pt_pt",  "pt-PT"),
            Map.entry("ro_ro",  "ro"),
            Map.entry("ru_ru",  "ru"),
            Map.entry("sk_sk",  "sk"),
            Map.entry("sl_si",  "sl"),
            Map.entry("sr_sp",  "sr"),
            Map.entry("sv_se",  "sv"),
            Map.entry("sw_ke",  "sw"),
            Map.entry("ta_in",  "ta"),
            Map.entry("te_in",  "te"),
            Map.entry("th_th",  "th"),
            Map.entry("tr_tr",  "tr"),
            Map.entry("uk_ua",  "uk"),
            Map.entry("ur_pk",  "ur"),
            Map.entry("vi_vn",  "vi"),
            Map.entry("zh_cn",  "zh-CN"),
            Map.entry("zh_tw",  "zh-TW")
    );

    public static String getCEFLanguageCode(String localeCode) {
        return CEF_MAP.getOrDefault(localeCode, CEF_MAP.get("en_us"));
    }

    public static boolean isSupported(String localeCode) {
        return CEF_MAP.containsKey(localeCode);
    }
}
