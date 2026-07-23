package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.GameLevel
import com.example.game.ObstacleType
import com.example.game.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CatBoard(
    level: GameLevel,
    playerPath: List<Point>,
    catExpression: String,
    onCellTouch: (row: Int, col: Int) -> Unit,
    selectedSkin: String = "ORANGE",
    selectedTheme: String = "CREAM",
    modifier: Modifier = Modifier
) {
    // Soft Pastel Color Palette based on selectedTheme and selectedSkin
    val tileBgColor: Color
    val gridLineColor: Color
    val tileColor: Color
    val obstacleBgColor: Color

    when (selectedTheme) {
        "LAVENDER" -> {
            tileBgColor = Color(0xFFF3E5F5)     // Soft pastel purple
            gridLineColor = Color(0xFFD1C4E9)   // Light violet grid lines
            tileColor = Color(0xFFFFFFFF)       // White tiles
            obstacleBgColor = Color(0xFFEDE7F6) // Muted lavender
        }
        "COSMIC" -> {
            tileBgColor = Color(0xFF1B1B22)     // Deep space dark grey
            gridLineColor = Color(0xFF3E3E52)   // Space blue lines
            tileColor = Color(0xFF282834)       // Dark tiles
            obstacleBgColor = Color(0xFF21212B) // Muted space black
        }
        "BEACH" -> {
            tileBgColor = Color(0xFFFFF5CC)     // Sand yellow background
            gridLineColor = Color(0xFFFFE082)   // Soft golden lines
            tileColor = Color(0xFFFFFDF0)       // Warm sand tiles
            obstacleBgColor = Color(0xFFFFF59D) // Muted gold
        }
        else -> { // "CREAM"
            tileBgColor = Color(0xFFF9F6F0)     // Warm cozy cream
            gridLineColor = Color(0xFFE8E2D5)   // Light line grid
            tileColor = Color(0xFFFFFFFF)       // Pure white tiles
            obstacleBgColor = Color(0xFFEFECE5) // Muted grey-cream
        }
    }

    val catPrimary: Color
    val catDark: Color
    val catLight: Color
    val catPink = Color(0xFFFFB2B2)            // Pink ears/blush remains consistent

    when (selectedSkin) {
        "TUXEDO" -> {
            catPrimary = Color(0xFF2C3E50)     // Tuxedo Dark Slate Grey
            catDark = Color(0xFF1A252F)        // Dark Charcoal
            catLight = Color(0xFFECEFF1)       // Soft White Highlights
        }
        "CALICO" -> {
            catPrimary = Color(0xFFE67E22)     // Bright Orange patch
            catDark = Color(0xFF2D3E50)        // Calico Dark Charcoal patch
            catLight = Color(0xFFFCF3CF)       // Warm Cream Base
        }
        "PINK_BUNNY" -> {
            catPrimary = Color(0xFFFFB6C1)     // Soft Pink
            catDark = Color(0xFFFF69B4)        // Warm Hot Pink stripes
            catLight = Color(0xFFFFF0F5)       // Glowing Lavender Blush
        }
        "TIGER" -> {
            catPrimary = Color(0xFFD35400)     // Rich Amber Gold
            catDark = Color(0xFF111111)        // Bold Black Tiger Stripes
            catLight = Color(0xFFF39C12)       // Light Gold Highlights
        }
        else -> { // "ORANGE" (Classic)
            catPrimary = Color(0xFFFF9E4F)     // Classic Cat Orange
            catDark = Color(0xFFE67E22)        // Cat Stripes Dark Orange
            catLight = Color(0xFFFFC382)       // Cat Highlight
        }
    }

    val pulseAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(level.cols.toFloat() / level.rows.toFloat())
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(tileBgColor)
            .padding(8.dp)
            .testTag("cat_puzzle_board")
    ) {
        val boardWidth = constraints.maxWidth.toFloat()
        val boardHeight = constraints.maxHeight.toFloat()

        val cellWidth = boardWidth / level.cols
        val cellHeight = boardHeight / level.rows

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(level.cols, level.rows) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                val offset = change.position
                                val col = (offset.x / cellWidth).toInt().coerceIn(0, level.cols - 1)
                                val row = (offset.y / cellHeight).toInt().coerceIn(0, level.rows - 1)
                                onCellTouch(row, col)
                                change.consume()
                            }
                        }
                    }
                }
        ) {
            for (r in 0 until level.rows) {
                for (c in 0 until level.cols) {
                    val tileLeft = c * cellWidth
                    val tileTop = r * cellHeight
                    val padding = 4f

                    val isObstacle = level.obstacles.containsKey(Point(r, c))
                    drawRoundRect(
                        color = if (isObstacle) obstacleBgColor else tileColor,
                        topLeft = Offset(tileLeft + padding, tileTop + padding),
                        size = Size(cellWidth - padding * 2, cellHeight - padding * 2),
                        cornerRadius = CornerRadius(16f, 16f)
                    )
                    if (!isObstacle && Point(r, c) !in playerPath) {
                        drawCircle(
                            color = gridLineColor,
                            radius = 4f,
                            center = Offset(tileLeft + cellWidth / 2, tileTop + cellHeight / 2)
                        )
                    }
                }
            }
            for ((point, type) in level.obstacles) {
                val tileLeft = point.c * cellWidth
                val tileTop = point.r * cellHeight
                val centerX = tileLeft + cellWidth / 2
                val centerY = tileTop + cellHeight / 2
                val size = minOf(cellWidth, cellHeight)

                when (type) {
                    ObstacleType.YARN -> {
                        val yarnColor = Color(0xFFC39BD3)
                        val lineYarnColor = Color(0xFF884EA0)
                        drawCircle(
                            color = yarnColor,
                            radius = size * 0.3f,
                            center = Offset(centerX, centerY)
                        )
                        drawArc(
                            color = lineYarnColor,
                            startAngle = 0f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(centerX - size * 0.2f, centerY - size * 0.2f),
                            size = Size(size * 0.4f, size * 0.4f),
                            style = Stroke(width = 3f)
                        )
                        drawArc(
                            color = lineYarnColor,
                            startAngle = 120f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(centerX - size * 0.25f, centerY - size * 0.15f),
                            size = Size(size * 0.45f, size * 0.35f),
                            style = Stroke(width = 3f)
                        )
                        val threadPath = Path().apply {
                            moveTo(centerX + size * 0.2f, centerY + size * 0.2f)
                            quadraticTo(
                                centerX + size * 0.4f, centerY + size * 0.3f,
                                centerX + size * 0.35f, centerY + size * 0.45f
                            )
                        }
                        drawPath(
                            path = threadPath,
                            color = lineYarnColor,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                    }
                    ObstacleType.MOUSE -> {
                        val mouseColor = Color(0xFFBDC3C7)
                        val earColor = Color(0xFFF1948A)
                        drawOval(
                            color = mouseColor,
                            topLeft = Offset(centerX - size * 0.28f, centerY - size * 0.18f),
                            size = Size(size * 0.5f, size * 0.32f)
                        )
                        drawCircle(
                            color = earColor,
                            radius = size * 0.08f,
                            center = Offset(centerX - size * 0.08f, centerY - size * 0.18f)
                        )
                        drawCircle(
                            color = earColor,
                            radius = size * 0.08f,
                            center = Offset(centerX - size * 0.22f, centerY - size * 0.18f)
                        )
                        val mouseTail = Path().apply {
                            moveTo(centerX + size * 0.22f, centerY)
                            quadraticTo(
                                centerX + size * 0.4f, centerY - size * 0.1f,
                                centerX + size * 0.45f, centerY + size * 0.1f
                            )
                        }
                        drawPath(
                            path = mouseTail,
                            color = Color.DarkGray,
                            style = Stroke(width = 2f, cap = StrokeCap.Round)
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = 2.5f,
                            center = Offset(centerX + size * 0.1f, centerY - size * 0.04f)
                        )
                    }
                    ObstacleType.SCRATCH -> {
                        val woodColor = Color(0xFFD35400)
                        val innerWoodColor = Color(0xFFE67E22)
                        val scratchColor = Color(0xFF5D4037)

                        drawRoundRect(
                            color = woodColor,
                            topLeft = Offset(centerX - size * 0.35f, centerY - size * 0.25f),
                            size = Size(size * 0.7f, size * 0.5f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        drawRoundRect(
                            color = innerWoodColor,
                            topLeft = Offset(centerX - size * 0.28f, centerY - size * 0.2f),
                            size = Size(size * 0.56f, size * 0.4f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        drawLine(
                            color = scratchColor,
                            start = Offset(centerX - size * 0.15f, centerY - size * 0.12f),
                            end = Offset(centerX - size * 0.1f, centerY + size * 0.12f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = scratchColor,
                            start = Offset(centerX + size * 0.05f, centerY - size * 0.1f),
                            end = Offset(centerX + size * 0.08f, centerY + size * 0.14f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                    ObstacleType.PLANT -> {
                        val potColor = Color(0xFFE59866)
                        val leafColor = Color(0xFF52BE80)

                        // Leaves
                        drawOval(
                            color = leafColor,
                            topLeft = Offset(centerX - size * 0.12f, centerY - size * 0.35f),
                            size = Size(size * 0.24f, size * 0.25f)
                        )
                        drawOval(
                            color = leafColor,
                            topLeft = Offset(centerX - size * 0.32f, centerY - size * 0.28f),
                            size = Size(size * 0.25f, size * 0.22f)
                        )
                        drawOval(
                            color = leafColor,
                            topLeft = Offset(centerX + size * 0.08f, centerY - size * 0.28f),
                            size = Size(size * 0.25f, size * 0.22f)
                        )

                        // Pot
                        val potPath = Path().apply {
                            moveTo(centerX - size * 0.25f, centerY - size * 0.05f)
                            lineTo(centerX + size * 0.25f, centerY - size * 0.05f)
                            lineTo(centerX + size * 0.18f, centerY + size * 0.28f)
                            lineTo(centerX - size * 0.18f, centerY + size * 0.28f)
                            close()
                        }
                        drawPath(potPath, potColor)
                        // Pot lip
                        drawRoundRect(
                            color = Color(0xFFD35400),
                            topLeft = Offset(centerX - size * 0.28f, centerY - size * 0.09f),
                            size = Size(size * 0.56f, size * 0.08f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }
            }

            // C. Draw the Cat Body (The continuous stretched path)
            if (playerPath.size > 1) {
                val bodyStrokeWidth = minOf(cellWidth, cellHeight) * 0.58f

                // Create a smooth continuous path through cell centers
                val catBodyPath = Path().apply {
                    val firstCenter = playerPath.first().toOffset(cellWidth, cellHeight)
                    moveTo(firstCenter.x, firstCenter.y)

                    for (i in 1 until playerPath.size) {
                        val currCenter = playerPath[i].toOffset(cellWidth, cellHeight)
                        lineTo(currCenter.x, currCenter.y)
                    }
                }

                // 1. Draw outer body outline shadow for depth
                drawPath(
                    path = catBodyPath,
                    color = Color(0x33000000),
                    style = Stroke(
                        width = bodyStrokeWidth + 8f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 2. Draw the main Orange Cat Body
                drawPath(
                    path = catBodyPath,
                    color = catPrimary,
                    style = Stroke(
                        width = bodyStrokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 3. Draw Cat stripes along the body segments
                for (i in 0 until playerPath.size) {
                    val curr = playerPath[i]
                    val currCenter = curr.toOffset(cellWidth, cellHeight)

                    // Draw stripes based on neighbors to make them wrap nicely
                    val prev = if (i > 0) playerPath[i - 1] else null
                    val next = if (i < playerPath.size - 1) playerPath[i + 1] else null

                    if (prev != null && next != null) {
                        // Mid body: draw diagonal/perpendicular striped patches
                        val isHorizontal = prev.r == next.r
                        if (isHorizontal) {
                            // Vertical stripes wrapping the body
                            drawRoundRect(
                                color = catDark,
                                topLeft = Offset(currCenter.x - 3f, currCenter.y - bodyStrokeWidth / 2f + 2f),
                                size = Size(6f, bodyStrokeWidth - 4f),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        } else if (prev.c == next.c) {
                            // Horizontal stripes wrapping the body
                            drawRoundRect(
                                color = catDark,
                                topLeft = Offset(currCenter.x - bodyStrokeWidth / 2f + 2f, currCenter.y - 3f),
                                size = Size(bodyStrokeWidth - 4f, 6f),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        }
                    } else if (prev != null) {
                        // Near the head
                        val isHorizontal = prev.r == curr.r
                        if (isHorizontal) {
                            drawRoundRect(
                                color = catDark,
                                topLeft = Offset(currCenter.x - 3f, currCenter.y - bodyStrokeWidth / 3f),
                                size = Size(6f, bodyStrokeWidth * 0.66f),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        } else {
                            drawRoundRect(
                                color = catDark,
                                topLeft = Offset(currCenter.x - bodyStrokeWidth / 3f, currCenter.y - 3f),
                                size = Size(bodyStrokeWidth * 0.66f, 6f),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        }
                    }
                }
            }

            // D. Draw the Cat Tail (At the starting cell)
            if (playerPath.isNotEmpty()) {
                val start = playerPath.first()
                val startCenter = start.toOffset(cellWidth, cellHeight)
                val size = minOf(cellWidth, cellHeight)
                val tailThickness = size * 0.15f

                // Draw a cute curved, waving orange cat tail
                val tailPath = Path().apply {
                    moveTo(startCenter.x, startCenter.y)
                    // Wave offset based on wiggle animation
                    val waveOffset = sin(System.currentTimeMillis() / 150.0) * 10.0
                    cubicTo(
                        startCenter.x - size * 0.2f, startCenter.y - size * 0.2f,
                        startCenter.x - size * 0.1f + waveOffset.toFloat(), startCenter.y - size * 0.5f,
                        startCenter.x - size * 0.3f + waveOffset.toFloat(), startCenter.y - size * 0.6f
                    )
                }

                // Draw tail outline
                drawPath(
                    path = tailPath,
                    color = catDark,
                    style = Stroke(width = tailThickness + 4f, cap = StrokeCap.Round)
                )
                // Draw tail main body
                drawPath(
                    path = tailPath,
                    color = catPrimary,
                    style = Stroke(width = tailThickness, cap = StrokeCap.Round)
                )
                // Draw cute white tail tip
                drawCircle(
                    color = Color.White,
                    radius = tailThickness * 0.6f,
                    center = Offset(startCenter.x - size * 0.3f + (sin(System.currentTimeMillis() / 150.0) * 10.0).toFloat(), startCenter.y - size * 0.6f)
                )
            }

            // E. Draw the Cat Head (At the active head coordinate)
            if (playerPath.isNotEmpty()) {
                val head = playerPath.last()
                val headCenter = head.toOffset(cellWidth, cellHeight)
                val size = minOf(cellWidth, cellHeight)
                val headRadius = size * 0.35f

                // Determine rotation/direction based on the direction from the predecessor
                var rotationAngle = 0f // Facing UP by default
                if (playerPath.size > 1) {
                    val prev = playerPath[playerPath.size - 2]
                    rotationAngle = when {
                        head.r < prev.r -> 0f      // Up
                        head.r > prev.r -> 180f    // Down
                        head.c > prev.c -> 90f     // Right
                        head.c < prev.c -> 270f    // Left
                        else -> 0f
                    }
                }

                // Draw Head with orientation
                drawCatHead(
                    centerX = headCenter.x,
                    centerY = headCenter.y,
                    radius = headRadius,
                    rotationDegrees = rotationAngle,
                    expression = catExpression,
                    catOrange = catPrimary,
                    catOrangeDark = catDark,
                    catOrangeLight = catLight,
                    catPink = catPink
                )
            }
        }
    }
}

// Map grid coordinate to Canvas Offset (center of the tile)
fun Point.toOffset(cellWidth: Float, cellHeight: Float): Offset {
    return Offset(
        x = c * cellWidth + cellWidth / 2,
        y = r * cellHeight + cellHeight / 2
    )
}

// Function to draw cat's ears, face, whiskers and blinking eyes
fun DrawScope.drawCatHead(
    centerX: Float,
    centerY: Float,
    radius: Float,
    rotationDegrees: Float,
    expression: String,
    catOrange: Color,
    catOrangeDark: Color,
    catOrangeLight: Color,
    catPink: Color
) {
    // We can draw a beautiful rotated cat head using standard Canvas rotate
    val radians = rotationDegrees * PI / 180.0

    // Rotates a coordinate around the head's center
    fun rotateOffset(offsetX: Float, offsetY: Float): Offset {
        val dx = offsetX - centerX
        val dy = offsetY - centerY
        val rx = dx * cos(radians) - dy * sin(radians) + centerX
        val ry = dx * sin(radians) + dy * cos(radians) + centerY
        return Offset(rx.toFloat(), ry.toFloat())
    }

    // 1. Draw Ears (Pointy Triangles at top left & top right)
    // Left Ear
    val leftEarPath = Path().apply {
        val p1 = rotateOffset(centerX - radius * 0.8f, centerY - radius * 0.3f)
        val p2 = rotateOffset(centerX - radius * 0.8f, centerY - radius * 1.1f)
        val p3 = rotateOffset(centerX - radius * 0.2f, centerY - radius * 0.7f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(leftEarPath, catOrange)

    // Left inner pink ear
    val leftInnerEarPath = Path().apply {
        val p1 = rotateOffset(centerX - radius * 0.7f, centerY - radius * 0.4f)
        val p2 = rotateOffset(centerX - radius * 0.72f, centerY - radius * 0.95f)
        val p3 = rotateOffset(centerX - radius * 0.3f, centerY - radius * 0.65f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(leftInnerEarPath, catPink)

    // Right Ear
    val rightEarPath = Path().apply {
        val p1 = rotateOffset(centerX + radius * 0.8f, centerY - radius * 0.3f)
        val p2 = rotateOffset(centerX + radius * 0.8f, centerY - radius * 1.1f)
        val p3 = rotateOffset(centerX + radius * 0.2f, centerY - radius * 0.7f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(rightEarPath, catOrange)

    // Right inner pink ear
    val rightInnerEarPath = Path().apply {
        val p1 = rotateOffset(centerX + radius * 0.7f, centerY - radius * 0.4f)
        val p2 = rotateOffset(centerX + radius * 0.72f, centerY - radius * 0.95f)
        val p3 = rotateOffset(centerX + radius * 0.3f, centerY - radius * 0.65f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(rightInnerEarPath, catPink)

    // 2. Draw Main Head Round Circle
    drawCircle(
        color = catOrange,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // Cheek highlight fur spots (White / lighter orange)
    drawCircle(
        color = catOrangeLight,
        radius = radius * 0.4f,
        center = rotateOffset(centerX - radius * 0.4f, centerY + radius * 0.4f)
    )
    drawCircle(
        color = catOrangeLight,
        radius = radius * 0.4f,
        center = rotateOffset(centerX + radius * 0.4f, centerY + radius * 0.4f)
    )

    // Tabby forehead stripes (Cute 3 small triangles)
    val stripe1 = Path().apply {
        val p1 = rotateOffset(centerX - 6f, centerY - radius)
        val p2 = rotateOffset(centerX + 6f, centerY - radius)
        val p3 = rotateOffset(centerX, centerY - radius * 0.7f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(stripe1, catOrangeDark)

    val stripe2 = Path().apply {
        val p1 = rotateOffset(centerX - radius * 0.3f - 4f, centerY - radius * 0.95f)
        val p2 = rotateOffset(centerX - radius * 0.3f + 4f, centerY - radius * 0.95f)
        val p3 = rotateOffset(centerX - radius * 0.25f, centerY - radius * 0.72f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(stripe2, catOrangeDark)

    val stripe3 = Path().apply {
        val p1 = rotateOffset(centerX + radius * 0.3f - 4f, centerY - radius * 0.95f)
        val p2 = rotateOffset(centerX + radius * 0.3f + 4f, centerY - radius * 0.95f)
        val p3 = rotateOffset(centerX + radius * 0.25f, centerY - radius * 0.72f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(stripe3, catOrangeDark)

    // 3. Draw Eyes based on Expression
    val leftEyeCenter = rotateOffset(centerX - radius * 0.38f, centerY - radius * 0.1f)
    val rightEyeCenter = rotateOffset(centerX + radius * 0.38f, centerY - radius * 0.1f)

    when (expression) {
        "HAPPY", "CELEBRATING" -> {
            // Happy closed eyes: curved arcs like `^ ^`
            drawArc(
                color = Color.Black,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(leftEyeCenter.x - radius * 0.15f, leftEyeCenter.y - radius * 0.1f),
                size = Size(radius * 0.3f, radius * 0.2f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color.Black,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(rightEyeCenter.x - radius * 0.15f, rightEyeCenter.y - radius * 0.1f),
                size = Size(radius * 0.3f, radius * 0.2f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            // Star in eyes or happy blush cheeks
            drawCircle(color = catPink, radius = radius * 0.2f, center = rotateOffset(centerX - radius * 0.55f, centerY + radius * 0.25f))
            drawCircle(color = catPink, radius = radius * 0.2f, center = rotateOffset(centerX + radius * 0.55f, centerY + radius * 0.25f))
        }
        "THINKING" -> {
            // One closed eye squinting, one dot eye!
            drawCircle(color = Color.Black, radius = radius * 0.1f, center = leftEyeCenter)
            // Drawing squinting arc for right eye
            drawArc(
                color = Color.Black,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(rightEyeCenter.x - radius * 0.12f, rightEyeCenter.y - radius * 0.08f),
                size = Size(radius * 0.24f, radius * 0.16f),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )
        }
        else -> {
            // Normal: Big round glistening black eyes with dual white reflections!
            val eyeRad = radius * 0.13f
            drawCircle(color = Color.Black, radius = eyeRad, center = leftEyeCenter)
            drawCircle(color = Color.Black, radius = eyeRad, center = rightEyeCenter)

            // Sparkles (White dots)
            drawCircle(color = Color.White, radius = eyeRad * 0.35f, center = Offset(leftEyeCenter.x - eyeRad * 0.3f, leftEyeCenter.y - eyeRad * 0.3f))
            drawCircle(color = Color.White, radius = eyeRad * 0.35f, center = Offset(rightEyeCenter.x - eyeRad * 0.3f, rightEyeCenter.y - eyeRad * 0.3f))
            drawCircle(color = Color.White, radius = eyeRad * 0.18f, center = Offset(leftEyeCenter.x + eyeRad * 0.3f, leftEyeCenter.y + eyeRad * 0.3f))
            drawCircle(color = Color.White, radius = eyeRad * 0.18f, center = Offset(rightEyeCenter.x + eyeRad * 0.3f, rightEyeCenter.y + eyeRad * 0.3f))

            // Soft cute blush cheeks
            drawCircle(color = catPink.copy(alpha = 0.7f), radius = radius * 0.16f, center = rotateOffset(centerX - radius * 0.58f, centerY + radius * 0.28f))
            drawCircle(color = catPink.copy(alpha = 0.7f), radius = radius * 0.16f, center = rotateOffset(centerX + radius * 0.58f, centerY + radius * 0.28f))
        }
    }

    // 4. Draw Cute Pink Nose (small triangle)
    val nosePath = Path().apply {
        val p1 = rotateOffset(centerX - 5f, centerY + radius * 0.05f)
        val p2 = rotateOffset(centerX + 5f, centerY + radius * 0.05f)
        val p3 = rotateOffset(centerX, centerY + radius * 0.13f)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(nosePath, catPink)

    // 5. Draw Cute Smiling Mouth "w"
    val mouthPath = Path().apply {
        val startL = rotateOffset(centerX - radius * 0.18f, centerY + radius * 0.12f)
        val mid = rotateOffset(centerX, centerY + radius * 0.18f)
        val endR = rotateOffset(centerX + radius * 0.18f, centerY + radius * 0.12f)

        moveTo(startL.x, startL.y)
        quadraticTo(
            rotateOffset(centerX - radius * 0.09f, centerY + radius * 0.25f).x,
            rotateOffset(centerX - radius * 0.09f, centerY + radius * 0.25f).y,
            mid.x, mid.y
        )
        quadraticTo(
            rotateOffset(centerX + radius * 0.09f, centerY + radius * 0.25f).x,
            rotateOffset(centerX + radius * 0.09f, centerY + radius * 0.25f).y,
            endR.x, endR.y
        )
    }
    drawPath(
        path = mouthPath,
        color = Color(0xFF5D4037),
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )

    // 6. Draw Whiskers (3 whiskers on each side)
    // Left Whiskers
    val wL1_Start = rotateOffset(centerX - radius * 0.75f, centerY + radius * 0.15f)
    val wL1_End = rotateOffset(centerX - radius * 1.35f, centerY + radius * 0.08f)
    drawLine(color = Color.White, start = wL1_Start, end = wL1_End, strokeWidth = 3f, cap = StrokeCap.Round)

    val wL2_Start = rotateOffset(centerX - radius * 0.78f, centerY + radius * 0.25f)
    val wL2_End = rotateOffset(centerX - radius * 1.4f, centerY + radius * 0.25f)
    drawLine(color = Color.White, start = wL2_Start, end = wL2_End, strokeWidth = 3f, cap = StrokeCap.Round)

    val wL3_Start = rotateOffset(centerX - radius * 0.75f, centerY + radius * 0.35f)
    val wL3_End = rotateOffset(centerX - radius * 1.32f, centerY + radius * 0.42f)
    drawLine(color = Color.White, start = wL3_Start, end = wL3_End, strokeWidth = 3f, cap = StrokeCap.Round)

    // Right Whiskers
    val wR1_Start = rotateOffset(centerX + radius * 0.75f, centerY + radius * 0.15f)
    val wR1_End = rotateOffset(centerX + radius * 1.35f, centerY + radius * 0.08f)
    drawLine(color = Color.White, start = wR1_Start, end = wR1_End, strokeWidth = 3f, cap = StrokeCap.Round)

    val wR2_Start = rotateOffset(centerX + radius * 0.78f, centerY + radius * 0.25f)
    val wR2_End = rotateOffset(centerX + radius * 1.4f, centerY + radius * 0.25f)
    drawLine(color = Color.White, start = wR2_Start, end = wR2_End, strokeWidth = 3f, cap = StrokeCap.Round)

    val wR3_Start = rotateOffset(centerX + radius * 0.75f, centerY + radius * 0.35f)
    val wR3_End = rotateOffset(centerX + radius * 1.32f, centerY + radius * 0.42f)
    drawLine(color = Color.White, start = wR3_Start, end = wR3_End, strokeWidth = 3f, cap = StrokeCap.Round)
}
