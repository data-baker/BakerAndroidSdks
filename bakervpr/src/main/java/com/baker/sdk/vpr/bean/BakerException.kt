package com.baker.sdk.vpr.bean

import java.io.IOException
import java.lang.Exception

/**
 * @author hsj55
 * 2021/2/2
 */
class BakerException : IOException {
    var code: String? = null
    override var message: String? = null

    constructor(code: String?, message: String?) {
        this.code = code
        this.message = message
    }

    constructor(code: String?) {
        this.code = code
    }

    constructor(e: Exception?)

    override fun toString(): String {
        return "BakerException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}'
    }
}