package sublimeSpam.ui

import scala.swing._
import Swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import java.awt.event.ActionListener
import util.Random

import sublimeSpam._
import sublimeSpam.io.Loader

object MainMenu extends SimpleSwingApplication {
  
  
  val rand = new Random
  
  val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()    // Test
  
  val windowWidth = 600
  val windowHeight = 800
  val buttonWidth = 150
  val buttonHeight = 75
  val ballRadius = 50
  val ballRoundnessPercent = 75
  val decorQuickAmount = 12
  
  var hasStarted = false
  var colorAngle = rand.nextInt(360).toFloat
  
  /// Components to load in advance: (?)
  InstructionsPage
  ///
  
  val decorQuicks = collection.mutable.Buffer[DecorativeQuick]()
  for (i <- 0 until 12) {
    decorQuicks += new DecorativeQuick(rand, windowWidth, windowHeight)
  }
  "hello there".toStream.map(_.toInt)
  val nameDialog: GameFieldDialog = new GameFieldDialog("Username", "Please enter your username.", ("Ok", String => {
    Data.username = String
    Loader.saveToConfig("username", String)
    timer.start()
    }))
  
  val mainFrame = new MainFrame {
    title = "Sublime Spam"
    resizable = false
    this.location = new Point((screenSize.getWidth.toInt - windowWidth) / 2, 20)
    
    val nameLabel = new Label {
      minimumSize   = new Dimension(windowWidth, windowHeight - 600)
      preferredSize = new Dimension(windowWidth, windowHeight - 600)
      maximumSize   = new Dimension(windowWidth, windowHeight - 600)
      opaque = false
      foreground = Color.getHSBColor((359 - colorAngle).toFloat / 359, 0.60F, 0.20F)
      text = "<html><center>A Game By<br>Berthur  /  Jon G.</center></html>"
    }
    
    val mainPanel: BorderPanel = new BorderPanel {
      minimumSize   = new Dimension(windowWidth, windowHeight)
      preferredSize = new Dimension(windowWidth, windowHeight)
      maximumSize   = new Dimension(windowWidth, windowHeight)
      override def paintComponent(g: Graphics2D) = {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setColor(java.awt.Color.getHSBColor(colorAngle.toFloat / 359, 0.60F, 0.20F))
        g.fillRect(0, 0, windowWidth, windowHeight)
        decorQuicks.foreach( decorQuick => {
          g.setColor( decorQuick.railColor match {case 0 => new Color(7, 118, 108, 48); case 1 => new Color(172, 172, 0, 48); case 2 => new Color(28, 111, 15, 48) } )
          var percent = 100
          g.fillRoundRect(decorQuick.x - ballRadius * percent/100, decorQuick.y - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundnessPercent, ballRoundnessPercent)
          g.setColor( decorQuick.railColor match {case 0 => new Color(18, 158, 156, 24); case 1 => new Color(188, 188, 0, 24); case 2 => new Color(42, 172, 22, 24) } )
          percent = 90
          g.fillRoundRect(decorQuick.x - ballRadius * percent/100, decorQuick.y - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundnessPercent, ballRoundnessPercent)
          g.setColor( decorQuick.railColor match {case 0 => new Color(27, 198, 175, 12); case 1 => new Color(218, 218, 0, 12); case 2 => new Color(61, 240, 33, 12) } )
          percent = 70
          g.fillRoundRect(decorQuick.x - ballRadius * percent/100, decorQuick.y - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundnessPercent, ballRoundnessPercent)
        })
      }
      
      this.add(new Panel {
        minimumSize   = new Dimension(windowWidth, 100)
        preferredSize = new Dimension(windowWidth, 100)
        maximumSize   = new Dimension(windowWidth, 100)
        opaque = false
      }, BorderPanel.Position.North)
      
      this.add(new Panel {
        minimumSize   = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        preferredSize = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        maximumSize   = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        opaque = false
      }, BorderPanel.Position.West)
      
      this.add(new Panel {
        minimumSize   = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        preferredSize = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        maximumSize   = new Dimension((windowWidth - buttonWidth) / 2, windowHeight - 200)
        opaque = false
      }, BorderPanel.Position.East)
      
      this.add(nameLabel, BorderPanel.Position.South)
      
      this.add(new BoxPanel(Orientation.Vertical) {
        opaque = false
        
        contents += new Button {
          minimumSize   = new Dimension(buttonWidth, buttonHeight)
          preferredSize = new Dimension(buttonWidth, buttonHeight)
          maximumSize   = new Dimension(buttonWidth, buttonHeight)
          text = "Play track"        //To contain a list of songs, with their info, including highscores etc.
          action = new Action(text) {
            def apply() = {
              updateContents(SongListPanel)
            }
          }
        }
        
        contents += VStrut(5)
        
        contents += new Button {
          minimumSize   = new Dimension(buttonWidth, buttonHeight)
          preferredSize = new Dimension(buttonWidth, buttonHeight)
          maximumSize   = new Dimension(buttonWidth, buttonHeight)
          text = "Record track"
          action = new Action(text) {
            def apply() = {
              updateContents(RecorderListPanel)
            }
          }
        }
        
        contents += VStrut(5)
        
        contents += new Button {
          minimumSize   = new Dimension(buttonWidth, buttonHeight)
          preferredSize = new Dimension(buttonWidth, buttonHeight)
          maximumSize   = new Dimension(buttonWidth, buttonHeight)
          text = "Settings"
          action = new Action(text) {
            def apply() = {
              updateContents(Settings)
            }
          }
        }
        
        contents += VStrut(5)
        
        contents += new Button {
          minimumSize   = new Dimension(buttonWidth, buttonHeight)
          preferredSize = new Dimension(buttonWidth, buttonHeight)
          maximumSize   = new Dimension(buttonWidth, buttonHeight)
          text = "Instructions"
          action = new Action(text) {
            def apply() = {
              updateContents(InstructionsPage)
              InstructionsPage.scrollPane.verticalScrollBar.value = 0
            }
          }
        }
        
         contents += VStrut(5)
        
        contents += new Button {
          minimumSize   = new Dimension(buttonWidth, buttonHeight)
          preferredSize = new Dimension(buttonWidth, buttonHeight)
          maximumSize   = new Dimension(buttonWidth, buttonHeight)
          text = "Quit game"
          action = new Action(text) {
            def apply() = {
              System.exit(0)
            }
          }
        }
        
      }, BorderPanel.Position.Center)
      
      
    }
    
    
    
    
    ///
    contents = mainPanel
    //timer.start()
    ///
  }
  
