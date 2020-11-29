import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private const val PETAL_COUNT = 100
private const val MIN_SPAWN_THRESHOLD_MS = 500

@Composable
fun Petals(modifier: Modifier = Modifier) = WithConstraints {
  val simulation = remember { PetalSimulation() }
  val frameTime by frameTimeMillis()
  val path = remember {
    "M12 32.8L12 32.8C5.3 32.8 0 28.8 0 24 0 19.1 5.4 15.1 12 15.2 14.5 15.2 16.8 15.7 18.7 16.7L18.7 16.7 18.7 16.7C18.7 16.7 18.8 16.7 18.9 16.8 21.8 18.2 36.3 23.9 48 24 40.5 27.7 29.1 32.8 12.1 32.8L12 32.8 12 32.8Z".asPath()
  }

  Canvas(modifier = modifier) {
    simulation.update(frameTime, constraints.maxWidth.toDp(), constraints.maxHeight.toDp())
    val petals = simulation.petals

    for (index in petals.indices) {
      val petal = petals[index]
      translate(petal.x.toPx(), petal.y.toPx()) {
        rotate(petal.rotation, Offset(16.dp.toPx(), 16.dp.toPx())) {
          drawPath(path = path, color = Color.Magenta, alpha = 0.7f)
        }
      }
    }
  }
}

private data class Petal(
  var x: Dp = 0.dp,
  var y: Dp = 0.dp,
  var xSpeed: Dp = 0.dp,
  var ySpeed: Dp = 0.dp,
  var rotation: Float = 0f,
  var rotationSpeed: Float = 0f,
  var period: Float = 0f,
  var amplitude: Float = 0f
)

private class PetalSimulation {
  private val activePetals = ArrayList<Petal>(PETAL_COUNT)
  private val pool = ArrayDeque<Petal>(PETAL_COUNT).apply {
    repeat(PETAL_COUNT) { add(Petal()) }
  }

  private var lastUpdateTime = 0L
  private var lastSpawnTime = 0L
  private var worldWidth = 0.dp
  private var worldHeight = 0.dp

  val petals: List<Petal> get() = activePetals

  fun update(time: Long, width: Dp, height: Dp) {
    worldWidth = width
    worldHeight = height

    if (time - lastSpawnTime > MIN_SPAWN_THRESHOLD_MS && pool.isNotEmpty()) {
      lastSpawnTime = time

      val petal = pool.removeFirst().apply {
        x = 0.dp
        y = 0.dp
        xSpeed = 96.dp + 96.dp * Random.nextFloat()
        ySpeed = 48.dp + 48.dp * Random.nextFloat()
        rotation = Random.nextFloat() * 360f
        rotationSpeed = 0.5f + Random.nextFloat() * 180 * if (Random.nextBoolean()) 1 else -1
        period = 0.05f + Random.nextFloat() * 0.6f
        amplitude = 0.003f + Random.nextFloat() * 0.05f
      }

      activePetals.add(petal)
    }
    val deltaSeconds = (time - lastUpdateTime) / 1000f

    for (index in activePetals.indices.reversed()) {
      with(activePetals[index]) {
        rotation += rotationSpeed * deltaSeconds
        x += xSpeed * deltaSeconds
        y += (period * sin(amplitude * x.value) + (ySpeed.value * deltaSeconds)).dp

        if (x > worldWidth) {
          activePetals.remove(this)
          pool.add(this)
        }
      }
    }

    lastUpdateTime = time
  }
}
