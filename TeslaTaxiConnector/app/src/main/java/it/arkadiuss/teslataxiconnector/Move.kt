package it.arkadiuss.teslataxiconnector

enum class Move constructor(private val character: Char) {
    RIGHT('r'), LEFT('l'), FORWARD('f'),
    BACKWARD('b'), STOP('s'), STOP_TURN('w');

    fun character(): Char {
        return character
    }
}
