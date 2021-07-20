package com.example.canvasapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs)
{

    private var drawPath : CustomPath ? =null       // An variable of CustomPath inner class to use it further.
    private var canvasBitmap: Bitmap?  = null       // An instance of the Bitmap.
    private var drawPaint : Paint? = null           // The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private var canvasPaint : Paint? =null          // Instance of canvas paint view.
    private var brushSize : Float =0.toFloat()      // brush size or stroke draw on canvas
    private var color = Color.BLUE                  // a variable  for stroke / brush size paint view
    private var canvas: Canvas? = null
    /**
     * A variable for canvas which will be initialized later and used.
     *
     *The Canvas class holds the "draw" calls. To draw something, you need 4 basic components: A Bitmap to hold the pixels, a Canvas to host
     * the draw calls (writing into the bitmap), a drawing primitive (e.g. Rect,
     * Path, text, Bitmap), and a paint (to describe the colors and styles for the
     * drawing)
     */

    private val paths= ArrayList<CustomPath>()         // arraylist for storing paths
    private val mUndoPaths = ArrayList<CustomPath>()   // it stores also path but this arrayList for undo state

    init {
        setUpDrawing()
    }

    /**
     * This method initializes the attributes of the
     * ViewForDrawing class.
     */
    private fun setUpDrawing() {
       drawPaint = Paint()
        drawPath =CustomPath(color, brushSize)
        drawPaint!!.color=color
        drawPaint!!.style =Paint.Style.STROKE            // This is to draw a STROKE style
        drawPaint!!.strokeJoin = Paint.Join.ROUND        // This is for store join
        drawPaint!!.strokeCap = Paint.Cap.ROUND         // This is for stroke Cap
        canvasPaint= Paint(Paint.DITHER_FLAG)           // Paint flag that enables dithering when blitting.
        brushSize=20.toFloat()                          // default size or we can initial brush/ stroke size is defined.

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap!!)
    }

    // Change Canvas to Canvas? if fails
    /*
    * This method is called when a stroke is drawn on the canvas
    * as a part of the painting.
    */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap !!, 0f, 0f, canvasPaint)

        //TODO for persist line on screen
        for (path in paths)
        {
            drawPaint!!.strokeWidth = path!!.brushThickness
            drawPaint !!.color =path!!.color
            canvas.drawPath(path!!, drawPaint!!)

        }

        //TODO if draw path is not empty than canvas.draw is executed
        if(! drawPath !! .isEmpty)
        {
            drawPaint!!.strokeWidth = drawPath!!.brushThickness
            drawPaint !!.color =drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }

    }

    /**
     * This method acts as an event listener when a touch
     * event is detected on the device.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX= event?.x
        val touchY=event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath!!.color = color
                drawPath!!.brushThickness = brushSize

                drawPath!!.reset() // Clear any lines and curves from the path, making it empty.
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath!!.moveTo(
                            touchX,
                            touchY
                        )
                    }
                } // Set the beginning of the next contour to the point (x,y).
            }

            // for movement
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath!!.lineTo(
                            touchX,
                            touchY
                        )
                    }
                } // Add a line from the last point to the specified point (x,y).
            }

            //Release the screen
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath!!)       //Add when to stroke is drawn to canvas and added in the path arraylist
                drawPath = CustomPath(color, brushSize)
            }
            else -> return false
        }

       invalidate()
        return true

    }

    /**
     * This method is called when either the brush or the eraser
     * sizes are to be changed. This method sets the brush/eraser
     * sizes to the new values depending on user selection.
     */
    fun setSizeForBrush(newSize: Float)
    {
        brushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  //this fix the pixels corresponding to different screens
        newSize, resources.displayMetrics)
        drawPaint!!.strokeWidth=brushSize
    }

    /**
     * This function is called when the user desires a color change.
     * This functions sets the color of a store to selected color and able to draw on view using that color.
     *
     * @param newColor
     */
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        drawPaint!!.color = color
    }

    /**
     * This function is called when the user selects the undo
     * command from the application. This function removes the
     * last stroke input by the user depending on the
     * number of times undo has been activated.
     */
    fun onClickUndo() {
        if (paths.size > 0) {

            mUndoPaths.add(paths.removeAt(paths.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }

    // An inner class for custom path with two params as color and stroke size.
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
    {

    }
}