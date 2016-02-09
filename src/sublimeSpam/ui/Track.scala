package sublimeSpam.ui

import scala.swing._
import java.awt.{Color, BasicStroke, Graphics2D, RenderingHints}
import java.awt.event.ActionListener
import scala.io.Source
import java.io._

import sublimeSpam._
import sublimeSpam.io.Loader

class Track(trackPath: String, soundtrackPath: String, trackDesc: String) extends SimpleSwingApplication {
  println(trackPath + ", " + soundtrackPath + ", " + trackDesc)
  //val trackName = "DarudeSandstorm"
  /*val trackName = {
    val file = new File("current_track.txt")
    if (!file.exists()) throw new LoadTrackException("Requested track file does not exist.")
    val source = Source.fromFile(file)
    val result = source.getLines.next()
    println("Now playing: " + result)
    result
  }*/
  
  val railNo = 3
  val game = Loader.loadTrack(trackPath)
  val controls: Map[Int, () => Unit] = Map(
      27 -> (() => {keysPressed(27) = false; mainFrame.esc()}),
      32 -> (() => {if (mainFrame.timer.isRunning) mainFrame.pause() else mainFrame.continue()}),
      71 -> (() => hitRail(0)),
      72 -> (() => hitRail(1)),
      74 -> (() => hitRail(2)),
      1071 -> (() => releaseRail(0)),
      1072 -> (() => releaseRail(1)),
      1074 -> (() => releaseRail(2))
      )
  val keysPressed = Array.ofDim[Boolean](200)    // <<< amount of key ids?!
  val fps = 100
  val offset = 0    //The amount of ms the soundtrack is postponed (positive?)
  val timeBufferBeforeClosing = 3000
  val closeTime = game.duration + timeBufferBeforeClosing
  var startTime = 0L
  var lastPause = 0L
  
  val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()    // Test
  
  var entityVisibilityTime = -1
  var arenaWidth = -1
  var arenaHeight = -1
  var headerHeight = -1
  var ballRadius = -1
  var ballRoundness = -1
  
  def loadConfigValues() = {
    entityVisibilityTime = Data.entityVisibilityTime
    arenaWidth = Data.arenaWidth
    arenaHeight = Data.arenaHeight
    headerHeight = Data.headerHeight
    ballRadius = Data.ballRadius
    ballRoundness = Data.ballRoundness
  }
  
  loadConfigValues
  
  val soundtrack = new Soundtrack(soundtrackPath, offset)
  
  def hitRail(railNo: Int) = game.rails(railNo).hit(game.time)
  
  def releaseRail(railNo: Int) = game.rails(railNo).release(game.time)
  
