package sublimeSpam.test

object GeneralTesting extends App {
  
  
  def test1() = {
    import sublimeSpam.io.Confuser._
    val str = """Cookies are most commonly baked until crisp or just long enough that they remain soft, but some kinds of cookies are not baked at all. Cookies are made in a wide variety of styles, using an array of ingredients including sugars, spices, chocolate, butter, peanut butter, nuts, or dried fruits. The softness of the cookie may depend on how long it is baked.

A general theory of cookies may be formulated this way. Despite its descent from cakes and other sweetened breads, the cookie in almost all its forms has abandoned water as a medium for cohesion. Water in cakes serves to make the base (in the case of cakes called "batter"[4]) as thin as possible, which allows the bubbles – responsible for a cake's fluffiness – to better form. In the cookie, the agent of cohesion has become some form of oil. Oils, whether they be in the form of butter, egg yolks, vegetable oils, or lard, are much more viscous than water and evaporate freely at a much higher temperature than water. Thus a cake made with butter or eggs instead of water is far denser after removal from the oven.

Oils in baked cakes do not behave as soda tends to in the finished result. Rather than evaporating and thickening the mixture, they remain, saturating the bubbles of escaped gases from what little water there might have been in the eggs, if added, and the carbon dioxide released by heating the baking powder. This saturation produces the most texturally attractive feature of the cookie, and indeed all fried foods: crispness saturated with a moisture (namely oil) that does not sink into it."""
  
    val confusedStr = invert(rightRotate(str, 8))
    
    println("Confused String: " + confusedStr)
    println("----")
    
    val bytes = stringToByteArray(confusedStr)
    
    println("Byte-to-char confused String: " + bytes.map(_.toChar).mkString)
    println("----")
    
    val deconfusedStr = leftRotate(invert(byteArrayToString(bytes)), 8)
    
    println("Deconfused String: " + deconfusedStr)
    println("----")
    
    println("Match: " + (str == deconfusedStr))
  }
  
  def test2() = {
    import sublimeSpam.io.Loader
    val map = collection.mutable.Map[String, (Int, String, String)]()
    map += "Berthur/Rabbia E Tarantella;Ennio Morricone (Inglorious Basterds);h;1" -> (123456, "Berthur", "22-8-2015")
    map += "Berthur/Sandstorm;Darude;m;1" -> (12345, "Berthur", "22-8-2015")
    map += "Bapums/Through fire and flames;Dragonforce;x;1" -> (1234, "Berthur", "25-8-2015")
    val data = Map[String, (Int, String, String)]() ++ map
    Loader.saveHighScores(data)
    println("Data written!")
    val readResult = Loader.loadHighScores
    println("Reading:")
    println(readResult)
  }
  
  
}