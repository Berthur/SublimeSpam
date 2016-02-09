package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.Color

import sublimeSpam._
import ui.MainMenu._

class ResultPanel(trackPath: String, trackDesc: String, game: Game, previousBestOption: Option[(String, (Int, String, String))]) extends BorderPanel {
  
  
  
  minimumSize   = new Dimension(windowWidth, windowHeight)
  preferredSize = new Dimension(windowWidth, windowHeight)
  maximumSize   = new Dimension(windowWidth, windowHeight)
  
  this.background = new Color(35, 20, 53)
  
  
  add(new FlowPanel {
    minimumSize   = new Dimension(windowWidth, 60)
    preferredSize = new Dimension(windowWidth, 60)
    maximumSize   = new Dimension(windowWidth, 60)
    opaque = false
    contents += new Button("Accept") {
      action = new Action(text) {
        def apply() = {
          updateContents(mainFrame.mainPanel)
        }
      }
    }
  }, BorderPanel.Position.North)
  
  add (new BoxPanel(Orientation.Vertical) {
    minimumSize   = new Dimension(windowWidth, windowHeight - 100)
    preferredSize = new Dimension(windowWidth, windowHeight - 100)
    maximumSize   = new Dimension(windowWidth, windowHeight - 100)
    border = EmptyBorder(20)
    opaque = false
    
    contents += new FlowPanel {  //Score
      opaque = false
      contents += new Label {
        val formattedScoreString = Data.formatLargeNumber(game.score)
        text = formattedScoreString
        font = new java.awt.Font("High tower text", 0, 72)
        foreground = Color.YELLOW
      }
    }
    contents += new FlowPanel {  //Percentage
      opaque = false
      contents += new Label {
        val formattedScoreString = Data.formatLargeNumber(game.score)
        text = Math.round(game.hits.toDouble * 100 / Math.max(1, game.entitiesLeft + game.passed)) + "%"
        font = new java.awt.Font("High tower text", 0, 42)
        foreground = Color.YELLOW
      }
    }
    contents += VStrut(40)
    contents += new FlowPanel {  //Trackname
      opaque = false
      contents += new Label {
        text = "<html><center>" + trackDesc.split("-")(0).trim //+ "<br>" + trackDesc.split("-")(1).trim
        font = new java.awt.Font("High tower text", 0, 36)
        foreground = Color.CYAN
      }
    }
    contents += new FlowPanel {  //Artist
      opaque = false
      contents += new Label {
        text = "<html><center>" + trackDesc.split("-")(1).trim
        font = new java.awt.Font("High tower text", 0, 24)
        foreground = Color.CYAN
      }
    }
    contents += VStrut(40)
    if (previousBestOption.isDefined) {
      val newRecord = game.score > previousBestOption.get._2._1
      if (newRecord) {
        contents += new FlowPanel {  //New record notif
          opaque = false
          contents += new Label {
            text = "NEW RECORD!"
            font = new java.awt.Font("High tower text", 0, 28)
            foreground = Color.GREEN
          }
        }
        contents += new FlowPanel {  //New record notif
          opaque = false
          contents += new Label {
            text = "Previous best:"
            font = new java.awt.Font("High tower text", 0, 24)
            foreground = Color.WHITE
          }
        }
      } else {
        contents += new FlowPanel {  //New record notif
          opaque = false
          contents += new Label {
            text = "Current record:"
            font = new java.awt.Font("High tower text", 0, 24)
            foreground = Color.WHITE
          }
        }
      }
      contents += new FlowPanel {  //New record notif
        opaque = false
        contents += new Label {
          text = Data.formatLargeNumber(previousBestOption.get._2._1) + " by " + previousBestOption.get._2._2 + " on " + previousBestOption.get._2._3
          font = new java.awt.Font("High tower text", 0, 16)
          foreground = Color.WHITE
        }
      }
    }
    contents += new FlowPanel {  //Track id
      opaque = false
      contents += new Label {
        text = "Track id: " + trackPath
        font = new java.awt.Font("High tower text", 0, 16)
        foreground = Color.GRAY
      }
    }
    
    
  }, BorderPanel.Position.Center)
  
}