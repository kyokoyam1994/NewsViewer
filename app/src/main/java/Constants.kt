import com.example.yokoyama.newsviewer.Country
import com.example.yokoyama.newsviewer.Language

const val ARTICLES_PER_PAGE_KEY = "Articles_Per_Page"
const val COUNTRY_KEY = "Country"
const val LANGUAGE_KEY = "Language"
const val SOURCES_KEY = "Sources"

const val LEFT_BIAS = 4
const val RIGHT_BIAS = 5
const val MIN_PAGE = 1
const val MAX_ARTICLES = 1000

const val ARTICLES_PER_PAGE_DEFAULT = 20
@JvmField val COUNTRY_DEFAULT = Country.UNITED_STATES
@JvmField val LANGUAGE_DEFAULT = Language.ALL