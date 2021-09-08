import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main(args: Array<String>): Unit {

    Napier.base(DebugAntilog())

    io.ktor.server.netty.EngineMain.main(args)

}
