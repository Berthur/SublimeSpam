package sublimeSpam.ui

import scala.swing._

/**
 * Represents a specific type of simple dialog window, containing a title, text and
 * an arbitrary number of buttons with their respective actions. The dialog is closed
 * if any button is pressed.
 * @param dialogTitle The title of the dialog.
 * @param msg: The text that is displayed in the dialog.
 * @param buttons: An arbitrary number of (button text, button action) pairs.
 */
class GameDialog(dialogTitle: String, msg: String, buttons: (String, () => Unit)*) extends Dialog {
  this.centerOnScreen()
  title = dialogTitle
  contents = new BorderPanel {
    layout(new BoxPanel(Orientation.Vertical) {
      border = Swing.EmptyBorder(5,5,5,5)
      contents += new Label(msg)
    }) = BorderPanel.Position.Center
    layout(new FlowPanel(FlowPanel.Alignment.Right)(
        buttons.map(button => Button(button._1) {button._2.apply(); close()}):_*
        )) = BorderPanel.Position.South
  }
}


/**
 * Represents a specific type of simple dialog window, containing a title, text, an
 * input text field and an arbitrary number of buttons with their respective actions.
 * The dialog is closed if any button is pressed.
 * The text field's input (as it is right then) is passed as the String parameter to
 * all button functions when called.
 * @param dialogTitle The title of the dialog.
 * @param msg: The text that is displayed in the dialog.
 * @param buttons: An arbitrary number of (button text, button action) pairs.
 */
class GameFieldDialog(dialogTitle: String, msg: String, buttons: (String, String => Unit)*) extends Dialog {
  this.centerOnScreen()
  private val textField = new TextField
  title = dialogTitle
  contents = new BorderPanel {
    layout(new BoxPanel(Orientation.Vertical) {
      border = Swing.EmptyBorder(5,5,5,5)
      contents += new Label(msg)
      contents += textField
    }) = BorderPanel.Position.Center
    layout(new FlowPanel(FlowPanel.Alignment.Right)(
        buttons.map(button => Button(button._1) {button._2.apply(input); close()}):_*
        )) = BorderPanel.Position.South
  }
  def input = textField.text
  def empty() = textField.text = ""
}