  val mainFrame = new MainFrame {
    
    def start() = {
      startTime = System.currentTimeMillis
      pause()
    }
    
    def pause() = {
      timer.stop()
      lastPause = System.currentTimeMillis
      soundtrack.pause()
      /*val before = System.nanoTime
      lastPause = System.currentTimeMillis
      timer.stop()
      val between = System.nanoTime
      soundtrack.pause()
      val after = System.nanoTime
      val difference = (after - between) - (between - before)
      println(difference.toDouble / 1000000000)
      lastPause = lastPause + (difference / 1000000)*/
    }
    
    def continue() = {
      startTime += (System.currentTimeMillis - lastPause)
      frame()
      soundtrack.play(game.time - offset)
      timer.start()
      
      
      
     /* val before = System.nanoTime
      startTime += (System.currentTimeMillis - lastPause)
      timer.start()
      val between = System.nanoTime
      soundtrack.play()
      val after = System.nanoTime
      val difference = (after - between) - (between - before)
      println(difference.toDouble / 1000000000)*/
    }
    
    val escDialog = new GameDialog("Exit", "Are you sure you want to exit the track?",
          ("Yes", () => {
            closeTrack
          }),
          ("Cancel", () => {
            Unit
          })
          )
    
    def esc() = {
      pause()
      escDialog.open()
    }
    
    val arena = new Panel {
      
      minimumSize   = new Dimension(arenaWidth, arenaHeight)
      preferredSize = new Dimension(arenaWidth, arenaHeight)
      maximumSize   = new Dimension(arenaWidth, arenaHeight)
      
      override def paintComponent(g: Graphics2D) = {
        
        //Background:
        g.setColor(new Color(25, 25, 50))
        g.fillRect(0, 0, arenaWidth, arenaHeight)
        
        //Goal area:
        val goalLineY = arenaHeight - ballRadius
        for (i <- 0 until railNo) {
          if (game.rails(i).missCountdown > 0) {
            game.rails(i).missCountdown -= 1
            g.setColor(new Color(160, 0, 0))
          } else {
            g.setColor(new Color(0, 131, 83))
          }
          g.fillRect((arenaWidth / railNo) * i, goalLineY - ballRadius, (arenaWidth / railNo) * (i + 1), goalLineY + ballRadius)
        }
        
        //Goal line:
        g.setColor(new Color(0, 80, 50))
        g.setStroke(new BasicStroke(4))
        g.drawLine(0, goalLineY - 3, arenaWidth, goalLineY - 3)
        g.setColor(new Color(0, 20, 0))
        g.setStroke(new BasicStroke(2))
        g.drawLine(0, goalLineY, arenaWidth, goalLineY)
        g.setColor(new Color(0, 80, 50))
        g.setStroke(new BasicStroke(4))
        g.drawLine(0, goalLineY + 3, arenaWidth, goalLineY + 3)
        
        //Rail borders:
        g.setColor(Color.BLACK)
        g.setStroke(new BasicStroke(6))
        for (lineNr <- 1 until game.rails.length) {
          val lineSpace = arenaWidth / game.rails.length
          g.drawLine(lineNr * lineSpace, 0, lineNr * lineSpace, arenaHeight)
        }
        g.setStroke(new BasicStroke(1))
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        def drawQuick(timeAhead: Long, rail: Int) = {    //rail 0, 1, 2, ...
          require (timeAhead <= entityVisibilityTime)
          val centerX = ((arenaWidth / game.rails.length) * (rail + 0.5)).toInt
          val targetY = goalLineY
          val distanceY = ((timeAhead.toDouble / entityVisibilityTime) * targetY).toInt
          val centerY = targetY - distanceY
          
          //val drawX = centerX - ballRadius
          //val drawY = centerY - ballRadius
          
          g.setColor( rail match {case 0 => new Color(7, 118, 108); case 1 => new Color(172, 172, 0); case 2 => new Color(28, 111, 15) } )
          //g.fillRoundRect(drawX, drawY, 2 * ballRadius, 2 * ballRadius, ballRoundnessPercent, ballRoundnessPercent)
          var percent = 100
          g.fillRoundRect(centerX - ballRadius * percent/100, centerY - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundness, ballRoundness)
          g.setColor( rail match {case 0 => new Color(18, 158, 156); case 1 => new Color(188, 188, 0); case 2 => new Color(42, 172, 22) } )
          //g.fillRoundRect(drawX + 5, drawY + 5, 2 * ballRadius - 10, 2 * ballRadius - 10, ballRoundnessPercent, ballRoundnessPercent)
          percent = 90
          g.fillRoundRect(centerX - ballRadius * percent/100, centerY - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundness, ballRoundness)
          g.setColor( rail match {case 0 => new Color(27, 198, 175); case 1 => new Color(218, 218, 0); case 2 => new Color(61, 240, 33) } )
          //g.fillRoundRect(drawX + 15, drawY + 15, 2 * ballRadius - 30, 2 * ballRadius - 30, ballRoundnessPercent, ballRoundnessPercent)
          percent = 70
          g.fillRoundRect(centerX - ballRadius * percent/100, centerY - ballRadius * percent/100, 2 * ballRadius * percent/100, 2 * ballRadius * percent/100, ballRoundness, ballRoundness)
        }
        
        def drawTail(timeAhead: Long, duration: Long, rail: Int, relativeOff: Double) = {
          
          val hypotheticalStartY = ((timeAhead.toDouble / entityVisibilityTime) * goalLineY).toInt
          val hypotheticalEndY = ((timeAhead + duration).toDouble / entityVisibilityTime * goalLineY).toInt
          
          val startY = Math.min(goalLineY + (relativeOff * ballRadius).toInt, goalLineY - hypotheticalStartY)
          val endY = Math.max(0 - 2*ballRadius, goalLineY - hypotheticalEndY)
          
          val centerX = ((arenaWidth / game.rails.length) * (rail + 0.5)).toInt
          
          g.setColor(new Color(198, 198, 198))
          g.fillRoundRect(centerX - 35, endY, 70, startY - endY, 50, 50)
          g.setColor(new Color(181, 181, 181))
          g.fillRoundRect(centerX - 25, endY + 10, 50, startY - endY - 10, 50, 50)
          g.setColor(new Color(168, 168, 168))
          g.fillRoundRect(centerX - 15, endY + 20, 30, startY - endY - 20, 50, 50)
          
          if (timeAhead <= entityVisibilityTime && timeAhead > 0) {
            drawQuick(timeAhead, rail)
          }
        }
        
        for (railNo <- 0 until game.rails.length) {
          val rail = game.rails(railNo)
          val entities = rail.getEntities(game.time + entityVisibilityTime)
          for (entity <- entities) {
            val timeAhead = entity.time - game.time
            entity.typeChar match {
              
              case 'q' => {
                if (timeAhead > entityVisibilityTime) {
                  println("ERROR! (timeAhead > entityVisibilityTime)")
                } else if (timeAhead + game.threshold < 0) {
                    println("ERROR! (timeAhead + game.threshold < 0)")
                } else {
                  drawQuick(timeAhead, railNo)
                }
              }
            
              case 't' => {
                if (timeAhead > entityVisibilityTime) {
                  println("ERROR! (timeAhead > entityVisibilityTime)")
                } else {
                  drawTail(timeAhead, entity.asInstanceOf[Tail].duration, railNo, game.rails(railNo).lastTailOff.toDouble / game.threshold)
                }
              }
            }  
          }
        }
        
        def drawAccuracyMeter() = {
          //Background:
          val mx = arenaWidth - 60
          val mdx = 40
          val my = 40
          val mdy = 140
          val mColor = if (!game.lastHitOffset.isDefined) Color.RED else {
            val relativeOffset = Math.abs(game.lastHitOffset.get.toDouble / game.threshold)
            if (relativeOffset < 0.1) new Color(0, 255, 51)
            else if (relativeOffset < 0.2) new Color(108, 191, 0)
            else if (relativeOffset < 0.4) new Color(140, 174, 4)
            else if (relativeOffset < 0.7) new Color(216, 240, 0)
            else new Color(230, 184, 0)
          }
          g.setColor(mColor)
          g.fillRect(mx, my, mdx, mdy)
          //Accuracy indicator:
          if (game.lastHitOffset.isDefined) {
            val hity = mdy / 2 + ((game.lastHitOffset.get.toDouble / game.threshold) * (mdy / 2)).toInt
            g.setColor(Color.BLACK)
            g.fillRoundRect(mx - 5, my + hity - 5, mdx + 10, 10, 5, 5)
          }
          //Middle line:
          g.setColor(Color.WHITE)
          g.drawLine(mx, my + mdy / 2, mx + mdx, my + mdy / 2)
        }
        
        drawAccuracyMeter()
        
        def drawDurationMeter() = {
          val progress = game.time.toDouble / game.duration
          val x = (arenaWidth * progress).toInt
          g.setColor(Color.WHITE)
          g.fillRect(0, 0, x, 2)
        }
        
        drawDurationMeter()
        
      }
    }
    
    val percentageLabel = new Label {
      this.font = new Font("Times New Roman", java.awt.Font.BOLD, 15)
      this.foreground = new Color(255, 205, 0)
      def update() = text = Math.round(game.hits.toDouble * 100 / Math.max(1, game.passed)) + "%"
      update()
    }
    
    val scoreLabel = new Label {
      minimumSize   = new Dimension(arenaWidth / 3, headerHeight)
      preferredSize = new Dimension(arenaWidth / 3, headerHeight)
      maximumSize   = new Dimension(arenaWidth / 3, headerHeight)
      this.font = new Font("Times New Roman", java.awt.Font.BOLD, 30)
      this.foreground = new Color(255, 205, 0)
      def update() = text = Data.formatLargeNumber(game.score)
      update()
    }
    
    val multiplierLabel = new Label {
      this.font = new Font("Times New Roman", java.awt.Font.BOLD, 15)
      this.foreground = new Color(255, 205, 0)
      def update() = text = "x" + game.multiplier
      update()
    }
    
    val header = new FlowPanel {
      minimumSize   = new Dimension(arenaWidth, headerHeight)
      preferredSize = new Dimension(arenaWidth, headerHeight)
      maximumSize   = new Dimension(arenaWidth, headerHeight)
      
      override def paintComponent(g: Graphics2D) = {
        g.setColor(new Color(0, 80, 60))
        g.fillRect(0, 0, arenaWidth, headerHeight)
      }
      
      contents += percentageLabel
      contents += scoreLabel
      contents += multiplierLabel
      
    }
    
    title = "Sublime Spam   /   " + trackDesc
    resizable = false
    this.location = new Point((screenSize.getWidth.toInt - arenaWidth) / 2, 0)
    minimumSize   = new Dimension(arenaWidth, arenaHeight + headerHeight + 29)
    preferredSize = new Dimension(arenaWidth, arenaHeight + headerHeight + 29)
    maximumSize   = new Dimension(arenaWidth, arenaHeight + headerHeight + 29)
      
    this.contents = new BoxPanel(Orientation.Vertical) {
      this.contents += header
      this.contents += arena
    }
    
    arena.requestFocus()
    listenTo(arena.keys)
    
    reactions += {
      
      case event.KeyPressed(src, key, _, _) => {
        if (controls.keys.toSeq.contains(key.id) && !keysPressed(key.id)) {
          keysPressed(key.id) = true
          controls(key.id)()
        }
      }
      
      case event.KeyReleased(src, key, _, _) => {
        keysPressed(key.id) = false
        if (controls.keys.toSeq.contains(1000 + key.id)) {
          controls(1000 + key.id)()
        }
      }
    }
    
    def closeTrack = {
      
      SongListPanel.playingTrack = None
      pause()
      val previousBestOption: Option[(String, (Int, String, String))] = if (Data.highScores.contains(trackPath)) Some((trackPath, Data.highScores(trackPath))) else None
      val resultPanel = new ResultPanel(trackPath, trackDesc, game, previousBestOption)
      Data.registerScore(game.score, trackPath, Data.username)
      MainMenu.updateContents(resultPanel)
      dispose
    }
    
    def frame() = {
      val timeNow = System.currentTimeMillis
      game.time = timeNow - startTime
      game.frame()
      percentageLabel.update()
      scoreLabel.update()
      multiplierLabel.update()
      arena.repaint()
      if (game.time > closeTime) {closeTrack}
    }
    
    
    val timer: javax.swing.Timer = new javax.swing.Timer(1000 / fps, Swing.ActionListener(e => {
      frame()
    }))
    
    
    ///
    start()
    ///
  
  }
  
  
  
  
  def top = mainFrame


}