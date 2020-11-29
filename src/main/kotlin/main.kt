import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

fun main() = Window(size = IntSize(1600, 900)) {
  DesktopMaterialTheme(colors = darkColors()) {
    Surface {
      Box(modifier = Modifier.fillMaxSize()) {
        Petals()
      }
    }
  }
}
