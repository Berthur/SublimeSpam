package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import java.awt.event.ActionListener

import sublimeSpam._
import sublimeSpam.io.Loader

import MainMenu._

object RecorderListPanel extends BorderPanel {
  
  val buttonWidth = 500
  val buttonHeight = 80
  
  var trackList = Loader.getmp3List(Data.username)
  
  var recordingTrack: Option[Recorder] = None
  
  minimumSize   = new Dimension(windowWidth, windowHeight)
  preferredSize = new Dimension(windowWidth, windowHeight)
  maximumSize   = new Dimension(windowWidth, windowHeight)
  
  this.background = new Color(35, 20, 53)
  
  /*add(new Panel {
    minimumSize   = new Dimension(50, windowHeight)
    preferredSize = new Dimension(50, windowHeight)
    maximumSize   = new Dimension(50, windowHeight)
    opaque = false
  }, BorderPanel.Position.West)*/
  
  add(new FlowPanel {
    minimumSize   = new Dimension(windowWidth, 100)
    preferredSize = new Dimension(windowWidth, 100)
    maximumSize   = new Dimension(windowWidth, 100)
    opaque = false
    contents += new Button("Back") {
      action = new Action(text) {
        def apply() = {
          updateContents(mainFrame.mainPanel)
        }
      }
    }
  }, BorderPanel.Position.North)
  
  /*add(new Panel {
    minimumSize   = new Dimension(50, windowHeight)
    preferredSize = new Dimension(50, windowHeight)
    maximumSize   = new Dimension(50, windowHeight)
    opaque = false
  }, BorderPanel.Position.East)*/
  
  val boxPanel = new BoxPanel(Orientation.Vertical) {
    background = Color.BLACK
    //this.opaque = false
  }
  
  def load() = {
    boxPanel.contents.clear()
    trackList = Loader.getmp3List(Data.username)
    for (trackStrings <- trackList) {
      boxPanel.contents += new RecorderTrackButton(trackStrings(0), trackStrings(1))
    }
  }
  
  load()
  
  val scrollPane = new ScrollPane {
    opaque = false
    minimumSize   = new Dimension(windowWidth - 100, windowHeight - 100)
    preferredSize = new Dimension(windowWidth - 100, windowHeight - 100)
    maximumSize   = new Dimension(windowWidth - 100, windowHeight - 100)
    val paneBorder = EmptyBorder(20)
    border = paneBorder
    this.horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
    this.verticalScrollBar.unitIncrement = 16
  }
  
  scrollPane.contents = boxPanel
   
  add(scrollPane, BorderPanel.Position.Center)
  
}

class RecorderTrackButton(trackName: String, artist: String) extends Button {
  val w = SongListPanel.buttonWidth
  val h = SongListPanel.buttonHeight
  minimumSize   = new Dimension(w, h)
  preferredSize = new Dimension(w, h)
  maximumSize   = new Dimension(w, h)
  
  override def paintComponent(g: Graphics2D) {
    val originalTransform = g.getTransform
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(new Color(10, 64, 101))
    g.fillRect(0, 0, w, h)
    g.setColor(new Color(178, 151, 16))
    g.setFont(new java.awt.Font("High tower text", 0, 28))
    g.drawString(trackName, 80, 40)
    g.setFont(new java.awt.Font("High tower text", 0, 16))
    g.drawString(artist, 80, 60)
  }
  
  action = new Action(trackName) {
    def apply() = {
      /*if (!SongListPanel.playingTrack.isDefined) {
        SongListPanel.playingTrack = Some(new Track(author+"/"+trackName+";"+artist+";"+difficulty+";"+id, author+"/"+trackName+";"+artist, trackName + " - " + artist))
        SongListPanel.playingTrack.get.startup(Array[String]())      // <<< Expand on this (save score, allow closing only the track window etc.
      }*/
      if (!RecorderListPanel.recordingTrack.isDefined) {
        RecorderListPanel.recordingTrack = Some(new Recorder(trackName + ";" + artist + ".mp3"))
        RecorderListPanel.recordingTrack.get.startup(Array[String]())
      }
    }
  }
  
  
}
