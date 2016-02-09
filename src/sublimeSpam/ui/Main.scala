package sublimeSpam.ui

import sublimeSpam._
import sublimeSpam.io.Confuser._
import sublimeSpam.io.Loader

object Main {
  
  def main(args: Array[String]) = {
    prepareUsername()
  }
  
  val nameDialog: GameFieldDialog = new GameFieldDialog("Username", "Please enter your username.", ("Ok", String => {
    if (String.find(_.isLetterOrDigit).isDefined && String.length >= 3 && String.length < 20 && !String.contains(";")) {
      Data.username = String
      Loader.saveToConfig("username", String)
      println("USERNAME OK")
      resume()
    } else {
      println("USERNAME NOT OK")
      askUsernameAgain()
    }

    }))
  
  def prepareUsername() = {
      if (Data.username.trim.isEmpty) {
        println("Opening name dialog")
        nameDialog.open()
      } else {
        resume()
      }
  }
  
  def askUsernameAgain() = {
    nameDialog.empty()
    val waitThread = new Thread(new Runnable {
      def run() {
        Thread.sleep(1000)
        nameDialog.open()
      }
    })
    waitThread.start()
  }
  
  def resume() = {
    MainMenu.startup(Array[String]())
  }
  
}