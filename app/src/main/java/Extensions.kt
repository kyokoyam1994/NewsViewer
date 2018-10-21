import android.content.res.Resources
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


//fun Single<T>.asyncIO() : Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

//Extension properties for converting between dp and px
val Int.dp : Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px : Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()