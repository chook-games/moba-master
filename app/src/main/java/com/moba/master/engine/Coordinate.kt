package com.moba.master.engine

import kotlin.math.sqrt

/**
 * Immutable 2D coordinate for the world space.
 * All entity positions use this value type.
 */
data class Coordinate(val x: Float, val y: Float) {

    /** Euclidean distance to another coordinate. */
    fun distanceTo(other: Coordinate): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }

    /** Returns a new coordinate moved towards [target] by [distance]. */
    fun moveTowards(target: Coordinate, distance: Float): Coordinate {
        val dx = target.x - x
        val dy = target.y - y
        val dist = sqrt(dx * dx + dy * dy)
        if (dist <= 0f || dist <= distance) return target
        val ratio = distance / dist
        return Coordinate(x + dx * ratio, y + dy * ratio)
    }

    /** Direction unit vector towards target. */
    fun directionTo(target: Coordinate): Pair<Float, Float> {
        val dx = target.x - x
        val dy = target.y - y
        val dist = sqrt(dx * dx + dy * dy)
        if (dist <= 0f) return Pair(0f, 0f)
        return Pair(dx / dist, dy / dist)
    }

    operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)
    operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Coordinate(x * scalar, y * scalar)
}
