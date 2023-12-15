import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun drawPolygon(): BufferedImage {
    val image = BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB)
    val g2d = image.createGraphics()

    // Set background color
//    g2d.color = Color.BLACK
//    g2d.fillRect(0, 0, image.width, image.height)

    // Define polygon points
    val xPoints = intArrayOf(50, 100, 200, 250, 200, 100, 50)
    val yPoints = intArrayOf(150, 250, 250, 150, 50, 50, 150)
    val nPoints = xPoints.size

    // Create and draw polygon
    g2d.color = Color.YELLOW
    g2d.drawPolyline(xPoints, yPoints, nPoints)
//    g2d.dispose()
    return image
}

//fun main() {
//    val image = drawPolygon()
//    val outputFile = File("polygon_image.png")
//    ImageIO.write(image, "png", outputFile)
//    println("Image created successfully.")
//}