  def top = mainFrame
  
  def updateContents(c: Component): Unit = {
    if (c == mainFrame.mainPanel) {
      timer.start()
    } else {
      timer.stop()
    }
    if (c == SongListPanel) SongListPanel.load()
    else if (c == RecorderListPanel) RecorderListPanel.load()
    mainFrame.contents = c
    mainFrame.repaint
  }
  
  /**
   * Finds out if all that is required to launch (such as a valid username)
   * is as it should be, and fixes it.
   */
  def makePreparations = {
    if (!nameDialog.visible) {
      if (Data.username.trim.isEmpty) {
        timer.stop()
        nameDialog.open()
      } else {
        hasStarted = true
        timer.start()
      }
    }
  }
  
  val timer: javax.swing.Timer = new javax.swing.Timer(20, Swing.ActionListener(e => {
    if (!hasStarted) {
      makePreparations
    } else {
      colorAngle += 0.2F
      if (colorAngle >= 360) colorAngle = 0
      decorQuicks.foreach( decorQuick => {
        val result = decorQuick.updatePosition()
        if (!result) {
          decorQuicks -= decorQuick
          decorQuicks += new DecorativeQuick(rand, windowWidth, windowHeight)
        }
      })
      //mainFrame.nameLabel.foreground = Color.getHSBColor(((colorAngle + 180) % 360).toFloat / 359, 0.60F, 0.50F)
      val currentColor = Color.getHSBColor(colorAngle.toFloat / 359, 0.60F, 0.50F)
      //mainFrame.nameLabel.foreground = new Color(currentColor.getGreen, currentColor.getBlue, currentColor.getRed)
      mainFrame.nameLabel.foreground = new Color(255 - currentColor.getRed, 255 - currentColor.getGreen, 255 - currentColor.getBlue)
      mainFrame.mainPanel.repaint()
    }
  }))
  
  timer.start()
  
  /*if (Data.username.isEmpty) {
    //timer.stop()
    //val d = new GameFieldDialog("What's your name?", "What's your quest?", ("!!!", () =>println("What's your favourite colour?")))
    //d.open()
    timer.stop()
    val t = new GameDialog("Lol", "Double-lol", ("heh", () => println(":DDD")))
    t.open()
    
    println("!")
  } else {
    //timer.start()
  }*/

  
}
