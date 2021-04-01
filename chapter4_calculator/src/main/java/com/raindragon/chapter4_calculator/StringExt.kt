package com.raindragon.chapter4_calculator

fun String.isNumber(): Boolean{
    return try{
        this.toBigInteger()
        true
    }catch (e : NumberFormatException){
        false
    }
}