/*
 * Module: r2-shared-kotlin
 * Developers: Quentin Gliosca
 *
 * Copyright (c) 2020. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.shared.util

/** A lightweight implementation of URI Template (RFC 6570).
 *
 * Only handles simple cases, fitting Readium's use cases.
 * See https://tools.ietf.org/html/rfc6570
 */
data class URITemplate(val uri: String) {

    /**
     * List of URI template parameter keys, if the [Link] is templated.
     */
    val parameters: List<String> by lazy {
            "\\{\\??([^}]+)}".toRegex()
                .findAll(uri).toList()
                .flatMap { it.groupValues[1].split(",") }
                .distinct()
    }

    /**
     * Expands the HREF by replacing URI template variables by the given parameters.
     */
    fun expand(parameters: Map<String, String>): String {
        fun expandSimpleString(string: String, parameters: Map<String, String>): String =
            string.split(",").joinToString(",") { parameters[it] ?: "" }

        fun expandFormStyle(string: String, parameters: Map<String, String>): String =
            "?" + string.split(",").joinToString("&") { "${it}=${parameters[it].orEmpty()}" }

        return "\\{(\\??)([^}]+)}".toRegex().replace(uri) {
            if (it.groupValues[1].isEmpty())
                expandSimpleString(it.groupValues[2], parameters)
            else
                expandFormStyle(it.groupValues[2], parameters)
        }
    }
}