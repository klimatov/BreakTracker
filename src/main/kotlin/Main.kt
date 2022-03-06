/*

- Копию на гитхаб
- Мигающий красным фон при сработке таймера
- Начало отсчета кнопкой старт превращающейся в ресет
- Переписать красивее код
- При добавлении/изменении времени фон сбрасывается на дефолтовый

*/

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.System.currentTimeMillis
import javax.sound.sampled.AudioSystem


class TimerState {
    var startTime = currentTimeMillis()
    var currentTime = mutableStateOf("00:00:00")
    var durationTime = mutableStateOf("00:00:05")

    fun updateTime() {
        val count = (currentTimeMillis()-startTime) // 1000
        currentTime.value = toTimeFormat(count)
    }

    fun resetTime() {
        startTime = currentTimeMillis()
    }

    fun changeDuration(value: Int) {
        if (toMillisFormat(durationTime.value)+value >= 0) durationTime.value = toTimeFormat(toMillisFormat(durationTime.value)+value)
    }

}

fun toTimeFormat(millis : Long): String = String.format("%02d:%02d:%02d",millis/1000/3600,(millis/1000 % 3600)/60,millis/1000 % 60)
fun toMillisFormat(time : String): Long = 1000 * time.split(":").map{it.toInt()}.run { this[0]*3600 + this[1]*60 + this[2] }.toLong()

@Composable
@Preview
fun App() {
    val timerState = TimerState()
    val text by remember {timerState.currentTime}

    MaterialTheme {
        Column(
            Modifier.background(color = Color.Red, shape = RectangleShape)
            .fillMaxSize()
            .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text)
            Button(onClick = {
                timerState.resetTime()
            }) {
                Text("Reset!")
            }

            Row{
                Column (horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {timerState.changeDuration(60*60*1000)}, Modifier.width(40.dp)){Text("↑")}
                    Text(timerState.durationTime.value.split(":")[0])
                    Button(onClick = {timerState.changeDuration(-60*60*1000)}, Modifier.width(40.dp)){Text("↓")}
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {timerState.changeDuration(60*1000)}, Modifier.width(40.dp)){Text("↑")}
                    Text(timerState.durationTime.value.split(":")[1])
                    Button(onClick = {timerState.changeDuration(-60*1000)}, Modifier.width(40.dp)){Text("↓")}
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {timerState.changeDuration(1000)}, Modifier.width(40.dp)){Text("↑")}
                    Text(timerState.durationTime.value.split(":")[2])
                    Button(onClick = {timerState.changeDuration(-1000)}, Modifier.width(40.dp)){Text("↓")}
                }
            }
        }
    }

    GlobalScope.launch {
        while (true) {
            delay(1000L)
            timerState.updateTime()
            val testVal = timerState.currentTime.value
            println(toMillisFormat(testVal))
            if ((timerState.startTime+(toMillisFormat(timerState.durationTime.value))) < currentTimeMillis())
                timerAlarm()
        }
    }
    println("gogogo")


}

private fun timerAlarm() {
    println("oops")
    val soundFile = File("src\\main\\resources\\bell2.wav") //Звуковой файл
    //Получаем AudioInputStream
    //Вот тут могут полететь IOException и UnsupportedAudioFileException
    val ais = AudioSystem.getAudioInputStream(soundFile)
    //Получаем реализацию интерфейса Clip
    //Может выкинуть LineUnavailableException
    val clip = AudioSystem.getClip()
    //Загружаем наш звуковой поток в Clip
    //Может выкинуть IOException и LineUnavailableException
    clip.open(ais)
    clip.framePosition = 0 //устанавливаем указатель на старт
    clip.start() //Поехали!!!
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BreakTracker",
        state = WindowState(width = 300.dp, height = 300.dp)
        ) {
        App()
    }
}
