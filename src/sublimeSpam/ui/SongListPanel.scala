package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import java.awt.event.ActionListener

import sublimeSpam._
import sublimeSpam.io.Loader

import MainMenu._

object SongListPanel extends BorderPanel {
  
  val buttonWidth = windowWidth - 60
  val buttonHeight = 80
  
  var trackList = Loader.getTrackList
  
  var playingTrack: Option[Track] = None
  
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
    trackList = Loader.getTrackList
    for (trackStrings <- trackList) {
      boxPanel.contents += new TrackButton(trackStrings(0), trackStrings(1), trackStrings(2), trackStrings(3), trackStrings(4))
    }
  }
  
  load
  
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

class TrackButton(trackName: String, artist: String, difficulty: String, id: String, author: String) extends Button {
  val w = SongListPanel.buttonWidth
  val h = SongListPanel.buttonHeight
  val trackPath = author + "/" + trackName + ";" + artist + ";" + difficulty + ";" + id
  minimumSize   = new Dimension(w, h)
  preferredSize = new Dimension(w, h)
  maximumSize   = new Dimension(w, h)
  
  val difficultyString = difficulty match {
    case "e" => "easy"
    case "m" => "medium"
    case "h" => "hard"
    case "x" => "extreme"
    case "t" => "test"
    case _ => "unknown"
  }
  
  val difficultyColor = difficulty match {
    case "e" => new Color(43, 182, 90)
    case "m" => new Color(216, 216, 40)
    case "h" => new Color(255, 148, 25)
    case "x" => new Color(223, 43, 43)
    case "t" => Color.WHITE
    case _ => Color.WHITE
  }
  
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
    g.setFont(new java.awt.Font("High tower text", 0, 20))
    g.drawString("by " + author, 400, 20)
    if (Data.highScores.contains(trackPath)) {
      g.setColor(Color.CYAN)
      g.drawString(Data.formatLargeNumber(Data.highScores(trackPath)._1), 400, 60)
    }
    g.setColor(difficultyColor)
    g.rotate(Math.PI / 4)
    g.setFont(new java.awt.Font("High tower text", 0, 18))
    g.drawString(difficultyString, 25, 5)
    g.setTransform(originalTransform)
  }
  
  action = new Action(trackName) {
    def apply() = {
      if (!SongListPanel.playingTrack.isDefined) {
        SongListPanel.playingTrack = Some(new Track(author+"/"+trackName+";"+artist+";"+difficulty+";"+id, author+"/"+trackName+";"+artist + ".mp3", trackName + " - " + artist))
        SongListPanel.playingTrack.get.startup(Array[String]())      // <<< Expand on this (save score, allow closing only the track window etc.
      }
    }
  }
  
  
}
