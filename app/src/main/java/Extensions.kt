import android.content.res.Resources




//Extension properties for converting between dp and px
val Int.dp : Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px : Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()