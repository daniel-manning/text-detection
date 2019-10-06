package models

case class ColourARGB(alpha:Int, red:Int, green:Int, blue: Int){

  def isWhite:Boolean = red == 0 &&
                        green == 0 &&
                        blue == 0

  def isBlack:Boolean = red == 255 &&
                        green == 255 &&
                        blue == 255

}

object ColourARGB {
  def apply(pixel:Int): ColourARGB = {
    val alpha = (pixel >> 24) & 0xff
    val red = (pixel >> 16) & 0xff
    val green = (pixel >> 8) & 0xff
    val blue = pixel & 0xff

    ColourARGB(alpha, red, green, blue)
  }
}