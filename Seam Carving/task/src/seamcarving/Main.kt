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
            val normEnergy = normalizeEnergies(energy,maxEnergy)

            for (x in 0 until image.width){
                for (y in 0 until image.height){
                    val colorNew = Color(normEnergy[x][y],normEnergy[x][y],normEnergy[x][y])
                    image.setRGB(x,y,colorNew.rgb)
                }
            }

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
                }
            }
        }
        return max
    }

    private fun normalizeEnergies(energy:Array<DoubleArray>,maxEnergyValue:Double):Array<IntArray>{
        val output = Array(energy.size) { IntArray(energy[0].size) }

        for (x in energy.indices){
            for (y in energy[0].indices){
                output[x][y] = (255.0 * energy[x][y] / maxEnergyValue).toInt()
            }
        }

        return output
    }

    private fun calculateEnergy(image: BufferedImage):Array<DoubleArray> {
        val width = image.width
        val height = image.height
        val energy = Array(width){ DoubleArray(height) }

        for (x in 0 until width){
            for(y in 0 until height){
                val gx = computeGradientX(image, x, y, width)
                val gy = computeGradientY(image, x, y, height)
                energy[x][y] = sqrt( gx.pow(2.0) + gy.pow(2.0))
            }
        }
        return energy
    }

    private fun computeGradientX(image: BufferedImage, x: Int, y: Int, width: Int): Double {
        val leftX = if(x - 1 >= 0) x-1 else x
        val rightX = if(x + 1 < width) x+1 else x

        val colorLeft = Color(image.getRGB(leftX,y))
        val colorRight = Color(image.getRGB(rightX,y))

        val rx = colorLeft.red - colorRight.red
        val gx = colorLeft.green - colorRight.green
        val bx = colorLeft.blue - colorRight.blue

        return rx.toDouble().pow(2.0) + gx.toDouble().pow(2.0) + bx.toDouble().pow(2.0)
    }

    private fun computeGradientY(image: BufferedImage, x: Int, y: Int, height: Int): Double {
        val topY = if(y - 1 >= 0) y-1 else y
        val bottomY = if(y + 1 < height) y+1 else y

        val colorTop = Color(image.getRGB(x,topY))
        val colorBottom = Color(image.getRGB(x,bottomY))

        val ry = colorTop.red - colorBottom.red
        val gy = colorTop.green - colorBottom.green
        val by = colorTop.blue - colorBottom.blue

        return ry.toDouble().pow(2.0)+gy.toDouble().pow(2.0)+by.toDouble().pow(2.0)
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
}
