package seamcarving

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args:Array<String>) {
    var importFile:String? = null
    var exportFile:String? = null

    val listFiles = mutableListOf<String>()

    args.forEachIndexed{ index,arg ->
        when(arg){
            "-in"       -> importFile = args.getOrNull(index+1)
            "-out"      -> exportFile = args.getOrNull(index+1)
            "-compare"  -> {
                for (i in args.indices){
                    if (i>0) {
                        val file = args.getOrNull(i)
                        listFiles.add(file?:"")
                    }
                }
            }
        }
    }

    val carver = SeamCarving()
    carver.produce(importFile,exportFile)
    //carver.compare(listFiles)

}

class SeamCarving{
    private val workingDir = System.getProperty("user.dir")
    private val separator = File.separator
    private val folderPath = "${workingDir}${separator}"

    private fun makeRectCrossingLine(image:BufferedImage):Graphics2D{
        val graphics = image.createGraphics()

        // Set background color
        graphics.color = Color.BLACK
        graphics.fillRect(0,0, image.width, image.height)

        // Create and draw
        graphics.color = Color.RED
        graphics.drawLine(0, 0, image.width-1, image.height-1)
        graphics.drawLine(0, image.height-1, image.width-1, 0)

        return graphics
    }

    fun produce(importFile: String?, exportFile: String?) {
        try {
            val image = ImageIO.read(File(folderPath+importFile))
            val energy = calculateEnergy(image)
            val maxEnergy = getMaxEnergy(energy)
            // println(maxEnergy)
            val listToFile = mutableListOf<String>()

            for (x in 0 until image.width){
                for (y in 0 until image.height){
                    val normEnergy = (255.0 * energy[x][y] / maxEnergy).toInt()
                    val colorNew = Color(normEnergy,normEnergy,normEnergy)
                    image.setRGB(x,y,colorNew.rgb)

                    // listToFile.add("$x,$y: ${energy[x][y]} | $normEnergy")
                }
            }
            // saveToTextFile(listToFile,"${folderPath}produceTest.txt")

            // write to file
            ImageIO.write(image, "png", File(folderPath+exportFile))
        }catch (e:Exception){
            val msg = "An error occurred: ${e.message}"
            println(msg)
        }
    }

    private fun getMaxEnergy(energy:Array<DoubleArray>):Double{
        var max = Double.MIN_VALUE
        for (row in energy){
            for (value in row){
                if (value > max){
                    max = value
                    // println("$row,$value: $max")
                }
            }
        }
        return max
    }

    private fun calculateEnergy(image: BufferedImage):Array<DoubleArray> {
        val width = image.width
        val height = image.height
        val energy = Array(width){ DoubleArray(height) }

        for (x in 0 until width){
            for(y in 0 until height){
                val gx = computeGradientX(image, x, y, width)
                val gy = computeGradientY(image, x, y, height)
                energy[x][y] = sqrt((gx + gy).toDouble())
            }
        }
        return energy
    }

    private fun computeGradientX(image: BufferedImage, x: Int, y: Int, width: Int): Int {
        val leftX = if(x + 1 >= width){
            x - 2
        } else if(x - 1 >= 0) {
            x - 1
        } else {
            x
        }

        val rightX = if(x - 1 < 0){
            x + 2
        } else if (x + 1 < width) {
            x + 1
        } else {
            x
        }

        //print("x->$x: leftX->$leftX, rightX->$rightX | ")

        val colorLeft = Color(image.getRGB(leftX,y))
        val colorRight = Color(image.getRGB(rightX,y))

        val rx = colorLeft.red - colorRight.red
        val gx = colorLeft.green - colorRight.green
        val bx = colorLeft.blue - colorRight.blue

        return rx * rx + gx * gx + bx * bx
    }

    private fun computeGradientY(image: BufferedImage, x: Int, y: Int, height: Int): Int {
        val topY = if(y + 1 >= height){
            y - 2
        }else if(y - 1 >= 0) {
            y - 1
        } else {
            y
        }

        val bottomY = if(y - 1 < 0){
            y + 2
        } else if(y + 1 < height) {
            y + 1
        } else {
            y
        }

        //print("y->$y: topY->$topY, bottomY->$bottomY\n")

        val colorTop = Color(image.getRGB(x,topY))
        val colorBottom = Color(image.getRGB(x,bottomY))

        val ry = colorTop.red - colorBottom.red
        val gy = colorTop.green - colorBottom.green
        val by = colorTop.blue - colorBottom.blue

        return ry * ry + gy * gy + by * by
    }

    private fun makeNegative(importFile: String?): BufferedImage? {
        val image = ImageIO.read(importFile?.let { File(it) })

        for (x in 0 until image.width){
            for (y in 0 until image.height){
                val color = Color(image.getRGB(x,y))
                val colorNew = Color(255-color.red,255-color.green,255-color.blue)
                image.setRGB(x,y,colorNew.rgb)
            }
        }
        return image
    }

    private fun getRGBValues(image: BufferedImage): List<String> {
        val rgbValues = mutableListOf<String>()

        val width = image.width
        val height = image.height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = Color(image.getRGB(x, y))
                val red = color.red
                val green = color.green
                val blue = color.blue

                val rgbString = "[$x,$y]: $red,$green,$blue"
                rgbValues.add(rgbString)
            }
        }

        return rgbValues
    }

    private fun saveToTextFile(rgbValues: List<String>, filePath: String) {
        val file = File(filePath)
        file.bufferedWriter().use { writer ->
            for (rgb in rgbValues) {
                writer.write("$rgb\n")
            }
        }
        println(filePath)
    }

    fun compare(compareFiles: MutableList<String>) {
        try {
            var i = 1
            compareFiles.forEach {
                val image = ImageIO.read(File(folderPath+it))
                val arrColor = getRGBValues(image)
                val fileOutputStr = "${folderPath}test_${i}.txt"
                saveToTextFile(arrColor,fileOutputStr)
                i++
            }
        }catch (e:Exception){
            val msg = "An error occurred: ${e.message}"
            println(msg)
        }
    }
}
