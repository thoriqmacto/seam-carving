package seamcarving

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args:Array<String>) {
    var importFile:String? = null
    var exportFile:String? = null

    args.forEachIndexed{ index,arg ->
        when(arg){
            "-in"  -> importFile = args.getOrNull(index+1)
            "-out" -> exportFile = args.getOrNull(index+1)
        }
    }

    val carver = SeamCarving()
    carver.produce(importFile,exportFile)

}

class SeamCarving{
    var width = 0
    var height = 0
    val scaling = 1
    var imageFilename = ""

    private val workingDir = System.getProperty("user.dir")
    private val separator = File.separator
    private val folderPath = "${workingDir}${separator}"

    init {
        // println("Enter rectangle width:")
        // width = readln().toInt()
        // println("Enter rectangle height:")
        // height = readln().toInt()
        // println("Enter output image name:")
        // imageFilename = readln()
    }

    private fun stage1(image:BufferedImage):Graphics2D{
        val graphics = image.createGraphics()
        // Set background color
        graphics.color = Color.BLACK
        graphics.fillRect(0,0, (image.width * scaling), (image.height * scaling))

        // Create and draw
        graphics.color = Color.RED
        graphics.drawLine(0, 0, width-1, height-1)
        graphics.drawLine(0, height-1, width-1, 0)

        return graphics
    }

    fun produce(importFile: String?, exportFile: String?) {
        try {
            val filePath = "${folderPath}${imageFilename}"
            val file = File(filePath)

            val widthScale = width * scaling
            val heightScale = height * scaling
            // val image = BufferedImage(widthScale,heightScale,BufferedImage.TYPE_INT_RGB)

            // Stage-1
            // val stage1Graphics = stage1(graphics)

            // Stage-2
            val stage2Graphics = stage2(folderPath+importFile)

            // write to file
            ImageIO.write(stage2Graphics, "png", File(folderPath+exportFile))
        }catch (e:Exception){
            val msg = "An error occurred: ${e.message}"
            println(msg)
        }
    }

    private fun stage2(importFile: String?): BufferedImage? {
        val imageIn = ImageIO.read(importFile?.let { File(it) })

        for (x in 0 until imageIn.width){
            for (y in 0 until imageIn.height){
                val color = Color(imageIn.getRGB(x,y))
                val colorNew = Color(255-color.red,255-color.green,255-color.blue)
                imageIn.setRGB(x,y,colorNew.rgb)
            }
        }

        return imageIn
    }
}
