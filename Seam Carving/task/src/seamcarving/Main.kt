package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val carver = SeamCarving()
    carver.produce()

}

class SeamCarving{
    var width = 0
    var height = 0
    val scaling = 1
    var imageFilename = ""

    private val workingDir = System.getProperty("user.dir")
    private val separator = File.separator
    private val folderPath = "${workingDir}${separator}"

    fun produce() {
        try {
            val filePathOutput = "${folderPath}${imageFilename}"
            val file = File(filePathOutput)

            val widthScale = width * scaling
            val heightScale = height * scaling
            val image = BufferedImage(widthScale,heightScale,BufferedImage.TYPE_INT_RGB)
            val graphics = image.createGraphics()

            // Set background color
            graphics.color = Color.BLACK
            graphics.fillRect(0,0, (image.width * scaling), (image.height * scaling))

            // Create and draw
            graphics.color = Color.RED
            graphics.drawLine(0, 0, width-1, height-1)
            graphics.drawLine(0, height-1, width-1, 0)

            // write to file
            ImageIO.write(image, "png", file)
        }catch (e:Exception){
            val msg = "An error occurred: ${e.message}"
            println(msg)
        }
    }

    init {
        println("Enter rectangle width:")
        width = readln().toInt()
        println("Enter rectangle height:")
        height = readln().toInt()
        println("Enter output image name:")
        imageFilename = readln()
    }
}
