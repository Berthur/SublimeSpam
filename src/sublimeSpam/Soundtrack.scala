package sublimeSpam

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths

class Soundtrack(name: String, offset: Int) extends javafx.embed.swing.JFXPanel() {
  
  val defaultSoundName = "tracks/default.mp3"
  val soundName = "tracks/" + name        //"tracks/" + name + ".mp3"
  val sound = try {
    new Media(Paths.get(soundName).toUri().toString())
    } catch {
      case e: Throwable => new Media(Paths.get(defaultSoundName).toUri().toString())
    }
  val mediaPlayer = new MediaPlayer(sound)
  var initiated = false
  
  def play(time: Double) = {
    mediaPlayer.setStartTime(javafx.util.Duration.millis(time))
    mediaPlayer.play()
  }
  
  def pause() = mediaPlayer.pause()
  
  def currentTime  = mediaPlayer.getCurrentTime.toMillis().toInt
  
}