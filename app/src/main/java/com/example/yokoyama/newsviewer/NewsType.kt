package com.example.yokoyama.newsviewer

sealed class NewsType {
    class Everything (val language: Language) : NewsType() { val title = "Home" }
    class TopHeadlines(val category: Category, val country: Country) : NewsType()
}

enum class Category (val abbr: String, val displayName: String) {
    BUSINESS("business", "Business"),
    ENTERTAINMENT("entertainment", "Entertainment"),
    GENERAL("general", "General"),
    HEALTH("health", "Health"),
    SCIENCE("science", "Science"),
    SPORTS("sports", "Sports"),
    TECHNOLOGY("technology", "Technology")
}

enum class Country (val abbr: String, val displayName: String, val imageResource: Int?) {
    ARGENTINA("ar", "Argentina", R.drawable.country_ar),
    AUSTRALIA("au", "Australia", R.drawable.country_au),
    AUSTRIA("at", "Austria", R.drawable.country_at),
    BELGIUM("be", "Belgium", R.drawable.country_be),
    BRAZIL("br", "Brazil", R.drawable.country_br),
    BULGARIA("bg", "Bulgaria", R.drawable.country_bg),
    CANADA("ca", "Canada", R.drawable.country_ca),
    CHINA("cn", "China", R.drawable.country_cn),
    COLOMBIA("co", "Colombia", R.drawable.country_co),
    CUBA("cu", "Cuba", R.drawable.country_cu),
    CZECH_REPUBLIC("cz", "Czech Republic", R.drawable.country_cz),
    EGYPT("eg", "Egypt", R.drawable.country_eg),
    FRANCE("fr", "France", R.drawable.country_fr),
    GERMANY("de", "Germany", R.drawable.country_de),
    GREECE("gr", "Greece", R.drawable.country_gr),
    HONG_KONG("hk", "Hong Kong", R.drawable.country_hk),
    HUNGARY("hu", "Hungary", R.drawable.country_hu),
    INDIA("in", "India", R.drawable.country_in),
    INDONESIA("id", "Indonesia", R.drawable.country_id),
    IRELAND("ie", "Ireland", R.drawable.country_ie),
    ISRAEL("il", "Israel", R.drawable.country_il),
    ITALY("it", "Italy", R.drawable.country_it),
    JAPAN("jp", "Japan", R.drawable.country_jp),
    LATVIA("lv", "Latvia", R.drawable.country_lv),
    LITHUANIA("lt", "Lithuania", R.drawable.country_lt),
    MALAYSIA("my", "Malaysia", R.drawable.country_my),
    MEXICO("mx", "Mexico", R.drawable.country_mx),
    MOROCCO("ma", "Morocco", R.drawable.country_ma),
    NETHERLANDS("nl", "Netherlands", R.drawable.country_nl),
    NEW_ZEALAND("nz", "New Zealand", R.drawable.country_nz),
    NIGERIA("ng", "Nigeria", R.drawable.country_ng),
    NORWAY("no", "Norway", R.drawable.country_no),
    PHILIPPINES("ph", "Philippines", R.drawable.country_ph),
    POLAND("pl", "Poland", R.drawable.country_pl),
    PORTUGAL("pt", "Portugal", R.drawable.country_pt),
    ROMANIA("ro", "Romania", R.drawable.country_ro),
    RUSSIA("ru", "Russia", R.drawable.country_ru),
    SAUDI_ARABIA("sa","Saudi Arabia", R.drawable.country_sa),
    SERBIA("rs", "Serbia", R.drawable.country_rs),
    SINGAPORE("sg", "Singapore", R.drawable.country_sg),
    SLOVAKIA("sk", "Slovakia", R.drawable.country_sk),
    SLOVENIA("si", "Slovenia", R.drawable.country_si),
    SOUTH_AFRICA("za", "South Africa", R.drawable.country_za),
    SOUTH_KOREA("kr", "South Korea", R.drawable.country_kr),
    SWEDEN("se", "Sweden", R.drawable.country_se),
    TAIWAN("tw", "Taiwan", R.drawable.country_tw),
    THAILAND("th", "Thailand", R.drawable.country_th),
    TURKEY("tr", "Turkey", R.drawable.country_tr),
    UKRAINE("ua", "Ukraine", R.drawable.country_ua),
    UNITED_KINGDOM("gb", "United Kingdom", R.drawable.country_gb),
    UNITED_STATES("us", "United States", R.drawable.country_us),
    VENUZUELA("ve", "Venezuela", R.drawable.country_ve);
}

enum class Language (val abbr: String?, val displayName: String) {
    ALL(null, "All Languages"),
    ARABIC("ar", "Arabic"),
    GERMAN("de", "German"),
    ENGLISH("en", "English"),
    SPANISH("es", "Spanish"),
    FRENCH("fr", "French"),
    ITALIAN("it", "Italian"),
    DUTCH("nl", "Dutch"),
    NORWEGIAN("no", "Norwegian"),
    PORTUGUESE("pt", "Portuguese"),
    RUSSIAN("ru", "Russian"),
    CHINESE("zh", "Chinese")
}