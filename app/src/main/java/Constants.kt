import com.example.yokoyama.newsviewer.data.Country
import com.example.yokoyama.newsviewer.data.Language

const val ARTICLES_PER_PAGE_KEY = "ARTICLES_PER_PAGE_KEY"
const val COUNTRY_KEY = "COUNTRY_KEY"
const val LANGUAGE_KEY = "LANGUAGE_KEY"
const val SOURCES_KEY = "SOURCES_KEY"

const val NESTED_SCROLL_POSITION_KEY = "NESTED_SCROLL_POSITION_KEY"
const val COUNTRY_DIALOG_SELECTED_INDEX_KEY : String = "COUNTRY_DIALOG_SELECTED_INDEX_KEY"
const val LANGUAGE_DIALOG_SELECTED_INDEX_KEY : String = "LANGUAGE_DIALOG_SELECTED_INDEX_KEY"
const val SOURCES_DIALOG_SELECTED_SOURCES_KEY : String = "SOURCES_DIALOG_SELECTED_SOURCES_KEY"

const val LEFT_BIAS = 4
const val RIGHT_BIAS = 5
const val MIN_PAGE = 1
const val MAX_ARTICLES = 1000

const val ARTICLES_PER_PAGE_DEFAULT = 10
@JvmField val COUNTRY_DEFAULT = Country.UNITED_STATES
@JvmField val LANGUAGE_DEFAULT = Language.ALL