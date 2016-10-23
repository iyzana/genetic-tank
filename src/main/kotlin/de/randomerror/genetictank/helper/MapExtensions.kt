package de.randomerror.genetictank.helper

/**
 * Created by henri on 24.10.16.
 */

fun <K, V> Map<K, V>.forEach(block: (K, V) -> Unit): Unit = this.forEach { block(it.key, it.value) }