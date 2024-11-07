package com.xperiencelabs.arapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var modelNode: ArModelNode
    private lateinit var movePlanets: TextView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var plusButton: ExtendedFloatingActionButton
    private lateinit var minusButton: ExtendedFloatingActionButton

    // Initial scale factor for the model
    private var scaleFactor = 1f
    private val scaleStep = 0.1f // Change in scale on each button press

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        placeButton = findViewById(R.id.lockPlanets)
        movePlanets = findViewById(R.id.movePlanets)
        plusButton = findViewById(R.id.plusbutton)
        minusButton = findViewById(R.id.minusbutton)

        placeButton.setOnClickListener {
            placeModel()
        }

        plusButton.setOnClickListener {
            zoomIn()
        }

        minusButton.setOnClickListener {
            zoomOut()
        }

        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/solar_system.glb",
                scaleToUnits = scaleFactor,
                centerOrigin = Position(-0.5f)
            ) {
                sceneView.planeRenderer.isVisible = true
                movePlanets.visibility = View.GONE
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
            }
        }

        sceneView.addChild(modelNode)
    }

    private fun placeModel() {
        modelNode.anchor()
        sceneView.planeRenderer.isVisible = false
        movePlanets.visibility = View.VISIBLE
    }

    private fun zoomIn() {
        scaleFactor += scaleStep
        modelNode.scale = Position(scaleFactor, scaleFactor, scaleFactor)
    }

    private fun zoomOut() {
        if (scaleFactor > scaleStep) {  // Prevents the scale from becoming too small or zero
            scaleFactor -= scaleStep
            modelNode.scale = Position(scaleFactor, scaleFactor, scaleFactor)
        }
    }
}
