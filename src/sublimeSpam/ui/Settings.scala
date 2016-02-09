package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.{Color, Font}

import sublimeSpam._
import MainMenu._


object Settings extends BorderPanel {
  minimumSize   = new Dimension(windowWidth, windowHeight)
  preferredSize = new Dimension(windowWidth, windowHeight)
  maximumSize   = new Dimension(windowWidth, windowHeight)
  border = EmptyBorder(10)
  background = new Color(35, 20, 53)
  
  //Controls
  
  private class WhiteLabel(t: String, header: Boolean) extends Label {
    foreground = Color.WHITE
    font = if (header) new Font("High tower text", Font.BOLD | Font.ITALIC, 18) else new Font("High tower text", 0, 14)
    text = t
  }
  
  add(new FlowPanel {
    minimumSize   = new Dimension(windowWidth, 60)
    preferredSize = new Dimension(windowWidth, 60)
    maximumSize   = new Dimension(windowWidth, 60)
    opaque = false
    //border = EmptyBorder(20)
  }, BorderPanel.Position.North)
  
  add(new FlowPanel {
    minimumSize   = new Dimension(windowWidth, 60)
    preferredSize = new Dimension(windowWidth, 60)
    maximumSize   = new Dimension(windowWidth, 60)
    opaque = false
    border = EmptyBorder(20)
    contents += new Button("Cancel") {
      action = new Action(text) {
        def apply() = {
          loadSettings()
          updateContents(mainFrame.mainPanel)
        }
      }
    }
    contents += new Button("Reset") {
      action = new Action(text) {
        def apply() = {
          loadSettings()
        }
      }
    }
    contents += new Button("Apply") {
      action = new Action(text) {
        def apply() = {
          saveSettings()
          updateContents(mainFrame.mainPanel)
        }
      }
    }
  }, BorderPanel.Position.South)
  
  
  val settingPanel = new BoxPanel(Orientation.Vertical) {
    opaque = false
    
    val usernameField = new TextField {
      text = Data.username
      font = new Font("High tower text", 0, 18)
      maximumSize   = new Dimension(windowWidth / 2, 30)
      preferredSize = new Dimension(windowWidth / 2, 30)
      minimumSize   = new Dimension(windowWidth / 2, 30)
    }
    
    val heightSlider = new Slider {
      value = Data.windowHeightValue
      opaque = false
      maximumSize   = new Dimension(windowWidth / 2, 75)
      preferredSize = new Dimension(windowWidth / 2, 75)
      minimumSize   = new Dimension(windowWidth / 2, 75)
      this.min = 0
      this.max = 4
      this.majorTickSpacing = 1
      this.paintTicks = true
      this.paintLabels = true
      
      this.labels = Map(0 -> new WhiteLabel("Very small", false), 1 -> new WhiteLabel("Small", false), 2 -> new WhiteLabel("Medium", false), 3 -> new WhiteLabel("<html><center>Large<br>(default)", false), 4 -> new WhiteLabel("Very large", false))
    }
    
    val speedSlider = new Slider {
      value = Data.speedValue
      opaque = false
      maximumSize   = new Dimension(windowWidth / 2, 75)
      preferredSize = new Dimension(windowWidth / 2, 75)
      minimumSize   = new Dimension(windowWidth / 2, 75)
      this.min = 0
      this.max = 4
      this.majorTickSpacing = 1
      this.paintTicks = true
      this.paintLabels = true
      
      this.labels = Map(0 -> new WhiteLabel("Very slow", false), 1 -> new WhiteLabel("Slow", false), 2 -> new WhiteLabel("Medium", false), 3 -> new WhiteLabel("<html><center>Fast<br>(default)", false), 4 -> new WhiteLabel("Very fast", false))
    }
    

    
    contents += new FlowPanel {opaque = false; contents += new WhiteLabel("Username:", true); contents += HStrut(30); contents += usernameField}
    contents += new FlowPanel {opaque = false; contents += new WhiteLabel("<html>Window height:<br>(in-game)", true); contents += HStrut(30); contents += heightSlider}
    contents += new FlowPanel {opaque = false; contents += new WhiteLabel("Falling speed:", true); contents += HStrut(30); contents += speedSlider}

  }
  
  /**
   * Loads the Data values so that the values in Settings matches those in Data.
   */
  def loadSettings() = {
    settingPanel.usernameField.text = Data.username
    settingPanel.heightSlider.value = Data.windowHeightValue
    settingPanel.speedSlider.value = Data.speedValue
  }
  
  /**
   * Saves the values from Settings to Data so that those in Data match those in Settings, and also updates the config file.
   */
  def saveSettings() = {
    Data.username = settingPanel.usernameField.text
    Data.windowHeightValue = settingPanel.heightSlider.value
    Data.speedValue = settingPanel.speedSlider.value
    Data.updateConfig()
  }
  
  add(settingPanel, BorderPanel.Position.Center)
  
  
}
