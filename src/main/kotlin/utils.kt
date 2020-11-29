import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.dispatch.withFrameMillis
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser

@Composable
fun frameTimeMillis(): State<Long> {
  val millisState = mutableStateOf(0L)

  LaunchedEffect(Unit) {
    val startTime = withFrameMillis { it }

    while (true) {
      withFrameMillis { frameTime ->
        millisState.value = frameTime - startTime
      }
    }
  }

  return millisState
}

fun String.asPath(): Path {
  return PathParser().parsePathString(this).toPath()
}
