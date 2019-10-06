import java.awt.image.{BufferedImage, ColorConvertOp, Raster}
import java.io.File

import detection.CannyEdgeDetector
import javax.imageio.ImageIO
import models.{ColourARGB, Coordinates}

object ImageTest extends App {
  val neighbourhood = List((-1, -1), (-1, 0), (-1, 1),
                           ( 0, -1), ( 0, 0), ( 0, 1),
                           ( 1, -1), ( 1, 0), ( 1, 1))

  val frame = ImageIO.read(new File(args(0)))

  def testImageOutput(low:Float, high:Float): BufferedImage = {
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

    rgbImage
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
    val dataBuffer = raster.getDataBuffer
    println(s"DataBuffer: Datatype-${dataBuffer.getDataType} size-${dataBuffer.getSize} offsets-${dataBuffer.getOffsets}")
    val a: List[(Coordinates, ColourARGB)] = for{
      x <- (boundingRectangle.x until boundingRectangle.x + boundingRectangle.width).toList
      y <- (boundingRectangle.y until boundingRectangle.y + boundingRectangle.height).toList
    } yield (Coordinates(x,y), ColourARGB(edgeImage.getRGB(x, y)))

    //a.foreach(println)

    a.filter(_._2.isBlack).foreach(println)
    //

    calculateGradientLine(Coordinates(604,96), a)
  }

  def calculateGradientLine(coordinates: Coordinates, pixels: List[(Coordinates, ColourARGB)]) = {


    val neighbourCoordinates = neighbourhood.map(l => Coordinates(coordinates.x + l._1, coordinates.y + l._2))

    println("---calculateGradientLine---")
    pixels
      .filter(_._2.isBlack)
      .filter(c => neighbourCoordinates.contains(c._1))
      .map(_._1)
      .foreach(println)
  }

  strokeWidthTransform(testImageOutput(0.5f, 1.0f))

}
