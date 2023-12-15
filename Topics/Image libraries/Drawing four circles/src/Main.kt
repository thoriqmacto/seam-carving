import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun drawCircles(): BufferedImage {
    val width = 200
    val height = 200
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    val graphics = bufferedImage.createGraphics()
//    graphics.color = Color.WHITE // Set background color
//    graphics.fillRect(0, 0, width, height) // Fill background with white

    val circleSize = 100

    // Draw red circle
    graphics.color = Color.RED
    graphics.drawOval(50, 50, circleSize, circleSize)

    // Draw yellow circle
    graphics.color = Color.YELLOW
    graphics.drawOval(50, 75, circleSize, circleSize)

    // Draw green circle
    graphics.color = Color.GREEN
    graphics.drawOval(75, 50, circleSize, circleSize)

    // Draw blue circle
    graphics.color = Color.BLUE
    graphics.drawOval(75, 75, circleSize, circleSize)

    graphics.dispose()
    return bufferedImage
}

//fun main() {
//    val image = drawCircles()
//    val outputFile = File("circles_image.png")
//    ImageIO.write(image, "png", outputFile)
//    println("Image created successfully.")
//}