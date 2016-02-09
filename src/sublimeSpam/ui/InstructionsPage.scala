package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.Color

import sublimeSpam.io.Loader
import MainMenu._

object InstructionsPage extends BorderPanel {
  
  minimumSize   = new Dimension(windowWidth, windowHeight)
  preferredSize = new Dimension(windowWidth, windowHeight)
  maximumSize   = new Dimension(windowWidth, windowHeight)
  border = EmptyBorder(10)
  background = new Color(35, 20, 53)
  
  add(new FlowPanel {
    minimumSize   = new Dimension(windowWidth, 80)
    preferredSize = new Dimension(windowWidth, 80)
    maximumSize   = new Dimension(windowWidth, 80)
    opaque = false
    border = EmptyBorder(20)
    contents += new Button("Back") {
      action = new Action(text) {
        def apply() = {
          updateContents(mainFrame.mainPanel)
        }
      }
    }
  }, BorderPanel.Position.North)
  
  val textField = new TextArea {
        editable = false
        opaque = false
        foreground = Color.WHITE
        border = EmptyBorder(20)
        font = new java.awt.Font("High tower text", 0, 18)
        lineWrap = true
        wordWrap = true
      }

  val scrollPane = new ScrollPane {
    minimumSize   = new Dimension(windowWidth - 50, windowHeight - 150)
    preferredSize = new Dimension(windowWidth - 50, windowHeight - 150)
    maximumSize   = new Dimension(windowWidth - 50, windowHeight - 150)
    this.horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
    this.verticalScrollBar.unitIncrement = 16
    this.border = Swing.EmptyBorder
    
    contents = new BoxPanel(Orientation.Vertical) {
      background = new Color(35, 20, 53)
      contents += textField
    }
  }
  
  add(scrollPane, BorderPanel.Position.South)
  
  scrollPane.verticalScrollBar.value = 0
  
  def addText(s: String) = {
    textField.text += s
  }
  
  addText(Loader.loadFileToString("resources/Instructions.txt"))
  
}