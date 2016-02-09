package sublimeSpam.ui

import scala.swing._
import scala.io.Source      //Temporary
import java.io._

import sublimeSpam._
import sublimeSpam.io.Loader

class Recorder(mp3trackname: String) extends SimpleSwingApplication {
  //var trackname = "Test8"
  /*var trackname = {
    val file = new File("recording_track.txt")
    if (!file.exists()) throw new LoadTrackException("Requested track file does not exist.")
    val source = Source.fromFile(file)
    val result = source.getLines.next()
    println("Now recording: " + result)
    result
  }*/
  val width = 600
  val height = 800
  val fullHeight = 810
  val railNo = 3
  val fps = 100
  val minimumTailDuration = 250
  val controls: Map[Int, () => Unit] = Map(
      //32 -> (() => (if (!hasStarted) start() else if (!hasEnded) end())),
      32 -> (() => Unit),
      71 -> (() => hitRail(0, 'q')),
      72 -> (() => hitRail(1, 'q')),
      74 -> (() => hitRail(2, 'q')),
      1071 -> (() => releaseRail(0)),
      1072 -> (() => releaseRail(1)),
      1074 -> (() => releaseRail(2))
      )
      
  val keysPressed = Array.ofDim[Boolean](200)    // <<< amount of key ids?!
  val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()    // Test
  
  var hasStarted = false
  var hasEnded = false
  var startTime = 0L
  var timeNow = 0L
  val game = new Game(railNo)
  
  val soundtrack = new Soundtrack(Data.username + "/" + mp3trackname, 0)
  
  def hitRail(railNo: Int, entityType: Char) = {
    val entity = entityType match {
      case 'q' => new Quick(timeNow)
      case 't' => new Tail(timeNow, 1000)    // <<< SHOULD NOT HAPPEN.
      case _ => throw new RecorderError("Entity type '" + entityType + "' unknown.")
    }
    game.rails(railNo).add(entity)
  }
  
  def releaseRail(railNo: Int) = {
    val rail = game.rails(railNo)
    if (!rail.isEmpty) {
      val endTime = timeNow
      val startTime = rail.lastEntity.get.time
      if (endTime - startTime > minimumTailDuration) {          //Must be held for at least 100ms for it to become a long.
        rail.replaceLast(new Tail(startTime, endTime - startTime))
      }
    }
  }
  
  def top = new MainFrame {
    val trackStrings = (mp3trackname.take(mp3trackname.length - 4) + " ").split(";").map(_.trim)
    title = "Recording: " + trackStrings(0) + " - " + trackStrings(1)
    resizable = false
    this.location = new Point((screenSize.getWidth.toInt - width) / 2, 0)
    minimumSize   = new Dimension(width, fullHeight)
    preferredSize = new Dimension(width, fullHeight)
    maximumSize   = new Dimension(width, fullHeight)
    val mainBox = new BoxPanel(Orientation.Vertical) {
      // >>> Settings for rails, file name etc.
    }
    contents = mainBox
    
    
    mainBox.requestFocus()
    listenTo(mainBox.keys)
    
    reactions += {
      
      case event.KeyPressed(src, key, _, _) => {
        if (controls.keys.toSeq.contains(key.id) && !keysPressed(key.id)) {
          keysPressed(key.id) = true
          if (key.id == 32) {    //Space
            if (!hasStarted) start()
            else if (!hasEnded) end()
          }
          else if (hasStarted && !hasEnded) {  //Other key
            controls(key.id)()
          }
        }
      }
      
      case event.KeyReleased(src, key, _, _) => {
        if (controls.keys.toSeq.contains(key.id)) {
          keysPressed(key.id) = false
        }
        if (controls.keys.toSeq.contains(1000 + key.id)) {
          controls(1000 + key.id)()
        }
      }
      
    }
    
    val timer: javax.swing.Timer = new javax.swing.Timer(1000 / fps, Swing.ActionListener(e => {
      if (hasStarted && !hasEnded) frame()
      else if (hasEnded) writeAndClose()
    }))
    
    def start() = {
      println("STARTS.")
      hasStarted = true
      startTime = System.currentTimeMillis()
      timer.start()
      soundtrack.play(0)      // <<<<
    }
    
    def end() = {
      hasEnded = true
      soundtrack.pause()
    }
    
    def writeAndClose() = {
      timer.stop()
      if (game.rails.find(!_.isEmpty).isDefined) {
        
        def write(difficulty: Char) = {
          Loader.saveTrack(game, mp3trackname, difficulty.toString, Data.username)    // <<< ASK FOR DIFFICULTY
          println("WRITING AND CLOSING.")
          closeTrack
        }
        
        val difficultyDialog = new GameDialog("Difficulty", "Please enter the difficulty level you think fits this track (can be altered in the files afterwards).",
            ("Easy", () => write('e')),
            ("Medium", () => write('m')),
            ("Hard", () => write('h')),
            ("Extreme", () => write('x'))
        )
        difficultyDialog.open()
      } else {
        println("EMPTY GAME. CLOSING WITHOUT SAVING.")
        closeTrack
      }
    }
    
    def closeTrack = {
      RecorderListPanel.recordingTrack = None
      timer.stop()
      dispose
    }
    
    def frame() = {
      timeNow = System.currentTimeMillis() - startTime
    }
    
  }
  
  /*def start() = {
    hasStarted = true
    startTime = System.currentTimeMillis()
    timer.start()
  }
  
  def end() = {
    hasEnded = false
  }
  
  def writeAndClose() = {
    Loader.saveTrack(game, filename)
    System.exit(1)
  }
  
  def frame() = {
    timeNow = System.currentTimeMillis() - startTime
  }*/
  
}

class RecorderError(msg: String) extends Error(msg)