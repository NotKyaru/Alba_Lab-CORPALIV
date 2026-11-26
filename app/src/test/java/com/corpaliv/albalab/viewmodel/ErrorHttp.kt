package com.corpaliv.albalab.viewmodel

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Simula la excepción que lanza Retrofit cuando el backend responde con [codigo]. */
fun errorHttp(codigo: Int) = HttpException(Response.error<Any>(codigo, """{"error":"x"}""".toResponseBody()))
