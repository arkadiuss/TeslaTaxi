package it.arkadiuss.teslataxiconnector

fun <T> Array<T>.shiftLeft(newLastValue: T) {
    for(i in 0 until this.size - 1) {
        this[i] = this[i+1]
    }
    this[this.lastIndex] = newLastValue
}