import android.content.res.Resources
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

operator fun CompositeDisposable.plusAssign(disposable: Disposable) { add(disposable) }

//Shorthand function to subscribe on IO thread and observing on main thread
fun <T> Single<T>.asyncIO() : Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

//Extension properties for converting between dp and px
val Int.dp : Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px : Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()