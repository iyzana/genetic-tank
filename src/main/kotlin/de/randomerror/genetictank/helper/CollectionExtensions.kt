package de.randomerror.genetictank.helper

import java.util.*

/**
 * Created by henri on 24.10.16.
 */

fun <K, V> Map<K, V>.forEach(block: (K, V) -> Unit): Unit = this.forEach { block(it.key, it.value) }

operator fun <T> Array<Array<T>>.get(i: Int, j: Int) = this[i][j]

operator fun <T> Collection<T>.get(range: IntRange) = this.filterIndexed { index, _value -> index in range }

fun <T> List<T>.shuffled(random: Random = Random()) = apply { Collections.shuffle(this, random) }