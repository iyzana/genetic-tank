package de.randomerror.genetictank

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

// don't look here, just some evil stacktrace analysis... did I just say stacktrace analysis
val log: Logger
    get() = getLogger(Throwable().stackTrace[1].className)

