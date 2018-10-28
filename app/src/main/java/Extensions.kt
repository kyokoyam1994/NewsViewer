import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import com.example.yokoyama.newsviewer.Country
import com.example.yokoyama.newsviewer.Language
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

operator fun CompositeDisposable.plusAssign(disposable: Disposable) { add(disposable) }

//Shared preference extension functions
operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Type is not supported")
    }
}

inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null) : T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Type is not supported")
    }
}

inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    operation(editor)
    editor.apply()
}

//Shorthand function to subscribe on IO thread and observing on main thread
fun <T> Single<T>.asyncIO() : Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

//Shorthand property for getting default shared preferences
val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

val Context.currCountry : Country
    get() {
        val country : String? = defaultSharedPreferences[COUNTRY_KEY]
        return country?.let { Country.valueOf(country) } ?: COUNTRY_DEFAULT
    }

val Context.currLanguage : Language
    get() {
        val language : String? = defaultSharedPreferences[LANGUAGE_KEY]
        return language?.let { Language.valueOf(language) } ?: LANGUAGE_DEFAULT
    }

//Extension properties for converting between dp and px
val Int.dp : Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px : Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()