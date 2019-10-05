import java.awt.image.{BufferedImage, ColorConvertOp, Raster}
import java.io.File

import detection.CannyEdgeDetector
import javax.imageio.ImageIO

object ImageTest extends App {
  val frame = ImageIO.read(new File(args(0)))

  def testImageOutput(low:Float, high:Float) = {
    println(s"-------------low: ${low} ------ high: ${high}")
    println("-------------starting edge detection---------------")
    val detector = new CannyEdgeDetector()
    //adjust its parameters as desired
    detector.setLowThreshold(low)
    detector.setHighThreshold(high)
    //apply it to an image
    detector.setSourceImage(frame)
    println("-------------starting processing---------------")
    detector.process()
    val edges = detector.getEdgesImage

    println(s"----------convert to rgb--------------")
    val rgbImage = new BufferedImage(edges.getWidth(),
      edges.getHeight(), BufferedImage.TYPE_INT_RGB)

    val op = new ColorConvertOp(null)
    op.filter(edges, rgbImage)

    println("-------------output edge detection image---------------")
    ImageIO.write(rgbImage, "jpg", new File(s"${args(0)}_${low}_${high}.jpg"))
  }

  def getDetectedEdges(path:String, low:Float = 0.5f, high:Float = 1.0f):BufferedImage = {
    println("-------------starting edge detection---------------")
    val detector = new CannyEdgeDetector()
    //adjust its parameters as desired
    detector.setLowThreshold(low)
    detector.setHighThreshold(high)
    //apply it to an image
    detector.setSourceImage(frame)
    println("-------------starting processing---------------")
    detector.process()
    detector.getEdgesImage
  }

  def strokeWidthTransform(edgeImage:BufferedImage) = {
    val raster = edgeImage.getData
    val boundingRectangle = raster.getBounds
    for{
      x <- (boundingRectangle.x until boundingRectangle.x + boundingRectangle.width).toList
      y <- (boundingRectangle.y until boundingRectangle.y + boundingRectangle.height).toList
    } yield something(raster, x, y)

    //
  }

  def something(raster:Raster, x: Int, y: Int) = {
    println(s"***************** x: ${x} y: ${y}")
    var values = Array.ofDim[Double](raster.getNumBands)
    val rets = raster.getPixel(x,y, values)
    println(s"***************** rets: **************")
    rets.foreach(l => print(s"${l}, "))
    print('\n')
    println(s"***************** values ***************")
    values.foreach(l => print(s"${l}, "))
    print('\n')
  }

  //strokeWidthTransform(getDetectedEdges(""))

  testImageOutput(0.5f, 1.0f)

}
